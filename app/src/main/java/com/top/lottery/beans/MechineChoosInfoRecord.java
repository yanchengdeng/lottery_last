package com.top.lottery.beans;

import java.io.Serializable;

//机选信息
public class MechineChoosInfoRecord implements Serializable {

    public String[] code;

    public String[] identical;//同号
    public String[] difference;//不同号


}
