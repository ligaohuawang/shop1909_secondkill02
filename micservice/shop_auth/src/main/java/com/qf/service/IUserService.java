package com.qf.service;

import com.qf.entity.User;

public interface IUserService {
    public int insertUser(User user);

    public User selectUserByUsername(String username);

    public void updatePassword(String username, String newpassword);
}
