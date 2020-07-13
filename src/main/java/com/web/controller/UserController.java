package com.web.controller;


import com.bean.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.service.UserService;
import com.web.model.JsonModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    private Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    private final String SECRET = "643ddf882e44f4a33300c9a6d0a7c23e";
    private final String APPID = "wx18828b5bf2caf663";
    private final String WXSERVER = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + SECRET + "&grant_type=authorization_code";

    @PostMapping("/ibike/onLogin")
    public @ResponseBody
    JsonModel onLogin(JsonModel jm, @RequestBody Map<String, String> value) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String wxurl = WXSERVER + "&js_code=" + value.get("jscode");
            String resp = restTemplate.getForEntity(wxurl, String.class).getBody();
            Map<String, String> map = new HashMap<>();
            map = new Gson().fromJson(resp, map.getClass());
            User user = userService.selectMember(map.get("openid"));
            if (user == null) {
                user = new User();
                user.setOpenid(map.get("openid"));
                user.setStatus(0);
                userService.addMember(user);
            }
            String sessionKey = userService.redisSessionKey(map.get("openid"), map.get("session_key"));
            Map<String, String> res = new HashMap<>();
            res.put("uuid", sessionKey);
            res.put("openid", map.get("openid"));
            res.put("status", user.getStatus() + "");
            res.put("phoneNum", user.getPhoneNum() + "");
            jm.setCode(1);
            jm.setObj(res);
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg("登陆失败！");
        }
        return jm;
    }

    @PostMapping("/ibike/genCode")
    public @ResponseBody
    JsonModel genSMSCode(JsonModel jm, @RequestBody Map<String, String> value) {
        try {
            // 生成4位随机数 -> 调用短信接口发送验证码 -> 将手机号对应的验证码保存到redis中，并且设置这个key的有效时长
            userService.genVerifyCode(value.get("nationCode"), value.get("phoneNum"));
            jm.setCode(1);
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @PostMapping("/ibike/verify")
    public @ResponseBody
    JsonModel verify(JsonModel jm, @RequestBody User user) {
        boolean flag = false;
        try {
            flag = userService.verify(user);
            if (flag) {
                jm.setCode(1);
            } else {
                jm.setCode(0);
                jm.setMsg("验证码错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @PostMapping("/ibike/deposit")
    public @ResponseBody
    JsonModel deposit(JsonModel jm, @RequestBody User user) {
        boolean flag = false;
        try {
            flag = userService.deposit(user);
            if (flag) {
                jm.setCode(1);
            } else {
                jm.setCode(0);
                jm.setMsg("意外错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }

    @PostMapping("/ibike/identity")
    public @ResponseBody
    JsonModel identity(JsonModel jm, @RequestBody User user) {
        boolean flag = false;
        try {
            flag = userService.identity(user);
            if (flag) {
                jm.setCode(1);
            } else {
                jm.setCode(0);
                jm.setMsg("错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }


    @PostMapping("/ibike/recharge")
    public @ResponseBody
    JsonModel recharge(JsonModel jm, @RequestBody User user) {
        boolean flag = false;
        try {
            flag = userService.recharge(user.getBalance(), user.getPhoneNum());
            if (flag) {
                jm.setCode(1);
            } else {
                jm.setCode(0);
                jm.setMsg("错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }
        return jm;
    }
}
