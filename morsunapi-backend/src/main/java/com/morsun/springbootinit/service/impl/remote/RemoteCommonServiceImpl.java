package com.morsun.springbootinit.service.impl.remote;

import com.morsun.morsunapicommom.serivce.RemoteCommonService;
import com.morsun.springbootinit.constant.RedisConstant;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @package_name: com.morsun.springbootinit.service.impl.remote
 * @date: 2023/8/4
 * @week: 星期五
 * @message:
 * @author: morSun
 */
@DubboService
public class RemoteCommonServiceImpl implements RemoteCommonService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean RandomNumIsRepeat(String nonce) {
        List<String> range = stringRedisTemplate.opsForList().range(RedisConstant.INVOKE_INTERFACE_NONCE_KEY, 0, -1);
        if (range.contains(nonce))
        {
            return true;
        }
        return false;
    }
}
