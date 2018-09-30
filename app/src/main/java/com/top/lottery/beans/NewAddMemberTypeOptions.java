package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

public class NewAddMemberTypeOptions implements Serializable {
    public String id;//1,
    public String title;//会员,
    public List<NewAddMemberTypeOptionsChild> option;
    public boolean isSelected;
}
