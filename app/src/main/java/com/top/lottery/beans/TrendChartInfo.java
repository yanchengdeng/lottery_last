package com.top.lottery.beans;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.List;

//走势图信息
public class TrendChartInfo implements Serializable {

    public List<TrendCodeInfo> list;
    public LinkedTreeMap<String,String> number;//出现次数
    public LinkedTreeMap<String,String> average_missing;//平均最大次数
    public LinkedTreeMap<String,String> max_missing;//最大遗漏
    public LinkedTreeMap<String,String> max_double_hits;//最大连击
}
