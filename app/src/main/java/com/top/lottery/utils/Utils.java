package com.top.lottery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.LotterRecord;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.beans.MainWinCode;
import com.top.lottery.beans.MissValueInfo;
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
    static List<MissValueInfo> missValus = new ArrayList<>();
    public static Activity context;
    public static ArrayList<CharSequence> prizeText = new ArrayList<>();
    public static ArrayList<CharSequence> prizeText_open = new ArrayList<>();

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
        if (Constants.LOG_DEBUG) {
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
    public static List<MissValueInfo> parseMissValue(List<MissValueInfo> missValues) {
        missValus = missValues;
        return missValus;
    }

    public static List<MissValueInfo> getMissValues() {
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


    /**
     * @param trendCodeInfo
     * @param hideShow      :hide   show
     * @return
     */
    public static List<TerdNormalBall> get3CodeForTrend(TrendCodeInfo trendCodeInfo, String hideShow) {
        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();

        TerdNormalBall awardBallInfoFist = new TerdNormalBall();
        awardBallInfoFist.awardId = trendCodeInfo.award_id.substring(trendCodeInfo.award_id.length() - 2, trendCodeInfo.award_id.length());
        terdNormalBalls.add(awardBallInfoFist);

        //中奖

        for (String codes : trendCodeInfo.prize_code) {
            TerdNormalBall awardBallInfo = new TerdNormalBall();
            awardBallInfo.value = codes;
            awardBallInfo.isAwardCode = true;
            terdNormalBalls.add(awardBallInfo);
        }

        //和值
        TerdNormalBall awardBallInfoADD = new TerdNormalBall();
        awardBallInfoADD.value = Utils.getSum(trendCodeInfo.prize_code);
        awardBallInfoADD.isExtraValue = true;
        terdNormalBalls.add(awardBallInfoADD);
        //跨度
        TerdNormalBall awardBallInfoDiff = new TerdNormalBall();
        awardBallInfoDiff.value = Utils.getDiff(trendCodeInfo.prize_code);
        awardBallInfoDiff.isExtraValue = true;
        terdNormalBalls.add(awardBallInfoDiff);

        //需要红圈红圈
        for (int i = 1; i < 7; i++) {
            TerdNormalBall awardBallInfo = new TerdNormalBall();
            awardBallInfo.value = String.valueOf(i);
            if (trendCodeInfo.missing_value != null && trendCodeInfo.missing_value.size() > 0) {
                awardBallInfo.missVlaue = trendCodeInfo.missing_value.get(awardBallInfo.value);
            } else {
                awardBallInfo.missVlaue = "0";
            }
            awardBallInfo.isShowMiss = hideShow.equals("show") ? true : false;
            awardBallInfo.isAwardCode = getIsAwardValue(awardBallInfo.value, trendCodeInfo.prize_code);
            awardBallInfo.isNeedRedCircle = getIsAwardValue(awardBallInfo.value, trendCodeInfo.prize_code);
//            awardBallInfo.isTopThree = getisTopThree(awardBallInfo.value, trendCodeInfo.prize_code);

            terdNormalBalls.add(awardBallInfo);
        }


        return terdNormalBalls;
    }

    //获取最大值 -最小值
    private static String getDiff(String[] prize_code) {
        int max, min;
        max = Integer.parseInt(prize_code[0]);
        min = Integer.parseInt(prize_code[0]);
        for (int i = 0; i < prize_code.length; i++) {

            if (max < Integer.parseInt(prize_code[i])) {
                max = Integer.parseInt(prize_code[i]);
            }

            if (min > Integer.parseInt(prize_code[i])) {
                min = Integer.parseInt(prize_code[i]);
            }
        }
        return String.valueOf(max - min);
    }

    private static String getSum(String[] prize_code) {
        int sum = 0;
        try {
            for (int i = 0; i < prize_code.length; i++) {
                sum += Integer.parseInt(prize_code[i]);
            }
        } catch (Exception E) {

        } finally {

        }
        return String.valueOf(sum);
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


    //获取统计球的信息
    public static List<TerdNormalBall> get3CodeForTrendCount(String name, LinkedTreeMap<String, String> number) {
        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();

        TerdNormalBall awardBallInfoFist = new TerdNormalBall();
        awardBallInfoFist.awardId = name;
        terdNormalBalls.add(awardBallInfoFist);

        for (int i = 0; i < 5; i++) {
            TerdNormalBall empty = new TerdNormalBall();
            empty.isCountDismiss = true;
            terdNormalBalls.add(empty);
        }


        for (int i = 1; i < 7; i++) {
            TerdNormalBall awardBallInfo = new TerdNormalBall();
            awardBallInfo.value = String.valueOf(i);
            awardBallInfo.missVlaue = number.get(awardBallInfo.value);
            awardBallInfo.isAwardCode = false;
            awardBallInfo.isCount = true;

            terdNormalBalls.add(awardBallInfo);
        }

        return terdNormalBalls;


    }


    //获取统计球的信息
    public static List<TerdNormalBall> get3CodeForTrendCount( LinkedTreeMap<String, String> number) {
        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();



        for (int i = 1; i < 7; i++) {
            TerdNormalBall awardBallInfo = new TerdNormalBall();
            awardBallInfo.value = String.valueOf(i);
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
        return ScreenUtils.getScreenHeight() - ConvertUtils.dp2px(56) - ConvertUtils.dp2px(48) - getStateBar2(context);
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

    public static boolean checkUseID(EditText etUserName, Context mContext) {
        if (TextUtils.isEmpty(etUserName.getEditableText().toString())) {
            ToastUtils.showShort(mContext.getString(R.string.input_user_name));
            return false;
        }

        if (etUserName.getEditableText().toString().trim().length() != 8) {
            ToastUtils.showShort(mContext.getString(R.string.input_user_name_right));
            return false;
        }

        return true;
    }


    public static void showToast(Response response) {

        ToastUtils.showShort(Utils.toastInfo(response));
    }

    public static List<AwardBallInfo> parseThreeCodes(String score) {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (!TextUtils.isEmpty(score)) {
            if (score.contains(":") && score.contains("\n")) {
                String codes[] = score.split("\n");
                for (String code : codes) {
                    AwardBallInfo awardBallInfo = new AwardBallInfo();
                    String[] codeValue = code.split(":");
                    awardBallInfo.value = codeValue[0];
                    awardBallInfo.price = codeValue[1];
                    awardBallInfos.add(awardBallInfo);
                }
            } else {
                AwardBallInfo awardBallInfo = new AwardBallInfo();
                awardBallInfo.value = score;
                awardBallInfos.add(awardBallInfo);
            }
        }
        return awardBallInfos;
    }


    /**
     * "title":"和值", "":"type:1
     * "title":"三同号通选", "":"type:2
     * "title":"三同号单选","":"type:3
     * "title":"二同号复选","":"type:4
     * "title":"二同号单选","":"type:5
     * "title":"三不同号","":"type:6
     * "title":"二不同号","":"type:7
     * "title":"三连号通选","":"type:8
     * "title":"三不同号胆拖","":"type:9
     * "title":"二不同号胆拖","":"type:10
     */
    public static List<AwardBallInfo> parseThreeCodes(LotteryInfo lotteryInfo, String score) {
        int type = lotteryInfo.type;
        if (type == 1) {
            return parseThreeCodes(score);
        } else {
            List<AwardBallInfo> awardBallInfos = new ArrayList<>();

            if (type == 3) {
                for (int i = 1; i < 7; i++) {
                    AwardBallInfo awardBallInfo = new AwardBallInfo();
                    awardBallInfo.value = i + "" + i + "" + i;
                    awardBallInfo.price = score;
                    awardBallInfos.add(awardBallInfo);
                }
            } else if (type == 2 || type == 8) {
                AwardBallInfo awardBallInfo = new AwardBallInfo();
                awardBallInfo.value = lotteryInfo.title;
                awardBallInfo.price = score;
                awardBallInfos.add(awardBallInfo);
            } else if (type == 4) {
                for (int i = 1; i < 7; i++) {
                    AwardBallInfo awardBallInfo = new AwardBallInfo();
                    awardBallInfo.value = i + "" + i + "*";
                    awardBallInfo.price = score;
                    awardBallInfos.add(awardBallInfo);
                }
            } else if (type == 6 || type == 7) {
                for (int i = 1; i < 7; i++) {
                    AwardBallInfo awardBallInfo = new AwardBallInfo();
                    awardBallInfo.value = String.valueOf(i);
                    awardBallInfo.price = score;
                    awardBallInfos.add(awardBallInfo);
                }
            } else if (type == 5 || type == 9 || type == 10) {
                for (int i = 1; i < 7; i++) {
                    AwardBallInfo awardBallInfo = new AwardBallInfo();
                    awardBallInfo.value = String.valueOf(i);
                    awardBallInfo.price = score;
                    awardBallInfos.add(awardBallInfo);
                }
            }

            return awardBallInfos;
        }
    }

    public static List<AwardBallInfo> parseThreeCodesDoule(String score) {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            AwardBallInfo awardBallInfo = new AwardBallInfo();
            awardBallInfo.value = i + "" + i;
            awardBallInfo.price = score;
            awardBallInfos.add(awardBallInfo);
        }

        return awardBallInfos;
    }

    public static int getSpanCount(int type) {
        if (type == 1) {
            return 4;
        } else if (type == 3 || type == 4 || type == 6 || type == 7) {
            return 4;
        } else if (type == 2 || type == 8) {
            return 1;
        }
        return 4;
    }

    public static int getSpanCountForChart(int type) {
        if (type == 1) {
            return 8;
        } else if (type == 3 || type == 4 || type == 6 || type == 7) {
            return 6;
        } else if (type == 2 || type == 8) {
            return 1;
        }
        return 6;
    }

    public static String getMissValuesByKey(String code) {
        if (missValus == null || TextUtils.isEmpty(code)) {
            return "0";
        }
        MissValueInfo chooseMiss = null;
        for (MissValueInfo item : missValus) {
            if (code.equals(item.code)) {
                chooseMiss = item;
            }

        }
        return chooseMiss == null ? "0" : chooseMiss.value;
    }

    public static ArrayList<CharSequence> getStringData(List<LotterRecord> body) {
        prizeText.clear();
        return prizeText = getXX(body);
    }


    //开奖界面
    public static ArrayList<CharSequence> getStringDataOpen(List<LotterRecord> body) {
        prizeText_open.clear();
        return prizeText_open = getXXOpen(body);
    }


    public static ArrayList<CharSequence> getXXOpen(List<LotterRecord> body) {
        if (body.size() > 0) {
            if (body.size() > 3) {
                prizeText_open.add(getStringRecordOpen(body.subList(0, 3)));
                try {
                    body.remove(0);
                    body.remove(1);
                    body.remove(2);
                }catch (Exception E){

                }
                getXXOpen(body);
            } else {
                prizeText_open.add(getStringRecordOpen(body));
                return prizeText_open;
            }
        } else {
            return prizeText_open;
        }
        return prizeText_open;
    }

    public static ArrayList<CharSequence> getXX(List<LotterRecord> body) {
        if (body.size() > 0) {
            if (body.size() > 3) {
                prizeText.add(getStringRecord(body.subList(0, 3)));
                try {
                    body.remove(0);
                    body.remove(1);
                    body.remove(2);
                }catch (Exception e){

                }
                getXX(body);
            } else {
                prizeText.add(getStringRecord(body));
                return prizeText;
            }
        } else {
            return prizeText;
        }
        return prizeText;
    }

    private static String getStringRecord(List<LotterRecord> body) {
//        ArrayList<CharSequence> strins = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        if (body != null && body.size() > 0) {
            for (int i = 0; i < body.size(); i++) {
                String item = "<p style=\"line-height:100%\">恭喜：<b><font color=\"#ffefbf\">" + "【" + body.get(i).uid + "】" + "</font></b>投" + body.get(i).lid_title + "中" + "<b><font  color=\"#ffefbf\">" + body.get(i).reward_score + "</font></b>分<br/></p>";
                stringBuilder.append(item);
//                strins.add(stringBuilder);
            }
        }
        return stringBuilder.toString();
    }

    private static String getStringRecordOpen(List<LotterRecord> body) {
        String ss = "<div style=\"text-align:center;\"><span style=\"padding:5px 5px; background:#F2AE47; width:10px; height:10px;border-radius:20px;\"> </span>";
        ArrayList<CharSequence> strins = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        if (body != null && body.size() > 0) {
            for (int i = 0; i < body.size(); i++) {
                String item = "<p style=\"line-height:100%\">恭喜:<b><font color=\"#4FBAFC\">" + "【" + body.get(i).uid + "】" + "</font></b>投" + body.get(i).lid_title + "中" + "<b><font  color=\"#D81122\">" + body.get(i).reward_score + "</font></b>分<br/></p>";
                stringBuilder.append(item);
//                strins.add(stringBuilder);
            }
        }
        return stringBuilder.toString();
    }
}
