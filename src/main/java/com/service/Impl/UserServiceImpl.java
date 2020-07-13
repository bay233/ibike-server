package com.service.Impl;

import com.bean.User;
import com.google.gson.Gson;
import com.mongodb.client.result.UpdateResult;
import com.service.UserService;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private StringRedisTemplate stringredisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;



    @Override
    public User selectMember(String openid) {
        Query q=new Query(
                Criteria.where("openid").is(openid) );
        return this.mongoTemplate.findOne(q, User.class,"users");
    }

    @Override
    public boolean addMember(User u) {
        User users = mongoTemplate.insert(u, "users");
        if (users != null){
            return true;
        }
        return false;
    }


    @Override
    public String redisSessionKey(String openId, String sessionKey) {
        String rsession = UUID.randomUUID().toString();
        // (3) 首先根据openId，取出来之前存的openId对应的sessionKey的值。
        String oldSeesionKey =stringredisTemplate.opsForValue().get(   openId );
        if (oldSeesionKey != null && !"".equals(oldSeesionKey)) {
            // (4) 删除之前openId对应的缓存
            stringredisTemplate.delete(    oldSeesionKey  );
        }
        // (5) 开始缓存新的sessionKey：  格式:  { uuid:{ "openId":openId,"sessionKey":sessionKey }  }
        Map<String,String> m=new HashMap<>();
        m.put("openId", openId);
        m.put("sessionKey", sessionKey);
        String s=new Gson().toJson( m);
        //stringRedisTemplate.opsForValue().set(rsession, s, 30*24*60*60, TimeUnit.SECONDS);
        stringredisTemplate.opsForValue().set(rsession, s, 5*60, TimeUnit.SECONDS);

        //开始缓存新的openId与session对应关系 ：  {openId: rsession}
        //stringRedisTemplate.opsForValue().set(openId, rsession, 30*24*60*60, TimeUnit.SECONDS);
        stringredisTemplate.opsForValue().set( openId, rsession, 5*60, TimeUnit.SECONDS);
        return rsession;
    }


    @Override
    public void genVerifyCode(String nationCode, String phoneNum) throws Exception {
        // redisTemplate.
        ValueOperations vo = stringredisTemplate.opsForValue();
        // 生成验证码
        String code = (int)((Math.random() * 9 + 1) * 1000) + "";

        // 发送短信
        // SmsUtils.sendSms(code, new String[] {nationCode+phoneNum});
        logger.info("接受到的号码为："+phoneNum);
        vo.set(phoneNum,code,120, TimeUnit.SECONDS);

    }

    @Override
    public boolean verify(User user) {
        boolean flag = false;
        String phoneNum = user.getPhoneNum();
        String verifyCode = user.getVerifyCode();
        String code = stringredisTemplate.opsForValue().get(phoneNum);
        logger.info("验证码数据:"+user);
        logger.info("请求的verifyCode:" + verifyCode +"\tredis中code:"+code);
        if (verifyCode != null && verifyCode.equals(code)){
            // 验证成功后，将用户信息保存到 mongo中
            mongoTemplate.updateFirst(new Query(Criteria.where("openid").is(user.getOpenid())),
                    new Update().set("status",1).set("phoneNum", phoneNum),
                    "users");
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean deposit(User user) {
        int status = 2;
        double money = 299.0;
        logger.info("修改数据:"+user);
        UpdateResult result = mongoTemplate.updateFirst(
                new Query(Criteria.where("openid").is(user.getOpenid())),
                        //.with(Sort.by(Sort.Order.desc("phoneNum"))),
                new Update().set("status",status).set("deposit", money),
                "users"
        );
        if (result.getModifiedCount() == 1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean identity(User user) {
        // TODO: 调用第三方接口验证用户身份证是否是真实的
        int status = 3;
        UpdateResult result = mongoTemplate.updateFirst(
                new Query(Criteria.where("openid").is(user.getOpenid())),
                new Update().set("status", status)
                        .set("name", user.getName())
                        .set("idNum", user.getIdNum()),
                "users"
        );
        if (result.getModifiedCount() == 1){
            return true;
        }

        return false;
    }

    @Override
    public boolean recharge(double balance, String phoneNum) {
        User user = mongoTemplate.findOne(new Query().addCriteria(Criteria.where("phoneNum").is(phoneNum)),
                User.class,
                "users");
        UpdateResult result = mongoTemplate.updateFirst(
                new Query(Criteria.where("phoneNum").is(phoneNum)),
                new Update().set("balance", user.getBalance() + balance),
                "users"
        );
        if (result.getModifiedCount() == 1){
            return true;
        }
        return false;
    }
}
