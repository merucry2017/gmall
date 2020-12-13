package com.merc.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.bean.PmsBaseCatalog1;
import com.merc.gmall.bean.PmsBaseCatalog2;
import com.merc.gmall.bean.PmsBaseCatalog3;
import com.merc.gmall.service.CatalogService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;

    @ApiOperation(value = "后台管理系统平台属性列表的三级分类",notes = "author:hxq")
    @ApiImplicitParam(name = "catalog2Id",value = "二级分类的id",required = true,paramType = "String")
    @PostMapping("getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){

        List<PmsBaseCatalog3> catalog3s = catalogService.getCatalog3(catalog2Id);
        return catalog3s;
    }

    @ApiOperation(value = "后台管理系统平台属性列表的二级分类",notes = "author:hxq")
    @ApiImplicitParam(name = "catalog1Id",value = "一级分类的id",required = true,paramType = "String")
    @PostMapping("getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){

        List<PmsBaseCatalog2> catalog2s = catalogService.getCatalog2(catalog1Id);
        return catalog2s;
    }

    @ApiOperation(value = "后台管理系统平台属性列表的一级分类",notes = "author:hxq")
    @ApiImplicitParam
    @PostMapping("getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1(){

        List<PmsBaseCatalog1> catalog1s = catalogService.getCatalog1();
        return catalog1s;
    }
}
