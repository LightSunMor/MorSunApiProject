package com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke;

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
public class UserInterfaceInvokeQueryRequest extends PageRequest implements Serializable {
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
