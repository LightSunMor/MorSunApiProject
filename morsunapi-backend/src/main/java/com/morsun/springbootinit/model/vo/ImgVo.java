package com.morsun.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @package_name: com.morsun.springbootinit.model.vo
 * @date: 2024/4/17
 * @week: 星期三
 * @message:
 * @author: morSun
 */
@Data
public class ImgVo implements Serializable {
    private static final long serialVersionUID = 3190812321355513914L;
    private String uid;
    private String name;
    private String status;
    private String url;
}
