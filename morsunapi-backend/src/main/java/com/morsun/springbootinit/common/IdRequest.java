package com.morsun.springbootinit.common;

import lombok.Data;

import java.io.Serializable;

/**
 * id请求
 *
 * @author morsun
 * @from 知识星球
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}