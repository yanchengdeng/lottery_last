package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

///子
public class ManageMemberItem implements Serializable {

    public String uid;//81160000,
    public String score;//0.00,
    public int user_type;//3
    public String type;
    public String title;
   public List<MemberTabsItem> buttons;
}
