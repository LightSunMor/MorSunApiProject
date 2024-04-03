package com.morsun.interfacesi.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @package_name: com.morsun.interfacesi.utils
 * @date: 2024/3/18
 * @week: 星期一
 * @message: 请求工具包
 * @author: morSun
 */
@Slf4j
public class RequestUtils {
// 将请求参数处理为get请求的Query参数

    /**
     *  拼接生成url todo 知识点 反射应用
     * @param baseUrl 基础url
     * @param params 请求参数
     * @param <T> 泛型参数类型
     * @return
     * @throws RuntimeException
     */
    public static <T> String buildUrl(String baseUrl,T params) throws RuntimeException{
        StringBuilder builderUrl = new StringBuilder(baseUrl);
        Field[] fields = params.getClass().getDeclaredFields();
        boolean isFirstParam =true;
        for (Field field : fields) {
            //暴力反射
            field.setAccessible(true);
            String name = field.getName();
            // 跳过序列化UID属性
            if ("serialVersionUID".equals(name))
            {
                continue;
            }
            try {
                // 反射方法拿到属性的值。正常应该是params.getName()
                Object value = field.get(params);
                if (value!=null){
                    if (isFirstParam){
                        builderUrl.append("?").append("=").append(value);
                        isFirstParam = false;
                    }else {
                        builderUrl.append("&").append("=").append(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("构建url异常");
            }
        }
        return builderUrl.toString();
    }



}


