package com.morsun.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @package_name: com.morsun.springbootinit.model.vo
 * @date: 2023/7/24
 * @week: 星期一
 * @message:
 * @author: morSun
 */
@Data
public class InterfaceInfoVO {
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
     *  接口展示背景图
     */
    private String imageSrc;

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

    /**
     * 创建人
     */
    private Long userId;

    /**
     *   调用一次所需太阳币
     */
    private Integer onceSunCoin;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date create_time;

}
