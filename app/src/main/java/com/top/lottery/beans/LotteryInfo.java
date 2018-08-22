package com.top.lottery.beans;

import java.io.Serializable;

//彩球玩法配置信息

/**
 * type://1-任选，2-前一、前二、前三直选等，3-前二、前三组选，4-胆拖任选，6-胆拖组选
 num://3 球的个数

 是否可以机选号码
 1-可以机选号码
 0-不可以机选号码
 */
public class LotteryInfo implements Serializable {

   public String lottery_id;//3,
        public String title;//任选三,
    public String   options;//type;//1
    //num://3,
    public int   mechine;//1
    public int type;
    public int num;
}
