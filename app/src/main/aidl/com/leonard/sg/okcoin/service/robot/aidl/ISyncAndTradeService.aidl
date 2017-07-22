package com.leonard.sg.okcoin.service.robot.aidl;

interface ISyncAndTradeService {

    void startTradeEngine();
    void stopTradeEngine();

    String[] getRMBAccountInfo();
    String[] getBTCAccountInfo();

    String getCurrentBTCPrice();
    int getPriceQueryTimes();

    String getTotalProfit();

    int getRobotCheckTimes();

    String[] getVolumeForTransactionPoints();

    void setIndicatorForSpecificTransactionPoint(in int index, in int value);

    String[] getValueOfTransactionPoints();

    void setValueForSpecificTransactionPoint(in int index, in int value);

    String[] getTransactionPointsInitialGapSequence();

    void setSpecificTransactionPointGapValue(in int index, in int value);

    int getWhetherCanProcessIndicator(in int index);

}
