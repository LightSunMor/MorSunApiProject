package com.morsun.interfacesi.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.morsun.clientsdk.model.params.IpInfoParams;
import com.morsun.clientsdk.model.params.RandomWallpaperParams;
import com.morsun.clientsdk.model.params.WeatherParams;
import com.morsun.clientsdk.model.response.PoisonousChickenSoupResponse;
import com.morsun.clientsdk.model.response.RandomWallpaperResponse;
import com.morsun.clientsdk.model.response.ResultResponse;
import com.morsun.interfacesi.utils.RequestUtils;
import com.morsun.interfacesi.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
/**
 * @package_name: com.morsun.interfacesi.controller
 * @date: 2024/3/7
 * @week: 星期四
 * @message: Api模拟接口
 * @author: morSun
 */
@RestController
@Slf4j
public class ApiInterfaceDemoController {

    /**
     *  天气信息查询
    * @param weatherParams 天气参数控制返回哪里的天气
     * @return
     */
    @GetMapping("/weatherInfo")
    public ResultResponse weatherInfoQuery(WeatherParams weatherParams)
    {
        String url = RequestUtils.buildUrl("https://api.vvhan.com/api/weather", weatherParams);
        String response = HttpUtil.get(url);
        ResultResponse resultResponse = ResponseUtils.baseResponse(response);
        return resultResponse;
    }


    /**
     *  IP地址信息
     * @param params ip地址
     * @return 返回的基础响应体，因为在sdk中就是这样写的，所以这里应该返回统一
     */
    @GetMapping("/ipInfo")
    public ResultResponse ipInfoQueryByIpNum(IpInfoParams params){
        String url = RequestUtils.buildUrl("https://api.vvhan.com/api/ipInfo", params);
        String response = HttpUtil.get(url);
        log.info("ip请求参数：{}，响应结果：{}",params,response);
        ResultResponse resultResponse = ResponseUtils.baseResponse(response);

        return resultResponse;
    }

    /**
     * 随机生成毒鸡汤
     * @return
     */
    @GetMapping("/poisonousChickenSoup")
    public PoisonousChickenSoupResponse getPoisonousChickenSoup() {
        String res = HttpUtil.get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
        return JSONUtil.toBean(res,PoisonousChickenSoupResponse.class);
    }

    /**
     * 随机情话
     *
     * @return
     */
    //有时候你会惹我生气，让我难过，可每当我想要发脾气，看到你噘嘴委屈的样子，我就忍不住想哄，绷不住想笑，朋友们说我脾气好，我倒觉得是你太可爱。
    @GetMapping("/loveTalk")
    public String randomLoveTalk() {
        return HttpUtil.get("https://api.vvhan.com/api/text/love");
    }

    /**
     *  随机壁纸提供，
     * @param randomWallpaperParams 填写参数控制返回的壁纸
     * @return
     */
    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php"; // 柒木的cos对象存储
        String url = RequestUtils.buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        log.info("随机壁纸接口参数：{}",randomWallpaperParams);
        return JSONUtil.toBean(HttpUtil.get(url), RandomWallpaperResponse.class);
    }

    //...todo 待提供更多的接口

}
