package com.leonard.sg.okcoin.service.robot.model.response;

import java.math.BigDecimal;

/**
 * Created by leonard on 18/4/15.
 */
public class UserAccountInfoSuccess {

    private class Info {

        private class Funds {

            private class Asset {

                private BigDecimal net;
                private BigDecimal total;

            }

            private class Free {

                private BigDecimal btc;
                private BigDecimal cny;
                private BigDecimal ltc;

            }

            private class Frozen {

                private BigDecimal btc;
                private BigDecimal cny;
                private BigDecimal ltc;

            }

            private Asset asset;
            private Free free;
            private Frozen freezed;

        }

        private Funds funds;

    }

    private Info info;
    private boolean result;

    public BigDecimal getRMBSum() {
        return info.funds.free.cny.add(info.funds.freezed.cny);
    }

    public BigDecimal getRMBAvl() {
        return info.funds.free.cny;
    }

    public BigDecimal getRMBFrozen() {
        return info.funds.freezed.cny;
    }

    public BigDecimal getBTCSum() {
        return info.funds.free.btc.add(info.funds.freezed.btc);
    }

    public BigDecimal getBTCAvl() {
        return info.funds.free.btc;
    }

    public BigDecimal getBTCFrozen() {
        return info.funds.freezed.btc;
    }

}
