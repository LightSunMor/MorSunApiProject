package com.morsun.springbootinit.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morsun.springbootinit.annotation.AuthCheck;
import com.morsun.springbootinit.common.BaseResponse;
import com.morsun.springbootinit.common.DeleteRequest;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.common.ResultUtils;
import com.morsun.springbootinit.constant.UserConstant;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.model.dto.sunCoin.SunCoinAddRequest;
import com.morsun.springbootinit.model.dto.sunCoin.SunCoinQueryRequest;
import com.morsun.springbootinit.model.dto.sunCoin.SunCoinUpdateRequest;
import com.morsun.springbootinit.model.entity.SunCoinInfo;
import com.morsun.springbootinit.model.vo.SunCoinInfoVo;
import com.morsun.springbootinit.service.SunCoinInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @package_name: com.morsun.springbootinit.controller
 * @date: 2024/1/28
 * @week: 星期日
 * @message: 太阳币业务控制层
 * @author: morSun
 */
@RestController
@RequestMapping("/sunCoin")
public class SunCoinController {
    @Resource
    private SunCoinInfoService sunCoinInfoService;


    // region 增删改查

    /**
     *  新增太阳币信息
     * @param sunCoinAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addSunCoinInfo(@RequestBody SunCoinAddRequest sunCoinAddRequest, HttpServletRequest request)
    {
        if (sunCoinAddRequest==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SunCoinInfo sunCoinInfo = new SunCoinInfo();
        BeanUtils.copyProperties(sunCoinAddRequest,sunCoinInfo);

        //校验添加内容是否符合数据库要求
        sunCoinInfoService.validSunCoinInfo(sunCoinInfo,true);

        boolean result = sunCoinInfoService.save(sunCoinInfo);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        // 返回新增数据主键值
        Long id = sunCoinInfo.getId();
        return ResultUtils.success(id);
    }

    /**
     *  删除
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteSunCoinInfo(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request){
        if (deleteRequest==null || deleteRequest.getId()<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断当前请求对应的数据是否存在
        SunCoinInfo byId = sunCoinInfoService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(byId==null,ErrorCode.NOT_FOUND_ERROR);

        boolean b = sunCoinInfoService.removeById(deleteRequest.getId());
        return  ResultUtils.success(b);
    }

    /***
     * 修改
     * @param sunCoinUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSunCoinInfo(@RequestBody SunCoinUpdateRequest sunCoinUpdateRequest ,HttpServletRequest request){
        if (sunCoinUpdateRequest==null || sunCoinUpdateRequest.getId()<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SunCoinInfo addObj = new SunCoinInfo();
        BeanUtils.copyProperties(sunCoinUpdateRequest,addObj);
        //判断是否存在这个id对应的数据
        SunCoinInfo byId = sunCoinInfoService.getById(addObj.getId());
        ThrowUtils.throwIf(ObjectUtil.isNull(byId),ErrorCode.NOT_FOUND_ERROR);
        boolean b = sunCoinInfoService.updateById(addObj);
        return ResultUtils.success(b);
    }

    /**
     *   根据id获取
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SunCoinInfoVo> getSunCoinInfoVoById(long id,HttpServletRequest request){
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SunCoinInfo byId = sunCoinInfoService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(byId),ErrorCode.NOT_FOUND_ERROR);

        return ResultUtils.success(sunCoinInfoService.getSunCoinInfoVo(byId,request));
    }

    /**
     *  获取所有的信息列表(可传入条件)
     * @param request
     * @return
     */
    @PostMapping("/get/voList")
    public BaseResponse<List<SunCoinInfoVo>> getSunCoinInfoList(@RequestBody SunCoinQueryRequest sunCoinQueryRequest, HttpServletRequest request){
        // todo 存到redis中
        LambdaQueryWrapper<SunCoinInfo> queryWrapper = sunCoinInfoService.getQueryWrapper(sunCoinQueryRequest);
        List<SunCoinInfo> list = sunCoinInfoService.list(queryWrapper);
        // 转为vo
        List<SunCoinInfoVo> sunCoinInfoVos = sunCoinInfoService.getSunCoinInfoVo(list,request);
        return ResultUtils.success(sunCoinInfoVos);
    }

    // endregion


}
