package com.service;

import java.util.Map;

public interface LogService {

    /**
     * 保存操作日志
      */
    void save(Map<String,String> log, String collection);

}
