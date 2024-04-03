package com.morsun.morsunapigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @package_name: com.morsun.morsunapigateway.filter
 * @date: 2024/3/16
 * @week: 星期六
 * @message: 缓存主体网关筛选器！！！
 *  有这个缓存过滤器的目的：目的是为了解决请求体只能读取一次的问题，因为在调用 DataBufferUtils.join() 方法时，请求体会被消费掉，所以需要将其缓存起来，以便后续处理器或过滤器可以再次读取。
 * @author: morSun
 */
@Component
public class CacheBodyGateWayFilter implements Ordered, GlobalFilter {

    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getHeaders().getContentType() == null){
            return chain.filter(exchange); //直接不让往下走了，是个无效请求
        }else {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        DataBufferUtils.retain(dataBuffer);
                        // publisher
                        Flux<DataBuffer> cachedFlux =
                                Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        // corePublisher mutatedRequest 变异请求
                        ServerHttpRequestDecorator mutatedRequest  = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
                       exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY,cachedFlux);
                       // 放行变异请求
                       return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }
    }


    @Override
    public int getOrder() {
        // 最高等级的过滤器，请求第一个接触到它
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
