package com.morsun.springbootinit.model.dto.interfaceinfo;

import com.morsun.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @package_name: com.morsun.springbootinit.model.dto.interfaceinfo
 * @date: 2023/7/24
 * @week: 星期一
 * @message: 接口信息查询dto
 * @author: morSun
 */
@Data
public class InterfaceInfoQueryRequest  extends PageRequest implements Serializable {
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

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    private Integer is_deleted;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;
}
