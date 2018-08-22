package com.top.lottery.base;

/**
*
* Author: 邓言诚  Create at : 2018/8/12  22:04
* Email: yanchengdeng@gmail.com
* Describle: 定义常量
*/
public class Constants {
    //数据传递形式
    public static final String PASS_STRING = "pass_string";
    public static final String PASS_OBJECT = "pass_object";
    //编码格式
    public static final String SERVER_ENCODE = "utf-8";
    //服务端秘钥
    public static final String SERVER_KEY = "JF0XMw6XhwU8jXHH";
    public static final String APP_ID = "ANDRIOD_1_0_0";
    public static final String APP_SERCET = "EncBigData2018";
    public static final String USER_INFO = "user_info";
    public static final String IMEI = "imei";

    //可投注时间
    public static final long TIME_CAN_TOUZU =(8*60+45)*1000;//8分  45 秒
    //不可投注时间
    public static final long TIME_CAN_NOT_TOUZHU = (1*60+15)*1000;//1 分15秒
    public static final String DATE_FORMAR = "yyyy-MM-dd hh:mm:ss";


    public static  boolean DEBUG = true;

    public static class Net {

        /**
         * 测试环境   发布环境
         **/
        public static final String BASE_NAME = DEBUG ? "://88.mayimayi.cn:8888/api.php/V1/" : "://88.mayimayi.cn:8888/api.php/V1";
        public static final String HTTP_CERTIFY =  "http" ;//: "https";


        //http://88.mayimayi.cn:8888/api.php/V1/public/login
        public static final String BASE_API = HTTP_CERTIFY + BASE_NAME;



        //登陆
        public static final String LOGIN_IN = BASE_API+"public/login";
        //退出登录
        public static final String LOGIN_OUT = BASE_API+"public/logout";
        //用户详情
        public static final String USER_DETAIL = BASE_API+"user/detail";
        //设置密码
        public static final String USER_SETPASSWORD = BASE_API+"user/setPassword";
        //获取最新可以投注的期数信息
        public static final String LOTTERY_GETNEWESTAWARDINFO = BASE_API+"lottery/getNewestAwardInfo";
        //获取最新的开奖期数的信息，放在首页
        public static final String LOTTERY_GETNEWESTPRIZEAWARD = BASE_API+"lottery/getNewestPrizeAward";
        //获取用户可以玩的彩种
        public static final String LOTTERY_GETLIDS = BASE_API+"lottery/getLids";
        // // 获取该彩种的玩法列表
        public static final String LOTTERY_GETLOTTERYS = BASE_API+"lottery/getLotterys";
        //获取最新榜单列表
        public static final String AWARD_GETLIST = BASE_API+"award/getList";
        //通过玩法ID获取该玩法的配置信息
        public static final String LOTTERY_GETLOTTERYBYID = BASE_API+"lottery/getLotteryById";



    }

}
