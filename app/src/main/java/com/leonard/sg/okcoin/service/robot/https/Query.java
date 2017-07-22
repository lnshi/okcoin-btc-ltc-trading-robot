package com.leonard.sg.okcoin.service.robot.https;

import com.google.gson.GsonBuilder;
import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCAccountInfo;
import com.leonard.sg.okcoin.service.robot.data.from.internet.BTCCurrentPrice;
import com.leonard.sg.okcoin.service.robot.data.from.internet.RMBAccountInfo;
import com.leonard.sg.okcoin.service.robot.model.response.BTCPriceInfoSuccess;
import com.leonard.sg.okcoin.service.robot.model.response.OrderStatusInfoSuccess;
import com.leonard.sg.okcoin.service.robot.model.response.UserAccountInfoSuccess;
import com.leonard.sg.okcoin.service.robot.util.TradeRelatedUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class Query {

    public static boolean queryUserAccountInfo() {

        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");

        HttpClient httpClient = new HttpClient();

        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);

        PostMethod postMethod = new PostMethod(SyncConstants.API_QUERY_USER_ACCOUNT_INFO);
        postMethod.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);

        Map<String, String> baseInputMap = new HashMap<String, String>();
        baseInputMap.put("api_key", SyncConstants.CN_API_KEY);

        String mySign = TradeRelatedUtil.getMySign(baseInputMap);

        NameValuePair[] data = {
            new NameValuePair("api_key", SyncConstants.CN_API_KEY),
            new NameValuePair("sign", mySign)
        };

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

        String userAccountInfoStr = null;

        try {
            StringWriter userAccountInfoWriter = new StringWriter();
            IOUtils.copy(postMethod.getResponseBodyAsStream(), userAccountInfoWriter, "UTF-8");
            userAccountInfoStr = userAccountInfoWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (userAccountInfoStr != null && userAccountInfoStr.contains("true")) {
            UserAccountInfoSuccess userAccountInfoSuccess = new GsonBuilder().create().fromJson(userAccountInfoStr, UserAccountInfoSuccess.class);

            RMBAccountInfo.setRMBAccountInfo(userAccountInfoSuccess.getRMBSum(), userAccountInfoSuccess.getRMBAvl(), userAccountInfoSuccess.getRMBFrozen());

            BTCAccountInfo.setBTCAccountInfo(userAccountInfoSuccess.getBTCSum(), userAccountInfoSuccess.getBTCAvl(), userAccountInfoSuccess.getBTCFrozen());

            return true;
        }

        return false;

    }

    public static boolean queryBTCCurrentPrice() {

        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");

        HttpClient httpClient = new HttpClient();

        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);

        GetMethod getMethod = new GetMethod(SyncConstants.API_QUERY_BTC_CURRENT_PRICE);
        getMethod.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);

        int statusCode = -1;

        try {
            statusCode = httpClient.executeMethod(getMethod);
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

        String BTCCurrentInfoStr = null;

        try {
            StringWriter BTCCurrentInfoWriter = new StringWriter();
            IOUtils.copy(getMethod.getResponseBodyAsStream(), BTCCurrentInfoWriter, "UTF-8");
            BTCCurrentInfoStr = BTCCurrentInfoWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BTCCurrentInfoStr != null && BTCCurrentInfoStr.contains("ticker")) {
            BTCPriceInfoSuccess btcPriceInfoSuccess = new GsonBuilder().create().fromJson(BTCCurrentInfoStr, BTCPriceInfoSuccess.class);

            BTCCurrentPrice.setBTCCurrentPrice(btcPriceInfoSuccess.getBTCCurrentPrice());

            return true;
        }

        return false;

    }

    public static OrderStatusInfoSuccess querySingleOrderStatus(SyncConstants.COIN_TYPE symbol, String orderId) {

        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");

        HttpClient httpClient = new HttpClient();

        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);

        PostMethod postMethod = new PostMethod(SyncConstants.API_QUERY_ORDER_STATUS);

        postMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);

        Map<String, String> inputMap = new HashMap<String, String>();

        inputMap.put("api_key", SyncConstants.CN_API_KEY);

        inputMap.put("symbol", symbol.toString());
        inputMap.put("order_id", orderId);

        String mySign = TradeRelatedUtil.getMySign(inputMap);

        NameValuePair[] data = new NameValuePair[4];
        data[0] = new NameValuePair("api_key", SyncConstants.CN_API_KEY);
        data[1] = new NameValuePair("symbol", symbol.toString());
        data[2] = new NameValuePair("order_id", orderId);
        data[3] = new NameValuePair("sign", mySign);

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
            return null;
        }

        String orderStatusInfoStr = null;

        try {
            StringWriter orderStatusInfoWriter = new StringWriter();
            IOUtils.copy(postMethod.getResponseBodyAsStream(), orderStatusInfoWriter, "UTF-8");
            orderStatusInfoStr = orderStatusInfoWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (orderStatusInfoStr != null && orderStatusInfoStr.contains("true")) {
            OrderStatusInfoSuccess orderStatusInfoSuccess = new GsonBuilder().create().fromJson(orderStatusInfoStr, OrderStatusInfoSuccess.class);

            if (orderStatusInfoSuccess.getOrderStatus() == 2) {
                return  orderStatusInfoSuccess;
            }

        }

        return null;

    }

}
