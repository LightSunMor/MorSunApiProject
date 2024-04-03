package com.morsun.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.dto.interfaceinfo
 * @date: 2023/7/24
 * @week: 星期一
 * @message: 信息接口dto
 * @author: morSun
 */
@Data
public class InterfaceInfoUpdateRequest {
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;
    /**
     * 请求参数
     */
    private String requestParams;
    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 状态
     */
    private Integer status;

}
