package com.morsun.springbootinit.model.dto.user;

import lombok.Data;

/**
 * @package_name: com.morsun.springbootinit.model.dto.user
 * @date: 2024/1/21
 * @week: 星期日
 * @message: 用户更新密码请求体
 * @author: morSun
 */
@Data
public class UserPasswordUpdateRequest {
//    private Long id;

    private String oldPassword;

    private String newPassword;
    private static final long serialVersionUID = 1L;

}
