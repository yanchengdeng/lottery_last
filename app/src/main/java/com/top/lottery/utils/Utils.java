package com.top.lottery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lzy.okgo.model.Response;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.beans.MainWinCode;
import com.top.lottery.beans.TerdNormalBall;
import com.top.lottery.beans.TrendCodeInfo;
import com.top.lottery.beans.UseAuth;
import com.top.lottery.beans.UserInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {


    ///遗漏值
    static LinkedTreeMap<String, String> missValus = new LinkedTreeMap<String, String>();
    public static Activity context;

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
        data.put("version", AppUtils.getAppVersionName());
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
        String json = SPUtils.getInstance().getString(Constants.TOKEN);
        if (!TextUtils.isEmpty(json)) {
            return new String(EncodeUtils.base64Encode(json));
        } else {
            return "";
        }
    }


    //加载类通用提示
    public static String toastInfo(Response<LotteryResponse<LotteryResponse>> response) {
        if (response.getException() == null || TextUtils.isEmpty(response.getException().getMessage())) {
            return "当前网络环境较差，请检查您的网络";
        }
        String toast = response.getException().getMessage();
        try {
            LotteryResponse info = new Gson().fromJson(toast, LotteryResponse.class);
            if (info != null) {
                if (TextUtils.isEmpty(info.msg)) {
                    return "稍后再试";
                } else {
                    if (info.code == -99) {
                        return "-99";
                    } else {
                        return info.msg;
                    }
                }
            }
        } catch (Exception ex) {

        }

        return "稍后再试";
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


    /***
     * 用户信息
     * @return
     */
    public static void saveUserInfo(UserInfo userInfo) {
        String userinfo = new Gson().toJson(userInfo);
        SPUtils.getInstance().put(Constants.USER_INFO, userinfo);
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

    //十一个球 序列   再加一个空球
    public static List<MainWinCode> get12Code(int colorMipmap) {
        List<MainWinCode> awardBallInfos = new ArrayList<>();
        MainWinCode empty = new MainWinCode();
        empty.code = " ";
        empty.codeBg = colorMipmap;
        awardBallInfos.add(empty);
        for (int i = 1; i < 12; i++) {
            MainWinCode awardBallInfo = new MainWinCode();
            if (i < 10) {
                awardBallInfo.code = "0" + i;
            } else {
                awardBallInfo.code = String.valueOf(i);
            }
            awardBallInfo.codeBg = colorMipmap;
            awardBallInfos.add(awardBallInfo);
        }
        return awardBallInfos;
    }

//
//    //十一个球 序列
//    public static List<AwardBallInfo> get11Code() {
//        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
//        for (int i = 1; i < 12; i++) {
//            AwardBallInfo awardBallInfo = new AwardBallInfo();
//            if (i < 10) {
//                awardBallInfo.value = "0" + i;
//            } else {
//                awardBallInfo.value = String.valueOf(i);
//            }
//            awardBallInfos.add(awardBallInfo);
//        }
//        return awardBallInfos;
//    }


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
        missValus = missValues;
        return missValus;
    }

    public static LinkedTreeMap<String, String> getMissValues() {
        return missValus;
    }

    //获取走势图数据

    /**
     * @param trendCodeInfo
     * @param hideShow      :hide   show
     * @return
     */
    public static List<TerdNormalBall> get11CodeForTrend(TrendCodeInfo trendCodeInfo, String hideShow) {
        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();

        TerdNormalBall awardBallInfoFist = new TerdNormalBall();
        awardBallInfoFist.awardId = trendCodeInfo.award_id.substring(trendCodeInfo.award_id.length() - 2, trendCodeInfo.award_id.length());
        terdNormalBalls.add(awardBallInfoFist);

        for (int i = 1; i < 12; i++) {
            TerdNormalBall awardBallInfo = new TerdNormalBall();
            if (i < 10) {
                awardBallInfo.value = "0" + i;
            } else {
                awardBallInfo.value = String.valueOf(i);
            }
            awardBallInfo.missVlaue = trendCodeInfo.missing_value.get(awardBallInfo.value);
            awardBallInfo.isShowMiss = hideShow.equals("show") ? true : false;
            awardBallInfo.isAwardCode = getIsAwardValue(awardBallInfo.value, trendCodeInfo.prize_code);
            awardBallInfo.isTopThree = getisTopThree(awardBallInfo.value, trendCodeInfo.prize_code);

            terdNormalBalls.add(awardBallInfo);
        }


        return terdNormalBalls;
    }


    //获取统计球的信息
    public static List<TerdNormalBall> get11CodeForTrendCount(String name, LinkedTreeMap<String, String> number) {
        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();

        TerdNormalBall awardBallInfoFist = new TerdNormalBall();
        awardBallInfoFist.awardId = name;
        terdNormalBalls.add(awardBallInfoFist);


        for (int i = 1; i < 12; i++) {
            TerdNormalBall awardBallInfo = new TerdNormalBall();
            if (i < 10) {
                awardBallInfo.value = "0" + i;
            } else {
                awardBallInfo.value = String.valueOf(i);
            }
            awardBallInfo.missVlaue = number.get(awardBallInfo.value);
            awardBallInfo.isAwardCode = false;
            awardBallInfo.isCount = true;

            terdNormalBalls.add(awardBallInfo);
        }

        return terdNormalBalls;


    }

    //是否是选中的值
    private static boolean getIsAwardValue(String value, String[] prize_code) {
        boolean isAward = false;

        for (String item : prize_code) {
            if (value.equals(item)) {
                isAward = true;
            }
        }
        return isAward;
    }

    //是否是选中的值
    private static boolean getisTopThree(String value, String[] prize_code) {
        boolean isTopsThree = false;

        for (String item : prize_code) {
            if (value.equals(prize_code[0]) || value.equals(prize_code[1]) || value.equals(prize_code[2])) {
                isTopsThree = true;
            }
        }
        return isTopsThree;
    }

    /***
     * 用户权限
     * @return
     */
    public static UseAuth getAuth() {
        String useauth = SPUtils.getInstance().getString(Constants.USER_AUTH);
        if (TextUtils.isEmpty(useauth)) {
            return new UseAuth();
        } else {
            UseAuth useAuth = new Gson().fromJson(useauth, UseAuth.class);
            return useAuth;
        }
    }


    /***
     * 用户权限
     * @return
     */
    public static void saveUserAuth(UseAuth useAuth) {
        String useauth = new Gson().toJson(useAuth);
        SPUtils.getInstance().put(Constants.USER_AUTH, useauth);
    }

    public static List<LotteryType> gePeriData() {
        String[] dates = new String[]{"today", "week", "month", "three_month"};
        String[] dates_info = new String[]{"当天", "近一周", "近一月", "近三月"};
        List<LotteryType> LotteryTypes = new ArrayList<>();
        for (int i = 0; i < dates.length; i++) {
            LotteryType lotteryType = new LotteryType();
            lotteryType.title = dates_info[i];
            lotteryType.lottery_type = dates[i];
            if (i == 1) {
                lotteryType.isSelect = true;
            }
            LotteryTypes.add(lotteryType);
        }

        return LotteryTypes;
    }


    public static List<LotteryType> geStatusData() {
        // 1：进行中2：已完成3：已中止
        String[] status = new String[]{"1", "2", "3"};
        String[] status_info = new String[]{"进行中", "已完成", "已中止"};
        List<LotteryType> LotteryTypes = new ArrayList<>();
        for (int i = 0; i < status.length; i++) {
            LotteryType lotteryType = new LotteryType();
            lotteryType.title = status_info[i];
            lotteryType.lottery_type = status[i];
            if (i == 0) {
                lotteryType.isSelect = true;
            }
            LotteryTypes.add(lotteryType);
        }

        return LotteryTypes;
    }

    public static int getPeriodPopHeight(Context context) {
        return ScreenUtils.getScreenHeight() - ConvertUtils.dp2px(56)-ConvertUtils.dp2px(48)-getStateBar2(context);
    }


    private static int getStateBar2(Context context) {
        Class c = null;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConvertUtils.dp2px(20);
    }
}
