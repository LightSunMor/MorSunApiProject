package com.morsun.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.dto.interfaceinfo
 * @date: 2024/3/13
 * @week: 星期三
 * @message: 请求参数
 * @author: morSun
 */
@Data
// k-v 键值对
public class requestParams {
    private String fieldName;
    private String value;
}
