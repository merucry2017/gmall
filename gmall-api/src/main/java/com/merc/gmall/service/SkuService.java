package com.merc.gmall.service;

import com.merc.gmall.bean.PmsSkuInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId, String ip);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    List<PmsSkuInfo> getAllSku();

    boolean checkPrice(String productSkuId, BigDecimal productPrice);

    String deleteBatchSkuBySpuId(String spuId);
}
