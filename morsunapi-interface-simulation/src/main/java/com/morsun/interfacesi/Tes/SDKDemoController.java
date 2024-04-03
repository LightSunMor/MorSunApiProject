package com.morsun.interfacesi.Tes;

import com.morsun.clientsdk.client.MorApiClient;
import com.morsun.clientsdk.exception.ApiException;
import com.morsun.clientsdk.model.params.IpInfoParams;
import com.morsun.clientsdk.model.params.RandomWallpaperParams;
import com.morsun.clientsdk.model.request.LoveRequest;
import com.morsun.clientsdk.model.response.LoveResponse;
import com.morsun.clientsdk.model.response.PoisonousChickenSoupResponse;
import com.morsun.clientsdk.model.response.RandomWallpaperResponse;
import com.morsun.clientsdk.model.response.ResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @package_name: com.morsun.interfacesi.Tes
 * @date: 2024/4/3
 * @week: 星期三
 * @message:
 * @author: morSun
 */
@RestController
public class SDKDemoController {

    @Resource
    private MorApiClient morApiClient;


    @GetMapping("/getIp")
    public Object getIP(String ip)
    {
        IpInfoParams params = new IpInfoParams();
        params.setIp(ip);
        ResultResponse ipInfo = null;
        try {
            ipInfo = morApiClient.getIpInfo(params);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return ipInfo;
    }

    @GetMapping("/randomLove")
    public Object getLove()
    {
        LoveResponse loveResponse = null;
        try {
            loveResponse = morApiClient.randomLoveTalk();
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return loveResponse;
    }

    @GetMapping("/randomSoup")
    public Object getSoup()
    {
        PoisonousChickenSoupResponse poisonousChickenSoup = null;
        try {
            poisonousChickenSoup = morApiClient.getPoisonousChickenSoup();
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return poisonousChickenSoup;
    }

    @GetMapping("/pic")
    public Object getPic(@RequestParam(required = false)String method,@RequestParam(required = false) String lx)
    {
        RandomWallpaperParams params = new RandomWallpaperParams();
        params.setMethod(method);
        params.setLx(lx);
        RandomWallpaperResponse response = null;
        try {
            response = morApiClient.getRandomWallpaper(params);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return response;
    }
}
