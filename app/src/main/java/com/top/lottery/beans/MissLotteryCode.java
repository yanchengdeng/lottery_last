package com.top.lottery.beans;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

//遗漏值
public class MissLotteryCode implements Serializable {

    public LinkedTreeMap<String,String> missing_value;
}
