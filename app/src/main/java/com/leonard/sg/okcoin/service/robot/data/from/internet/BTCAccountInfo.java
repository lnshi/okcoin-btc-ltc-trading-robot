package com.leonard.sg.okcoin.service.robot.data.from.internet;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class BTCAccountInfo {

    private static BigDecimal BTCSum = BigDecimal.ZERO;
    private static BigDecimal BTCAvl = BigDecimal.ZERO;
    private static BigDecimal BTCFrozen = BigDecimal.ZERO;

    public static BigDecimal getBTCSum() {
        return BTCSum.setScale(SyncConstants.BTC_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized BigDecimal getBTCAvl() {
        return BTCAvl.setScale(SyncConstants.BTC_ACCURACY, RoundingMode.DOWN);
    }
    public static synchronized void setBTCAvl(BigDecimal amount) {
        BTCAvl = BTCAvl.add(amount);
    }

    public static synchronized BigDecimal getBTCFrozen() {
        return BTCFrozen.setScale(SyncConstants.BTC_ACCURACY, RoundingMode.DOWN);
    }
    public static synchronized void setBTCFrozen(BigDecimal amount) {
        BTCFrozen = BTCFrozen.add(amount);
    }

    public static synchronized void setBTCAccountInfo(BigDecimal sum, BigDecimal avl, BigDecimal frozen) {
        BTCSum = sum;
        BTCAvl = avl;
        BTCFrozen = frozen;
    }

}
