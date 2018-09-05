package com.top.lottery.beans;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

//走势图开奖号码
public class TrendCodeInfo implements Serializable {

    public String prize_code[];//04 02 01 08 07,
    public String award_id;

    public LinkedTreeMap<String,String> missing_value;

}
