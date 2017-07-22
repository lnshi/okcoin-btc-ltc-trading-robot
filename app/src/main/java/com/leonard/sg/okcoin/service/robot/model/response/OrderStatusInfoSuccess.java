package com.leonard.sg.okcoin.service.robot.model.response;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by leonard on 19/4/15.
 */
public class OrderStatusInfoSuccess {

    private class Orders {
        private BigDecimal amount;
        private BigDecimal avg_price;
        private Long create_date;
        private BigDecimal deal_amount;
        private String order_id;
        private String orders_id;
        private BigDecimal price;
        private int status;
        private String symbol;
        private String type;
    }

    private boolean result;
    private List<Orders> orders;

    public int getOrderStatus() {
        return orders.get(0).status;
    }

    public BigDecimal getCompletedCoinAmount() {
        return orders.get(0).deal_amount;
    }

    public BigDecimal getAveragePrice() {
        return orders.get(0).avg_price;
    }

    public BigDecimal getOrderRMBAmount() {
        return orders.get(0).deal_amount.multiply(orders.get(0).avg_price).setScale(SyncConstants.RMB_ACCURACY, RoundingMode.UP);
    }

}
