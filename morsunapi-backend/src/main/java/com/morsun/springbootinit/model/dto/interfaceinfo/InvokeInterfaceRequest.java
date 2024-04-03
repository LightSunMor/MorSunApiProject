package com.morsun.springbootinit.model.dto.interfaceinfo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * @package_name: com.morsun.springbootinit.model.dto.interfaceinfo
 * @date: 2023/8/1
 * @week: 星期二
 * @message: 在线测试接口
 * @author: morSun
 */
@Data
public class InvokeInterfaceRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    // 接口id
    private Long id;

//    private String userRequestParams;
//
//    private String RequestBodyParams;
//    private String QueryParams;

    private List<requestParams> requestParams;
}
