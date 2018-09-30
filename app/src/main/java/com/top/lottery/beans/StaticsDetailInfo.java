package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

//贡献详情
public class StaticsDetailInfo implements Serializable {

    public StaticsDetailMemberInfo member;
    public StaticsDetailSumInfo sum;
    public List<StaticsDetailListInfo> list;
}
