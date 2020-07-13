package com.web.controller;


import com.bean.Bike;
import com.bean.Repair;
import com.service.BikeService;
import com.web.model.JsonModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Controller
@Api(value="小辰出行单车信息操作接口",tags= {"单车信息","控制层"})
public class BikeController {

    @Autowired
    BikeService bikeService;


    @RequestMapping(value = "/ibike/open",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    @ApiOperation(  value="用户端开锁操作",notes="给指定的共享单车开锁，参数以json格式传过来")
    public JsonModel open(@ApiIgnore JsonModel jm, @RequestBody Bike bike){
        try {
            //bikeService.open(bike);
            bikeService.open(bike);
            jm.setCode(1);
        }catch (Exception e){
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }


    @RequestMapping(value = "/ibike/findNearAll",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    @ApiOperation(  value="查询用户附近的车辆",notes="查询用户附近的40辆车")
    public JsonModel findNaerAll(@ApiIgnore JsonModel jm, @RequestBody Bike bike){
        try {
            List<Bike> bikes = bikeService.findNearAll(bike);
            jm.setCode(1);
            jm.setObj(bikes);
        }catch (Exception e){
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @RequestMapping(value = "/ibike/repair",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    @ApiOperation(  value="报修",notes="报修")
    public JsonModel repair(@ApiIgnore JsonModel jm, @RequestBody Repair repair){
        try {
            boolean b = bikeService.repair(repair);
            if (b){
                jm.setCode(1);
            }else {
                jm.setCode(0);
                jm.setMsg("错误！");
            }
        }catch (Exception e){
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }


}
