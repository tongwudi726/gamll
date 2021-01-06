package com.atguigu.gmall.item.vo;

import lombok.Data;

import java.util.List;

@Data
public class ItemSaleVo {
    private String type; // 积分 满减 打折
    private String desc; // 描述信息
}
