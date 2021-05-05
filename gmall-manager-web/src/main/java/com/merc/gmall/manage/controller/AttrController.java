package com.merc.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.annotations.LoginRequired;
import com.merc.gmall.bean.PmsBaseAttrInfo;
import com.merc.gmall.bean.PmsBaseAttrValue;
import com.merc.gmall.bean.PmsBaseSaleAttr;
import com.merc.gmall.bean.Result;
import com.merc.gmall.service.AttrService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class AttrController  {

    @Reference
    AttrService attrService;

    @ApiOperation(value = "返回首页",notes = "author:hxq")
    @GetMapping("/")
    @LoginRequired(loginSuccess = false)
    public void getIndex(HttpServletResponse resp) throws IOException {
        resp.sendRedirect("http://localhost:8083/index");
    }

    @ApiOperation(value = "获取商品spu基本销售属性列表",notes = "author:hxq")
    @ApiImplicitParam
    @PostMapping("baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){

        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = attrService.baseSaleAttrList();
        return pmsBaseSaleAttrs;
    }

    @ApiOperation(value = "添加商品spu属性",notes = "author:hxq")
    @ApiImplicitParam(name = "pmsBaseAttrInfo",value = "商品spu基本属性信息",
            required = true,paramType = "com.merc.gmall.bean.PmsBaseAttrInfo")
    @PostMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){

        String success = attrService.saveAttrInfo(pmsBaseAttrInfo);

        return "success";
    }

    @ApiOperation(value = "后台管理系统平台属性spu属性信息",notes = "author:hxq")
    @ApiImplicitParam(name = "catalog3Id",value = "三级分类id",
            required = true,paramType = "String")
    @GetMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }

    @ApiOperation(value = "修改商品spu属性值",notes = "author:hxq")
    @ApiImplicitParam(name = "attrId",value = "属性id",required = true,paramType = "String")
    @PostMapping("getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){

        List<PmsBaseAttrValue> pmsBaseAttrValues = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }

    @ApiOperation(value = "删除商品spu属性值Info",notes = "author:hxq")
    @ApiImplicitParam(name = "attrId",value = "属性id",required = true,paramType = "com.merc.gmall.bean.Result")
    @DeleteMapping("deleteAttrInfoById")
    public Result deleteAttrInfoById(String attrId){
        Result result = new Result();
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrService.getAttrValueList(attrId);
        if(pmsBaseAttrValues!=null&&pmsBaseAttrValues.size()>0){
            result.setMessage("删除失败！请确保子目录为空");
            result.setState("406");
            return result;
        }
        String r = attrService.deleteAttrInfoById(attrId);
        if(r!=null&&r.equals("fail")){
            result.setMessage("删除失败！不能重复删除");
            result.setState("500");
        }else{
            result.setMessage("操作成功");
            result.setState("200");
        }
        return result;
    }

    @ApiOperation(value = "删除商品spu属性值Value",notes = "author:hxq")
    @ApiImplicitParam(name = "attrId",value = "属性id",required = true,paramType = "com.merc.gmall.bean.Result")
    @DeleteMapping("deleteAttrValueById")
    public Result deleteAttrValueById(String attrId){
        Result result = new Result();
        String r = attrService.deleteAttrValueById(attrId);
        if(r!=null&&r.equals("fail")){
            result.setMessage("删除失败！不能重复删除");
            result.setState("500");
        }else{
            result.setMessage("操作成功");
            result.setState("200");
        }
        return result;
    }
}
