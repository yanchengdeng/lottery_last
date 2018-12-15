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
    public static final String PASS_CHILD_UID = "child_uid";
    public static final String PASS_BOLLEAN = "pass_boolean";
    public static final String PASS_START_TIME = "start_time";
    public static final String PASS_END_TIME = "pass_boolean";
    public static final String PASS_LOOTERY_TYPE="pass_lottery_type";
    public static final String PASS_WEEK_TYPE = "pass_week_type";
    //编码格式
    public static final String SERVER_ENCODE = "utf-8";
    //服务端秘钥
    public static final String SERVER_KEY = "JF0XMw6XhwU8jXHH";
    public static final String APP_ID = "ANDRIOD_1_0_0";
    public static final String APP_SERCET = "EncBigData2018";
    public static final String USER_INFO = "user_info";
    public static final String USER_AUTH = "user_auth";
    public static final String IMEI = "imei";

    //可投注时间
    public static final long TIME_CAN_TOUZU = (8 * 60 + 45) * 1000;//8分  45 秒
    //不可投注时间
    public static final long TIME_CAN_NOT_TOUZHU = (1 * 60 + 15) * 1000;//1 分15秒
    public static final String DATE_FORMAR = "yyyy-MM-dd HH:mm:ss";
    //返回主页请求码
    public static final int BACK_TO_MAIN = 300;
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "user_token";
    public static final String ERROR_CODE_AWARD_EXPERID = "-99";
    public static String LASTEST_AWARD_ID = "2018082601";
    public static String LASTER_AWARD_END_TIME = "2018-08-26 17:00:00";


    public static boolean DEBUG = false;
    public static boolean LOG_DEBUG = true;
    public static int PAGE_SIZE = 15;

    public static boolean HAS_VESRSION_TIPS ;//是否提示过版本更新
    public static int KEY_BORAD_HIEGHT = 250;

    public static class Net {

        /**
         * 测试环境   发布环境
         **/
        public static final String BASE_URL = DEBUG ? "://88.mayimayi.cn:8888/" : "://88.lzsscp.com/";
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
        //获取密保问题列表
        public static final String PUBLIC_GETQUESTIONS = BASE_API+"public/getQuestions";
        //校验密保
        public static final String PUBLIC_CHECKANSWER = BASE_API+"public/checkAnswer";
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
        //积分转出
        /**
         * 10：返利转出
         15：分红积分转出
         16：分红积分转入
         17：信用积分转入
         */
        public static final String USER_SCOREOPERATE= BASE_API + "user/scoreOperate";


        //个人中心菜单
        public static final String USER_GETCENTERMENULIST = BASE_API+"user/getCenterMenuList";
        //站内信
        public static final String MESSAGE_GETLIST = BASE_API + "message/getlist";
        //站内信详情
        public static final String MESSAGE_DETAIL = BASE_API + "message/detail";
        //新消息
        public static final String MESSAGE_NEWMESSSAGESUM = BASE_API+"message/newMesssageSum";


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
        //购彩订单详情
        public static final String RECORD_DETAIL = BASE_API+"record/detail";
        //下级用户列表
        public static final String SCORELOG_GETCHILDUSERLIST = BASE_API+"scoreLog/getChildUserList";
        //撤单
        public static final String RECORD_CANCELORDER = BASE_API+"record/cancelOrder";
        //停止追号
        public static final String RECORD_CANCELCHASE = BASE_API+"record/cancelChase";
        //继续投注
        public static final String CART_ADDBYORDERID = BASE_API+"cart/addByOrderId";
        //追号列表
        public static final String RECORD_CHASELIST = BASE_API+"record/chaseList";
        //追号详情
        public static final String AWARD_DETAIL = BASE_API+"award/detail";


        //走势图
        public static final String AWARD_TRENDCHART = BASE_API + "award/trendchart";
        //获取走势图配置信息
        public static final String USER_GETTRENDCHARTCONFIG = BASE_API + "user/getTrendchartConfig";
        //设置走势图配置信息
        public static final String USER_SETTRENDCHARTCONFIG = BASE_API + "user/setTrendchartConfig";


        //后台管理
//        获取各级别会员列表
        public static final String LEADER_GETCHILDLIST = BASE_API+"leader/getChildList";
        //充值积分
        public static final String USER_DEPOSIT = BASE_API+"user/deposit";
        //新增、修改员工配置信息
        public static final String LEADER_GETUSEROPTION = BASE_API+"leader/getUserOption";
        //获取会员权限
        public static final String LEADER_GETAUTHOPTION = BASE_API+"leader/getAuthOption";
        //设置会员权限
        public static final String LEADER_SETAUTH = BASE_API+"leader/setAuth";
        //会员积分操作
        //1：给会员充值，
        //3：给会员提现，
        //19：给会员信用分授权，
        //21：会员信用分收回
        public static final String LEADER_CHILDSCOREOPERATE = BASE_API+"leader/childScoreOperate";
        //添加会员
        public static final String LEADER_ADDUSER = BASE_API+"leader/addUser";
        //修改会员信息
        public static final String LEADER_SETUSER = BASE_API+"leader/setUser";
        //获取会员基本信息
        public static final String LEADER_GETUSERINFO = BASE_API+"leader/getUserInfo";

        //利润收益
        public static final String STATISTICS_GETSTATISTICSBYUID = BASE_API+"statistics/getStatisticsByUid";
        //收益参数
        public static final String STATISTICS_GETPARAMS = BASE_API+"statistics/getParams";
        //获取统计信息
        public static final String STATISTICS_GETSCORESTATISTICSBYUID = BASE_API+"statistics/getScoreStatisticsByUid";
        //积分统计类型
        public static final String STATISTICS_GETSCORETYPELIST = BASE_API+"statistics/getScoreTypeList";
        //积分列表
        public static final String STATISTICS_GETSCOREDETAILLISTBYUID = BASE_API+"statistics/getScoreDetailListByUid";
        //积分日志
        public static final String STATISTICS_DETAILSCORELOG = BASE_API+"statistics/detailScoreLog";
        //利益汇总
        public static final String STATISTICS_GETLIST = BASE_API+"statistics/getList";
        //版本更新
        public static final String CLIENT_CHECKVERSION = BASE_API+"client/checkVersion";
        //获取积分中有玩过的彩种
        public static final String RECORD_GETLIDS = BASE_API+"record/getLids";


    }

}
