package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuVo;

import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;



@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallSmsClient gmallSmsClient;
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
        /// 1.保存spu相关
        // 1.1. 保存spu基本信息 spu_info
        Long spuId = saveSpu(spuVo);
        // 1.2. 保存spu的描述信息 spu_info_desc
            saveSpuDesc(spuVo, spuId);
        //1.3保存spu的规格参数信息
        saveBaseAttr(spuVo, spuId);
        /// 2. 保存sku相关信息
        saveSku(spuVo, spuId);

    }

    private void saveSku(SpuVo spuVo, Long spuId) {
        ///2.保存sku相关信息
        List<SkuVo> skuVos = spuVo.getSkus();
        if(CollectionUtils.isEmpty(skuVos)){
            return;
        }
        skuVos.forEach(skuVo -> {
            //2.1保存sku的基本信息
            SkuEntity skuEntity = new SkuEntity();
            BeanUtils.copyProperties(skuVo,skuEntity);
            //品牌和分类id需要从spuVo中获取
            skuEntity.setBrandId(spuVo.getBrandId());
            skuEntity.setCatagoryId(spuVo.getCategoryId());
            //获取图片列表
            List<String> images = skuVo.getImages();
            if(!CollectionUtils.isEmpty(images)){
                //设置第一张图片作为默认图片
                skuEntity.setDefaultImage(skuEntity.getDefaultImage()==null?images.get(0):skuEntity.getDefaultImage());
            }
            skuEntity.setSpuId(spuId);
            this.skuMapper.insert(skuEntity);
            // 获取skuId
            Long skuId = skuEntity.getId();

            //2.2保存图片的信息
            if(!CollectionUtils.isEmpty(images)){
                String defaultImage = images.get(0);
                List<SkuImagesEntity> skuImagesEntityList = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(defaultImage, image) ? 1 : 0);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setSort(0);
                    skuImagesEntity.setUrl(image);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                 this.skuImagesService.saveBatch(skuImagesEntityList);
            }
            //2.3保存sku的规格参数（销售属性）
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            saleAttrs.forEach(saleAttr->{
                //设置属性名，需要根据id查询SkuAttrValueEntity
                saleAttr.setSkuId(skuId);
                saleAttr.setSort(0);
            });
            this.skuAttrValueService.saveBatch(saleAttrs);

            //3.保存营销相关信息，需要远程调用gmall-sms
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo,skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            this.gmallSmsClient.saveSkuSaleInfo(skuSaleVo);
            //3.1积分优惠

            //3.2满减优惠

            //3.3数量折扣

        });
    }

    private void saveBaseAttr(SpuVo spuVo, Long spuId) {
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

    private void saveSpuDesc(SpuVo spuVo, Long spuId) {
        //1.2保存spu描述信息 spu_info_desc
        SpuDescEntity spuDescEntity = new SpuDescEntity();
        //这里描述信息等同于spu一个表 作了分表操作用的还是同一个表
        spuDescEntity.setSpuId(spuId);
        //把商品的图片描述，保存到spu详情中，图片地址以逗号进行分割成字符串传入进去
        spuDescEntity.setDecript(StringUtils.join(spuVo.getSpuImages(),","));
        this.spuDescMapper.insert(spuDescEntity);
    }

    private Long saveSpu(SpuVo spuVo) {
        ///1.保存spu相关
        //1.1保存spu基本信息，spu_info
        spuVo.setPublishStatus(1); //默认已上架
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        //mybatis框架会new一个这个类对应的空对象用set进行赋值
        //赋值后会回显id到这个对象里面
        this.save(spuVo);
        return spuVo.getId();
    }

}