package com.leonard.sg.okcoin.service.robot.data.from.internet;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class RMBAccountInfo {

    private static BigDecimal RMBSum = BigDecimal.ZERO;
    private static BigDecimal RMBAvl = BigDecimal.ZERO;
    private static BigDecimal RMBFrozen = BigDecimal.ZERO;

    public static BigDecimal getRMBSum() {
        return RMBSum.setScale(SyncConstants.RMB_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized BigDecimal getRMBAvl() {
        return RMBAvl.setScale(SyncConstants.RMB_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized void setRMBAvl(BigDecimal amount) {
        RMBAvl = RMBAvl.add(amount);
    }

    public static synchronized BigDecimal getRMBFrozen() {
        return RMBFrozen.setScale(SyncConstants.RMB_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized void setRMBFrozen(BigDecimal amount) {
        RMBFrozen = RMBFrozen.add(amount);
    }

    public static synchronized void setRMBAccountInfo(BigDecimal sum, BigDecimal avl, BigDecimal frozen) {
        RMBSum = sum;
        RMBAvl = avl;
        RMBFrozen = frozen;
    }

}
