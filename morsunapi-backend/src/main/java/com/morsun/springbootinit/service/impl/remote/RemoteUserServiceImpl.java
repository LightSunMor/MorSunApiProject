package com.morsun.springbootinit.service.impl.remote;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morsun.morsunapicommom.serivce.RemoteUserService;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.mapper.UserMapper;
import com.morsun.springbootinit.model.entity.User;
import com.morsun.springbootinit.service.UserService;
import org.apache.commons.lang3.StringUtils;
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
public class RemoteUserServiceImpl implements RemoteUserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;
    /**
     *  根据 ak获取用户
     */
    @Override
    public User getUserSk(String accessKey) {
        if (StringUtils.isBlank(accessKey))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccessKey,accessKey);
        User user = userMapper.selectOne(wrapper);
        return user;
    }

    @Override
    public boolean setUserSk(User user) {
        ThrowUtils.throwIf(ObjectUtil.isNull(user),ErrorCode.PARAMS_ERROR,"修改后用户信息保存失败，为空");
        boolean update = userService.updateById(user);
        return update;
    }
}
