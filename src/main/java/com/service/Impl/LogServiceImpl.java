package com.service.Impl;

import com.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Map<String,String> log, String collection) {
        mongoTemplate.save(log,collection);
    }

}
