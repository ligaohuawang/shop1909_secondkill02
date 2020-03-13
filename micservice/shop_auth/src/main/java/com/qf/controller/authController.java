package com.qf.controller;

import com.alibaba.fastjson.JSON;
import com.qf.entity.ResultData;
import com.qf.entity.User;
import com.qf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/auth")
public class authController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 1.去登录页
     *
     * @return
     */
    @RequestMapping("/loginPage")
    public String loginPage() {
        return "login";
    }

    /**
     * 2.去注册页
     *
     * @return
     */
    @RequestMapping("/registerPage")
    public String registerPage() {
        return "register";
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @RequestMapping("/registerUser")
    public String registerUser(User user) {
        int result = iUserService.insertUser(user);
        if (result > 0) {
            return "login";
        } else {
            return "register";
        }
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @RequestMapping("/login")
    public String login(User user, HttpServletResponse response, String returnUrl) {
        //1.调用用户服务根据username查询用户信息
        User user2 = iUserService.selectUserByUsername(user.getUsername());
        //2.判断用户是否存在
        if (user2 != null && user2.getPassword().equals(user.getPassword())) {
            //2.1用户存在，将用户信息保存到redis
            String LoginSign = java.util.UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(LoginSign, user2);
            redisTemplate.expire(LoginSign, 7, TimeUnit.DAYS);
            //2.2返回cookie给浏览器
            Cookie cookie = new Cookie("LoginSign", LoginSign);
            cookie.setMaxAge(60 * 60 * 24 * 7);//单位是秒
            cookie.setPath("/");//路径
            cookie.setDomain("localhost");
            response.addCookie(cookie);
            //2.3如果returnUrl是空的
            if (returnUrl == null || returnUrl.equals("")) {
                //如果是空的就发请求给路由网关，回到首页
                returnUrl = "http://localhost:8081";
            }
            //编码
            try {
                returnUrl = URLEncoder.encode(returnUrl, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            //跳转到首页
            /*redirect:http://localhost:8085/cart/shopcartmerge?returnUrl=" + returnUrl*/
            return "redirect:http://localhost:8081";


        }
        //用户不存在，返回登录页
        return "redirect:auth/loginPage";
    }

    /**
     * 页面ajax发请求，统一登录认证
     *
     * @param LoginSign
     * @param callback
     * @return
     */
    @RequestMapping("/isLogin")
    @ResponseBody
    public String isLogin(@CookieValue(name = "LoginSign", required = false) String LoginSign, String callback) {
        ResultData<User> resultData = new ResultData<>();
        //1.先拿到Cookie
        System.out.println("收到Cookie：" + LoginSign);
        //2.先判断LoginSign是否为空，如果不为空在去redis查找
        if (LoginSign != null) {
            //3.根据Cookie即LoginSign去redis查找用户信息
            User user = (User) redisTemplate.opsForValue().get(LoginSign);
            //4.判断user是否为空，如果不为空就证明已经登录，如果为空就是cookie过期或者cookie无效
            if (user != null) {
                //已经登录
                resultData.setCode(ResultData.ResultCodeList.OK).setData(user);
            } else {
                //未登录
                resultData.setCode(ResultData.ResultCodeList.ERROR).setData(user);
            }
        }
        return callback != null ? callback + "(" + JSON.toJSONString(resultData) + ")" : JSON.toJSONString(resultData);
    }

    @RequestMapping("/logout")
    public String logout(@CookieValue(name = "LoginSign", required = false) String LoginSign, HttpServletResponse response) {
        System.out.println("注销请求：" + LoginSign);
        redisTemplate.delete(LoginSign);

        Cookie cookie = new Cookie("LoginSign", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);

        return "redirect:/auth/loginPage";

    }
}
