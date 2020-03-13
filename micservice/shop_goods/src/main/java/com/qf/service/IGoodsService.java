package com.qf.service;

import com.qf.entity.Goods;

import java.util.Date;
import java.util.List;

public interface IGoodsService {
    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    Integer addGoods(Goods goods);

    /**
     * 查询商品列表
     *
     * @return
     */
    List<Goods> goodsList();

    /**
     * 查询秒杀场次对应的商品信息
     *
     * @param date
     * @return
     */
    List<Goods> queryKillList(Date date);

    /**
     * 修改库存
     *
     * @param gid
     * @param gnumber
     */
    void updateKillSave(Integer gid, Integer gnumber);

    /**
     * 根据id查询秒杀商品信息
     *
     * @param gid
     * @return
     */
    Goods queryById(Integer gid);
}
