package com.morsun.springbootinit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morsun.springbootinit.common.BaseResponse;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.common.ResultUtils;
import com.morsun.springbootinit.constant.CommonConstant;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.exception.ThrowUtils;
import com.morsun.springbootinit.mapper.UserInterfaceInvokeMapper;
import com.morsun.springbootinit.model.dto.userInterfaceInfoInvoke.UserInterfaceInvokeQueryRequest;
import com.morsun.springbootinit.model.entity.InterfaceInfo;
import com.morsun.springbootinit.model.entity.User;
import com.morsun.springbootinit.model.entity.UserInterfaceInvoke;
import com.morsun.springbootinit.model.vo.UserInterfaceInfoInvokeVO;
import com.morsun.springbootinit.service.InterfaceInfoService;
import com.morsun.springbootinit.service.UserInterfaceInvokeService;
import com.morsun.springbootinit.service.UserService;
import com.morsun.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86176
* @description 针对表【user_interface_invoke(`user_interface_invoke`)】的数据库操作Service实现
* @createDate 2023-08-01 16:13:33
*/
@Service
public class UserInterfaceInvokeServiceImpl extends ServiceImpl<UserInterfaceInvokeMapper, UserInterfaceInvoke>
    implements UserInterfaceInvokeService{
    @Resource
    private UserService userService;
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Override
    public void validUserInterfaceInfoInvoke(UserInterfaceInvoke userInterfaceInvoke, boolean add) {


        if (userInterfaceInvoke == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userInterfaceInvoke.getId();
        Long userId = userInterfaceInvoke.getUserId();
        Long interfaceInfoId = userInterfaceInvoke.getInterfaceInfoId();
        Integer status = userInterfaceInvoke.getStatus();
        Integer totalNum = userInterfaceInvoke.getTotalNum();
        Integer leaveNum = userInterfaceInvoke.getLeaveNum();


        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(userId.toString(),interfaceInfoId.toString(),totalNum.toString(),leaveNum.toString()), ErrorCode.PARAMS_ERROR);
        }

        if (ObjectUtil.isNull(userId))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口创建人无效");
        }
    }

    @Override
    public QueryWrapper<UserInterfaceInvoke> getQueryWrapper(UserInterfaceInvokeQueryRequest userInterfaceInvokeQueryRequest) {


        // 分页条件查询处理
        QueryWrapper<UserInterfaceInvoke> queryWrapper = new QueryWrapper<>();

        if (userInterfaceInvokeQueryRequest == null) {
            return queryWrapper;
        }
        Long userId = userInterfaceInvokeQueryRequest.getUserId();
        Long interfaceInfoId = userInterfaceInvokeQueryRequest.getInterfaceInfoId();
        Integer status = userInterfaceInvokeQueryRequest.getStatus();
        Integer totalNum = userInterfaceInvokeQueryRequest.getTotalNum();
        Integer is_deleted = userInterfaceInvokeQueryRequest.getIs_deleted();
        Date create_time = userInterfaceInvokeQueryRequest.getCreate_time();
        Date update_time = userInterfaceInvokeQueryRequest.getUpdate_time();
        String sortField = userInterfaceInvokeQueryRequest.getSortField();
        String sortOrder = userInterfaceInvokeQueryRequest.getSortOrder();


        // 定义 queryWarpper
        queryWrapper.like(ObjectUtil.isNotNull(userId), "userId", userId);
        queryWrapper.like(ObjectUtil.isNotNull(interfaceInfoId), "interfaceInfoId", interfaceInfoId);
        queryWrapper.eq(ObjectUtil.isNotNull(status),"status",status);


        queryWrapper.eq("is_deleted", ObjectUtil.isNotNull(is_deleted)?is_deleted:0);
        // todo 知识点: 排序处理，采用拼接sql的方法
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;

    }
    // 用户个人接口调用记录查看
    @Override
    public Page<UserInterfaceInfoInvokeVO> getUserByIdInterfaceInfoInvokeInfoVOPage(Page<UserInterfaceInvoke> userInterfaceInvokePage, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);

        List<UserInterfaceInfoInvokeVO> infoVOS = userInterfaceInvokePage.getRecords().stream().map(userInterfaceInvoke -> {
            UserInterfaceInfoInvokeVO infoVO = new UserInterfaceInfoInvokeVO();
            BeanUtils.copyProperties(userInterfaceInvoke, infoVO);
            // 拿到接口的其他信息
            Long interfaceInfoId = infoVO.getInterfaceInfoId();
            InterfaceInfo byId = interfaceInfoService.getById(interfaceInfoId);
            infoVO.setInterfaceName(byId.getName());
            infoVO.setInterfaceDescription(byId.getDescription());
            return infoVO;
        }).filter(userInterfaceInfoInvokeVO -> userInterfaceInfoInvokeVO.getUserId().equals(loginUser.getId()))
                .collect(Collectors.toList());
        Page<UserInterfaceInfoInvokeVO> infoVOPage = new Page<>();
        // 分页的一些信息
        infoVOPage.setRecords(infoVOS)
                .setTotal(userInterfaceInvokePage.getTotal())
                .setSize(userInterfaceInvokePage.getSize())
                .setCurrent(userInterfaceInvokePage.getCurrent())
                .setPages(userInterfaceInvokePage.getPages());

        return infoVOPage;
    }

    // 管理接口调用记录查看
    @Override
    public Page<UserInterfaceInfoInvokeVO> getUserInterfaceInfoInvokeInfoVOPage(Page<UserInterfaceInvoke> userInterfaceInvokePage, HttpServletRequest request) {

        List<UserInterfaceInfoInvokeVO> infoVOS = userInterfaceInvokePage.getRecords().stream().map(userInterfaceInvoke -> {
            UserInterfaceInfoInvokeVO infoVO = new UserInterfaceInfoInvokeVO();
            BeanUtils.copyProperties(userInterfaceInvoke, infoVO);
            return infoVO;
        }).collect(Collectors.toList());

        Page<UserInterfaceInfoInvokeVO> infoVOPage = new Page<>();
        // 分页的一些信息
        infoVOPage.setRecords(infoVOS)
                .setTotal(userInterfaceInvokePage.getTotal())
                .setSize(userInterfaceInvokePage.getSize())
                .setCurrent(userInterfaceInvokePage.getCurrent())
                .setPages(userInterfaceInvokePage.getPages());

        return infoVOPage;
    }

    @Override
    public UserInterfaceInfoInvokeVO getUserInterfaceInfoInvokeInfoVO(UserInterfaceInvoke userInterfaceInvoke, HttpServletRequest request) {
        UserInterfaceInfoInvokeVO infoVO = new UserInterfaceInfoInvokeVO();
        BeanUtils.copyProperties(userInterfaceInvoke,infoVO);
        return infoVO;
    }

    /**
     * 调用接口，增加调用次数
     *   todo 面对这种可能出现大量修改数据库的情况，需要加锁（分布式锁等）
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    @Override
    public boolean invokeCount(long userId, long interfaceInfoId) {
        //之前有调用记录，直接在原来的基础上更改调用次数
        LambdaUpdateWrapper<UserInterfaceInvoke> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInterfaceInvoke::getUserId,userId);
        updateWrapper.eq(UserInterfaceInvoke::getInterfaceInfoId,interfaceInfoId);
        updateWrapper.setSql("totalNum = totalNum + 1");
        // leaveNum必须要大于0
//        updateWrapper.gt(UserInterfaceInvoke::getLeaveNum,0);

        return this.update(updateWrapper);
    }

    /**
     *  用户 开通接口
     * @param interfaceId
     * @param invokeTimes
     * @param request
     * @return
     */
    @Override
    public BaseResponse<String> openInterfaceOrIncrease(Long interfaceId, Integer invokeTimes, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        //2. 校验当前用户是否已经开通此接口
        long one = this.count(new LambdaQueryWrapper<UserInterfaceInvoke>().eq(UserInterfaceInvoke::getUserId, loginUser.getId())
                .eq(UserInterfaceInvoke::getInterfaceInfoId, interfaceId));
        boolean flag = false;
        if (one==0)
        {
            //3. 未开通就插入新数据
            UserInterfaceInvoke invoke = new UserInterfaceInvoke();
            invoke.setInterfaceInfoId(interfaceId);
            invoke.setUserId(loginUser.getId());
            invoke.setStatus(0);
            invoke.setTotalNum(0);
            invoke.setLeaveNum(invokeTimes);
            flag= this.save(invoke);
        }else
        {
            //4. 已开通就增加剩余调用次数
            flag = this.update(new LambdaUpdateWrapper<UserInterfaceInvoke>()
                    .eq(UserInterfaceInvoke::getInterfaceInfoId,interfaceId)
                    .eq(UserInterfaceInvoke::getUserId,loginUser.getId())
                    .setSql("leaveNum = leaveNum + "+invokeTimes)
            );
        }
        return flag? ResultUtils.success("开通成功") :ResultUtils.error(ErrorCode.SYSTEM_ERROR,"开通失败");
    }

    /**
     *  收集每一种接口的调用情况
     * @return
     */
    @Override
    public List<UserInterfaceInvoke> listInterfaceInvokeSituation() {
        QueryWrapper<UserInterfaceInvoke> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("interfaceInfoId","sum(totalNum) as totalNum")
                .ne("is_deleted",1)
                .groupBy("interfaceInfoId");
        List<UserInterfaceInvoke> list = this.list(queryWrapper);
        log.debug("查询出来的集合："+list);
        return list;
    }
}




