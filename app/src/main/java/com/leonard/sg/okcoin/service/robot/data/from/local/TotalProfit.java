package com.leonard.sg.okcoin.service.robot.data.from.local;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class TotalProfit {

    private static BigDecimal totalProfit = BigDecimal.ZERO;

    public static synchronized BigDecimal getTotalProfit() {
        return totalProfit.setScale(SyncConstants.RMB_ACCURACY, RoundingMode.DOWN);
    }
    public static synchronized void setTotalProfit(BigDecimal amount) {
        totalProfit = totalProfit.add(amount);
    }

}
