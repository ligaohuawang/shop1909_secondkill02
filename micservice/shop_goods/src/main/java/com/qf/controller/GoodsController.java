package com.qf.controller;

import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService iGoodsService;

    /**
     * 添加商品的目标方法
     *
     * @return
     */
    @RequestMapping("/addGoods")
    public Integer addGoods(@RequestBody Goods goods) {

        return iGoodsService.addGoods(goods);
    }

    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping("/goodsList")
    public List<Goods> goodsList() {
        List<Goods> goodsList = iGoodsService.goodsList();
        System.out.println("商品服务查询到商品列表" + goodsList);
        return goodsList;
    }

    /**
     * 查询对应秒杀场次的商品信息
     *
     * @param date
     * @return
     */
    @RequestMapping("/queryKillList")
    public List<Goods> queryKillList(@RequestBody Date date) {
        List<Goods> goodsList = iGoodsService.queryKillList(date);
        return goodsList;
    }

    /**
     * 根据id查询秒杀商品信息
     *
     * @param gid
     * @return
     */
    @RequestMapping("/queryKillById")
    public Goods queryById(@RequestParam("gid") Integer gid) {
        return iGoodsService.queryById(gid);
    }
}
