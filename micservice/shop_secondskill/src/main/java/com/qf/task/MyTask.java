package com.qf.task;

import com.alibaba.fastjson.JSON;
import com.qf.entity.Goods;
import com.qf.entity.WsMsgEntity;
import com.qf.feign.IGoodsFeign;
import com.qf.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MyTask {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IGoodsFeign iGoodsFeign;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * cron表达式：秒 分 时 日 月 [星期] 年
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void mytask() {

        //------------------------更新当前的秒杀场次--------------------------------
        //获取原来的场次,并且删除
        String oldtime = redisTemplate.opsForValue().get("killgoods_now");
        redisTemplate.delete("killgoods_" + oldtime);

        //更新redis中的场次时间
        String time = DateUtil.date2String(new Date(), "yyMMddHH");
        redisTemplate.opsForValue().set("killgoods_now", time);

    }

    /**
     * 表示每到50分时触发一次,开始进行秒杀提醒。2:50,3:50
     */
    @Scheduled(cron = "0 50 * * * *")
    public void mytask2() {
        //获得现在的时间，50分的
        Date date = new Date();
        //根据当前时间获得yyMMdd
        String yyMMdd = DateUtil.date2String(date, "yyMMdd");
        //根据当前时间获得HHmm
        String hhmm = DateUtil.date2String(date, "HHmm");
        while (true) {
            //拿到这些要提醒的消息
            Set<String> JsonStrings = redisTemplate.opsForZSet().rangeByScore("tixing_" + yyMMdd, Double.valueOf(hhmm), Double.valueOf(hhmm), 0, 100);
            //移除这些要提醒的消息
            redisTemplate.opsForZSet().remove("tixing_" + yyMMdd, JsonStrings.toArray());
            if (JsonStrings == null || JsonStrings.size() == 0) {
                //如果这个批次的消息被拿完
                break;
            }
            //循环推送消息
            for (String content : JsonStrings) {
                //因为拿到的是Json字符串，所以要转成对象
                Map map = JSON.parseObject(content, HashMap.class);
                Integer gid = (Integer) map.get("gid");
                Goods goods = iGoodsFeign.queryById(gid);
                map.put("good", goods);
                //将消息封装成固定的消息对象
                WsMsgEntity wsMsgEntity = new WsMsgEntity();
                wsMsgEntity.setFromid(-1);//-1是系统发送
                wsMsgEntity.setToid((Integer) map.get("uid"));
                wsMsgEntity.setType(3);
                wsMsgEntity.setData(map);
                //将map对象推送给对应的客户端
                rabbitTemplate.convertAndSend("netty_exchange", "", wsMsgEntity);
                System.out.println("给交换机发送消息" + wsMsgEntity);
            }
        }

    }
}
