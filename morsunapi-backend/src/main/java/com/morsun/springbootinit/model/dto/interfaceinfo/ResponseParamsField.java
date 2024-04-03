package com.morsun.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.dto.interfaceinfo
 * @date: 2024/3/17
 * @week: 星期日
 * @message:
 * @author: morSun
 */
@Data
public class ResponseParamsField {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
}
