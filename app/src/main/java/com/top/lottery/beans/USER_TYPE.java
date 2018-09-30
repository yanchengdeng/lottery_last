package com.top.lottery.beans;

/**
 * 1：为会员
 * 2：店家
 * 3：区代
 * 4：大代
 */
public enum USER_TYPE {
    USER_TYPE_MEMBER(1), USER_TYPE_SHOP(2), USER_TYPE_AREA(3), USER_TYPE_MANAGE(4);

    public int type;

    USER_TYPE(int i) {
        this.type = i;
    }

    public int getType() {
        return type;
    }
}
