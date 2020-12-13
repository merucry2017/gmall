package com.merc.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.bean.*;
import com.merc.gmall.manage.util.PmsUploadUtil;
import com.merc.gmall.service.SkuService;
import com.merc.gmall.service.SpuService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @Reference
    SkuService skuService;

    @ApiOperation(value = "根据商品id获取图片集合",notes = "author:hxq")
    @ApiImplicitParam(name = "spuId",value = "spu商品单元id",required = true,
        paramType = "com.merc.gmall.bean.PmsProductImage")
    @GetMapping("spuImageList")
    public List<PmsProductImage> spuImageList(String spuId){

        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }

    @ApiOperation(value = "获取销售属性列表",notes = "author:hxq")
    @ApiImplicitParam(name = "spuId",value = "spu商品id",required = true,
        paramType = "com.merc.gmall.bean.PmsProductSaleAttr")
    @GetMapping("spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){

        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrs;
    }


    @ApiOperation(value = "将图片或者音视频上传",notes = "author:hxq")
    @ApiImplicitParam(name = "multipartFile",value = "多重文件",required = true,
        paramType = "org.springframework.web.multipart.MultipartFile")
    @PostMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        // 将图片或者音视频上传到分布式的文件存储系统
        // 将图片的存储路径返回给页面
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }

    @ApiOperation(value = "上传商品spu",notes = "author:hxq")
    @ApiImplicitParam(name = "pmsProductInfo",value = "商品信息",required = true,
        paramType = "com.merc.gmall.bean.PmsProductInfo")
    @PostMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        spuService.saveSpuInfo(pmsProductInfo);

        return "success";
    }

    @ApiOperation(value = "删除商品spu",notes = "author:hxq")
    @ApiImplicitParam(name = "spuId",value = "商品spuId",required = true,
            paramType = "java.lang.String")
    @DeleteMapping("deleteSpuInfoById")
    public Result deleteSpuInfoById(String spuId){
        Result result = new Result();
        //先删除spu下的sku目录，再删除spu本身
        String r1 = skuService.deleteBatchSkuBySpuId(spuId);
        String r2 = spuService.deleteSpuInfoById(spuId);
        if(r2!=null&&r2.equals("fail")){
            result.setMessage("删除失败！不能重复删除");
            result.setState("500");
        }else{
            result.setMessage("操作成功");
            result.setState("200");
        }
        return result;
    }

    @ApiOperation(value = "请求商品属性列表",notes = "author:hxq")
    @ApiImplicitParam(name = "catalog3Id",value = "三级分类id",required = true,
        paramType = "java.lang.String")
    @GetMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id){

        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);

        return pmsProductInfos;
    }
}
