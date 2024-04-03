package com.morsun.springbootinit.model.dto.sunCoin;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.dto.sunCoin
 * @date: 2024/1/28
 * @week: 星期日
 * @message: 太阳币信息更新dto
 * @author: morSun
 */
@Data
public class SunCoinUpdateRequest {
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

}
