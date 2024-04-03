package com.morsun.springbootinit.service.impl.remote;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morsun.morsunapicommom.serivce.RemoteInterfaceUserService;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.model.entity.UserInterfaceInvoke;
import com.morsun.springbootinit.service.UserInterfaceInvokeService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @package_name: com.morsun.springbootinit.service.impl.remote
 * @date: 2023/8/4
 * @week: 星期五
 * @message:
 * @author: morSun
 */
@DubboService
public class RemoteInterfaceUserServiceImpl implements RemoteInterfaceUserService {
    @Resource
    private UserInterfaceInvokeService userInterfaceInvokeService;
    /**
     *  调用成功，修改调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    @Override
    public boolean interfaceInvokeCount(long userId, long interfaceInfoId) {
        if (userId<=0||interfaceInfoId<=0)
        {
            ThrowUtils.throwIf(true,ErrorCode.NOT_FOUND_ERROR,"用户或者接口信息id错误");
        }
        //判断是否可以调用
        UserInterfaceInvoke one = userInterfaceInvokeService.getOne(new LambdaQueryWrapper<UserInterfaceInvoke>()
                .eq(UserInterfaceInvoke::getUserId, userId)
                .eq(UserInterfaceInvoke::getInterfaceInfoId, interfaceInfoId)
        );
        boolean b=false;

        if (ObjectUtil.isNull(one))
        {
            // 如果是第一次调用，就新增调用记录
            //todo 知识点 注意UserInterfaceInvoke实体类中的属性要配上@TableFiled匹配表的列名，以此才能插入不出错
            UserInterfaceInvoke invoke = new UserInterfaceInvoke();
            invoke.setUserId(userId);
            invoke.setInterfaceInfoId(interfaceInfoId);
            b=userInterfaceInvokeService.save(invoke);
        }else {
            // 如果不是第一次调用，就增加调用次数
             b = userInterfaceInvokeService.invokeCount(userId, interfaceInfoId);
        }
        return b;
    }


    /**
     *  是否可以调用
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    @Override
    public boolean canInvokeInterface(long userId, long interfaceInfoId){
        ThrowUtils.throwIf(userId<0||interfaceInfoId<0,ErrorCode.PARAMS_ERROR);

        UserInterfaceInvoke one = userInterfaceInvokeService.getOne(new LambdaQueryWrapper<UserInterfaceInvoke>()
                .eq(UserInterfaceInvoke::getUserId, userId)
                .eq(UserInterfaceInvoke::getInterfaceInfoId, interfaceInfoId)
        );
        // 没有调用过
        if (ObjectUtil.isNull(one))
        {
            return true;
        }
        return one.getStatus() == 0;
    }
}
