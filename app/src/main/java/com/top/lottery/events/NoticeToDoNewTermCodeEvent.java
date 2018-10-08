package com.top.lottery.events;


import java.io.Serializable;

//通知 该类去执行操作
public class NoticeToDoNewTermCodeEvent implements Serializable{
    public String className;//标记当前可视界面的完整类名
    public String methodName;//调用接口的方法名
}
