package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author tongwudi
 * @email tongwudi@atguigu.com
 * @date 2020-12-15 13:46:56
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
	
}
