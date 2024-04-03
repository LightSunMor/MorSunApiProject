package com.morsun.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * `user_interface_invoke`
 * @TableName user_interface_invoke
 */
@TableName(value ="user_interface_invoke")
@Data
public class UserInterfaceInvoke implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调用用户 id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 接口 id
     */
    @TableField("interfaceInfoId")
    private Long interfaceInfoId;

    /**
     * 0-正常，1-禁用 （阻止用户违反某些规则还继续调用接口）
     */
    @TableField("status")
    private Integer status;

    /**
     * 总调用次数
     */
    @TableField("totalNum")
    private Integer totalNum;

    @TableField(exist = false)
    private Integer total;

    /**
     * 剩余可调用次数
     */
    private Integer leaveNum;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableField("is_deleted")
    private Integer is_deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date create_time;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date update_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}