package com.morsun.morsunapicommom.model.dto;

import lombok.Data;

/**
 * @package_name: com.morsun.morsunapicommom.model.dto
 * @date: 2024/3/16
 * @week: 星期六
 * @message: 请求参数字段 (todo 知识点 数据库中以json方式存储，但是在Java程序里需要转为以下的形态进行使用)
 * @author: morSun
 */
@Data
public class RequestParamsField {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
    // 是 or 否
    private String required;
}
