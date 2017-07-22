package com.leonard.sg.okcoin.service.robot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.leonard.sg.okcoin.MainActivity;
import com.leonard.sg.okcoin.R;
import com.leonard.sg.okcoin.service.robot.aidl.ISyncAndTradeService;
import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCAccountInfo;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCCurrentPrice;
import com.leonard.sg.okcoin.service.robot.data.from.internet.RMBAccountInfo;
import com.leonard.sg.okcoin.service.robot.data.from.local.CommonStructure;
import com.leonard.sg.okcoin.service.robot.data.from.local.TotalProfit;
import com.leonard.sg.okcoin.service.robot.https.Query;
import com.leonard.sg.okcoin.service.robot.strategy.coverage.Conditions;
import com.leonard.sg.okcoin.service.robot.strategy.coverage.StrategyJudger;
import com.leonard.sg.okcoin.service.robot.strategy.coverage.TransactionVolume;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SyncAndTradeService extends Service {

    private ScheduledExecutorService syncAndTradeScheduledExecutorService;
    private ScheduledFuture<?> tradeThreadScheduledFuture;

    /*
     * Called by the system when the service is first created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundSyncAndTradeService();

        syncAndTradeScheduledExecutorService = Executors.newScheduledThreadPool(SyncConstants.EXECUTOR_CORE_POOL_SIZE);

        /*
         * Init user account info and set initial volume for every possible transaction point
         */
        syncAndTradeScheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (Query.queryUserAccountInfo()) {

                        if (Query.queryBTCCurrentPrice()) {
                            Conditions.init_POSSIBLE_TRANSACTION_POINTS();
                            TransactionVolume.calculateTransactionVolume();
                            return;
                        } else {
                            try {
                                Thread.sleep(SyncConstants.INCOMPLETE_TRANSACTION_THREAD_SLEEP_INTERVAL_IN_MILLISECOND);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(SyncConstants.INCOMPLETE_TRANSACTION_THREAD_SLEEP_INTERVAL_IN_MILLISECOND);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });

        syncAndTradeScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (CommonStructure.BTC_PRICE_QUERY_TIMES > 9999) {
                    CommonStructure.BTC_PRICE_QUERY_TIMES = 1;
                }
                CommonStructure.BTC_PRICE_QUERY_TIMES++;

                Query.queryBTCCurrentPrice();
            }
        }, 0, SyncConstants.BTC_PRICE_QUERY_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ISyncAndTradeService.Stub() {

            @Override
            public void startTradeEngine() throws RemoteException {
                tradeThreadScheduledFuture = syncAndTradeScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        StrategyJudger.strategyBuyJudger();
                    }
                }, 0, SyncConstants.ROBOT_CHECK_TRANSACTION_CONDITION_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
            }

            @Override
            public void stopTradeEngine() throws RemoteException {
                tradeThreadScheduledFuture.cancel(false);
            }

            @Override
            public String[] getRMBAccountInfo() throws RemoteException {
                return new String[] {
                    RMBAccountInfo.getRMBSum().toString(),
                    RMBAccountInfo.getRMBAvl().toString(),
                    RMBAccountInfo.getRMBFrozen().toString()
                };
            }

            @Override
            public String[] getBTCAccountInfo() throws RemoteException {
                return new String[] {
                    BTCAccountInfo.getBTCSum().toString(),
                    BTCAccountInfo.getBTCAvl().toString(),
                    BTCAccountInfo.getBTCFrozen().toString()
                };
            }

            @Override
            public String getCurrentBTCPrice() throws RemoteException {
                return BTCCurrentPrice.getBTCCurrentPrice().toString();
            }

            @Override
            public int getPriceQueryTimes() throws RemoteException {
                return CommonStructure.BTC_PRICE_QUERY_TIMES;
            }

            @Override
            public String getTotalProfit() throws RemoteException {
                return TotalProfit.getTotalProfit().toString();
            }

            @Override
            public int getRobotCheckTimes() throws RemoteException {
                return CommonStructure.ROBOT_CHECK_TIMES;
            }

            @Override
            public String[] getVolumeForTransactionPoints() throws RemoteException {
                return new String[] {
                    TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(0).toString(),
                    TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(1).toString(),
                    TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(2).toString(),
                    TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(3).toString(),
                    TransactionVolume.get_VOLUME_FOR_POSSIBLE_TRANSACTION_POINT_item(4).toString()
                };
            }

            @Override
            public void setIndicatorForSpecificTransactionPoint(int index, int value) throws RemoteException {
                Conditions.WHETHER_CAN_PROCESS_TRANSACTION_INDICATOR.set(index, value);
            }

            @Override
            public String[] getValueOfTransactionPoints() throws RemoteException {
                return new String[] {
                    Conditions.get_POSSIBLE_TRANSACTION_POINT(0).setScale(0, RoundingMode.DOWN).toString(),
                    Conditions.get_POSSIBLE_TRANSACTION_POINT(1).setScale(0, RoundingMode.DOWN).toString(),
                    Conditions.get_POSSIBLE_TRANSACTION_POINT(2).setScale(0, RoundingMode.DOWN).toString(),
                    Conditions.get_POSSIBLE_TRANSACTION_POINT(3).setScale(0, RoundingMode.DOWN).toString(),
                    Conditions.get_POSSIBLE_TRANSACTION_POINT(4).setScale(0, RoundingMode.DOWN).toString()
                };
            }

            @Override
            public void setValueForSpecificTransactionPoint(int index, int value) throws RemoteException {
                Conditions.set_POSSIBLE_TRANSACTION_POINT(index, new BigDecimal(value));
            }

            @Override
            public String[] getTransactionPointsInitialGapSequence() throws RemoteException {
                return new String[] {
                    String.valueOf(Conditions.INITIAL_GAP_SEQUENCE[0]),
                    String.valueOf(Conditions.INITIAL_GAP_SEQUENCE[1]),
                    String.valueOf(Conditions.INITIAL_GAP_SEQUENCE[2]),
                    String.valueOf(Conditions.INITIAL_GAP_SEQUENCE[3])
                };
            }

            @Override
            public void setSpecificTransactionPointGapValue(int index, int value) throws RemoteException {
                Conditions.INITIAL_GAP_SEQUENCE[index] = value;
            }

            @Override
            public int getWhetherCanProcessIndicator(int index) throws RemoteException {
                return Conditions.WHETHER_CAN_PROCESS_TRANSACTION_INDICATOR.get(index);
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        syncAndTradeScheduledExecutorService.shutdown();
    }

    private void startForegroundSyncAndTradeService() {
        final int MY_FOREGROUND_SERVICE_START_ID = 996539;
        startForeground(MY_FOREGROUND_SERVICE_START_ID, buildFixedNotification());
    }

    private Notification buildFixedNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new Notification.Builder(this)
            .setContentTitle("OKCoin Robot")
            .setContentText("SyncAndTradeService is running.")
            .setSmallIcon(R.drawable.bitcoin)
            .setContentIntent(pendingIntent)
            .build();
    }

}
