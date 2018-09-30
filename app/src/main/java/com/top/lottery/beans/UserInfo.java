package com.top.lottery.beans;

import java.io.Serializable;

//用户信息
public class UserInfo implements Serializable {

    public String uid;//10001001,
    public String username;//10001001,
    public String nickname = "";//,
    public Object tokens;

    public String credit_balance_score = "0";//0.00,
    public String credit_line_score = "0";//0.00,


    public String p_uid;//10001000,
    public int user_type;//1,//用户类型，1：为会员2：店家3：区代 4：大代
    public String score = "0";//8732.00,
    public String frozen_score ="0";//292.00,
    public float daili_score = 0.0f;//0.00
    public float bonus_score = 0.0f;//分红积分


}
