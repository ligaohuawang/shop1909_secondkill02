package com.qf.listener;

import com.qf.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


@Component
public class MyRabbitListener {
    @Autowired
    private Configuration configuration;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = "kill_queue2")
    public void msgHandler(Goods goods) {
        //同步商品的库存到redis中
        stringRedisTemplate.opsForValue().set("gsave_" + goods.getId(), goods.getGoodsKill().getKillSave() + "");

        System.out.println("接收到消息生成静态页");
        //获得classpath路径
        String path = MyRabbitListener.class.getResource("/").getPath() + "static/html";
        System.out.println("classpath:" + path);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        //参数，文件+文件名
        file = new File(file, goods.getId() + ".html");

        try (
                //准备一个静态页的输出路径
                Writer out = new FileWriter(file)
        ) {
            Template template = configuration.getTemplate("kill.ftlh");
            Map<String, Object> map = new HashMap<>();
            map.put("goods", goods);

            //生成静态页
            template.process(map, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
