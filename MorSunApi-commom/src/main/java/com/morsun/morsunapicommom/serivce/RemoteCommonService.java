package com.morsun.morsunapicommom.serivce;

/**
 * @package_name: com.morsun.commonservice.serivce
 * @date: 2023/8/4
 * @week: 星期五
 * @message: 通用service
 * @author: morSun
 */
public interface RemoteCommonService {

    /**
     *  判断 nonce 随机数是否重复了，redis中判断
     * @param nonce
     * @return
     */
    boolean RandomNumIsRepeat(String nonce);
}
