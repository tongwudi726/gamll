package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVo extends SkuEntity {
    private List<String> images;
    //积分活动
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;
     * 0 - 无优惠，成长积分是否赠送;
     * 1 - 无优惠，购物积分是否赠送;
     * 2 - 有优惠，成长积分是否赠送;
     * 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;

    //满减活动
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    /**
     * 是否叠加其他优惠
     */
    private Integer fullAddOther;
    //打折活动
    private Integer fullCount;
    private BigDecimal discount;
    /**
     * 是否叠加其他优惠
     */
    private  Integer addOther;

    private List<SkuAttrValueEntity> saleAttrs;
}
