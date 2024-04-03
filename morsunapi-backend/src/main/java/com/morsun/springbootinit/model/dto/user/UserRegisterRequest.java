package com.morsun.springbootinit.model.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author morsun
 * @from 知识星球
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String userName;

    private String userProfile;

    private MultipartFile avatarFile;
}
