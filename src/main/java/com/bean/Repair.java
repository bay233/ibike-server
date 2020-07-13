package com.bean;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "repair")
public class Repair implements Serializable {

    private String phoneNum;
    private Long bid;
    private Integer[] types;
    private String openid;
    private Double[] loc;

    @Transient
    private Double latitude;
    @Transient
    private Double longitude;
}
