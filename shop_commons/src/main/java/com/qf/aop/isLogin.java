package com.qf.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)//表示修饰在方法上
@Retention(RetentionPolicy.RUNTIME)//需要用到反射来操作这个注解
public @interface isLogin {
    //方法，是否必须登录，默认为false
    boolean mustLogin() default false;
}
