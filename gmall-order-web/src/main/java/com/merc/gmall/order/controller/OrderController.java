package com.merc.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.merc.gmall.annotations.LoginRequired;
import com.merc.gmall.bean.*;
import com.merc.gmall.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class OrderController {

    @Reference
    CartService cartService;

    @Reference
    UserService userService;

    @Reference
    OrderService orderService;

    @Reference
    SkuService skuService;

    @Reference
    AddressService addressService;

    @ApiOperation(value = "返回首页",notes = "author:hxq")
    @GetMapping("/")
    @LoginRequired(loginSuccess = false)
    public void getIndex(HttpServletResponse resp) throws IOException {
        resp.sendRedirect("http://localhost:8083/index");
    }

    @ApiOperation(value = "提交订单", notes = "author:hxq")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiveAddressId",value = "收货地址",
                    required = true,paramType = "java.lang.String"),
            @ApiImplicitParam(name = "totalAmount",value = "总金额",
                    required = true,paramType = "java.math.BigDecimal"),
            @ApiImplicitParam(name = "tradeCode",value = "交易码",
                    required = true,paramType = "java.lang.String")
    })
    @PostMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, String token, HttpServletRequest request, HttpServletResponse response) {


        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        // 检查交易码
        String success = orderService.checkTradeCode(memberId, tradeCode);

        if (success.equals("success")) {
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            // 订单对象
            OmsOrder omsOrder = new OmsOrder();
            // 设置自动确认时间（天）
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setDiscountAmount(null);
            //omsOrder.setFreightAmount(); 运费，支付后，在生成物流信息时
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote("快点发货");
            String outTradeNo = "gmall";
            outTradeNo = outTradeNo + System.currentTimeMillis();// 将毫秒时间戳拼接到外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format(new Date());// 将时间字符串拼接到外部订单号

            omsOrder.setOrderSn(outTradeNo);//外部订单号
            omsOrder.setPayAmount(totalAmount);
            // 设置支付方式：0->未支付；1->支付宝；2->微信
            omsOrder.setPayType(0);
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getReceiveAddressById(receiveAddressId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            // 当前日期加一天，一天后配送
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,1);
            Date time = c.getTime();
            omsOrder.setReceiveTime(time);
            // 设置订单来源：0->PC订单；1->app订单
            omsOrder.setSourceType(0);
            // 设置订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
            omsOrder.setStatus("0");
            // 设置订单类型：0->正常订单；1->秒杀订单
            omsOrder.setOrderType(0);
            omsOrder.setTotalAmount(totalAmount);

            // 根据用户id获得要购买的商品列表(购物车)，和总价格
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    // 获得订单详情列表
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    // 检价
                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    b = true;
                    if (b == false) {
                        ModelAndView mv = new ModelAndView("tradeFail");
                        return mv;
                    }
                    // 验库存,远程调用库存系统
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());

                    omsOrderItem.setOrderSn(outTradeNo);// 外部订单号，用来和其他系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("111111111111");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库对应的商品编号");// 在仓库中的skuId

                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);

            // 将订单和订单详情写入数据库
            // 删除购物车的对应商品
            orderService.saveOrder(omsOrder);

            // 重定向到支付系统
            ModelAndView mv = new ModelAndView("redirect:http://payment.gmall.com:8087/index");
            mv.addObject("outTradeNo",outTradeNo);
            mv.addObject("totalAmount",totalAmount);
            mv.addObject("token", token);
            mv.addObject("nickname", nickname);
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("tradeFail");
            mv.addObject("token", token);
            mv.addObject("nickName", nickname);
            mv.addObject("errMsg", "交易时间过长，交易码失效");
            return mv;
        }

    }

    @ApiOperation(value = "去购物车结算",notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public ModelAndView toTrade(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
        ModelAndView model = new ModelAndView();
        String token = request.getParameter("token");
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        // 收件人地址列表
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getReceiveAddressByMemberId(memberId);

        // 将购物车集合转化为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

        List<OmsOrderItem> omsOrderItems = new ArrayList<>();

        for (OmsCartItem omsCartItem : omsCartItems) {
            // 每循环一个购物车对象，就封装一个商品的详情到OmsOrderItem
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItems.add(omsOrderItem);
            }
        }
        // 将数据放到model中，再从前端页面获取此处的数据
        model.addObject("omsOrderItems", omsOrderItems);
        model.addObject("userAddressList", umsMemberReceiveAddresses);
        model.addObject("totalAmount", getTotalAmount(omsCartItems));
        model.addObject("nickName",nickname);
        model.addObject("memberId",memberId);

        // 生成交易码，为了在提交订单时做交易码的校验
        String tradeCode = orderService.genTradeCode(memberId);
        model.addObject("tradeCode", tradeCode);
        model.addObject("token", token);
        model.setViewName("trade");
        return model;
    }


    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if (omsCartItem.getIsChecked().equals("1")) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }
    @ApiOperation(value = "返回用户订单页面",notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("one_order.html")
    @LoginRequired(loginSuccess = true)
    public ModelAndView getOneOrder(HttpServletRequest request){
        String memberId = (String) request.getAttribute("memberId");
        List<OmsOrder> omsOrders = orderService.getOrderByMemberId(memberId);
        List<OmsOrderInfo> omsOrderInfos = new ArrayList<>(omsOrders.size());
        OmsOrderInfo omsOrderInfo;
        for(OmsOrder omsOrder: omsOrders) {
            omsOrderInfo = new OmsOrderInfo();
            omsOrderInfo.setId(omsOrder.getId());
            omsOrderInfo.setCreateTime(omsOrder.getCreateTime());
            omsOrderInfo.setOrderSn(omsOrder.getOrderSn());
            List<OmsOrderItem> orderItems = orderService.getOrderItemByOrderSn(omsOrder.getOrderSn());
            omsOrderInfo.setOrderItems(orderItems);
            omsOrderInfo.setMemberUsername(omsOrder.getMemberUsername());
            omsOrderInfo.setTotalAmount(omsOrder.getTotalAmount());
            int payType = omsOrder.getPayType();
            if(payType == 0) {
                omsOrderInfo.setPayTypeValue("未支付");
            } else {
                omsOrderInfo.setPayTypeValue("支付宝");
            }
            String status = omsOrder.getStatus();
            if(status.equals("0")) {
                omsOrderInfo.setStatusValue("待付款");
            } else {
                omsOrderInfo.setStatusValue("待发货");
            }
            omsOrderInfos.add(omsOrderInfo);
        }
        ModelAndView model = new ModelAndView();
        model.setViewName("one_order");
        model.addObject("omsOrderInfos", omsOrderInfos);
        return model;
    }

    @ApiOperation(value = "返回中国所有省的名称", notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("getAllHatProvince")
    public List<HatProvince> getAllHatProvince() {
        List<HatProvince> hatProvinces = addressService.getAllHatProvince();

        return hatProvinces;
    }

    @ApiOperation(value = "根据父id获取城市", notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("getAllHatCityByFather")
    public List<HatCity> getAllHatCityByFather(String father) {
        List<HatCity> hatCities = addressService.getAllHatCityByFather(father);
        return hatCities;
    }

    @ApiOperation(value = "根据父id获取县区", notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("getAllHatAreaByFather")
    public List<HatArea> getAllHatAreaByFather(String father) {
        List<HatArea> hatAreas = addressService.getAllHatAreaByFather(father);
        return hatAreas;
    }

    @ApiOperation(value = "返回新增地址页", notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("toAddress")
    @LoginRequired(loginSuccess = true)
    public ModelAndView toAddress(HttpServletRequest request) {
        String token = request.getParameter("token");
        String nickname = (String) request.getAttribute("nickname");        ModelAndView modelAndView = new ModelAndView("address");
        modelAndView.addObject("token", token);
        modelAndView.addObject("nickName", nickname);
        return modelAndView;
    }

    @ApiOperation(value = "新增收获地址", notes = "author:hxq")
    @ApiImplicitParam
    @PostMapping("addAddress")
    @LoginRequired(loginSuccess = true)
    public Result addAddress(UmsMemberReceiveAddress umsMemberReceiveAddress, HttpServletRequest request) {
        Result result = new Result();
        result.setSuccess(false);
        String memberId = (String) request.getAttribute("memberId");
        umsMemberReceiveAddress.setMemberId(memberId);
        umsMemberReceiveAddress.setDefaultStatus("0");
        result = userService.saveUmsMemberReceiveAddress(umsMemberReceiveAddress);
        ModelMap modelMap = new ModelMap();
        String token = request.getParameter("token");
        modelMap.addAttribute("token", token);
        return result;
    }

    @ApiOperation(value = "删除收获地址", notes = "author:hxq")
    @ApiImplicitParam
    @DeleteMapping("deleteAddressById")
    @LoginRequired(loginSuccess = true)
    public Result deleteAddressById(@RequestParam String id, HttpServletRequest request) {
        Result result = userService.deleteUmsMemberReceiveAddressById(id);
        return result;
    }

    @ApiOperation(value = "修改收获地址", notes = "author:hxq")
    @ApiImplicitParam
    @PostMapping("modifyAddressById")
    @LoginRequired(loginSuccess = true)
    public Result modifyAddressById(UmsMemberReceiveAddress umsMemberReceiveAddress, HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        umsMemberReceiveAddress.setMemberId(memberId);
        Result result = userService.modifyUmsMemberReceiveAddressById(umsMemberReceiveAddress);
        return result;
    }

    @ApiOperation(value = "根据id收获地址", notes = "author:hxq")
    @ApiImplicitParam
    @GetMapping("getAddressById")
    @LoginRequired(loginSuccess = true)
    public UmsMemberReceiveAddress getAddressById(@RequestParam String id, HttpServletRequest request) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getUmsMemberReceiveAddressById(id);
        return umsMemberReceiveAddress;
    }

    @ApiOperation(value = "根据orderSn删除订单", notes = "author:hxq")
    @ApiImplicitParam
    @DeleteMapping("deleteOrderById")
    @LoginRequired(loginSuccess = true)
    public Result deleteOrderById(@RequestParam String orderId, HttpServletRequest request) {
        Result result = new Result();
        result.setSuccess(true);
        try {
            orderService.deleteOrderById(orderId);
        }catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        return result;
    }
}

