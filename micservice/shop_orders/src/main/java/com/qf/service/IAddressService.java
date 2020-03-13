package com.qf.service;

import com.qf.entity.Address;

public interface IAddressService {
    /**
     * 根据用户id查询出用户地址，默认一个用户只有一个地址
     *
     * @param uid
     * @return
     */
    Address selectAddressById(Integer uid);
}
