package com.top.lottery.beans;

import java.io.Serializable;

//校验选择球的 信息

/**
 * "list":{
 * "list":[
 * "03 04 05",
 * "03 04 08",
 * "03 04 09",
 * "03 05 08",
 * "03 05 09",
 * "03 08 09",
 * "04 05 08",
 * "04 05 09",
 * "04 08 09",
 * "05 08 09"
 * ],
 * "record":{
 * "code":[
 * "03",
 * "04",
 * "05",
 * "08",
 * "09"
 * ]
 * }
 */
public class CheckSelectCodeInfo implements Serializable {

    public CheckSelectCodeInfoList list;


    class CheckSelectCodeInfoList implements Serializable {

    }

}
