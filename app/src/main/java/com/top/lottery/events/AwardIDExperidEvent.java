package com.top.lottery.events;

//彩票期数过期
public class AwardIDExperidEvent {
    public String methodName;
    public AwardIDExperidEvent(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
