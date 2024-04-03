package com.morsun.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morsun.springbootinit.model.entity.UserInterfaceInvoke;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86176
* @description 针对表【user_interface_invoke(`user_interface_invoke`)】的数据库操作Mapper
* @createDate 2023-08-01 16:13:33
* @Entity com.morsun.springbootinit.model.entity.UserInterfaceInvoke
*/
public interface UserInterfaceInvokeMapper extends BaseMapper<UserInterfaceInvoke> {

    /**
     *  分析接口的调用情况
     * @param n
     * @return
     */
    List<UserInterfaceInvoke> listInterfaceAnalysis(@Param("n") Integer n);
}




