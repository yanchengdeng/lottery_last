package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

//账单信息
public class AcounBillIInfo implements Serializable {

    public List<AcountBillItem> list;
    public String title;
    public AccountBillSum sum;

    public class AccountBillSum implements Serializable {

        public AccountBillSumItem recharge;
        public AccountBillSumItem reward;
        public AccountBillSumItem withdrawals;
        public AccountBillSumItem cost;
        public AccountBillSumItem daili;
        public AccountBillSumItem daili_number;

        public class AccountBillSumItem implements Serializable {
            public String title;////充值积分,
            public String score_type;//1,
            public String score;//0
        }
    }
}
