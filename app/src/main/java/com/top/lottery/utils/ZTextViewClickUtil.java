package com.top.lottery.utils;

public class ZTextViewClickUtil {
    private static long lastClickTime;

    // 判断是否为快速点击事件
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500)
            return true;
        lastClickTime = time;
        return false;
    }
}