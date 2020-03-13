package com.qf.aop;

import com.qf.entity.User;

public class UserHolder {
    //new一个线程
    private static ThreadLocal<User> threadLocalUser = new ThreadLocal<>();

    //保存登录状态
    private static boolean isLogin() {
        return threadLocalUser.get() != null;
    }

    public static void setUser(User user) {
        UserHolder.threadLocalUser.set(user);
    }

    public static User getUser() {
        return threadLocalUser.get();
    }
}
