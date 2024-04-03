package com.morsun.springbootinit.model.enums;

/**
 * @package_name: com.morsun.springbootinit.model.enums
 * @date: 2023/7/29
 * @week: 星期六
 * @message:
 * @author: morSun
 */
public enum InterfaceInfoStatusEnum {
    Online("上线",1),
    Offline("下线",0)
    ;

    private  final String msg;
    private  final int value;

    InterfaceInfoStatusEnum(String msg,int value) {
        this.msg= msg;
        this.value= value;
    }

    public String getMsg() {
        return msg;
    }

    public int getValue() {
        return value;
    }
}
