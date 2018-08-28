package com.top.lottery.beans;

import java.io.Serializable;

//配合信息
public class TrendSettingInfo implements Serializable {

    public String day;//today,
    public String sort;//asc,
    public String show_column;//show,
    public String show_missing;
    public TrendSetting setting;

    public class TrendSetting implements Serializable {
        public TrendSettingDeatil day;
        public TrendSettingDeatil sort;
        public TrendSettingDeatil show_column;
        public TrendSettingDeatil show_missing;

        public class TrendSettingDeatil implements Serializable {
            public String title;
            public TrendSettingValues list;

            public class TrendSettingValues implements Serializable {
                public String title;
                public String value;
                public boolean isSelected;
            }

        }
    }
}
