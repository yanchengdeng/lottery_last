package com.top.lottery.beans;

import com.contrarywind.interfaces.IPickerViewData;

import java.io.Serializable;

public class StaticType implements Serializable,IPickerViewData {
    public String color;//#4cae4c,
    public String score_type;//1,
    public String title;//充值(+)

    @Override
    public String getPickerViewText() {
        return title;
    }
}
