package com.top.lottery.beans;

import java.io.Serializable;

//最新可以投足的期数信息
public class LasterLotteryAwardInfo implements Serializable {

    public String award_id;//2018081939,
    public String server_time;//2018-08-19 14;//28;//23//   当前系统时间
    public String current_time;//2018-08-19 14;//30;//00//  本期投注开奖时间
    public String next_time;//2018-08-19 14;//40;//00,    //  下期投注开奖时间
    public int status;//1,
    public String message;//可以投注
}
