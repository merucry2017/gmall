package com.merc.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.merc.gmall.annotations.LoginRequired;
import com.merc.gmall.bean.PmsProductSaleAttr;
import com.merc.gmall.bean.PmsSkuInfo;
import com.merc.gmall.bean.PmsSkuSaleAttrValue;
import com.merc.gmall.service.SkuService;
import com.merc.gmall.service.SpuService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @ApiOperation(value = "返回首页",notes = "author:hxq")
    @GetMapping("/")
    @LoginRequired(loginSuccess = false)
    public void getIndex(HttpServletResponse resp) throws IOException {
        resp.sendRedirect("http://localhost:8083/index");
    }

    @ApiOperation(value = "根据skuId返回商品详情页",notes = "author:hxq")
    @ApiImplicitParam(name = "skuId",value = "商品单元skuId",required = true,
        paramType = "java.lang.String")
    @GetMapping("/{skuId}.html")
    public ModelAndView item(@PathVariable String skuId, ModelMap map, HttpServletRequest request){
        ModelAndView model = new ModelAndView();

        String remoteAddr = request.getRemoteAddr();

        // request.getHeader("");// nginx负载均衡

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,remoteAddr);

        // 从数据库中获取不到数据
        if(pmsSkuInfo==null){
            model.setViewName("error");
            model.addObject("message","客官您来迟啦！该商品已经下架");
            return model;
        }

        //sku对象
//        map.put("skuInfo",pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
//        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        // 查询当前sku的spu的其他sku的集合的hash表
        Map<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";// "239|245"
            }
            skuSaleAttrHash.put(k,v);
        }

        // 将sku的销售属性hash表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
//        map.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);

        String nickname = (String)request.getAttribute("nickname");
        model.setViewName("item");
        model.addObject("skuInfo",pmsSkuInfo);
        model.addObject("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);
        model.addObject("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        model.addObject("nickname", nickname);
        return model;
    }

}
