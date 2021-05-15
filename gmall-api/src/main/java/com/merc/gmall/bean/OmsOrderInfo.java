package com.merc.gmall.bean;

import java.io.Serializable;
import java.util.List;

public class OmsOrderInfo extends OmsOrder implements Serializable {

    String payTypeValue;
    String sourceTypeValue;
    String statusValue;
    String orderTypeValue;

    String productId;
    String productName;
    String productPrice;
    String productQuantity;

    List<OmsOrderItem> orderItems;

    public String getPayTypeValue() {
        return payTypeValue;
    }

    public void setPayTypeValue(String payTypeValue) {
        this.payTypeValue = payTypeValue;
    }

    public String getSourceTypeValue() {
        return sourceTypeValue;
    }

    public void setSourceTypeValue(String sourceTypeValue) {
        this.sourceTypeValue = sourceTypeValue;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public String getOrderTypeValue() {
        return orderTypeValue;
    }

    public void setOrderTypeValue(String orderTypeValue) {
        this.orderTypeValue = orderTypeValue;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public List<OmsOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OmsOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
