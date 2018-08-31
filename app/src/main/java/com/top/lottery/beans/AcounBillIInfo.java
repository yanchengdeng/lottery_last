package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

//账单信息
public class AcounBillIInfo implements Serializable {

    public List<AcountBillItem> list;
    public String title;
}
