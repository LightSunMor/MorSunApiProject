package com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.dto.interfaceinfo
 * @date: 2023/7/24
 * @week: 星期一
 * @message: 信息接口dto
 * @author: morSun
 */
@Data
public class UserInterfaceInvokeUpdateRequest {
    private Long id;

    /**
     * 0-正常，1-禁用 （阻止用户违反某些规则还继续调用接口）
     */
    private Integer status;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余可调用次数
     */
    private Integer leaveNum;
}
