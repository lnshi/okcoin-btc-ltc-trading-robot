package com.leonard.sg.okcoin.service.robot.constant;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class SyncConstants {

    public static final int MAX_THREAD_NUMBER = 100;

    public static final int EXECUTOR_CORE_POOL_SIZE = 4;




    public static final BigDecimal MINIMUM_RMB_TRANSACTION_AMOUNT = new BigDecimal("50");

    public static final int RMB_ACCURACY = 2;

    public static final int BTC_ACCURACY = 2;

    public static final MathContext BTC_MATH_CONTEXT = new MathContext(BTC_ACCURACY, RoundingMode.DOWN);

    public static final int BTC_PRICE_QUERY_INTERVAL_IN_MILLISECOND = 2000;

    public static final int ROBOT_CHECK_TRANSACTION_CONDITION_INTERVAL_IN_MILLISECOND = 1000;

    public static final int INCOMPLETE_TRANSACTION_THREAD_SLEEP_INTERVAL_IN_MILLISECOND = 300;



    public static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static final String CN_API_KEY = "";

    public static final String CN_API_SECRET = "";



    /*
     * Transaction APIs and related variables
     */
    public static final BigDecimal BTC_MINIMUM_ORDER_AMOUNT = new BigDecimal("0.01");

    public static final BigDecimal LTC_MINIMUM_ORDER_AMOUNT = new BigDecimal("0.1");

    public static final String API_QUERY_USER_ACCOUNT_INFO = "https://www.okcoin.cn/api/v1/userinfo.do";

    public static final String API_QUERY_BTC_CURRENT_PRICE = "https://www.okcoin.cn/api/v1/ticker.do?symbol=btc_cny";

    public static final String API_PLACE_AN_ORDER = "https://www.okcoin.cn/api/v1/trade.do";

    public static final String API_QUERY_ORDER_STATUS = "https://www.okcoin.cn/api/v1/order_info.do";

    public enum COIN_TYPE {
        btc_cny
    }

    public enum ORDER_TYPE {
        buy, sell
    }

}
