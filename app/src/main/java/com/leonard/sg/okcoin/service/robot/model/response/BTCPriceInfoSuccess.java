package com.leonard.sg.okcoin.service.robot.model.response;

import java.math.BigDecimal;

/**
 * Created by leonard on 19/4/15.
 */
public class BTCPriceInfoSuccess {

    private class Ticker {

        private BigDecimal buy;
        private BigDecimal high;
        private BigDecimal last;
        private BigDecimal low;
        private BigDecimal sell;
        private BigDecimal vol;

    }

    private Long date;
    private Ticker ticker;

    public BigDecimal getBTCCurrentPrice() {
        return ticker.last;
    }

}
