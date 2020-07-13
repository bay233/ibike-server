package com.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
@ToString
public class JsonModel implements Serializable {
	private static final long serialVersionUID = -2519518733795311561L;

	private Integer code=0;
	private String msg;
	private Object obj;

}
