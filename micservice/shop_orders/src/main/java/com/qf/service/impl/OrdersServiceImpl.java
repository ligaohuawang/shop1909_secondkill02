package com.qf.service.impl;

import com.qf.dao.IOrderDetilsMapper;
import com.qf.dao.IOrderMapper;
import com.qf.entity.*;
import com.qf.feign.IGoodsFeign;
import com.qf.service.IAddressService;
import com.qf.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements IOrdersService {

    @Autowired
    private IGoodsFeign iGoodsFeign;
    @Autowired
    private IAddressService iAddressService;
    @Autowired
    private IOrderMapper iOrderMapper;
    @Autowired
    private IOrderDetilsMapper iOrderDetilsMapper;

    @Override
    public Orders createOrder(Integer[] cids, Integer aid, User user) {
        return null;
    }

    @Override
    public Orders QueryByOid(String orderid) {
        return null;
    }

    @Override
    public List<Orders> queryByUid(Integer id) {
        return null;
    }

    @Override
    public int updateOrderStatus(String orderid, Integer status) {
        return 0;
    }

    /**
     * 秒杀商品抢购成功创建订单及订单详情
     *
     * @param gid
     * @param uid
     * @param gnumber
     * @return
     */
    @Override
    @Transactional
    public int insertOrders(Integer gid, Integer uid, Integer gnumber) {
        //1.先根据商品id查询出商品信息,及根据用户id查询出收货地址
        Goods goods = iGoodsFeign.queryById(gid);
        Address address = iAddressService.selectAddressById(uid);
        //2.创建订单对象
        Orders orders = new Orders();
        orders.setCreateTime(new Date())
                .setStatus(0);
        orders.setOrderid(UUID.randomUUID().toString()).setUid(uid).setPerson(address.getPerson()).setAddress(address.getAddress()).setPhone(address.getPhone()).setCode(address.getCode()).setAllprice(goods.getGoodsKill().getKillPrice().multiply(BigDecimal.valueOf(gnumber)));
        iOrderMapper.insert(orders);
        //3.创建订单详情对象
        OrderDetils orderDetils = new OrderDetils();
        orderDetils.setCreateTime(new Date()).setStatus(0);
        orderDetils.setOid(orders.getId()).setGid(gid).setSubject(goods.getSubject()).setPrice(goods.getGoodsKill().getKillPrice()).setNumber(gnumber).setFongmianurl(goods.getFmurl()).setDetilsPrice(goods.getGoodsKill().getKillPrice().multiply(BigDecimal.valueOf(gnumber)));
        iOrderDetilsMapper.insert(orderDetils);
        return 1;
    }
}
