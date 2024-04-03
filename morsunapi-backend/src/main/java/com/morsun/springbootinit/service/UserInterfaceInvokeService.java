package com.morsun.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morsun.springbootinit.common.BaseResponse;
import com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke.UserInterfaceInvokeQueryRequest;
import com.morsun.springbootinit.model.entity.UserInterfaceInvoke;
import com.morsun.springbootinit.model.vo.UserInterfaceInfoInvokeVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86176
* @description 针对表【user_interface_invoke(`user_interface_invoke`)】的数据库操作Service
* @createDate 2023-08-01 16:13:33
*/
public interface UserInterfaceInvokeService extends IService<UserInterfaceInvoke> {
    /**
     * 校验
     *
     * @param userInterfaceInvoke
     * @param add
     */
    void validUserInterfaceInfoInvoke(UserInterfaceInvoke userInterfaceInvoke, boolean add);

    QueryWrapper<UserInterfaceInvoke> getQueryWrapper(UserInterfaceInvokeQueryRequest userInterfaceInvokeQueryRequest);

    // 用户个人接口调用记录查看
    Page<UserInterfaceInfoInvokeVO> getUserByIdInterfaceInfoInvokeInfoVOPage(Page<UserInterfaceInvoke> userInterfaceInvokePage, HttpServletRequest request);

    Page<UserInterfaceInfoInvokeVO> getUserInterfaceInfoInvokeInfoVOPage(Page<UserInterfaceInvoke> userInterfaceInvokePage, HttpServletRequest request);

    UserInterfaceInfoInvokeVO getUserInterfaceInfoInvokeInfoVO(UserInterfaceInvoke userInterfaceInvoke, HttpServletRequest request);

    /**
     *  接口 调用 统计
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean invokeCount(long userId,long interfaceInfoId);

    BaseResponse<String> openInterfaceOrIncrease(Long interfaceId, Integer invokeTimes, HttpServletRequest request);

    List<UserInterfaceInvoke> listInterfaceInvokeSituation();

}
