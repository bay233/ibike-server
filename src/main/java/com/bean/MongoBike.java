package com.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MongoBike  implements Serializable {
    private Long id;
    private Integer status;
    private Double[] loc;
    private String qrcode;
}
