package com.leonard.sg.okcoin.ui;

import android.graphics.Typeface;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leonard.sg.okcoin.MainActivity;
import com.leonard.sg.okcoin.R;
import com.leonard.sg.okcoin.constant.Constants;
import com.leonard.sg.okcoin.service.robot.aidl.ISyncAndTradeService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivityUI implements Runnable {
    /*
     * UI components
     */
    public  static boolean isUIComponentsInitialized = false;

    private TextView RMBSumValue;
    private TextView RMBAvlValue;
    private TextView RMBFrozenValue;

    private TextView BTCSumValue;
    private TextView BTCAvlValue;
    private TextView BTCFrozenValue;

    private TextView BTCCurrentPriceTextView;

    private TextView queryBTCCurrentPriceTimesTextView;

    private TextView totalProfitValue;

    private TextView robotCheckTransactionConditionTimesTextView;

    private TextView volumeFor1stTransactionPointTextView;
    private TextView volumeFor2ndTransactionPointTextView;
    private TextView volumeFor3rdTransactionPointTextView;
    private TextView volumeFor4thTransactionPointTextView;
    private TextView volumeFor5thTransactionPointTextView;

    private CheckBox canOn1stPoint;
    private CheckBox canOn2ndPoint;
    private CheckBox canOn3rdPoint;
    private CheckBox canOn4thPoint;
    private CheckBox canOn5thPoint;

    private TextView firstTransactionPointValue;
    private TextView secondTransactionPointValue;
    private TextView thirdTransactionPointValue;
    private TextView fourthTransactionPointValue;
    private TextView fifthTransactionPointValue;

    private RadioButton firstTransactionPoint;
    private RadioButton secondTransactionPoint;
    private RadioButton thirdTransactionPoint;
    private RadioButton fourthTransactionPoint;
    private RadioButton fifthTransactionPoint;

    private TextView firstGapValue;
    private TextView secondGapValue;
    private TextView thirdGapValue;
    private TextView fourthGapValue;

    private ImageButton decreaseButton;
    private ImageButton increaseButton;

    private Button startRobot;

    /*
     * other variables
     */
    private boolean isTransactionPointValuesInitialized = false;

    private boolean isTransactionPointsInitialGapSequenceInitialized = false;

    private boolean isRobotStarted = false;

    private String checkerTag = "A";

    private String traderTag = "A";

    /*
     * init in constructor
     */
    private MainActivity mainActivity;
    private ISyncAndTradeService syncAndTradeService;
    private ScheduledExecutorService mainActivityUIScheduledExecutorService;

    public MainActivityUI(MainActivity mainActivity, ISyncAndTradeService syncAndTradeService, ScheduledExecutorService mainActivityUIScheduledExecutorService) {
        this.mainActivity = mainActivity;
        this.syncAndTradeService = syncAndTradeService;
        this.mainActivityUIScheduledExecutorService = mainActivityUIScheduledExecutorService;
    }

    @Override
    public void run() {

        initUIComponents();

        updateWhetherCanProcessTransactionCheckboxesUI();

        initTransactionPointsInitialGapSequenceUI();
        updateTransactionPointRelatedUI();

        handelStartStopRobotButtonUI();

        circularlyUpdateUserRMBAccountInfoUI();
        circularlyUpdateUserBTCAccountInfoUI();
        circularlyUpdateCurrentBTCPriceUI();
        circularlyUpdateQueryBTCPriceTimesUI();
        circularlyUpdateTotalProfitUI();
        circularlyUpdateRobotCheckTimesUI();
        circularlyUpdateVolumeForTransactionPointsUI();
        circularlyUpdateTransactionPointsValueUI();

    }

    private void initUIComponents() {

        RMBSumValue = (TextView) mainActivity.findViewById(R.id.user_account_rmb_sum_value);
        RMBAvlValue = (TextView) mainActivity.findViewById(R.id.user_account_rmb_avl_value);
        RMBFrozenValue = (TextView) mainActivity.findViewById(R.id.user_account_rmb_frozen_value);

        BTCSumValue = (TextView) mainActivity.findViewById(R.id.user_account_btc_sum_value);
        BTCAvlValue = (TextView) mainActivity.findViewById(R.id.user_account_btc_avl_value);
        BTCFrozenValue = (TextView) mainActivity.findViewById(R.id.user_account_btc_frozen_value);

        BTCCurrentPriceTextView = (TextView) mainActivity.findViewById(R.id.btc_current_price);

        queryBTCCurrentPriceTimesTextView = (TextView) mainActivity.findViewById(R.id.query_current_btc_price_times);

        totalProfitValue = (TextView) mainActivity.findViewById(R.id.total_profit_value);

        robotCheckTransactionConditionTimesTextView = (TextView) mainActivity.findViewById(R.id.robot_check_transaction_condition_times);

        volumeFor1stTransactionPointTextView = (TextView) mainActivity.findViewById(R.id.volume_for_1st_transaction_point);
        volumeFor2ndTransactionPointTextView = (TextView) mainActivity.findViewById(R.id.volume_for_2nd_transaction_point);
        volumeFor3rdTransactionPointTextView = (TextView) mainActivity.findViewById(R.id.volume_for_3rd_transaction_point);
        volumeFor4thTransactionPointTextView = (TextView) mainActivity.findViewById(R.id.volume_for_4th_transaction_point);
        volumeFor5thTransactionPointTextView = (TextView) mainActivity.findViewById(R.id.volume_for_5th_transaction_point);

        canOn1stPoint = (CheckBox) mainActivity.findViewById(R.id.whether_can_process_transaction_on_1st_point);
        canOn2ndPoint = (CheckBox) mainActivity.findViewById(R.id.whether_can_process_transaction_on_2nd_point);
        canOn3rdPoint = (CheckBox) mainActivity.findViewById(R.id.whether_can_process_transaction_on_3rd_point);
        canOn4thPoint = (CheckBox) mainActivity.findViewById(R.id.whether_can_process_transaction_on_4th_point);
        canOn5thPoint = (CheckBox) mainActivity.findViewById(R.id.whether_can_process_transaction_on_5th_point);

        firstTransactionPointValue = (TextView) mainActivity.findViewById(R.id.first_transaction_point_value);
        secondTransactionPointValue = (TextView) mainActivity.findViewById(R.id.second_transaction_point_value);
        thirdTransactionPointValue = (TextView) mainActivity.findViewById(R.id.third_transaction_point_value);
        fourthTransactionPointValue = (TextView) mainActivity.findViewById(R.id.fourth_transaction_point_value);
        fifthTransactionPointValue = (TextView) mainActivity.findViewById(R.id.fifth_transaction_point_value);

        firstTransactionPoint = (RadioButton) mainActivity.findViewById(R.id.active_first_transaction_point);
        secondTransactionPoint = (RadioButton) mainActivity.findViewById(R.id.active_second_transaction_point);
        thirdTransactionPoint = (RadioButton) mainActivity.findViewById(R.id.active_third_transaction_point);
        fourthTransactionPoint = (RadioButton) mainActivity.findViewById(R.id.active_fourth_transaction_point);
        fifthTransactionPoint = (RadioButton) mainActivity.findViewById(R.id.active_fifth_transaction_point);

        firstGapValue = (TextView) mainActivity.findViewById(R.id.first_gap_value);
        secondGapValue = (TextView) mainActivity.findViewById(R.id.second_gap_value);
        thirdGapValue = (TextView) mainActivity.findViewById(R.id.third_gap_value);
        fourthGapValue = (TextView) mainActivity.findViewById(R.id.fourth_gap_value);

        decreaseButton = (ImageButton) mainActivity.findViewById(R.id.decrease_active_transaction_point);
        increaseButton = (ImageButton) mainActivity.findViewById(R.id.increase_active_transaction_point);

        startRobot = (Button) mainActivity.findViewById(R.id.confirm_start_robot);

        isUIComponentsInitialized = true;

    }

    private void circularlyUpdateUserRMBAccountInfoUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String[]> callable = new Callable<String[]>() {
                    @Override
                    public String[] call() throws Exception {
                        return syncAndTradeService.getRMBAccountInfo();
                    }
                };

                Future<String[]> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String[] userRMBAccountInfo = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RMBSumValue.setText(userRMBAccountInfo[0]);
                            RMBAvlValue.setText(userRMBAccountInfo[1]);
                            RMBFrozenValue.setText(userRMBAccountInfo[2]);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateUserBTCAccountInfoUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String[]> callable = new Callable<String[]>() {
                    @Override
                    public String[] call() throws Exception {
                        return syncAndTradeService.getBTCAccountInfo();
                    }
                };

                Future<String[]> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String[] userBTCAccountInfo = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BTCSumValue.setText(userBTCAccountInfo[0]);
                            BTCAvlValue.setText(userBTCAccountInfo[1]);
                            BTCFrozenValue.setText(userBTCAccountInfo[2]);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateCurrentBTCPriceUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String> callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return syncAndTradeService.getCurrentBTCPrice();
                    }
                };

                Future<String> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String currentBTCPrice = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BTCCurrentPriceTextView.setText(currentBTCPrice);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateQueryBTCPriceTimesUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String> callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String str = String.valueOf(syncAndTradeService.getPriceQueryTimes()) + checkerTag;
                        checkerTag = checkerTag.equals("A") ? "B" : "A";
                        return str;
                    }
                };

                Future<String> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String queryBTCCurrentPriceTimes = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryBTCCurrentPriceTimesTextView.setText(queryBTCCurrentPriceTimes);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateTotalProfitUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String> callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return syncAndTradeService.getTotalProfit();
                    }
                };

                Future<String> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String totalProfit = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            totalProfitValue.setText(totalProfit);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateRobotCheckTimesUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String> callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String str = String.valueOf(syncAndTradeService.getRobotCheckTimes()) + traderTag;
                        traderTag = traderTag.equals("A") ? "B" : "A";
                        return str;
                    }
                };

                Future<String> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String robotCheckTimes = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            robotCheckTransactionConditionTimesTextView.setText(robotCheckTimes);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateVolumeForTransactionPointsUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String[]> callable = new Callable<String[]>() {
                    @Override
                    public String[] call() throws Exception {
                        return syncAndTradeService.getVolumeForTransactionPoints();
                    }
                };

                Future<String[]> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String[] volumeForTransactionPoints = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            volumeFor1stTransactionPointTextView.setText(volumeForTransactionPoints[0]);
                            volumeFor2ndTransactionPointTextView.setText(volumeForTransactionPoints[1]);
                            volumeFor3rdTransactionPointTextView.setText(volumeForTransactionPoints[2]);
                            volumeFor4thTransactionPointTextView.setText(volumeForTransactionPoints[3]);
                            volumeFor5thTransactionPointTextView.setText(volumeForTransactionPoints[4]);
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void circularlyUpdateTransactionPointsValueUI() {

        mainActivityUIScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Callable<String[]> callable = new Callable<String[]>() {
                    @Override
                    public String[] call() throws Exception {
                        return syncAndTradeService.getValueOfTransactionPoints();
                    }
                };

                Future<String[]> future = mainActivityUIScheduledExecutorService.submit(callable);

                try {
                    final String[] transactionPointsValue = future.get();

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            firstTransactionPointValue.setText(transactionPointsValue[0]);
                            secondTransactionPointValue.setText(transactionPointsValue[1]);
                            thirdTransactionPointValue.setText(transactionPointsValue[2]);
                            fourthTransactionPointValue.setText(transactionPointsValue[3]);
                            fifthTransactionPointValue.setText(transactionPointsValue[4]);
                        }
                    });

                    isTransactionPointValuesInitialized = true;
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, Constants.UI_UPDATE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    private void updateWhetherCanProcessTransactionCheckboxesUI() {

        canOn1stPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int targetValue = isChecked? 1 : -1;
                try {
                    syncAndTradeService.setIndicatorForSpecificTransactionPoint(0, targetValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        canOn2ndPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int targetValue = isChecked? 1 : -1;
                try {
                    syncAndTradeService.setIndicatorForSpecificTransactionPoint(1, targetValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        canOn3rdPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int targetValue = isChecked? 1 : -1;
                try {
                    syncAndTradeService.setIndicatorForSpecificTransactionPoint(2, targetValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        canOn4thPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int targetValue = isChecked ? 1 : -1;
                try {
                    syncAndTradeService.setIndicatorForSpecificTransactionPoint(3, targetValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        canOn5thPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int targetValue = isChecked ? 1 : -1;
                try {
                    syncAndTradeService.setIndicatorForSpecificTransactionPoint(4, targetValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateTransactionPointRelatedUI() {

        firstTransactionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondTransactionPoint.setChecked(false);
                    thirdTransactionPoint.setChecked(false);
                    fourthTransactionPoint.setChecked(false);
                    fifthTransactionPoint.setChecked(false);

                    firstTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.active_green));
                    firstTransactionPointValue.setTypeface(null, Typeface.BOLD);


                    secondTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    secondTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    thirdTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    thirdTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fourthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fourthTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fifthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fifthTransactionPointValue.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        secondTransactionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstTransactionPoint.setChecked(false);
                    thirdTransactionPoint.setChecked(false);
                    fourthTransactionPoint.setChecked(false);
                    fifthTransactionPoint.setChecked(false);

                    secondTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.active_green));
                    secondTransactionPointValue.setTypeface(null, Typeface.BOLD);


                    firstTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    firstTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    thirdTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    thirdTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fourthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fourthTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fifthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fifthTransactionPointValue.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        thirdTransactionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstTransactionPoint.setChecked(false);
                    secondTransactionPoint.setChecked(false);
                    fourthTransactionPoint.setChecked(false);
                    fifthTransactionPoint.setChecked(false);

                    thirdTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.active_green));
                    thirdTransactionPointValue.setTypeface(null, Typeface.BOLD);


                    firstTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    firstTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    secondTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    secondTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fourthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fourthTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fifthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fifthTransactionPointValue.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        fourthTransactionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstTransactionPoint.setChecked(false);
                    secondTransactionPoint.setChecked(false);
                    thirdTransactionPoint.setChecked(false);
                    fifthTransactionPoint.setChecked(false);

                    fourthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.active_green));
                    fourthTransactionPointValue.setTypeface(null, Typeface.BOLD);


                    firstTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    firstTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    secondTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    secondTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    thirdTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    thirdTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fifthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fifthTransactionPointValue.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        fifthTransactionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstTransactionPoint.setChecked(false);
                    secondTransactionPoint.setChecked(false);
                    thirdTransactionPoint.setChecked(false);
                    fourthTransactionPoint.setChecked(false);

                    fifthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.active_green));
                    fifthTransactionPointValue.setTypeface(null, Typeface.BOLD);


                    firstTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    firstTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    secondTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    secondTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    thirdTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    thirdTransactionPointValue.setTypeface(null, Typeface.NORMAL);

                    fourthTransactionPointValue.setTextColor(mainActivity.getResources().getColor(R.color.normal_black));
                    fourthTransactionPointValue.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                decreaseButton.setClickable(false);
                decreaseButton.setEnabled(false);

                if (isTransactionPointValuesInitialized && isTransactionPointsInitialGapSequenceInitialized) {
                    if (firstTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(0) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                decreaseButton.setClickable(true);
                                decreaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int firstValue = Integer.valueOf(firstTransactionPointValue.getText().toString());
                        if (firstValue > 1) {

                            final int originalFirstGapValue = Integer.valueOf(firstGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    firstTransactionPointValue.setText(String.valueOf(firstValue - 1));
                                    firstGapValue.setText(String.valueOf(originalFirstGapValue + 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(0, firstValue - 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(0, originalFirstGapValue + 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (secondTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(1) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                decreaseButton.setClickable(true);
                                decreaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int firstValue = Integer.valueOf(firstTransactionPointValue.getText().toString());
                        final int secondValue = Integer.valueOf(secondTransactionPointValue.getText().toString());
                        if (secondValue - 1 >= firstValue + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalFirstGapValue = Integer.valueOf(firstGapValue.getText().toString());
                            final int originalSecondGapValue = Integer.valueOf(secondGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    secondTransactionPointValue.setText(String.valueOf(secondValue - 1));
                                    firstGapValue.setText(String.valueOf(originalFirstGapValue - 1));
                                    secondGapValue.setText(String.valueOf(originalSecondGapValue + 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(1, secondValue - 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(0, originalFirstGapValue - 1);
                                syncAndTradeService.setSpecificTransactionPointGapValue(1, originalSecondGapValue + 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (thirdTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(2) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                decreaseButton.setClickable(true);
                                decreaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int secondValue = Integer.valueOf(secondTransactionPointValue.getText().toString());
                        final int thirdValue = Integer.valueOf(thirdTransactionPointValue.getText().toString());
                        if (thirdValue - 1 >= secondValue + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalSecondGapValue = Integer.valueOf(secondGapValue.getText().toString());
                            final int originalThirdGapValue = Integer.valueOf(thirdGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    thirdTransactionPointValue.setText(String.valueOf(thirdValue - 1));
                                    secondGapValue.setText(String.valueOf(originalSecondGapValue - 1));
                                    thirdGapValue.setText(String.valueOf(originalThirdGapValue + 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(2, thirdValue - 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(1, originalSecondGapValue - 1);
                                syncAndTradeService.setSpecificTransactionPointGapValue(2, originalThirdGapValue + 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (fourthTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(3) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                decreaseButton.setClickable(true);
                                decreaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int thirdValue = Integer.valueOf(thirdTransactionPointValue.getText().toString());
                        final int fourthValue = Integer.valueOf(fourthTransactionPointValue.getText().toString());
                        if (fourthValue - 1 >= thirdValue + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalThirdGapValue = Integer.valueOf(thirdGapValue.getText().toString());
                            final int originalFourthGapValue = Integer.valueOf(fourthGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fourthTransactionPointValue.setText(String.valueOf(fourthValue - 1));
                                    thirdGapValue.setText(String.valueOf(originalThirdGapValue - 1));
                                    fourthGapValue.setText(String.valueOf(originalFourthGapValue + 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(3, fourthValue - 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(2, originalThirdGapValue - 1);
                                syncAndTradeService.setSpecificTransactionPointGapValue(3, originalFourthGapValue + 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (fifthTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(4) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                decreaseButton.setClickable(true);
                                decreaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int fourthValue = Integer.valueOf(fourthTransactionPointValue.getText().toString());
                        final int fifthValue = Integer.valueOf(fifthTransactionPointValue.getText().toString());
                        if (fifthValue - 1 >= fourthValue + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalFourthGapValue = Integer.valueOf(fourthGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fifthTransactionPointValue.setText(String.valueOf(fifthValue - 1));
                                    fourthGapValue.setText(String.valueOf(originalFourthGapValue - 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(4, fifthValue - 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(3, originalFourthGapValue - 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(mainActivity.getApplicationContext(), "Initializing...", Toast.LENGTH_LONG).show();
                }

                decreaseButton.setClickable(true);
                decreaseButton.setEnabled(true);
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increaseButton.setClickable(false);
                increaseButton.setEnabled(false);

                if (isTransactionPointValuesInitialized && isTransactionPointsInitialGapSequenceInitialized) {
                    if (firstTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(0) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                increaseButton.setClickable(true);
                                increaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int firstValue = Integer.valueOf(firstTransactionPointValue.getText().toString());
                        final int secondValue = Integer.valueOf(secondTransactionPointValue.getText().toString());
                        if (firstValue + 1 <= secondValue - Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalFirstGapValue = Integer.valueOf(firstGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    firstTransactionPointValue.setText(String.valueOf(firstValue + 1));
                                    firstGapValue.setText(String.valueOf(originalFirstGapValue - 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(0, firstValue + 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(0, originalFirstGapValue - 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (secondTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(1) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                increaseButton.setClickable(true);
                                increaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int secondValue = Integer.valueOf(secondTransactionPointValue.getText().toString());
                        final int thirdValue = Integer.valueOf(thirdTransactionPointValue.getText().toString());
                        if (secondValue + 1 <= thirdValue - Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalFirstGapValue = Integer.valueOf(firstGapValue.getText().toString());
                            final int originalSecondGapValue = Integer.valueOf(secondGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    secondTransactionPointValue.setText(String.valueOf(secondValue + 1));
                                    firstGapValue.setText(String.valueOf(originalFirstGapValue + 1));
                                    secondGapValue.setText(String.valueOf(originalSecondGapValue - 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(1, secondValue + 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(0, originalFirstGapValue + 1);
                                syncAndTradeService.setSpecificTransactionPointGapValue(1, originalSecondGapValue - 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (thirdTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(2) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                increaseButton.setClickable(true);
                                increaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int thirdValue = Integer.valueOf(thirdTransactionPointValue.getText().toString());
                        final int fourthValue = Integer.valueOf(fourthTransactionPointValue.getText().toString());
                        if (thirdValue + 1 <= fourthValue - Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalSecondGapValue = Integer.valueOf(secondGapValue.getText().toString());
                            final int originalThirdGapValue = Integer.valueOf(thirdGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    thirdTransactionPointValue.setText(String.valueOf(thirdValue + 1));
                                    secondGapValue.setText(String.valueOf(originalSecondGapValue + 1));
                                    thirdGapValue.setText(String.valueOf(originalThirdGapValue - 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(2, thirdValue + 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(1, originalSecondGapValue + 1);
                                syncAndTradeService.setSpecificTransactionPointGapValue(2, originalThirdGapValue - 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (fourthTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(3) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                increaseButton.setClickable(true);
                                increaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int fourthValue = Integer.valueOf(fourthTransactionPointValue.getText().toString());
                        final int fifthValue = Integer.valueOf(fifthTransactionPointValue.getText().toString());
                        if (fourthValue + 1 <= fifthValue - Constants.MINIMUM_GAP_BETWEEN_TWO_POINT) {

                            final int originalThirdGapValue = Integer.valueOf(thirdGapValue.getText().toString());
                            final int originalFourthGapValue = Integer.valueOf(fourthGapValue.getText().toString());

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fourthTransactionPointValue.setText(String.valueOf(fourthValue + 1));
                                    thirdGapValue.setText(String.valueOf(originalThirdGapValue + 1));
                                    fourthGapValue.setText(String.valueOf(originalFourthGapValue - 1));
                                }
                            });

                            try {
                                syncAndTradeService.setValueForSpecificTransactionPoint(3, fourthValue + 1);

                                syncAndTradeService.setSpecificTransactionPointGapValue(2, originalThirdGapValue + 1);
                                syncAndTradeService.setSpecificTransactionPointGapValue(3, originalFourthGapValue - 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mainActivity.getApplicationContext(), "Gap must be bigger than " + Constants.MINIMUM_GAP_BETWEEN_TWO_POINT, Toast.LENGTH_LONG).show();
                        }
                    } else if (fifthTransactionPoint.isChecked()) {

                        try {
                            if (syncAndTradeService.getWhetherCanProcessIndicator(4) == 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), "Incomplete order on this point", Toast.LENGTH_LONG).show();

                                increaseButton.setClickable(true);
                                increaseButton.setEnabled(true);

                                return;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        final int fifthValue = Integer.valueOf(fifthTransactionPointValue.getText().toString());
                        final int originalFourthGapValue = Integer.valueOf(fourthGapValue.getText().toString());

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fifthTransactionPointValue.setText(String.valueOf(fifthValue + 1));
                                fourthGapValue.setText(String.valueOf(originalFourthGapValue + 1));
                            }
                        });

                        try {
                            syncAndTradeService.setValueForSpecificTransactionPoint(4, fifthValue + 1);

                            syncAndTradeService.setSpecificTransactionPointGapValue(3, originalFourthGapValue + 1);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Toast.makeText(mainActivity.getApplicationContext(), "Initializing...", Toast.LENGTH_LONG).show();
                }

                increaseButton.setClickable(true);
                increaseButton.setEnabled(true);
            }
        });

    }

    private void initTransactionPointsInitialGapSequenceUI() {

        Callable<String[]> callable = new Callable<String[]>() {
            @Override
            public String[] call() throws Exception {
                return syncAndTradeService.getTransactionPointsInitialGapSequence();
            }
        };

        Future<String[]> future = mainActivityUIScheduledExecutorService.submit(callable);

        try {
            final String[] transactionPointsInitialGapSequence = future.get();

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    firstGapValue.setText(transactionPointsInitialGapSequence[0]);
                    secondGapValue.setText(transactionPointsInitialGapSequence[1]);
                    thirdGapValue.setText(transactionPointsInitialGapSequence[2]);
                    fourthGapValue.setText(transactionPointsInitialGapSequence[3]);
                }
            });

            isTransactionPointsInitialGapSequenceInitialized = true;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void handelStartStopRobotButtonUI() {

        startRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRobotStarted) {
                    try {
                        syncAndTradeService.startTradeEngine();

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startRobot.setText(mainActivity.getResources().getString(R.string.stop_robot));
                                startRobot.setBackground(mainActivity.getResources().getDrawable(R.drawable.custom_warning_confirm_button));
                            }
                        });

                        isRobotStarted = true;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        syncAndTradeService.stopTradeEngine();

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startRobot.setText(mainActivity.getResources().getString(R.string.start_robot));
                                startRobot.setBackground(mainActivity.getResources().getDrawable(R.drawable.custom_normal_confirm_button));
                            }
                        });

                        isRobotStarted = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
