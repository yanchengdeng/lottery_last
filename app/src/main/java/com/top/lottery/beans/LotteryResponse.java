package com.top.lottery.beans;

import java.io.Serializable;

// 返回数据模板
public class LotteryResponse<T> implements Serializable {


    public int code;//
    public String msg;//查询成功,
    public T body;

}