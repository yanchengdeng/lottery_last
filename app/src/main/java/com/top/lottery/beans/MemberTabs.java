package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;
//t
public class MemberTabs implements Serializable {

    public int user_type;//3,
    public String title;//区域列表,
    public List<MemberTabsItem> buttons;
}
