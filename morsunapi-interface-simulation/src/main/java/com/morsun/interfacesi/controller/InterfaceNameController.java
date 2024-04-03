package com.morsun.interfacesi.controller;

import com.morsun.clientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @package_name: com.morsun.controller
 * @date: 2023/7/26
 * @week: 星期三
 * @message: 模拟测试
 * @author: morSun
 */
@RestController
@RequestMapping("/name")
public class InterfaceNameController {
    @GetMapping()
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }


    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        return "POST 你的名字是" + user.getUsername();
    }
}
