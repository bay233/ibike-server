package com.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayModel implements Serializable {
    private Double longitude;
    private Double latitude;
    private String uuid;       //关于用户会话的id    相当于  sessionid
    private String openId;
    private String phoneNum;
    private Long startTime;
    private Long endTime;
    private Long totalTime;

    private Integer payMoney;
    private String logTime;
    private Long bid;
}
