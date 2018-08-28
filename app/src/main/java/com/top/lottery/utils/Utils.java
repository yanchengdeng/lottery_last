package com.top.lottery.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lzy.okgo.model.Response;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {


    ///遗漏值
    static LinkedTreeMap<String, String> missValus = new LinkedTreeMap<String, String>();

    public static String getImei() {
        return SPUtils.getInstance().getString(Constants.IMEI, "354765086202488");
    }

    public static void setImei(String imei) {
        if (!TextUtils.isEmpty(imei)) {
            SPUtils.getInstance().put(Constants.IMEI, imei);
        } else {
            //TODO  有些设备imei 获取不到怎么办？
            SPUtils.getInstance().put(Constants.IMEI, "354765086202488");
        }
    }


    /***
     * 打开一个新界面
     *
     * @param pClass
     */
    public static void openActivity(Activity activity, Class<?> pClass) {
        openActivity(activity, pClass, null, 0);
    }


    /***
     * 打开一个新界面
     *alipay_ic.png
     * @param pClass
     */
    public static void openActivity(Activity activity, Class<?> pClass, Bundle pBundle) {
        openActivity(activity, pClass, pBundle, 0);
    }

    /***
     * 打开新界面
     *
     * @param pClass
     * @param requestCode 请求码
     */
    public static void openActivity(Activity activity, Class<?> pClass, int requestCode) {
        openActivity(activity, pClass, null, requestCode);
    }

    /***
     * 打开新界面
     *
     * @param pClass      界面类
     * @param pBundle     携带参数
     * @param requestCode 请求码
     */
    public static void openActivity(Activity mContext, Class<?> pClass, Bundle pBundle, int requestCode) {
        Intent intent = new Intent(mContext, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(mContext, intent, requestCode);
    }

    /***
     * 开启新界面
     *
     * @param context     Context 对象
     * @param intent      意向
     * @param requestCode 请求码 0代表未有返回码
     */
    public static void startActivity(Activity context, Intent intent, int requestCode) {
        if (requestCode == 0) {
            context.startActivity(intent);
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }


    public static HashMap<String, String> getParams(HashMap<String, String> data) {
        data.put("version", "1.0");
        data.put("app_imei", getImei());
        data.put("app_id", Constants.APP_ID);
        data.put("app_sign", getAppSign());
        if (!TextUtils.isEmpty(getToken())) {
            data.put("app_token", getToken());
        }
        if (Constants.DEBUG) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("?");
            for (String key : data.keySet()) {
                stringBuffer.append(key).append("=").append(data.get(key)).append("&");
            }
            LogUtils.d("dyc", stringBuffer.toString());
        }
        return data;
    }

    private static String getAppSign() {
        StringBuilder sb = new StringBuilder();
        sb.append(getImei()).append(Constants.APP_SERCET).append(Constants.APP_ID);
        return DataConvertUtils.MD5(sb.toString()).substring(0, 20);
    }


    private static String getToken() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null && userInfo.tokens != null) {
            String json = new Gson().toJson(userInfo.tokens);
            return new String(EncodeUtils.base64Encode(json));
        } else {
            return "";
        }
    }


    //加载类通用提示
    public static String toastInfo(Response<LotteryResponse<Object>> response) {
        if (response.getException() == null || TextUtils.isEmpty(response.getException().getMessage())) {
            return "当前网络环境较差，请检查您的网络";
        }
        String toast = response.getException().getMessage();
        if (toast.startsWith("Unable to resolve host") || toast.startsWith("java.lang.IllegalStateException: Expected BEGIN_OBJECT") || toast.startsWith("Failed to connect") || toast.startsWith("failed to connect") || toast.startsWith("network")) {
            toast = "加载失败，似乎断网了~";
        } else if (toast.contains("time")) {
            toast = "当前网络环境较差，请检查您的网络";
        } else if (toast.contains("TimeoutException")) {
            toast = "当前网络环境较差，请检查您的网络";
        }
        return toast;
    }


    /***
     * 用户信息
     * @return
     */
    public static UserInfo getUserInfo() {
        String useinfo = SPUtils.getInstance().getString(Constants.USER_INFO);
        if (TextUtils.isEmpty(useinfo)) {
            return new UserInfo();
        } else {
            UserInfo userInfo = new Gson().fromJson(useinfo, UserInfo.class);
            return userInfo;
        }
    }

    /**
     * 登陆状态
     *
     * @return
     */
    public static boolean isLogin() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.uid)) {
            return true;
        } else {
            return false;
        }
    }


    public static String millis2FitTimeSpan(long millis, int precision) {
        if (precision <= 0) return null;
        precision = Math.min(precision, 5);
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        if (millis == 0) return 0 + units[precision - 1];
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            sb.append("-");
            millis = -millis;
        }
        int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
        for (int i = 0; i < precision; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= mode * unitLen[i];
                sb.append(mode).append(units[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 为首页 提供时间
     *
     * @param millis
     * @param precision
     * @return
     */
    public static int[] millis2FitTimeSpanForMain(long millis, int precision) {
        if (precision <= 0) return null;
        precision = Math.min(precision, 5);
        String[] units = {":", ":", ":", ":", "毫秒"};
        if (millis == 0) return new int[]{0, 0, 0, 0};
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            sb.append("-");
            millis = -millis;
        }
        int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
        for (int i = 0; i < precision; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= mode * unitLen[i];
                sb.append(mode).append(units[i]);
            }
        }

        String[] times = sb.toString().split(":");
        //分：秒[0,0,0,0]
        if (times.length == 2) {
            int[] mins = new int[4];
            if (Integer.parseInt(times[0]) < 10) {
                mins[0] = 0;
                mins[1] = Integer.parseInt(times[0]);
            } else {
                mins[0] = Integer.parseInt(times[0]) / 10;
                mins[1] = Integer.parseInt(times[0]) % 10;
            }


            if (Integer.parseInt(times[1]) < 10) {
                mins[2] = 0;
                mins[3] = Integer.parseInt(times[1]);
            } else {
                mins[2] = Integer.parseInt(times[1]) / 10;
                mins[3] = Integer.parseInt(times[1]) % 10;
            }
            return mins;
        } else if (times.length == 1) {

            //秒
            int[] seconds = new int[4];
            seconds[0] = 0;
            seconds[1] = 0;

            if (TextUtils.isEmpty(times[0])) {
                seconds[2] = 0;
                seconds[3] = 0;
            } else {
                if (Integer.parseInt(times[0]) < 10) {
                    seconds[2] = 0;
                    seconds[3] = Integer.parseInt(times[0]);
                } else {
                    seconds[2] = Integer.parseInt(times[0]) / 10;
                    seconds[3] = Integer.parseInt(times[0]) % 10;
                }
            }
            return seconds;
        } else if (times.length == 3) {
            //时：分：秒
            int[] hourss = new int[6];
            if (Integer.parseInt(times[0]) < 10) {
                hourss[0] = 0;
                hourss[1] = Integer.parseInt(times[0]);
            } else {
                hourss[0] = Integer.parseInt(times[0]) / 10;
                hourss[1] = Integer.parseInt(times[0]) % 10;
            }

            if (Integer.parseInt(times[1]) < 10) {
                hourss[2] = 0;
                hourss[3] = Integer.parseInt(times[1]);
            } else {
                hourss[2] = Integer.parseInt(times[1]) / 10;
                hourss[3] = Integer.parseInt(times[1]) % 10;
            }

            if (Integer.parseInt(times[2]) < 10) {
                hourss[4] = 0;
                hourss[5] = Integer.parseInt(times[2]);
            } else {
                hourss[4] = Integer.parseInt(times[2]) / 10;
                hourss[5] = Integer.parseInt(times[2]) % 10;
            }
            return hourss;

        }

        return new int[]{0, 0, 0, 0};
    }


    //十一个球 序列
    public static List<AwardBallInfo> get11Code() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        for (int i = 1; i < 12; i++) {
            AwardBallInfo awardBallInfo = new AwardBallInfo();
            if (i < 10) {
                awardBallInfo.value = "0" + i;
            } else {
                awardBallInfo.value = String.valueOf(i);
            }
            awardBallInfos.add(awardBallInfo);
        }
        return awardBallInfos;
    }


    /**
     * 计算组合数，即C(n, m) = n!/((n-m)! * m!)
     *
     * @param n
     * @param m
     * @return
     */
    public static int combination(int n, int m) {
        return (n >= m) ? factorial(n) / factorial(n - m) / factorial(m) : 0;
    }

    /**
     * 计算阶乘数，即n! = n * (n-1) * ... * 2 * 1
     *
     * @param n
     * @return
     */
    public static int factorial(int n) {
        return (n > 1) ? n * factorial(n - 1) : 1;
    }


    //遗漏值 转化为 键值对形式
    public static LinkedTreeMap<String, String> parseMissValue(LinkedTreeMap<String, String> missValues) {
        missValus  = missValues;
        return missValus;
    }

    public static LinkedTreeMap<String, String> getMissValues() {
        return missValus;
    }
}
