package com.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bean.Bike;
import com.bean.MongoBike;
import com.bean.Repair;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mapper.BikeMapper;
import com.mongodb.client.result.UpdateResult;
import com.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
public class BikeServiceImpl extends ServiceImpl<BikeMapper, Bike> implements BikeService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void open(Bike bike) {
        if (bike.getBid() == null) {
            throw new RuntimeException("缺少待开开车的编号");
        }
        Query query = new Query(Criteria.where("id").is(bike.getBid()));
        MongoBike b = mongoTemplate.findOne(query, MongoBike.class, "bike");
        if (b == null) {
            throw new RuntimeException("查无此车");
        }
        switch (b.getStatus()) {
            case Bike.UNACTIVE:
                throw new RuntimeException("此车暂未启用，请更换一辆");
            case Bike.USING:
                throw new RuntimeException("此车正在骑行中，请更换一辆");
            case Bike.INTROUBLE:
                throw new RuntimeException("此车正在待维修，请更换一辆");
        }
        bike.setStatus(Bike.USING);
        //baseMapper.updateById(bike);
        UpdateResult updateResult = mongoTemplate.updateFirst(new Query().addCriteria(Criteria.where("_id").is(bike.getBid())),
                new Update().set("status", Bike.USING), "bike");
    }

    @Override
    @Transactional(readOnly = true)
    public Bike findByBid(Long bid) {
        Bike bike = baseMapper.selectById(bid);
        return bike;
    }

    @Override
    public boolean addNewBike(Bike bike) {
        int insert = baseMapper.insert(bike);
        return insert > 0;
    }

    @Override
    public List<Bike> findNearAll(Bike bike) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(Bike.LOCK)).limit(40);
        Point point = new Point(bike.getLatitude(), bike.getLongitude());
        /*query.addCriteria(Criteria.where("status").is(Bike.LOCK))
                .addCriteria(Criteria.where("loc").near(point))
                .limit(40);*/

        NearQuery nearQuery = NearQuery.near(point).query(query);

        GeoResults<MongoBike> mongoBike = mongoTemplate.geoNear(nearQuery, MongoBike.class, "bike");
        List<Bike> list = new ArrayList<>();
        for (GeoResult<MongoBike> mbGeoResult : mongoBike) {
            MongoBike mb = mbGeoResult.getContent();
            Bike b = new Bike();
            b.setBid(mb.getId());
            b.setLatitude(mb.getLoc()[0]);
            b.setLongitude(mb.getLoc()[1]);
            b.setStatus(mb.getStatus());
            b.setQrcode(mb.getQrcode());
            list.add(b);
        }
        return list;
    }

    @Override
    public boolean repair(Repair repair) {
        Double[] loc = new Double[2];
        loc[0] = repair.getLatitude();
        loc[1] = repair.getLongitude();
        repair.setLoc(loc);
        Repair insert = mongoTemplate.insert(repair, "repair");
        UpdateResult updateResult = mongoTemplate.updateFirst(new Query()
                        .addCriteria(Criteria.where("_id").is(repair.getBid())),
                new Update().set("status", Bike.INTROUBLE), "bike");
        Bike bike = new Bike();
        bike.setBid(repair.getBid());
        bike.setStatus(3);
        // int update = baseMapper.updateById(bike);

        if (insert != null && updateResult.getModifiedCount() == 1) {
            // 数据埋点
            mongoTemplate.save(addRepairLogs(repair), "repairLogs");
            return true;
        }
        return false;
    }


    private Map<String, String> addRepairLogs(Repair repair) {
        RestTemplate restTemplate = new RestTemplate();
        // 密钥
        String key = "";
        String url = "https://apis.map.qq.com/ws/geocoder/v1/?key=" + key
                + "&location=" + repair.getLatitude() + "," + repair.getLongitude();
        Map<String, Object> map = new HashMap<>();
        map = restTemplate.getForEntity(url, map.getClass()).getBody();
        JsonObject jsonObject = new Gson().toJsonTree(map).getAsJsonObject();
        //System.out.println(map);

        String province = jsonObject.getAsJsonObject("result").getAsJsonObject("address_component")
                .get("province").getAsString();
        String city = jsonObject.getAsJsonObject("result").getAsJsonObject("address_component")
                .get("city").getAsString();
        String district = jsonObject.getAsJsonObject("result").getAsJsonObject("address_component")
                .get("district").getAsString();
        String street = jsonObject.getAsJsonObject("result").getAsJsonObject("address_component")
                .get("street").getAsString();
        String street_number = jsonObject.getAsJsonObject("result").getAsJsonObject("address_component")
                .get("street_number").getAsString();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("openid", repair.getOpenid());
        saveMap.put("phoneNum", repair.getPhoneNum());
        saveMap.put("bid", repair.getBid() + "");
        saveMap.put("types;", Arrays.asList(repair.getTypes()).toString());
        saveMap.put("lat", repair.getLatitude() + "");
        saveMap.put("lon", repair.getLongitude() + "");
        saveMap.put("province", province);
        saveMap.put("city", city);
        saveMap.put("district", district);
        saveMap.put("street", street);
        saveMap.put("street_number", street_number);
        saveMap.put("time", new Date().getTime() + "");

        return saveMap;
    }
}
