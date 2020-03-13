package com.qf.aop;

import com.qf.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

@Component
@Aspect
public class LoginAop {

    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(isLogin)")//1.会增强加上了isLogin注解的方法
    public Object isLogin(ProceedingJoinPoint proceedingJoinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Cookie[] cookies = request.getCookies();
        String cookie = null;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("LoginSign")) {
                    cookie = cookies[i].getValue();
                    break;
                }
            }
        }
        if (cookie != null) {
            User user = (User) redisTemplate.opsForValue().get(cookie);
            if (user != null) {
                UserHolder.setUser(user);
            } else {
                //不做处理
            }
        } else {
            //通过反射获取isLogin
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = methodSignature.getMethod();
            isLogin islogin = method.getAnnotation(isLogin.class);
            //获取通过反射获取isLogin的mustLogin方法的值
            boolean mustLogin = islogin.mustLogin();
            if (mustLogin) {
                String returnUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
                //编码url
                try {
                    returnUrl = URLEncoder.encode(returnUrl, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //强制跳转到登录页面
                String loginUrl = "http://localhost:8081/auth/loginPage?returnUrl=" + returnUrl;
                return "redirect:" + loginUrl;
            }
        }
        Object result = null;
        try {
            //执行目标方法及返回值，就是在这里去调用目标方法的
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //清空threadlocal
        UserHolder.setUser(null);
        return result;
    }
}


//1.获得cookie
//2.先判断cookie是否为空。根据cookie去redis查询用户
//3.如果用户为空
//3.1判断mustLogin的值
//3.1.1如果true强制去登录页面
//3.1.2为false，不做任何处理
//4.如果用户不为空
//4.1存储用户的信息，方便controller拿到做相应处理
