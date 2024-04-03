package com.morsun.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName sun_coin_info
 */
@TableName(value ="sun_coin_info")
@Data
public class SunCoinInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 太阳币的数量
     */
    private Integer sunCoinAmount;

    /**
     * 购买此类太阳币一次的金额
     */
    private String payMoney;

    /**
     * 状态：正常—0 异常—1
     */
    private Integer status;

    /**
     * 是否被删除：未删除—0 已删除—1
     */
    private Integer isDelete;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}