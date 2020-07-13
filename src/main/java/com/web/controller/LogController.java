package com.web.controller;

import com.service.LogService;
import com.web.model.JsonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class LogController {

    @Autowired
    private LogService logService;


    @PostMapping("/ibike/log/savelog")
    @ResponseBody
    public JsonModel ready(JsonModel jm, @RequestBody Map<String,String> log){
        logService.save(log, "logs");
        jm.setCode(1);
        return jm;
    }

    @PostMapping("/ibike/log/addPayLog")
    @ResponseBody
    public JsonModel addPayLog(JsonModel jm, @RequestBody Map<String,String> log){
        logService.save(log,"payLogs");
        jm.setCode(1);
        return jm;
    }

    @PostMapping("/ibike/log/addRideLog")
    @ResponseBody
    public JsonModel addRideLog(JsonModel jm, @RequestBody Map<String,String> log){
        logService.save(log,"rideLog");
        jm.setCode(1);
        return jm;
    }

    @PostMapping("/ibike/log/addEndLog")
    @ResponseBody
    public JsonModel addEndLog(JsonModel jm, @RequestBody Map<String,String> log){
        logService.save(log,"endLog");
        jm.setCode(1);
        return jm;
    }

}
