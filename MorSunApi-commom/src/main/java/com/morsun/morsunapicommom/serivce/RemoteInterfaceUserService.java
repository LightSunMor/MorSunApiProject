package com.morsun.morsunapicommom.serivce;

/**
 * @package_name: com.morsun.commonservice.serivce
 * @date: 2023/8/4
 * @week: 星期五
 * @message:
 * @author: morSun
 */
public interface RemoteInterfaceUserService {
       /**
        *  接口调研次数统计
        * @param userId
        * @param interfaceInfoId
        * @return
        */
       boolean interfaceInvokeCount(long userId, long interfaceInfoId);

       boolean canInvokeInterface(long userId, long interfaceInfoId);

}
