package com.atguigu.gmall.item.vo;

import lombok.Data;

import java.util.Set;

@Data
public class SaleAttrValueVo {
    private Long attrId;
    private String attrName;
    private Set<String> attrValues;
}
