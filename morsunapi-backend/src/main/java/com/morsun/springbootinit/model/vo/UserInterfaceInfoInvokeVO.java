package com.morsun.springbootinit.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @package_name: com.morsun.springbootinit.model.vo
 * @date: 2023/8/1
 * @week: 星期二
 * @message:
 * @author: morSun
 */
@Data
public class UserInterfaceInfoInvokeVO {
    private Long id;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 接口描述
     */
    private String interfaceDescription;

    /**
     * 0-正常，1-禁用 （阻止用户违反某些规则还继续调用接口）
     */
    private Integer status;

    /**
     * 总调用次数
     */
    private Integer totalNum;

//    /**
//     * 剩余可调用次数
//     */
//    private Integer leaveNum;

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
