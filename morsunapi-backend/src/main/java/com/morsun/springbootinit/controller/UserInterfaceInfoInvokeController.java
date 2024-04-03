package com.morsun.springbootinit.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.morsun.springbootinit.annotation.AuthCheck;
import com.morsun.springbootinit.common.BaseResponse;
import com.morsun.springbootinit.common.DeleteRequest;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.common.ResultUtils;
import com.morsun.springbootinit.constant.UserConstant;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.mapper.UserInterfaceInvokeMapper;
import com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke.UserInterfaceInvokeAddRequest;
import com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke.UserInterfaceInvokeQueryRequest;
import com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke.UserInterfaceInvokeUpdateRequest;
import com.morsun.springbootinit.model.entity.User;
import com.morsun.springbootinit.model.entity.UserInterfaceInvoke;
import com.morsun.springbootinit.model.vo.InterfaceAnalysisVO;
import com.morsun.springbootinit.model.vo.UserInterfaceInfoInvokeVO;
import com.morsun.springbootinit.service.InterfaceInfoService;
import com.morsun.springbootinit.service.UserInterfaceInvokeService;
import com.morsun.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子接口
 *
 * @author morsun
 * @from 知识星球
 */
@RestController
@RequestMapping("/userInterfaceInfoInvoke")
@Slf4j
public class UserInterfaceInfoInvokeController {
    @Resource
    private UserInterfaceInvokeService userInterfaceInvokeService;
    @Resource
    private UserInterfaceInvokeMapper userInterfaceInvokeMapper;
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserService userService;
    private final static Gson GSON = new Gson();
    // region 增删改查
    /**
     * 创建
     *
     * @param userInterfaceInvokeAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfoInvoke(@RequestBody UserInterfaceInvokeAddRequest userInterfaceInvokeAddRequest, HttpServletRequest request) {
        if (userInterfaceInvokeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInvoke userInterfaceInvoke = new UserInterfaceInvoke();
        BeanUtils.copyProperties(userInterfaceInvokeAddRequest, userInterfaceInvoke);

        Long userId = userService.getLoginUser(request).getId();
        userInterfaceInvoke.setUserId(userId);
        userInterfaceInvoke.setStatus(0);
        userInterfaceInvokeService.validUserInterfaceInfoInvoke(userInterfaceInvoke, true);

        boolean result = userInterfaceInvokeService.save(userInterfaceInvoke);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserUserInterfaceInfoInvokeInfoInvokeInfoId = userInterfaceInvoke.getId();
        return ResultUtils.success(newUserUserInterfaceInfoInvokeInfoInvokeInfoId);
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
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInvoke byId = userInterfaceInvokeService.getById(id);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!byId.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInvokeService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userInterfaceInvokeUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfoInvoke(@RequestBody UserInterfaceInvokeUpdateRequest userInterfaceInvokeUpdateRequest) {
        if (userInterfaceInvokeUpdateRequest == null || userInterfaceInvokeUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInvoke userInterfaceInvoke = new UserInterfaceInvoke();
        BeanUtils.copyProperties(userInterfaceInvokeUpdateRequest, userInterfaceInvoke);

        // 参数校验
        userInterfaceInvokeService.validUserInterfaceInfoInvoke(userInterfaceInvoke, false);
        long id = userInterfaceInvokeUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInvoke oldUserInterfaceInfoInvoke = userInterfaceInvokeService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfoInvoke == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userInterfaceInvokeService.updateById(userInterfaceInvoke);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserInterfaceInfoInvokeVO> getUserInterfaceInfoInvokeVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInvoke userInterfaceInvoke = userInterfaceInvokeService.getById(id);
        if (userInterfaceInvoke == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userInterfaceInvokeService.getUserInterfaceInfoInvokeInfoVO(userInterfaceInvoke, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userInterfaceInvokeQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserInterfaceInfoInvokeVO>> listUserInterfaceInfoInvokeInfoVOByPage(@RequestBody UserInterfaceInvokeQueryRequest userInterfaceInvokeQueryRequest,
            HttpServletRequest request) {
        long current = userInterfaceInvokeQueryRequest.getCurrent();
        long size = userInterfaceInvokeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 拿到分页的调用记录
        Page<UserInterfaceInvoke> userInterfaceInvokePage = userInterfaceInvokeService.page(new Page<>(current, size),
                userInterfaceInvokeService.getQueryWrapper(userInterfaceInvokeQueryRequest));
        return ResultUtils.success(userInterfaceInvokeService.getUserInterfaceInfoInvokeInfoVOPage(userInterfaceInvokePage, request));
    }

    /**
     * 根据用户分页获取接口调用列表（封装类）
     *
     * @param userInterfaceInvokeQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/voId")
    public BaseResponse<Page<UserInterfaceInfoInvokeVO>> listUserByIdInterfaceInfoInvokeInfoVOByPage(@RequestBody UserInterfaceInvokeQueryRequest userInterfaceInvokeQueryRequest,
                                                                                                 HttpServletRequest request) {
        long current = userInterfaceInvokeQueryRequest.getCurrent();
        long size = userInterfaceInvokeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 拿到分页的调用记录
        Page<UserInterfaceInvoke> userInterfaceInvokePage = userInterfaceInvokeService.page(new Page<>(current, size),
                userInterfaceInvokeService.getQueryWrapper(userInterfaceInvokeQueryRequest));
        return ResultUtils.success(userInterfaceInvokeService.getUserByIdInterfaceInfoInvokeInfoVOPage(userInterfaceInvokePage, request));
    }

    // endregion


    /**
     *  开通接口调用 暂时未用上
     * @param interfaceId
     * @param invokeTimes
     * @param request
     * @return
     */
    @PostMapping("/open-interface/{interfaceId}")
    public BaseResponse<String> openInterface(@PathVariable("interfaceId") Long interfaceId, Integer invokeTimes,HttpServletRequest request){
        //1. 校验参数是否正确(接口是否存在，是否可调，用户是否被封禁)
        ThrowUtils.throwIf(ObjectUtil.isNull(interfaceId)||interfaceId<=0||invokeTimes<0,ErrorCode.PARAMS_ERROR);
        return userInterfaceInvokeService.openInterfaceOrIncrease(interfaceId,invokeTimes,request);

    }

    /**
     * 提供接口分析数据 (管理员可见，看到所有接口调用情况)
     * @return
     */
    @PostMapping("/interface-analysis")
    public BaseResponse<List<InterfaceAnalysisVO>> interfaceAna()
    {
//        List<UserInterfaceInvoke> userInterfaceInvokeList = userInterfaceInvokeMapper.listInterfaceAnalysis(3);
        List<UserInterfaceInvoke> userInterfaceInvokeList = userInterfaceInvokeService.listInterfaceInvokeSituation();
        // 转vo
        List<InterfaceAnalysisVO> analysisVOList = userInterfaceInvokeList.stream().map(item -> {
            InterfaceAnalysisVO analysisVO = new InterfaceAnalysisVO();
            analysisVO.setValue(item.getTotalNum());
            String name = interfaceInfoService.getById(item.getInterfaceInfoId()).getName();
            analysisVO.setName(name);
            return analysisVO;
        }).collect(Collectors.toList());
        log.info("analysisVo:"+analysisVOList);
        return ResultUtils.success(analysisVOList);
    }

}
