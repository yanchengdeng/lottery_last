package com.top.lottery.beans;

import java.io.Serializable;

//会员统计
public class MemberStaticItem implements Serializable {

    public String member_uid;//10001000,
    public String score_total = "0";//309.60,
    public String score_type;//6,
    public String score_title;//代理返利(+),
    public String score_type_color;//#3EA9F5
    public String start_time;
    public String end_time;
}
