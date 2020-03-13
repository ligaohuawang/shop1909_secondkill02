package com.qf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.IAddressMapper;
import com.qf.entity.Address;
import com.qf.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    private IAddressMapper iAddressMapper;

    /**
     * 根据用户id查询出用户地址，默认一个用户只有一个地址
     *
     * @param uid
     * @return
     */
    @Override
    public Address selectAddressById(Integer uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid", uid);
        Address address = iAddressMapper.selectOne(queryWrapper);
        return address;
    }
}
