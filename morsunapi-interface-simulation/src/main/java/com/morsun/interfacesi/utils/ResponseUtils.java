package com.morsun.interfacesi.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.morsun.clientsdk.model.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @package_name: com.morsun.interfacesi.utils
 * @date: 2024/3/18
 * @week: 星期一
 * @message: 响应工具包 ,
 * 才能让一些不是map形式的返回结果变成map，从而可以json化
 * @author: morSun
 */
@Slf4j
public class ResponseUtils {
    /**
     * 通过GSON配合JSON 将string转为Map
     * @param response
     * @return
     */
    private static Map<String, Object> responseToMap(String response) {
        return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     *  再次处理结果response 中是否有success标记
     * @param response
     * @param <T>
     * @return
     */
    public static <T> ResultResponse baseResponse(String response) {

        Map<String, Object> fromResponse = responseToMap(response);

        boolean success = (boolean) fromResponse.get("success");
        ResultResponse baseResponse = new ResultResponse();

        if (!success) {
            baseResponse.setData(fromResponse);
            return baseResponse;
        }
        fromResponse.remove("success");
        baseResponse.setData(fromResponse);
        return baseResponse;
    }


}
