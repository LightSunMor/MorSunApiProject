package com.morsun.morsunapicommom.serivce;

import com.morsun.springbootinit.model.entity.User;

/**
 * @package_name: com.morsun.commonservice.serivce
 * @date: 2023/8/4
 * @week: 星期五
 * @message: 远程调用 service
 * @author: morSun
 */
public interface RemoteUserService {
    /**
     *  根据 accessKey 查看是否存在这个用户，并且返回sk
     * @param accessKey
     * @return user
     */
    User getUserSk(String accessKey);

    /**
     *  保存扣除太阳币后的用户信息
     * @param user
     * @return
     */
    boolean setUserSk(User user);
}
