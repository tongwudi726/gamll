package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.vo.SpuVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;

import javax.xml.crypto.Data;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo queryByCategoryId(PageParamVo pageParamVo, Long categoryId) {
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
        //如果分类ID不为0，要根据分类id查，不然查全部
        if(categoryId!=0){
            queryWrapper.eq("category_id",categoryId);
        }
        String key = pageParamVo.getKey();
        if(StringUtils.isNotBlank(key)){
            queryWrapper.and(t->t.like("name",key).or().like("id",key));
        }
        return  new PageResultVo(this.page(pageParamVo.getPage(),queryWrapper));
    }

    @Override
    public void bigSave(SpuVo spuVo) {
        ///1.保存spu相关
        //1.1保存spu基本信息，spu_info
        spuVo.setPublishStatus(1); //默认已上架
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        //mybatis框架会new一个这个类对应的空对象用set进行赋值
        //赋值后会回显id到这个对象里面
        this.save(spuVo);
        Long spuId = spuVo.getId();  //获取新增后的spuVoId
        //1.2保存spu描述信息 spu_info_desc
        SpuDescEntity spuDescEntity = new SpuDescEntity();
        //这里描述信息等同于spu一个表 作了分表操作用的还是同一个表
        spuDescEntity.setSpuId(spuId);
        //把商品的图片描述，保存到spu详情中，图片地址以逗号进行分割成字符串传入进去
        spuDescEntity.setDecript(StringUtils.join(spuVo.getSpuImages(),","));
        this.spuDescMapper.insert(spuDescEntity);
        //1.3保存spu的规格参数信息
        List<SpuAttrValueEntity> baseAttrs = spuVo.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueEntity -> {
                spuAttrValueEntity.setSpuId(spuId);
                spuAttrValueEntity.setSort(0);
                return spuAttrValueEntity;
            }).collect(Collectors.toList());
            spuAttrValueService.saveBatch(spuAttrValueEntities);
        }

    }

}