package com.qf.feign;

import com.qf.entity.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient("GOODS-SERVER")
public interface IGoodsFeign {
    /**
     * 添加商品
     *
     * @return
     */
    @RequestMapping("/goods/addGoods")
    public Integer addGoods(@RequestBody Goods goods);

    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping("/goods/goodsList")
    public List<Goods> goodsList();

    /**
     * 根据秒杀场次查询对应商品
     *
     * @param date
     * @return
     */
    @RequestMapping("/goods/queryKillList")
    List<Goods> queryKillList(@RequestBody Date date);

    /**
     * 根据id查询秒杀商品信息
     *
     * @param gid
     * @return
     */
    @RequestMapping("/goods/queryKillById")
    public Goods queryById(@RequestParam("gid") Integer gid);
}
