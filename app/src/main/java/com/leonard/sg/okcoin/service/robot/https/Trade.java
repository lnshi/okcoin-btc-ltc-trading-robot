package com.leonard.sg.okcoin.service.robot.https;

import com.google.gson.GsonBuilder;
import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.data.from.internet.RMBAccountInfo;
import com.leonard.sg.okcoin.service.robot.data.from.local.CommonStructure;
import com.leonard.sg.okcoin.service.robot.model.Order;
import com.leonard.sg.okcoin.service.robot.model.response.OrderStatusInfoSuccess;
import com.leonard.sg.okcoin.service.robot.model.response.PlaceOrderInfoSuccess;
import com.leonard.sg.okcoin.service.robot.strategy.coverage.TransactionVolume;
import com.leonard.sg.okcoin.service.robot.util.TradeRelatedUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class Trade extends Observable {

    private Order order;

    public Trade(Order order, Observer tradeThreadObserver) {
        this.order = order;
        addObserver(tradeThreadObserver);
    }

    public void tradeController() {

        if (checkOrderData()) {
            while (true) {
                if (placeOrder()) {

                    whenPlaceOrderSuccessfully();

                    while (true) {

                        OrderStatusInfoSuccess orderStatusInfoSuccess = Query.querySingleOrderStatus(order.getSymbol(), order.getOrderId());

                        if (orderStatusInfoSuccess != null) {

                            if (order.getType().equals(SyncConstants.ORDER_TYPE.sell)) {

                                while (true) {
                                    if (Query.queryUserAccountInfo()) {
                                        TransactionVolume.calculateTransactionVolume();
                                        tradeCompleted();
                                        return;
                                    } else {
                                        try {
                                            Thread.sleep(SyncConstants.INCOMPLETE_TRANSACTION_THREAD_SLEEP_INTERVAL_IN_MILLISECOND);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            } else {
                                tradeCompleted();
                            }

                        } else {
                            try {
                                Thread.sleep(SyncConstants.INCOMPLETE_TRANSACTION_THREAD_SLEEP_INTERVAL_IN_MILLISECOND);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
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

    }

    private void whenPlaceOrderSuccessfully() {
        if (order.getType().equals(SyncConstants.ORDER_TYPE.buy)) {
            BigDecimal orderRMBAmount = order.getPrice().multiply(order.getAmount()).setScale(SyncConstants.RMB_ACCURACY, RoundingMode.UP);
            RMBAccountInfo.setRMBAvl(orderRMBAmount.negate());
            RMBAccountInfo.setRMBFrozen(orderRMBAmount);
        }
    }

    private boolean checkOrderData() {

        if (order.getSymbol() == null) {
            return  false;
        }

        if (order.getType().equals(SyncConstants.ORDER_TYPE.buy) || order.getType().equals(SyncConstants.ORDER_TYPE.sell)) {
            if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0 || order.getPrice().compareTo(new BigDecimal("1000000")) > 0) {
                return false;
            }

            if (order.getSymbol().equals(SyncConstants.COIN_TYPE.btc_cny)) {
                if (order.getAmount().compareTo(SyncConstants.BTC_MINIMUM_ORDER_AMOUNT) < 0) {
                    return false;
                }
            } else {
                if (order.getAmount().compareTo(SyncConstants.LTC_MINIMUM_ORDER_AMOUNT) < 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean placeOrder() {

        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");

        HttpClient httpClient = new HttpClient();

        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);

        PostMethod postMethod = new PostMethod(SyncConstants.API_PLACE_AN_ORDER);

        postMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);

        Map<String, String> inputMap = new HashMap<String, String>();

        inputMap.put("api_key", SyncConstants.CN_API_KEY);
        inputMap.put("symbol", order.getSymbol().toString());
        inputMap.put("type", order.getType().toString());
        inputMap.put("price", order.getPrice().toString());
        inputMap.put("amount", order.getAmount().toString());

        String mySign = TradeRelatedUtil.getMySign(inputMap);

        NameValuePair[] data = new NameValuePair[6];

        data[0] = new NameValuePair("api_key", SyncConstants.CN_API_KEY);
        data[1] = new NameValuePair("symbol", order.getSymbol().toString());
        data[2] = new NameValuePair("type", order.getType().toString());
        data[3] = new NameValuePair("price", order.getPrice().toString());
        data[4] = new NameValuePair("amount", order.getAmount().toString());
        data[5] = new NameValuePair("sign", mySign);

        postMethod.setRequestBody(data);

        int statusCode = -1;

        try {
            statusCode = httpClient.executeMethod(postMethod);
        } catch (NoRouteToHostException | UnknownHostException e) {
            // Network issues, we can do something here
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (statusCode != 200) {
            return false;
        }

        String orderInfoStr = null;

        try {
            StringWriter orderInfoWriter = new StringWriter();
            IOUtils.copy(postMethod.getResponseBodyAsStream(), orderInfoWriter, "UTF-8");
            orderInfoStr = orderInfoWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (orderInfoStr != null && orderInfoStr.contains("true")) {
            PlaceOrderInfoSuccess placeOrderInfoSuccess = new GsonBuilder().create().fromJson(orderInfoStr, PlaceOrderInfoSuccess.class);

            order.setOrderId(placeOrderInfoSuccess.getOrderId());
            CommonStructure.SUCCESSFUL_ORDERS.offer(order);

            return true;
        }

        return false;

    }

    private void tradeCompleted() {
        setChanged();
        notifyObservers(order);
    }

}
