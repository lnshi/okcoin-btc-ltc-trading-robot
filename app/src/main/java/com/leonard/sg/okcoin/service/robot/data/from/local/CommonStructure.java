package com.leonard.sg.okcoin.service.robot.data.from.local;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.model.Order;

import java.util.concurrent.ArrayBlockingQueue;


public class CommonStructure {

    public static int BTC_PRICE_QUERY_TIMES = 0;

    public static int ROBOT_CHECK_TIMES = 0;

    public static ArrayBlockingQueue<Order> SUCCESSFUL_ORDERS = new ArrayBlockingQueue<Order>(SyncConstants.MAX_THREAD_NUMBER);

}
