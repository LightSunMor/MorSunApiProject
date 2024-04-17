package com.morsun.springbootinit.constant;

/**
 * 通用常量
 *
 * @author morsun
 * @from 知识星球
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 错误
     */
    String ERROR="error";

    /**
     *  成功结束
     */
    String SUCCESS="done";

    /**
     *  存储每次更新API权限时的时间，记得要分用户哦
     */
    String VOUCHER_CHANGE_REST_TIME="voucherchangeresttime";
}
