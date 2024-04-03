package com.morsun.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.mapper.SunCoinInfoMapper;
import com.morsun.springbootinit.model.dto.sunCoin.SunCoinQueryRequest;
import com.morsun.springbootinit.model.entity.SunCoinInfo;
import com.morsun.springbootinit.model.vo.SunCoinInfoVo;
import com.morsun.springbootinit.service.SunCoinInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86176
* @description 针对表【sun_coin_info】的数据库操作Service实现
* @createDate 2024-01-28 18:33:37
*/
@Service
public class SunCoinInfoServiceImpl extends ServiceImpl<SunCoinInfoMapper, SunCoinInfo>
    implements SunCoinInfoService{

    @Override
    public void validSunCoinInfo(SunCoinInfo sunCoinInfo, boolean add) {

        if (sunCoinInfo==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Integer sunCoinAmount = sunCoinInfo.getSunCoinAmount();
        String payMoney = sunCoinInfo.getPayMoney();

        if (add){
            ThrowUtils.throwIf(StringUtils.isAnyBlank(sunCoinAmount.toString(),payMoney),ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public SunCoinInfoVo getSunCoinInfoVo(SunCoinInfo byId, HttpServletRequest request) {
        SunCoinInfoVo vo = new SunCoinInfoVo();
        BeanUtils.copyProperties(byId,vo);

        return vo;
    }

    @Override
    public List<SunCoinInfoVo> getSunCoinInfoVo(List<SunCoinInfo> list, HttpServletRequest request) {

        List<SunCoinInfoVo> infoVos = list.stream().map(sunCoinInfo -> {
            SunCoinInfoVo infoVo = new SunCoinInfoVo();
            BeanUtils.copyProperties(sunCoinInfo, infoVo);
            return infoVo;
        }).collect(Collectors.toList());
        // 排序(记得待排序的对象要实现Comparable接口或者sorted中给到Comparator比较器实现体)
        List<SunCoinInfoVo> collect = infoVos.stream().sorted().collect(Collectors.toList());

        return collect;
    }

    @Override
    public LambdaQueryWrapper<SunCoinInfo> getQueryWrapper(SunCoinQueryRequest sunCoinQueryRequest) {
        LambdaQueryWrapper<SunCoinInfo> queryWrapper =new LambdaQueryWrapper<>();

        Long id = sunCoinQueryRequest.getId();
        Integer sunCoinAmount = sunCoinQueryRequest.getSunCoinAmount();
        String payMoney = sunCoinQueryRequest.getPayMoney();
        Integer status = sunCoinQueryRequest.getStatus();
        Integer isDelete = sunCoinQueryRequest.getIsDelete();
        Date createTime = sunCoinQueryRequest.getCreateTime();
        Date updateTime = sunCoinQueryRequest.getUpdateTime();
        String sortField = sunCoinQueryRequest.getSortField();
        String sortOrder = sunCoinQueryRequest.getSortOrder();

        queryWrapper.eq(id!=null,SunCoinInfo::getId,id)
                .eq(status==0||status==1,SunCoinInfo::getStatus,status)
                .eq(isDelete==0||isDelete==1,SunCoinInfo::getIsDelete,isDelete);

        return queryWrapper;
    }
}




