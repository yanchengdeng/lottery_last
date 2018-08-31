package com.top.lottery.base;

/**
 * Author: 邓言诚  Create at : 2018/8/12  22:04
 * Email: yanchengdeng@gmail.com
 * Describle: 定义常量
 */
public class Constants {
    //数据传递形式
    public static final String PASS_STRING = "pass_string";
    public static final String PASS_NAME = "pass_name";
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
    public static final long TIME_CAN_TOUZU = (8 * 60 + 45) * 1000;//8分  45 秒
    //不可投注时间
    public static final long TIME_CAN_NOT_TOUZHU = (1 * 60 + 15) * 1000;//1 分15秒
    public static final String DATE_FORMAR = "yyyy-MM-dd hh:mm:ss";
    //返回主页请求码
    public static final int BACK_TO_MAIN = 300;
    public static String LASTEST_AWARD_ID = "2018082601";
    public static String LASTER_AWARD_END_TIME = "2018-08-26 17:00:00";


    public static boolean DEBUG = true;
    public static int PAGE_SIZE = 15;

    public static class Net {

        /**
         * 测试环境   发布环境
         **/
        public static final String BASE_URL = DEBUG ? "://88.mayimayi.cn:8888/" : "://88.mayimayi.cn:8888/";
        public static final String BASE_NAME = BASE_URL + "api.php/V1/";
        public static final String HTTP_CERTIFY = "http";//: "https";

        //http://88.mayimayi.cn:8888/api.php/V1/public/login
        public static final String BASE_API = HTTP_CERTIFY + BASE_NAME;

        //网页1：委托投注协议
        //2：玩法说明
        //3：中奖攻略
        public static final String WEB_DEAL = HTTP_CERTIFY + BASE_URL + "lottery/article/mdetail/id/1.html";
        public static final String WEB_PLAY_WAY = HTTP_CERTIFY + BASE_URL + "lottery/article/mdetail/id/2.html";
        public static final String WEB_PLAY_GOT_AWARD_SKILL = HTTP_CERTIFY + BASE_URL + "lottery/article/mdetail/id/3.html";


        //登陆
        public static final String LOGIN_IN = BASE_API + "public/login";
        //忘记密码
        public static final String FORGET_PASSWORD = BASE_API+"public/forgetpassword";
        //退出登录
        public static final String LOGIN_OUT = BASE_API + "public/logout";
        //用户详情
        public static final String USER_DETAIL = BASE_API + "user/detail";
        //修改昵称
        public static final String USER_SETNICKNAME = BASE_API + "user/setNickname";
        //密保问题
        public static final String USER_SETSECURITY = BASE_API + "user/setSecurity";
        //查询是否设置密保
        public static final String USER_ISSETSECURITY = BASE_API+"user/isSetSecurity";
        //设置密码
        public static final String USER_SETPASSWORD = BASE_API + "user/setPassword";
        //用户权限
        public static final String USER_GETAUTH = BASE_API + "user/getAuth";
        //代理返利积分转出
        public static final String USER_ROLLOUT = BASE_API + "user/rollout";
        //站内信
        public static final String MESSAGE_GETLIST = BASE_API + "message/getlist";
        //站内信详情
        public static final String MESSAGE_DETAIL = BASE_API + "message/detail";


        //获取最新可以投注的期数信息
        public static final String LOTTERY_GETNEWESTAWARDINFO = BASE_API + "lottery/getNewestAwardInfo";
        //获取最新的开奖期数的信息，放在首页
        public static final String LOTTERY_GETNEWESTPRIZEAWARD = BASE_API + "lottery/getNewestPrizeAward";
        //获取用户可以玩的彩种
        public static final String LOTTERY_GETLIDS = BASE_API + "lottery/getLids";
        // // 获取该彩种的玩法列表
        public static final String LOTTERY_GETLOTTERYS = BASE_API + "lottery/getLotterys";
        //获取最新榜单列表
        public static final String AWARD_GETLIST = BASE_API + "award/getList";
        //通过玩法ID获取该玩法的配置信息
        public static final String LOTTERY_GETLOTTERYBYID = BASE_API + "lottery/getLotteryById";
        //获取该玩法id 的 遗漏值
        public static final String AWARD_MISSINGVALUE = BASE_API + "award/missingValue";
        //校验所选号是否合法
        public static final String CART_CHECKCODES = BASE_API + "cart/checkCodes";
        //校验所选期号是否合法
        public static final String CART_CHECKAWARD = BASE_API + "cart/checkAward";
        //机选或是随机投注
        public static final String CART_MECHINE = BASE_API + "cart/mechine";
        //加入购物车
        public static final String CART_ADDCART = BASE_API + "cart/addCart";
        //获取购物车信息
        public static final String CART_GETCART = BASE_API + "cart/getCart";
        //删除购物车数据
        public static final String CART_DELETE = BASE_API + "cart/delete";
        //变更倍数、追号设置、追号停止
        public static final String CART_CHANGE = BASE_API + "cart/change";
        //下单结算
        public static final String CART_PAY = BASE_API + "cart/pay";
        //使用最新期号投注
        public static final String CART_SAVECART = BASE_API + "cart/saveCart";


        //购彩记录
        public static final String RECORD_GETLIST = BASE_API + "record/getlist";
        //账单记录
        public static final String SCORELOG_GETLIST = BASE_API +"scoreLog/getList";


        //走势图
        public static final String AWARD_TRENDCHART = BASE_API + "award/trendchart";
        //获取走势图配置信息
        public static final String USER_GETTRENDCHARTCONFIG = BASE_API + "user/getTrendchartConfig";
        //设置走势图配置信息
        public static final String USER_SETTRENDCHARTCONFIG = BASE_API + "user/setTrendchartConfig";


    }

}
