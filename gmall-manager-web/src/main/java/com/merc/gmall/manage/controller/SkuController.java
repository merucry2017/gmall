package com.merc.gmall.manage.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.bean.PmsSkuInfo;
import com.merc.gmall.service.SkuService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SkuController {

    @Reference
    SkuService skuService;

    @ApiOperation(value = "保存商品sku",notes = "author:hxq")
    @ApiImplicitParam(name = "pmsSkuInfo",value = "商品sku信息",required = true,
        paramType = "com.merc.gmall.bean.PmsSkuInfo")
    @PostMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){

        // 将spuId封装给productId
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        // 处理默认图片
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if(skuDefaultImg!=null&&StringUtils.isBlank(skuDefaultImg)){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }


        skuService.saveSkuInfo(pmsSkuInfo);

        return "success";
    }
}
