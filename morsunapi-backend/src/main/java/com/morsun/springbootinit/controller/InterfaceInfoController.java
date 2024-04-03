package com.morsun.springbootinit.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.morsun.clientsdk.client.MorApiClient;
import com.morsun.clientsdk.exception.ApiException;
import com.morsun.clientsdk.model.request.CurrencyRequest;
import com.morsun.clientsdk.model.response.ResultResponse;
import com.morsun.springbootinit.annotation.AuthCheck;
import com.morsun.springbootinit.common.*;
import com.morsun.springbootinit.constant.UserConstant;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.model.dto.interfaceinfo.*;
import com.morsun.springbootinit.model.entity.InterfaceInfo;
import com.morsun.springbootinit.model.entity.User;
import com.morsun.springbootinit.model.enums.InterfaceInfoStatusEnum;
import com.morsun.springbootinit.model.vo.InterfaceInfoVO;
import com.morsun.springbootinit.service.InterfaceInfoService;
import com.morsun.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 帖子接口
 *
 * @author morsun
 * @from 知识星球
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private MorApiClient morApiClient;

    // 将json转为Java对象的工具
    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
   /*     List<String> tags = interfaceInfoAddRequest.getTags();
        if (tags != null) {
            interfaceInfo.setTags(GSON.toJson(tags));
        }*/
        Long userId = userService.getLoginUser(request).getId();
        interfaceInfo.setUserId(userId);
        interfaceInfo.setStatus(0);
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);

        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVO(interfaceInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        // todo 存到redis中
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // todo 学习这样的写法
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
    }

    /**
     * 分页获取  [当前用户]  创建的资源列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        interfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES❤ 查询，封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> searchInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.searchFromEs(interfaceInfoQueryRequest);
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
    }

    /**
     * 上线 （仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        //1. 判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //2. 判断接口是否可调用
        CurrencyRequest currencyRequest = new CurrencyRequest();
        currencyRequest.setPath(oldInterfaceInfo.getUrl());
        currencyRequest.setMethod(oldInterfaceInfo.getMethod());
        //todo 目前是写死的，后面想办法提供正确的参数
        currencyRequest.setRequestParams(new Object());
        ResultResponse response = null;
        try {
            response = morApiClient.request(currencyRequest);
        } catch (ApiException e) {
            log.error("接口上线时进行接口可用测试，出现问题，问题信息如下：{}",e.getMessage());
            e.printStackTrace();
        }
        ThrowUtils.throwIf(ObjectUtil.isNull(response.getData()),ErrorCode.SYSTEM_ERROR,"接口验证失败");
        //3. 修改接口的状态为 上线
        boolean result = interfaceInfoService.update(new LambdaUpdateWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getId,id)
                .set(InterfaceInfo::getStatus, InterfaceInfoStatusEnum.Online.getValue())
        );
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(idRequest.getId());
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.update(new LambdaUpdateWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getId,idRequest.getId())
                .set(InterfaceInfo::getStatus, InterfaceInfoStatusEnum.Offline.getValue()));
        return ResultUtils.success(result);
    }

    /**
     * 在线接口测试
     *
     * @param invokeInterfaceRequest
     * @return
     */
    @PostMapping("/invoke-testline")
    // 事务
    @Transactional(rollbackFor = Exception.class)
        public BaseResponse<Object> invokeOnLineInterfaceInfo(@RequestBody InvokeInterfaceRequest invokeInterfaceRequest,HttpServletRequest request) {
        /*
        * 假设前端已经写好了对应的请求参数
        *  请求体参数 {}
        *  query参数 ？
        * */
        if (invokeInterfaceRequest == null || invokeInterfaceRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = invokeInterfaceRequest.getId();
        //1. 判断接口是否存在or接口状态是否正常
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf( oldInterfaceInfo.getStatus()== InterfaceInfoStatusEnum.Offline.getValue(), ErrorCode.OPERATION_ERROR,"接口已下线");
        //2. 构建请求参数（前端拿到）
        List<requestParams> requestParamsList = invokeInterfaceRequest.getRequestParams();

        String requestParams = "{}";
        if (requestParamsList!=null&&requestParamsList.size()>0){
            // 构建json格式的参数体
            JsonObject jsonObject = new JsonObject();
            for (com.morsun.springbootinit.model.dto.interfaceinfo.requestParams params : requestParamsList) {
                jsonObject.addProperty(params.getFieldName(),params.getValue());
            }
            requestParams= GSON.toJson(jsonObject);
        }
        // 构建请求参数
        Map<String, Object> params = new Gson().fromJson(requestParams, new TypeToken<Map<String, Object>>() {
        }.getType());
        try {
            CurrencyRequest currencyRequest = new CurrencyRequest();
            // 直接从数据库中拿到method，url动态拿到
            currencyRequest.setMethod(oldInterfaceInfo.getMethod());
            currencyRequest.setPath(oldInterfaceInfo.getUrl());
            currencyRequest.setRequestParams(params);
            // 测试调用
            ResultResponse response = morApiClient.request(currencyRequest);

            log.info("从网关回来的响应结果：{}",response.getData());
            return ResultUtils.success(response.getData());
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }

        // region 3.gson将json格式的数据，退回属性样式，保证创建对应对象 (json -> 对象)
//        com.morsun.clientsdk.model.User fromJson = GSON.fromJson(invokeInterfaceRequest.getRequestParams().get(0).getValue(), com.morsun.clientsdk.model.User.class);

        // 配置client端的body属性(英文化)
//        morApiClient.setBody(fromJson.getUsername());
        // 这个sdk中的client应该提供一个方法，接收api 的名字，path，参数。底层动态调用对应的接口  todo 重构sdk
//        String username = morApiClient.invokeInterfaceOnInfo(id.toString(),oldInterfaceInfo.getName(),oldInterfaceInfo.getMethod(),"/name/user",null,invokeInterfaceRequest.getUserRequestParams());
//        String  username = morApiClient.getUsernameByPost(fromJson);
//        return ResultUtils.success("username");
        //endregion

    }

}
