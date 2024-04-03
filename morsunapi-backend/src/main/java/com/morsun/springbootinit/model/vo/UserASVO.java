package com.morsun.springbootinit.model.vo;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.vo
 * @date: 2023/8/6
 * @week: 星期日
 * @message:
 * @author: morSun
 */
@Data
public class UserASVO {
    private String salt;
    private String accessKey;
    private String secretKey;
}
