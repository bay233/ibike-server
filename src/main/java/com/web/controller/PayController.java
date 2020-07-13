package com.web.controller;


import com.bean.PayModel;
import com.service.PayService;
import com.web.model.JsonModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(value = "结算操作接口", tags = { "账单" })
public class PayController {
    @Autowired
    private PayService payService;

    @PostMapping("/ibike/payMoney")
    public @ResponseBody
    JsonModel payMoney(JsonModel jm, PayModel pm) {
        try {
            payService.pay(  pm );
            jm.setCode(1);
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(  e.getMessage());
        }
        return jm;
    }

}
