package com.morsun.morsunapicommom.serivce;

import java.util.Map;

/**
 * @package_name: com.morsun.commonservice.serivce
 * @date: 2023/8/4
 * @week: 星期五
 * @message:
 * @author: morSun
 */
public interface RemoteInterfaceService {
    /**
     *  根据接口的url和method，判断接口是否存在
     * @param url
     * @param method
     * @return 返回 接口id
     */
    long interfaceInfoExit(String url,String method) throws Exception;

    /**
     *  查询接口消耗信息
     * @param interfaceId
     * @return
     */
    Map<String,Object> interfaceInfoPropertiesMap(long interfaceId);
}
