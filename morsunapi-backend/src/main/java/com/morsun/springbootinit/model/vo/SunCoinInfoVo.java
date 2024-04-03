package com.morsun.springbootinit.model.vo;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @package_name: com.morsun.springbootinit.model.vo
 * @date: 2024/1/29
 * @week: 星期一
 * @message: 太阳币信息返回类
 * @author: morSun
 */
@Data
public class SunCoinInfoVo implements Comparable<SunCoinInfoVo> {
    /**
     *
     */
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
     * 最近调整时间
     */
    private Date updateTime;


    @Override
    public int compareTo(@NotNull SunCoinInfoVo o) {
        return this.sunCoinAmount-o.getSunCoinAmount();
    }
}
