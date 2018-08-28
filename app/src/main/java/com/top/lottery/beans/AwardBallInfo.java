package com.top.lottery.beans;

import java.io.Serializable;

//选球信息
public class AwardBallInfo implements Serializable {
    public String value;//球的数值
    public String missValue;//漏选球个数
    public boolean isSelected;//是否选择
    public boolean isShowMissValue;//遗漏值显示

}
