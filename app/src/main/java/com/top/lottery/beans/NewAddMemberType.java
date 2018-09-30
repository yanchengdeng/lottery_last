package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

public class NewAddMemberType implements Serializable {
    public String title;//:"用户类型",
    public List<NewAddMemberTypeOptions> option;
    public String tips;
}
