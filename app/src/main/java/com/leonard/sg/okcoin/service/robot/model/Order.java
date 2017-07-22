package com.leonard.sg.okcoin.service.robot.model;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.math.BigDecimal;

/**
 * Created by leonard on 19/4/15.
 */
public class Order {

    private String orderId;


    private SyncConstants.COIN_TYPE symbol;

    private SyncConstants.ORDER_TYPE type;

    private BigDecimal price;

    private BigDecimal amount;


    private Object[] extraFields;

    public Order(SyncConstants.COIN_TYPE symbol, SyncConstants.ORDER_TYPE type, BigDecimal price, BigDecimal amount, Object[] extraFields) {
        this.symbol = symbol;
        this.type = type;
        this.price = price;
        this.amount = amount;
        this.extraFields = extraFields;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public SyncConstants.COIN_TYPE getSymbol() {
        return symbol;
    }

    public void setSymbol(SyncConstants.COIN_TYPE symbol) {
        this.symbol = symbol;
    }

    public SyncConstants.ORDER_TYPE getType() {
        return type;
    }

    public void setType(SyncConstants.ORDER_TYPE type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Object[] getExtraFields() {
        return extraFields;
    }

    public void setExtraFields(Object[] extraFields) {
        this.extraFields = extraFields;
    }
}
