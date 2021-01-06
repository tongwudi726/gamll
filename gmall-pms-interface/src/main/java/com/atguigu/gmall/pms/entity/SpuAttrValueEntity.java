package com.atguigu.gamll.pms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * spu属性值
 * 
 * @author tongwudi
 * @email tongwudi@atguigu.com
 * @date 2020-12-14 20:04:19
 */
@Data
@TableName("pms_spu_attr_value")
public class SpuAttrValueEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 商品id
	 */
	private Long spuId;
	/**
	 * 属性id
	 */
	private Long attrId;
	/**
	 * 属性名
	 */
	private String attrName;
	/**
	 * 属性值
	 */
	private String attrValue;
	/**
	 * 顺序
	 */
	private Integer sort;

	public  void setValueSelected(List<Object> valueSelected){
		//如果接受空的集合则不设置
		if(CollectionUtils.isEmpty(valueSelected)){
			return;
		}
		this.setAttrValue(StringUtils.join(valueSelected,","));
	}


}
