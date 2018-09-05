package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

public class BelowClassInfo implements Serializable {
    public List<BelowClassInfoItem> list;
    public  BelowClassSum sum;


    public class BelowClassSum implements Serializable {
        public String reward_score;//1128.00,
        public String cost_score;//1676.00,
        public String fanli_score;//0
    }
}
