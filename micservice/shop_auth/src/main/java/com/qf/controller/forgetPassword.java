package com.qf.controller;

/*import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Email;*/

import com.qf.entity.ResultData;
import com.qf.entity.User;
import com.qf.service.IUserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/forgetPassword")
public class forgetPassword {
/*    @Reference
    private IUserService iUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    *//**
     * 前往忘记密码页
     * @return
     *//*
    @RequestMapping("/toForgetPasswordPage")
    public String toForgetPasswordPage(){
       return "forgetpasswordpage";
    }

    *//**
     * 发送邮件给用户
     * @return
     *//*
    @RequestMapping("/sendmail")
    @ResponseBody
    public ResultData<Map<String, String>> sendmail(String username){
        //1.先根据用户名查询用户信息得到用户注册时的邮箱
        User user = iUserService.selectUserByUsername(username);
        //用户不存在
        if (null==user){
            return new ResultData<Map<String, String>>().setCode(ResultData.ResultCodeList.ERROR).setMsg("用户名不存在！");
        }else {//用户存在,发送邮件
           //1.生成uuid
            String uuid = UUID.randomUUID().toString();
            //2.注入元素到redis中
            redisTemplate.opsForValue().set(uuid, username);
            redisTemplate.expire(uuid, 5, TimeUnit.MINUTES);
            String url = "http://localhost:8084/forgetPassword/updatePasswordPage?token=" + uuid;
            //3.构建邮件
            Email email = new Email();
            //3.1用户的Email
            email.setTo(user.getEmail());
            //3.2标题
            email.setSubject("明天放假");
            //3.3内容
            email.setContext("点击<a href='" +url+ "'>这里</a>找回密码");
            //3.4发送时间
            email.setSendTime(new Date());
            //4.发送邮件对象到交换机，也可以理解为通知邮件服务发送邮件，并提供创建这封邮件的原料
            rabbitTemplate.convertAndSend("mail_exchange", "", email);
            //5.得到用户邮箱
            String userEmail = user.getEmail();
            //5.1从第三个字母开始截取（不包括）
            String replaceUserEmail = userEmail.substring(3,userEmail.lastIndexOf("@")+1);
            //5.2展示给用户的邮件信息
            String returnToUserEmail = userEmail.replace(replaceUserEmail, "******");
            //6.前往对应邮箱登录页
            String tomail = "mail." + userEmail.substring(userEmail.lastIndexOf("@") + 1);

            Map<String, String> map = new HashMap<>();
            map.put("returnToUserEmail", returnToUserEmail);
            map.put("tomail", tomail);
            return new ResultData<Map<String, String>>()
                    .setCode(ResultData.ResultCodeList.OK)
                    .setMsg("邮件发送成功！")
                    .setData(map);
        }
    }

    *//**修改密码页面
     *
     * @return
     *//*
    @RequestMapping("/updatePasswordPage")
    public String updatePassword(){
        return "updatepasswordpage";
    }

    @RequestMapping("/updatepassword")
    public String updateUserPassword(String newpassword,String token){
        //根据token查询用户名
        String username =redisTemplate.opsForValue().get(token);
        if (username==null){
           return "errorPage";
        }
        iUserService.updatePassword(username,newpassword);

        return "login";
    }*/
}
