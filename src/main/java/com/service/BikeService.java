package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bean.Bike;
import com.bean.Repair;

import java.util.List;

public interface BikeService extends IService<Bike> {

    /**
     * 开锁
     *
     * @param bike
     */
    void open(Bike bike);

    /**
     * 查找车
     *
     * @param bid
     * @return
     */
    Bike findByBid(Long bid);

    /**
     * 添加车
     *
     * @param bike
     * @return
     */
    boolean addNewBike(Bike bike);

    /**
     * 查找附近的车
     *
     * @param bike
     * @return
     */
    List<Bike> findNearAll(Bike bike);

    /**
     * 报修
     * @param report
     * @return
     */
    boolean repair(Repair report);

}
