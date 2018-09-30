package com.top.lottery.beans;

import com.contrarywind.interfaces.IPickerViewData;

import java.io.Serializable;
import java.util.List;

public class NewAddMemberTypeOptionsChild implements Serializable,IPickerViewData {
    public String uid;//81100000,
    public String nickname;//牛逼区代,
    public String user_type;//3,
    public List<NewAddMemberTypeOptionsChild> child;

    @Override
    public String getPickerViewText() {
        return uid;
    }
}
