package com.merc.gmall.service;

import com.merc.gmall.bean.*;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);

    List<PmsSkuInfo> SkuSaleAttrValueListBySpu(String spuId);

    String deleteSpuInfoById(String spuId);
}
