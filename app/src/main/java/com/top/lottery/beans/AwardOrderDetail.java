package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

//投注订单详情
public class AwardOrderDetail implements Serializable {
    public String order_id;//20180902164020525410150,
    public String award_id;//2018090253,
    public String lid_title;//福建11选5（B盘）,
    public String lid_icon;//http;////88.mayimayi.cn;//8888/Public/res/images/11x5.jpg,
    public String cost_score;//8,
    public String reward_score;//0,
    public String record_times;//2,
    public int is_reward;//2,//订单状态：1：待开奖2：未中奖3：中奖4：已中止
    public String prize_code;//07 11 01 09 06,
    public String reward_title;//未中奖,
    public int is_chase;//0,//是否是追号订单，1：是追号订单，0-普通订单
    public String show_cancel_chase_button;//hide,是否显示停止追号按钮  show：显示停止追号的按钮  hide：不要显示停止追号的按钮

    public String lottery_type;
    public String lid;
    public String show_cancel_order_button;//hide,是否显示撤单按钮show：显示撤单按钮hide：不显示撤单按钮
    public String order_title;//普通订单,
    public String create_time;//2018-09-02 16;//40;//20,
    public int is_win_stop_chase;//0   追号中止
    public List<AwardOrderScore> records;

}
