package com.leonard.sg.okcoin.service.robot.strategy.coverage;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCCurrentPrice;
import com.leonard.sg.okcoin.service.robot.data.from.internet.RMBAccountInfo;
import com.leonard.sg.okcoin.service.robot.data.from.local.CommonStructure;
import com.leonard.sg.okcoin.service.robot.data.from.local.TotalProfit;
import com.leonard.sg.okcoin.service.robot.https.Trade;
import com.leonard.sg.okcoin.service.robot.model.Order;

import java.math.BigDecimal;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StrategyJudger implements Observer {

    private Order halfMadeOrder;

    public StrategyJudger() {
    }

    public StrategyJudger(Order halfMadeOrder) {
        this.halfMadeOrder = halfMadeOrder;
    }

    public static void strategyBuyJudger() {

        if (CommonStructure.ROBOT_CHECK_TIMES > 9999) {
            CommonStructure.ROBOT_CHECK_TIMES = 1;
        }
        CommonStructure.ROBOT_CHECK_TIMES++;

        for (int i = 0; i < Conditions.get_POSSIBLE_TRANSACTION_POINTS_length(); i++) {

            if (Conditions.WHETHER_CAN_PROCESS_TRANSACTION_INDICATOR.get(i) == 0) {
                continue;
            }

            BigDecimal currentBTCPrice = BTCCurrentPrice.getBTCCurrentPrice();

            if (currentBTCPrice.compareTo(Conditions.get_POSSIBLE_TRANSACTION_POINT(i).add(Conditions.ALLOWED_EXCURSION)) <= 0) {
                if (RMBAccountInfo.getRMBAvl().compareTo(SyncConstants.MINIMUM_RMB_TRANSACTION_AMOUNT) >= 0 &&
                        RMBAccountInfo.getRMBAvl().compareTo(currentBTCPrice.multiply(TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(i))) >= 0) {
                    if (Conditions.WHETHER_CAN_PROCESS_TRANSACTION_INDICATOR.compareAndSet(i, 1, 0)) {
                        Order order = new Order(SyncConstants.COIN_TYPE.btc_cny,
                            SyncConstants.ORDER_TYPE.buy,
                            currentBTCPrice,
                            TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(i),
                            new Object[] {
                                i
                            }
                        );

                        makeBuyOrder(order);
                    }
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data != null) {

            final Order order = (Order) data;

            if (order.getType().equals(SyncConstants.ORDER_TYPE.buy)) {
                ExecutorService sellOrderThread = Executors.newSingleThreadExecutor();
                sellOrderThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        new StrategyJudger(order).strategySellJudger();
                    }
                });
                sellOrderThread.shutdown();
            } else {
                Object[] extraFields = order.getExtraFields();

                int indicator = (int) extraFields[0];
                BigDecimal lastBuyPrice = (BigDecimal) extraFields[1];

                Conditions.WHETHER_CAN_PROCESS_TRANSACTION_INDICATOR.compareAndSet(indicator, 0, 1);

                TotalProfit.setTotalProfit(order.getPrice().subtract(lastBuyPrice).multiply(order.getAmount()));
            }

            CommonStructure.SUCCESSFUL_ORDERS.remove(order);
        }

    }

    private static void makeBuyOrder(final Order order) {
        ExecutorService buyOrderThread = Executors.newSingleThreadExecutor();
        buyOrderThread.execute(new Runnable() {
            @Override
            public void run() {
                new Trade(order, new StrategyJudger()).tradeController();
            }
        });
        buyOrderThread.shutdown();
    }

    private void strategySellJudger() {
        int indicator = (int) halfMadeOrder.getExtraFields()[0];

        while (true) {
            BigDecimal currentBTCPrice = BTCCurrentPrice.getBTCCurrentPrice();

            BigDecimal expectedSellPrice;

            if (indicator == Conditions.get_POSSIBLE_TRANSACTION_POINTS_length() - 1) {
                expectedSellPrice = Conditions.get_POSSIBLE_TRANSACTION_POINT(indicator).add(Conditions.MINIMUM_RAISE_FOR_TOP_TRANSACTION_POINT);
            } else {
                expectedSellPrice = Conditions.get_POSSIBLE_TRANSACTION_POINT(indicator + 1);
            }

            if (currentBTCPrice.compareTo(expectedSellPrice.subtract(Conditions.ALLOWED_EXCURSION)) >= 0) {
                Order order = new Order(SyncConstants.COIN_TYPE.btc_cny,
                    SyncConstants.ORDER_TYPE.sell,
                    currentBTCPrice,
                    halfMadeOrder.getAmount(),
                    new Object[] {
                        indicator,
                        halfMadeOrder.getPrice()
                    }
                );

                new Trade(order, new StrategyJudger()).tradeController();
                return;
            } else {
                try {
                    Thread.sleep(SyncConstants.INCOMPLETE_TRANSACTION_THREAD_SLEEP_INTERVAL_IN_MILLISECOND);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
