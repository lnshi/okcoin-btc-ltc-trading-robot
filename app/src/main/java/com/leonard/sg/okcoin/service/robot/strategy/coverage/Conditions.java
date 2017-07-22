package com.leonard.sg.okcoin.service.robot.strategy.coverage;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCCurrentPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by leonard on 23/4/15.
 */
public class Conditions {

    public static final BigDecimal MINIMUM_RAISE_FOR_TOP_TRANSACTION_POINT = new BigDecimal("10.01");

    public static final BigDecimal ALLOWED_EXCURSION = new BigDecimal("0.11");

    public static int[] INITIAL_GAP_SEQUENCE = {38, 10, 10, 23};

    public static AtomicIntegerArray WHETHER_CAN_PROCESS_TRANSACTION_INDICATOR = new AtomicIntegerArray(new int[] {-1, -1, -1, -1, -1});

    /*
     * Buy price for every possible transaction point
     */
    private static BigDecimal[] POSSIBLE_TRANSACTION_POINTS = {
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    };

    public static int get_POSSIBLE_TRANSACTION_POINTS_length() {
        return POSSIBLE_TRANSACTION_POINTS.length;
    }

    public static synchronized BigDecimal get_POSSIBLE_TRANSACTION_POINT(int index) {
        return POSSIBLE_TRANSACTION_POINTS[index].setScale(SyncConstants.RMB_ACCURACY, RoundingMode.DOWN);
    }

    public static synchronized void set_POSSIBLE_TRANSACTION_POINT(int index, BigDecimal value) {
        POSSIBLE_TRANSACTION_POINTS[index] = value;
    }

    public static synchronized void init_POSSIBLE_TRANSACTION_POINTS() {
        int currentBTCPrice = BTCCurrentPrice.getBTCCurrentPrice().setScale(0, RoundingMode.DOWN).intValueExact();

        POSSIBLE_TRANSACTION_POINTS[0] = new BigDecimal(currentBTCPrice - INITIAL_GAP_SEQUENCE[1] - INITIAL_GAP_SEQUENCE[0]);
        POSSIBLE_TRANSACTION_POINTS[1] = new BigDecimal(currentBTCPrice - INITIAL_GAP_SEQUENCE[1]);
        POSSIBLE_TRANSACTION_POINTS[2] = new BigDecimal(currentBTCPrice);

        POSSIBLE_TRANSACTION_POINTS[3] = new BigDecimal(currentBTCPrice + INITIAL_GAP_SEQUENCE[2]);
        POSSIBLE_TRANSACTION_POINTS[4] = new BigDecimal(currentBTCPrice + INITIAL_GAP_SEQUENCE[2] + INITIAL_GAP_SEQUENCE[3]);
    }

}
