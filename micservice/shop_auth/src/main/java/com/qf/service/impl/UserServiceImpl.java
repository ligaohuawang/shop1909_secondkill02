package com.qf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.IUserMapper;
import com.qf.entity.User;
import com.qf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserMapper iUserMapper;

    @Override
    public int insertUser(User user) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        Integer count = iUserMapper.selectCount(queryWrapper);
        if (count == 0) {
            return iUserMapper.insert(user);

        }
        return -1;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    @Override
    public User selectUserByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return iUserMapper.selectOne(queryWrapper);
    }

    /**
     * 修改用户密码
     *
     * @param username
     * @param newpassword
     */
    @Override
    public void updatePassword(String username, String newpassword) {
        //先根据用户名称查询用户信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);

        User user = iUserMapper.selectOne(queryWrapper);

        //设置用户密码
        user.setPassword(newpassword);
        //根据用户id覆盖数据库中原有用户
        iUserMapper.updateById(user);
    }
}
