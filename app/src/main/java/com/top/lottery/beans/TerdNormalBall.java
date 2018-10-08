package com.top.lottery.beans;

import java.io.Serializable;

//走势图每一行的数据
public class TerdNormalBall implements Serializable {

    public String awardId;
    public String value = "0";
    public boolean isShowMiss  = true;//是否显示遗失的号码
    public String missVlaue ="0";//遗漏值
    public boolean isAwardCode;//是否是选中的号码
    public boolean isTopThree;//前三个用蓝色标示
    public boolean isCount ;//是否是统计数据

}
