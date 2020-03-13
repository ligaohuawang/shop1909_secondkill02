package com.qf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.IGoodsImageMapper;
import com.qf.dao.IGoodsMapper;
import com.qf.dao.IGoodsSecondKillMapper;
import com.qf.entity.Goods;
import com.qf.entity.GoodsImages;
import com.qf.entity.GoodsSecondKill;
import com.qf.service.IGoodsService;
import com.qf.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "goods")
public class IGoodsServiceImpl implements IGoodsService {
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IGoodsImageMapper iGoodsImageMapper;
    @Autowired
    private IGoodsSecondKillMapper iGoodsSecondKillMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    @Override
    @Transactional
    @CacheEvict(key = "'kill_' + #goods.goodsKill.startTime.time", condition = "#goods.type == 2")
    public Integer addGoods(Goods goods) {
        //1.先添加入商品表，拿到回填的主键
        //2.添加入商品图片表
        //3.判断商品类型，如果是秒杀商品，入商品秒杀表
        iGoodsMapper.insert(goods);
        //添加封面
        GoodsImages goodsImages = new GoodsImages();
        goodsImages.setUrl(goods.getFmurl())
                .setGid(goods.getId())
                .setIsfengmian(1);
        iGoodsImageMapper.insert(goodsImages);
        //添加其他图片
        for (String otherUrl : goods.getOtherurls()) {
            GoodsImages goodsImages1 = new GoodsImages();
            goodsImages1.setUrl(otherUrl)
                    .setGid(goods.getId())
                    .setIsfengmian(0);
            iGoodsImageMapper.insert(goodsImages1);
        }

        //如果是秒杀商品
        if (goods.getType() == 2) {
            GoodsSecondKill goodsSecondKill = new GoodsSecondKill();
            goodsSecondKill = goods.getGoodsKill();
            goodsSecondKill.setGid(goods.getId());
            iGoodsSecondKillMapper.insert(goodsSecondKill);

            //将秒杀商品id放入redis集合中
            String timeSuffix = DateUtil.date2String(goodsSecondKill.getStartTime(), "yyMMddHH");
            stringRedisTemplate.opsForSet().add("killgoods_" + timeSuffix, goods.getId() + "");

        }
        //给交换机发送当前需要发布的秒杀商品的信息
        rabbitTemplate.convertAndSend("goods_exchange", "", goods);
        return 1;
    }

    /**
     * 查询商品列表
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Goods> goodsList() {
        List<Goods> goods = iGoodsMapper.selectList(null);
        for (Goods good : goods) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("gid", good.getId());
            List<GoodsImages> images = iGoodsImageMapper.selectList(queryWrapper);
            for (GoodsImages image : images) {
                if (image.getIsfengmian() == 1) {
                    good.setFmurl(image.getUrl());
                } else {
                    good.setOtherUrls(image.getUrl());
                }
            }

            if (good.getType() == 2) {
                GoodsSecondKill goodsSecondkill = iGoodsSecondKillMapper.selectOne(queryWrapper);
                good.setGoodsKill(goodsSecondkill);
            }
        }

        return goods;
    }


    /**
     * 查询秒杀场次对应的商品信息
     *
     * @param date
     * @return
     */
    @Override
    @Cacheable(key = "'kill_' + #date.time")
    public List<Goods> queryKillList(Date date) {
        System.out.println("查询了数据库！");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("start_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        List<GoodsSecondKill> list = iGoodsSecondKillMapper.selectList(queryWrapper);
        List<Goods> goodsList = new ArrayList<>();
        for (GoodsSecondKill goodsSecondKill : list) {
            Goods goods = iGoodsMapper.selectById(goodsSecondKill.getGid());
            //设置秒杀商品信息
            goods.setGoodsKill(goodsSecondKill);
            //查询图片
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("gid", goods.getId());
            List<GoodsImages> goodsImagesList = iGoodsImageMapper.selectList(queryWrapper1);

            for (GoodsImages image : goodsImagesList) {
                if (image.getIsfengmian() == 1) {
                    //是封面
                    goods.setFmurl(image.getUrl());
                } else {
                    //非封面
                    goods.setOtherUrls(image.getUrl());
                }
            }
            goodsList.add(goods);
        }
        return goodsList;
    }

    /**
     * 修改该秒杀商品的库存
     *
     * @param gid
     * @param gnumber
     */
    @Override
    public void updateKillSave(Integer gid, Integer gnumber) {
        //以下方式会有线程安全的问题：
       /* //1.先根据gid将该秒杀商品查出来
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("gid",gid);
        //秒杀商品为goodsSecondKill
        GoodsSecondKill goodsSecondKill = iGoodsSecondKillMapper.selectOne(queryWrapper);
        //2.修改库存
        goodsSecondKill.setKillSave(goodsSecondKill.getKillSave()-gnumber);
        //3.根据gid更改数据库该商品信息，保存该商品信息
        int result = iGoodsSecondKillMapper.update(goodsSecondKill, queryWrapper);*/

        //所以直接将修改库存和保存修改的信息在一条SQL语句中执行完成，数据库的增删改自带锁
        iGoodsSecondKillMapper.updateKillSave(gid, gnumber);
    }

    @Override
    public Goods queryById(Integer gid) {
        //查询所有商品
        Goods goods = iGoodsMapper.selectById(gid);

        //查询相关图片
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("gid", goods.getId());
        List<GoodsImages> images = iGoodsImageMapper.selectList(queryWrapper);

        for (GoodsImages image : images) {
            if (image.getIsfengmian() == 1) {
                //是封面
                goods.setFmurl(image.getUrl());
            } else {
                //非封面
                goods.setOtherUrls(image.getUrl());
            }
        }

        //处理秒杀信息
        if (goods.getType() == 2) {
            GoodsSecondKill goodsSecondkill = iGoodsSecondKillMapper.selectOne(queryWrapper);
            goods.setGoodsKill(goodsSecondkill);
        }

        return goods;
    }
}
