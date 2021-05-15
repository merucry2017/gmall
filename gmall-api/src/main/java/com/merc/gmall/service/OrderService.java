package com.merc.gmall.service;

import com.merc.gmall.bean.OmsOrder;
import com.merc.gmall.bean.OmsOrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    String checkTradeCode(String memberId, String tradeCode);

    String genTradeCode(String memberId);

    String getTradeCode(String memberId);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);

    List<OmsOrder> getOrderByMemberId(String memberId);

    List<OmsOrderItem> getOrderItemByOrderSn(String orderSn);
}
