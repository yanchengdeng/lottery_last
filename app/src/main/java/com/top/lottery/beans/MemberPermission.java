package com.top.lottery.beans;

import java.io.Serializable;

//会员权限
public class MemberPermission implements Serializable {
    public MemberPermissionItem lottery_list;
    public MemberPermissionItem bonus;
    public MemberPermissionItem reward_ratio;
    public MemberPermissionItem bonus_ratio;
    public MemberPermissionItem rebate;
    public MemberPermissionItem rebate_ratio;
    public MemberPermissionItem open_member;
    public MemberPermissionItem deposit;
    public MemberPermissionItem purchase;
    public MemberPermissionItem withdraw;
    public MemberPermissionItem rollout;
    public MemberPermissionItem bonus_rollout;


}
