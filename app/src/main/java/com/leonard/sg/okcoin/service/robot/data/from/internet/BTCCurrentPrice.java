package com.leonard.sg.okcoin.service.robot.data.from.internet;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class BTCCurrentPrice {

    private static BigDecimal BTCCurrentPrice = BigDecimal.ZERO;

    public static synchronized BigDecimal getBTCCurrentPrice() {
        return BTCCurrentPrice.setScale(SyncConstants.BTC_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized void setBTCCurrentPrice(BigDecimal price) {
        BTCCurrentPrice = price;
    }

}
