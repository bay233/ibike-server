package com.service;

import com.bean.User;

public interface UserService {
    /**
     * 获取验证码
     * @param nationCode
     * @param phoneNum
     * @throws Exception
     */
    void genVerifyCode(String nationCode, String phoneNum) throws Exception;

    /**
     * 绑定电话号码
     * @param user
     * @return
     */
    boolean verify(User user);

    /**
     * 押金充值
     * @param user
     * @return
     */
    boolean deposit(User user);

    /**
     * 验证用户身份证
     * @param user
     * @return
     */
    boolean identity(User user);

    /**
     * 充值
     * @param balance
     * @param phoneNum
     * @return
     */
    boolean recharge(double balance, String phoneNum);

    /**
     * 验证openid是否已经存在
     * @param openid
     * @return
     */
    User selectMember(String openid);

    /**
     * 添加一个用户
     * @param u
     */
    boolean addMember(User u);

    /**
     * 返回一个新生成的sessionKey
     * @param openId
     * @param sessionKey
     * @return
     */
    String redisSessionKey(String openId,String sessionKey);
}
