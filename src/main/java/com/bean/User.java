package com.bean;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Data
@Document(collection = "users")
public class User implements Serializable {
    private String openid;
    private int status;  //用户状态
    // 这个字段创建索引
    //@Indexed(unique = true)
    private String phoneNum;  // 用户号码
    private String name;   // 用户名
    private String idNum;  // 身份证
    private double deposit;  // 押金
    private double balance;   // 余额

    // 这个属性在数据库中不存储
    @Transient
    private String verifyCode;
}
