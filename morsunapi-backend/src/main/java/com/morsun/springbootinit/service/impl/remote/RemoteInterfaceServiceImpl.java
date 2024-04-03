package com.morsun.springbootinit.service.impl.remote;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morsun.morsunapicommom.serivce.RemoteInterfaceService;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.model.entity.InterfaceInfo;
import com.morsun.springbootinit.model.enums.InterfaceInfoStatusEnum;
import com.morsun.springbootinit.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @package_name: com.morsun.springbootinit.service.impl.remote
 * @date: 2023/8/4
 * @week: 星期五
 * @message:
 * @author: morSun
 */
@DubboService
public class RemoteInterfaceServiceImpl implements RemoteInterfaceService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     *  验证这个接口是否存在
     *  todo 仅用这两个参数，不能完全查出来这是哪一个接口，所以在设置api接口url时要注意
     * @param url
     * @param method
     * @return
     */
    @Override
    public long interfaceInfoExit(String url, String method) throws Exception{
        if (StringUtils.isBlank(url)||StringUtils.isBlank(method))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getMethod,method);
        queryWrapper.eq(InterfaceInfo::getUrl,url);
        InterfaceInfo one = interfaceInfoService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtil.isNull(one),ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(one.getStatus().equals(InterfaceInfoStatusEnum.Offline.getValue()),ErrorCode.NOT_FOUND_ERROR,"接口未开启");
        return one.getId();
    }

    /**
     *  根据 接口id 拿到接口每次调用应该消耗的太阳币
     * @param interfaceId
     * @return
     */
    @Override
    public Map<String,Object> interfaceInfoPropertiesMap(long interfaceId) {
        ThrowUtils.throwIf(interfaceId<0,ErrorCode.PARAMS_ERROR,"待调用接口id不正确");
        InterfaceInfo info = interfaceInfoService.getById(interfaceId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("sunCoin",info.getOnceSunCoin());
        // 拿到数据库中的请求参数
        map.put("requestParams",info.getRequestParams());
        //....待定
        return map;
    }
}