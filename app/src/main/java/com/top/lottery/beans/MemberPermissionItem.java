package com.top.lottery.beans;

import java.io.Serializable;
import java.util.List;

//会员权限
public class MemberPermissionItem implements Serializable {
    public String title;//彩种权限,
    public String type;//checkbox,
    public String tips;//一旦关闭，下级所有人将不能开启购彩返利功能。如果修改，下级所有人的奖金比例将设置为0,
    public String value;//0
    public List<MemberPermissionItemOptions> option;//


}
