package com.merc.gmall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.annotations.LoginRequired;
import com.merc.gmall.bean.OmsCartItem;
import com.merc.gmall.bean.PmsSkuInfo;
import com.merc.gmall.service.CartService;
import com.merc.gmall.service.SkuService;
import com.merc.gmall.util.CookieUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;
//返回的新的页面刷新替换掉原来的老的页面
    @ApiOperation(value = "根据skuId刷新替换掉原来老的页面",notes = "author:hxq")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isChecked",value = "是否被勾选",
            required = true,paramType = "java.lang.String"),
            @ApiImplicitParam(name = "skuId",value = "商品单元skuId",
            required = true,paramType = "java.lang.String")
    })
    @PostMapping("checkCart")
    @LoginRequired(loginSuccess = false)
    public ModelAndView checkCart(String isChecked, String skuId, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
        ModelAndView model = new ModelAndView();
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        // 调用服务，修改状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);

        // 将最新的数据从缓存中查出，渲染给内嵌页
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
        modelMap.put("cartList",omsCartItems);

        // 被勾选商品的总额
        BigDecimal totalAmount =getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);
        model.setViewName("cartList");
        return model;
    }


    @ApiOperation(value = "计算购物车")
    @ApiImplicitParam
    @GetMapping("cartList")
    @LoginRequired(loginSuccess = false)
    public ModelAndView cartList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
        ModelAndView model = new ModelAndView();
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        if(StringUtils.isNotBlank(memberId)){
            // 已经登录查询db
            omsCartItems = cartService.cartList(memberId);
        }else{
            // 没有登录查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie,OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }

        modelMap.put("cartList",omsCartItems);
        // 被勾选商品的总额
        BigDecimal totalAmount =getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);
        model.setViewName("cartList");
        return model;
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if(omsCartItem.getIsChecked().equals("1")){
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }

    @ApiOperation(value= "添加到购物车",notes = "author:hxq")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "商品单元skuId",
            required = true,paramType = "java.lang.String"),
            @ApiImplicitParam(name = "quantity",value = "商品数量",
            required = true,paramType = "int")
    })
    @PostMapping("addToCart")
    @LoginRequired(loginSuccess = false)
    @ResponseBody
    public ModelAndView addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();

        // 调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId, "");

        // 将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));


        // 判断用户是否登录
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");


        if (StringUtils.isBlank(memberId)) {
            // 用户没有登录

            // cookie里原有的购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isBlank(cartListCookie)) {
                // cookie为空
                omsCartItems.add(omsCartItem);
            } else {
                // cookie不为空
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                // 判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    // 之前添加过，更新购物车添加数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }
                } else {
                    // 之前没有添加，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }

            // 更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        } else {
            // 用户已经登录
            // 从db中查出购物车数据
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId,skuId);

            if(omsCartItemFromDb==null){
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname("test小明");
                omsCartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(omsCartItem);

            }else{
                // 该用户添加过当前商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }

            // 同步缓存
            cartService.flushCartCache(memberId);
        }
        ModelAndView model = new ModelAndView("success");
        model.addObject("skuInfo",skuInfo);
        model.addObject("skuNum",quantity);
        return model;
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {

        boolean b = false;

        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();

            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }


        return b;
    }

    @ApiOperation(value = "返回购物车",notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("One_JDshop.html")
    public ModelAndView getOneJDshop(){
        ModelAndView model = new ModelAndView();
        model.setViewName("One_JDshop");
        return model;
    }

}
