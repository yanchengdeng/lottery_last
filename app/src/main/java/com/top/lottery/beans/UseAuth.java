package com.top.lottery.beans;

import java.io.Serializable;

//用户权限

/**
 * deposit :积分充值权限，1-有此权限，0——无此权限
 withdraw :提现功能
 rollout :积分转出权限
 caiwu_deposit  :财务人员的充值权限，只有财务人员登录才会有此功能

 */
public class UseAuth implements Serializable {

    public int deposit;//0,
    public int withdraw;//0,
    public int rollout;//0,
    public int caiwu_deposit;//0
}
