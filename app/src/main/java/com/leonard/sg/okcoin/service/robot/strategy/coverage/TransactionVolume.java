package com.leonard.sg.okcoin.service.robot.strategy.coverage;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCAccountInfo;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCCurrentPrice;
import com.leonard.sg.okcoin.service.robot.data.from.internet.RMBAccountInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by leonard on 30/4/15.
 */
public class TransactionVolume {

    private static final BigDecimal[] VOLUME_RATIO_FOR_TRANSACTION_POINT = {
        new BigDecimal("0.14"),
        new BigDecimal("0.22"),
        new BigDecimal("0.371"),
        new BigDecimal("0.19"),
        new BigDecimal("0.079")
    };

    /*
     * Transaction volume for every possible transaction point
     */
    private static BigDecimal[] VOLUME_FOR_POSSIBLE_TRANSACTION_POINT = {
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    };

    public static synchronized BigDecimal get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(int index) {
        return VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[index].setScale(SyncConstants.BTC_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized void calculateTransactionVolume() {

        BigDecimal totalAvlAmount = BTCAccountInfo.getBTCAvl().add(
                RMBAccountInfo.getRMBAvl().divide(
                        BTCCurrentPrice.getBTCCurrentPrice(), SyncConstants.BTC_ACCURACY, RoundingMode.DOWN));

        BigDecimal tmpAmount = totalAvlAmount;

        if (tmpAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal volumeFor3rdPoint = totalAvlAmount.multiply(VOLUME_RATIO_FOR_TRANSACTION_POINT[2], SyncConstants.BTC_MATH_CONTEXT);
        VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[2] = volumeFor3rdPoint;
        tmpAmount = tmpAmount.subtract(volumeFor3rdPoint);

        if (tmpAmount.compareTo(BigDecimal.ZERO) <= 0) {
            VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[1] = tmpAmount;
            return;
        }

        BigDecimal volumeFor2ndPoint = totalAvlAmount.multiply(VOLUME_RATIO_FOR_TRANSACTION_POINT[1], SyncConstants.BTC_MATH_CONTEXT);
        VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[1] = volumeFor2ndPoint;
        tmpAmount = tmpAmount.subtract(volumeFor2ndPoint);

        if (tmpAmount.compareTo(BigDecimal.ZERO) <= 0) {
            VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[3] = tmpAmount;
            return;
        }

        BigDecimal volumeFor4thPoint = totalAvlAmount.multiply(VOLUME_RATIO_FOR_TRANSACTION_POINT[3], SyncConstants.BTC_MATH_CONTEXT);
        VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[3] = volumeFor4thPoint;
        tmpAmount = tmpAmount.subtract(volumeFor4thPoint);

        if (tmpAmount.compareTo(BigDecimal.ZERO) <= 0) {
            VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[0] = tmpAmount;
            return;
        }

        BigDecimal volumeFor1stPoint = totalAvlAmount.multiply(VOLUME_RATIO_FOR_TRANSACTION_POINT[0], SyncConstants.BTC_MATH_CONTEXT);
        VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[0] = volumeFor1stPoint;
        tmpAmount = tmpAmount.subtract(volumeFor1stPoint);

        if (tmpAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        VOLUME_FOR_POSSIBLE_TRANSACTION_POINT[4] = tmpAmount;

    }

}
