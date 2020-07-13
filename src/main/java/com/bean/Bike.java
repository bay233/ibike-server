package com.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("bike")
public class Bike implements Serializable {
    @TableId(value = "bId", type = IdType.AUTO)
    private Long bid;
    private Integer status;
    private String qrcode;
    private Double latitude;
    private Double longitude;

    // 0未启用
    public static final int UNACTIVE = 0;

    // 1启用但未解锁
    public static final int LOCK = 1;

    // 2开锁使用中
    public static final int USING = 2;

    // 3保修
    public static final int INTROUBLE = 3;

}
