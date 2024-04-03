package com.morsun.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.constant.CommonConstant;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.mapper.InterfaceInfoMapper;
import com.morsun.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.morsun.springbootinit.model.entity.InterfaceInfo;
import com.morsun.springbootinit.model.vo.InterfaceInfoVO;
import com.morsun.springbootinit.service.InterfaceInfoService;
import com.morsun.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86176
* @description 针对表【interface_info(`interface_info`)】的数据库操作Service实现
* @createDate 2023-07-24 21:47:38
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {


        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        Long userId = interfaceInfo.getUserId();


        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name,description,url,method,requestHeader,responseHeader), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
     /*   if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }*/
       /* if (ObjectUtil.isNull(userId))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口创建人无效");
        }*/
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request) {
        InterfaceInfoVO infoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo,infoVO);
        return infoVO;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request) {
        List<InterfaceInfoVO> infoVOS = interfaceInfoPage.getRecords().stream().map(interfaceInfo -> {
            InterfaceInfoVO infoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, infoVO);
            return infoVO;
        }).collect(Collectors.toList());

        Page<InterfaceInfoVO> infoVOPage = new Page<>();
        infoVOPage.setRecords(infoVOS)
        .setTotal(interfaceInfoPage.getTotal())
        .setSize(interfaceInfoPage.getSize())
        .setCurrent(interfaceInfoPage.getCurrent())
        .setPages(interfaceInfoPage.getPages());

        return infoVOPage;
    }


    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {


        // todo: 分页条件查询处理
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        String method = interfaceInfoQueryRequest.getMethod();
        String requestHeader = interfaceInfoQueryRequest.getRequestHeader();
        String responseHeader = interfaceInfoQueryRequest.getResponseHeader();
        Integer status = interfaceInfoQueryRequest.getStatus();
        Long userId = interfaceInfoQueryRequest.getUserId();
        Integer is_deleted = interfaceInfoQueryRequest.getIs_deleted();
        Date create_time = interfaceInfoQueryRequest.getCreate_time();
        Date update_time = interfaceInfoQueryRequest.getUpdate_time();
        long current = interfaceInfoQueryRequest.getCurrent();
        long pageSize = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        // 定义 queryWarpper
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);

        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<InterfaceInfo> searchFromEs(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        // todo:完善根据es来搜索的结果
        return null;
    }
}




