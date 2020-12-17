package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author tongwudi
 * @email tongwudi@atguigu.com
 * @date 2020-12-14 21:18:01
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
