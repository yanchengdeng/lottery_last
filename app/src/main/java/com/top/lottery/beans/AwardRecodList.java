package com.top.lottery.beans;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 中奖纪录
 */
public class AwardRecodList implements Serializable,MultiItemEntity {

    public static final int TEXT = 1;
    public static final int RECYCLE = 2;

    public String create_date;
    public List<AwardRecordItem> recordItems;

    private int itemType;


    public void  setItemType(int type){
        this.itemType = type;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
