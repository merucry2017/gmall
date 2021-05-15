package com.merc.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.merc.gmall.bean.*;
import com.merc.gmall.manage.mapper.*;
import com.merc.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    // 获取商品集合列表
    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {


        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);

        return pmsProductInfos;
    }

    // 保存商品集合信息
    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {

        // 保存商品信息
        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        // 生成商品主键
        String productId = pmsProductInfo.getId();

        // 保存商品图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        if(spuImageList!=null&&spuImageList.size()>0){
            for (PmsProductImage pmsProductImage : spuImageList) {
                pmsProductImage.setProductId(productId);
                pmsProductImageMapper.insertSelective(pmsProductImage);
            }
        }

        // 保存销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null&&spuSaleAttrList.size()>0){
            for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
                pmsProductSaleAttr.setProductId(productId);
                pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

                // 保存销售属性值
                List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
                for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                    pmsProductSaleAttrValue.setProductId(productId);
                    pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
                }
            }
        }

    }

    // 通过spuId获取商品集合列表
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> PmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);

        for (PmsProductSaleAttr productSaleAttr : PmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());// 销售属性id用的是系统的字典表中id，不是销售属性表的主键
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }

        return PmsProductSaleAttrs;
    }

    // 获取商品集合图片
    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);

        return pmsProductImages;

    }

    // 获取商品集合销售属性
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {

        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
        for(PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
            pmsProductSaleAttr.getSpuSaleAttrValueList().sort((p1, p2) -> {
                return Integer.parseInt(p1.getId()) - Integer.parseInt(p2.getId());
            });
        }
        return pmsProductSaleAttrs;
    }

    // 根据spuId获取商品集合
    @Override
    public List<PmsSkuInfo> SkuSaleAttrValueListBySpu(String spuId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(spuId);

        return pmsSkuInfos;
    }

    // 删除商品集合
    @Override
    public String deleteSpuInfoById(String spuId) {

        int result = pmsProductInfoMapper.deleteByPrimaryKey(spuId);
        if(result==0){
            return "fail";
        }
        return "success";
    }
}
