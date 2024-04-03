package com.morsun.morsunapigateway.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.morsun.morsunapicommom.model.dto.RequestParamsField;
import com.morsun.morsunapicommom.serivce.RemoteInterfaceService;
import com.morsun.morsunapicommom.serivce.RemoteInterfaceUserService;
import com.morsun.morsunapicommom.serivce.RemoteUserService;
import com.morsun.springbootinit.model.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bouncycastle.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.morsun.clientsdk.utils.SignUtil.genSign;
import static com.morsun.morsunapigateway.common.CommonVariable.REDIS_RANDOM_NUMBER_LIST_NAME;

/**
 * @package_name: com.morsun.morsunapigateway.filter
 * @date: 2023/8/2
 * @week: 星期三
 * @message:
 * @author: morSun
 */
@Slf4j
@Component // 注入到容器中
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITE_LIST = Arrays.asList("127.0.0.1","47.109.140.147");
    //todo: 这里应该参考笔记上的结合网关记录所有接口的host地址，这里测试写死
    private static final String HOST_ADDRESS= "http://localhost:8011";

    @DubboReference
    private RemoteUserService remoteUserService;

    @DubboReference
    private RemoteInterfaceService remoteInterfaceService;

    @DubboReference
    private RemoteInterfaceUserService remoteInterfaceUserService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     *  过期时间 7分钟
     */
    private final static long SEVEN_MINUTES = 7 * 60 * 1000L;
    /**
     *  用户被禁状态
     */
    private final static int BAN=1;


    // Mono 对象 就像前端的Promise对象，响应式编程对象
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从交换机中拿到请求和响应体，做一些处理
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2. 请求日志（从exchange拿到 reuqest 对象，打印请求日志）
        String requestMethod = request.getMethod().toString();
        String hostString = request.getLocalAddress().getHostString();
        String path = request.getPath().toString();

        StringBuilder sb =new StringBuilder();
        // 拿到的只是网关的host地址
        sb.append("请求的唯一ID：").append(request.getId()).append("\n")
                .append("请求的方法：").append(requestMethod).append("\n")
                .append("请求的查询参数：").append(request.getQueryParams()).append("\n")
                .append("请求的URI：").append(request.getURI().toString().trim()).append("\n")
                .append("请求的路径：").append(path).append("\n")
                .append("请求的来源地址：").append(hostString).append("\n")
                .append("请求的来源：").append(request.getRemoteAddress()).append("\n")
                .append("\n");
        // region 将请求日志放到文件中
        String requestInfo = new Date(System.currentTimeMillis()).toString()+"\n"+ new String(sb);

        try(BufferedWriter writer=new BufferedWriter(new FileWriter("D:\\星球项目\\API项目\\MorSunApiProject\\morsunapi-gateway\\src\\main\\resources\\RequestInfo.txt",true))) {
            writer.write(requestInfo);
            writer.newLine();// 换行
            writer.flush();
            System.out.println("请求日志已经写入 RequestInfo文件中.");
        } catch (IOException e) {
            log.error("请求日志写入失败");
            e.printStackTrace();
        }
        // endregion
        log.info(requestInfo);

        //3. 黑白名单（封禁ip）
        if (!WHITE_LIST.contains(hostString))
        {
            log.error(requestInfo+"{不是白名单内的ip，没有权限}");
            return handlerNoAuth(response);
        }
        //4. 用户鉴权 （ak，sk）
        HttpHeaders httpHeaders = request.getHeaders();
        String accessKey = httpHeaders.getFirst("accessKey"); //如果没有这个请求头不会报错，拿到null
        String secretKey;
        String sign = httpHeaders.getFirst("sign");
        String nonce = httpHeaders.getFirst("nonce");
        String timestamp =httpHeaders.getFirst("timestamp");
        String body = httpHeaders.getFirst("body");

        //done 这里的ak和sk校验方式，应该是从自己的库中判断，是否给其他人分配过这个ak和sk，从而判断是谁
        User userSk ;
        try {
             userSk = remoteUserService.getUserSk(accessKey);
            if (ObjectUtil.isNull(userSk))
            {
                log.error("校验ak和sk出现错误: 错误的ak和sk,不存在该用户");
                return handlerNoAuth(response);
            }
            if (userSk.getIsDelete().equals(BAN)) {
                log.error("校验ak和sk出现错误: 该用户已被删除");
                return handlerNoAuth(response);
            }
             // tips: 注意使用远程调用会抛出一个非业务异常，名叫
        } catch (RuntimeException e) {
            log.error("校验ak和sk出现错误:"+e.getMessage());
            return handlerNoAuth(response);
        }

        //done 从本系统中查询到secretKey
        secretKey =userSk.getSecretKey();
        // 校验随机数 （存储 redis） 直接当前项目创建
        ArrayList<String> nonceList = new ArrayList<>();
        //如果没有这样一个列表，会返回一个空列表，不会报错
        List<String> range = stringRedisTemplate.opsForList().range(REDIS_RANDOM_NUMBER_LIST_NAME, 0, -1);
        if (range.contains(nonce)) {
            log.error("随机数校验失败！！无权限继续");
            return handlerNoAuth(response);
        }else {
            // 将当前随机数放入list
            stringRedisTemplate.opsForList().leftPushIfPresent(REDIS_RANDOM_NUMBER_LIST_NAME,nonce);
        }
        //done 校验时间和当前时间不能超过7分钟 7*60*1000 。😏防止重发XHR
        if (timestamp != null && (System.currentTimeMillis() - Long.parseLong(timestamp)) > SEVEN_MINUTES) {
            log.error("校验时间过长，无法继续");
            return handlerNoAuth(response);
        }
        //done 和请求端以相同的方式生成签名 ,sk要在第一个todo中从数据库中查出来
        String genSign = genSign(body, secretKey);
        if (!genSign.equals(sign)) {
            log.error("签名不正确，无法继续");
            return handlerNoAuth(response);
        }
        //5. 请求的模拟接口是否存在(从数据中判断)

        String uri = request.getURI().toString().trim();
        // 截取？以前的url
        int index = uri.indexOf("?");
        String dataUrl=index!=-1?uri.substring(0,index):uri;
        // 检验方法请求
        ObjectUtils.isNotNull(requestMethod);

        long interfaceId = 0;
        try {
           interfaceId = remoteInterfaceService.interfaceInfoExit(dataUrl, requestMethod);
        } catch (Exception e) {
            log.error("检验接口，出现问题："+e.getMessage());
            return handlerNoAuth(response);
        }
        //  5.1 检验当前用户是否可以调用当前接口，根据接口调用记录  remoteInterfaceUserService
        try {
            boolean b = remoteInterfaceUserService.canInvokeInterface(userSk.getId(), interfaceId);
            if (!b){
                log.error("当前用户{}，被禁止调用接口{}",userSk.getUserName(),interfaceId);
                return handlerNoAuth(response);
            }
        } catch (Exception e) {
            log.error("检验用户禁用时，出现异常："+e.getMessage());
            return handlerNoAuth(response);
        }


        // todo 将所有接口信息存到redis中，在此校验时可以直接从redis中拿取数据

        // 6.调用目标接口，因为动态调用，需要拿到接口的相关信息
        Map<String,Object> interfaceInfoMap =null;

        try {
            interfaceInfoMap = remoteInterfaceService.interfaceInfoPropertiesMap(interfaceId);
            // 6.xx 用户是否有足够的太阳币进行调用？
            Integer restAmountCoin = userSk.getRestAmountCoin();// 调用用户剩余的太阳币
            int oncePay =Integer.parseInt(interfaceInfoMap.get("sunCoin").toString());
            if (restAmountCoin<=0||restAmountCoin<oncePay)
            {
                log.error("剩余太阳币不足，已无法调用接口，请充值后再次进行调用");
                return handleError(response);
            }
            MultiValueMap<String, String> queryParams = request.getQueryParams(); // 拿到get请求的参数
            String requestParams = interfaceInfoMap.get("requestParams").toString();
            // 注意Json化的格式风险错误
            List<RequestParamsField> list = new Gson().fromJson(requestParams,new TypeToken<List<RequestParamsField>>(){
            }.getType());

            //6.1 辨别是什么请求
            if ("POST".equals(requestMethod)){
                Object cacheBody = exchange.getAttribute(CacheBodyGateWayFilter.CACHE_REQUEST_BODY_OBJECT_KEY);
                // 强制转换 Flux<DataBuffer>
                String requestBody = getPostRequestBody((Flux<DataBuffer>) cacheBody);
                log.info("POST 请求参数" + requestBody);
                Map<String, Object> requestBodyMap = new Gson().fromJson(requestBody,new TypeToken<List<RequestParamsField>>(){}.getType());
                if (StringUtils.hasText(requestParams)){
                    for (RequestParamsField field : list) {
                        if ("是".equals(field.getRequired())){
                            // 判断请求体中给出的参数是否符合 接口的请求参数规则
                            if (!StringUtils.hasText((CharSequence) requestBodyMap.get(field.getFieldName())) || !requestBodyMap.containsKey(field.getFieldName()))
                            {
                                log.error("本次请求参数有误！！,{}为必选项。请详细阅读并参考MSAPI开发者文档",field.getFieldName());
                                return handleError(response);
                            }
                        }
                    }
                }

            }else if ("GET".equals(requestMethod)){
                log.info("GET 请求参数："+request.getQueryParams());
                // 校验请求参数
                if (StringUtils.hasText(requestParams)){
                    for (RequestParamsField field : list) {
                        if ("是".equals(field.getRequired())){
                            if (!StringUtils.hasText(queryParams.getFirst(field.getFieldName())) || !queryParams.containsKey(field.getFieldName()))
                            {
                                log.error("本次请求参数有误！！,{}为必选项。请详细阅读并参考MSAPI开发者文档",field.getFieldName());
                                return handleError(response);
                            }
                        }
                    }
                }
            }

            //7.done 请求转发调用模拟接口（流程跑通的关键）
            Mono<Void> res = responseFilter(exchange, chain, interfaceId, userSk,interfaceInfoMap);
            return res;
        } catch (Exception e) {
            log.error("gateway 处理响应异常\n" + e);
            return handleError(response);
        }

    }

    /**
     * 获取post请求正文
     *
     * @param body 身体
     * @return {@link String}
     */
    private String getPostRequestBody(Flux<DataBuffer> body) {
        // 原子性的，可自动更新对象引用
        AtomicReference<String> getBody = new AtomicReference<>();
        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            getBody.set(Strings.fromUTF8ByteArray(bytes));
        });
        return getBody.get();
    }

    /***
     *   处理响应
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> responseFilter(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceId,User userSk,Map<String,Object> interfaceInfoMap) throws Exception{

            ServerHttpResponse originalResponse = exchange.getResponse();
            // 数据缓存工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatusCode statusCode = originalResponse.getStatusCode();
            if(statusCode == HttpStatus.OK){
                //region 装饰原始response对象，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 在转发请求结束之后，再执行下面的逻辑（回调）
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // writeWith 接收一个Publisher执行方法
                            return super.writeWith(
                                   fluxBody.map(dataBuffer -> {
                                       //7.done 调用成功，扣除太阳币 （todo 可以使用redission分布式锁）
                                       try {
                                           Integer restAmountCoin = userSk.getRestAmountCoin();// 调用用户剩余的太阳币
                                           int oncePay =Integer.parseInt(interfaceInfoMap.get("sunCoin").toString());
                                            //7.1 调用完成， 扣除当前用户本次调用消耗的太阳币
                                           restAmountCoin-=oncePay;
                                           userSk.setRestAmountCoin(restAmountCoin);
                                           // 保存用户信息
                                           boolean b = remoteUserService.setUserSk(userSk);
                                           if (!b){
                                               throw new RuntimeException("更新用户扣费后信息失败！");
                                           }
                                           //7.2 调用完成，修改用户调用接口的记录
                                           boolean t = remoteInterfaceUserService.interfaceInvokeCount(userSk.getId(), interfaceId);
                                            if (!t){
                                                throw new RuntimeException("更新用户调用接口信息失败！");
                                            }
                                       } catch (RuntimeException e) {
                                           log.error("统计次数操作异常："+e.getMessage());
                                           e.printStackTrace();
                                       }

                                       // content 就是目的接口的返回值
                                       byte[] content = new byte[dataBuffer.readableByteCount()];
                                       dataBuffer.read(content);
                                       DataBufferUtils.release(dataBuffer);//释放掉内存
                                       // 构建日志
                                       StringBuilder sb2 = new StringBuilder(200);
                                       List<Object> rspArgs = new ArrayList<>();
                                       rspArgs.add(originalResponse.getStatusCode());
                                       //data 是 content规范化后的结果
                                       String data = new String(content, StandardCharsets.UTF_8);
                                       sb2.append(data);
                                       log.info("Response响应日志打印：data:{},statusCode:{}",sb2.toString(), rspArgs.toArray());//log.info(data, originalResponse.getStatusCode());
                                       return bufferFactory.wrap(content);
                               })
                            );

                        } else {
                            //8. 调用失败，返回一个规范的错误
                            handleError(originalResponse);
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // endregion

                // 放行 增强之后的response对象
                log.warn("增强后的响应对象：-->"+ JSONUtil.toJsonStr(decoratedResponse));
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
       // 放行 ( done （decoratedResponse）在这里并不会等待请求执行回来，再往下走，这是一个异步的操作，下面的代码可能会先执行真实的接口才被调用)
            return chain.filter(exchange);//降级处理返回数据
    }


    private Mono<Void> handlerNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        // todo :加一些响应信息（或者使用全局异常处理器）
        //提交响应，结束请求
        return response.setComplete();
    }
    private Mono<Void> handleError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR); // 500
        // todo :加一些响应信息（或者使用全局异常处理器）
        //提交响应，结束请求
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+70;
    }
}

