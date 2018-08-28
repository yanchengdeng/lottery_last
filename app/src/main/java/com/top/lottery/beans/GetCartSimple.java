package com.top.lottery.beans;

import java.io.Serializable;

//购物车信息
public class GetCartSimple implements Serializable {
    public String award_id;//2018082781,
    public String lottery_type;//1,
    public String lottery_lid;//1,
//    public List<CartItem> records;


    public String manual_lottery_id;//3,
    public int is_win_stop_chase;//0,追号中奖中止 追号中奖中止 0-否 1-是，默认为1
    public String total_chase_awards;//1,追加期数，默认为1期，就是不追加
    public String record_times;//1,倍数，默认为1倍
    public String total_cost_score;//4,
    public String total_number;//2


    public class CartItem implements Serializable {
        public String record_code_string;//03 08 09,
        public String lottery_title;//任选三共1注2积分,
        public String record_key;//0
    }

}
