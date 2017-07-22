package com.leonard.sg.okcoin.service.robot.util;

import com.leonard.sg.okcoin.service.robot.constant.SyncConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by leonard on 18/4/15.
 */
public class TradeRelatedUtil {

    /*
     * 返回MD5加密后的32位大写密钥值
     */
    public static String getMD5String(String str) {

        if (str == null || str.trim().length() == 0) {
            return "";
        }

        byte[] bytes = str.getBytes();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(bytes);
        bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i ++) {
            sb.append(SyncConstants.HEX_DIGITS[(bytes[i] & 0xf0) >> 4] + "" + SyncConstants.HEX_DIGITS[bytes[i] & 0xf]);
        }

        return sb.toString();
    }

    /*
     * 按照平台要求返回排序并连接后的参数串
     */
    public static String getLinkedString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String preString = "";

        for (int i = 0; i < keys.size(); i ++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {
                preString = preString + key + "=" + value;
            } else {
                preString = preString + key + "=" + value + "&";
            }
        }

        return preString;
    }

    /*
     * 获得最终的经过签名的要传输的字符串
     */
    public static String getMySign(Map<String, String> sArray) {
        return getMD5String(getLinkedString(sArray) + "&secret_key=" + SyncConstants.CN_API_SECRET);
    }

}
