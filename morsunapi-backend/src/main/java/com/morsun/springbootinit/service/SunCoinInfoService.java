package com.morsun.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morsun.springbootinit.model.dto.sunCoin.SunCoinQueryRequest;
import com.morsun.springbootinit.model.entity.SunCoinInfo;
import com.morsun.springbootinit.model.vo.SunCoinInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86176
* @description 针对表【sun_coin_info】的数据库操作Service
* @createDate 2024-01-28 18:33:37
*/
public interface SunCoinInfoService extends IService<SunCoinInfo> {

    void validSunCoinInfo(SunCoinInfo sunCoinInfo, boolean add);

    SunCoinInfoVo getSunCoinInfoVo(SunCoinInfo byId, HttpServletRequest request);

    List<SunCoinInfoVo> getSunCoinInfoVo(List<SunCoinInfo> list, HttpServletRequest request);

    LambdaQueryWrapper<SunCoinInfo> getQueryWrapper(SunCoinQueryRequest sunCoinQueryRequest);
}
