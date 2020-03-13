package com.qf.controller;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.qf.aop.UserHolder;
import com.qf.aop.isLogin;
import com.qf.entity.Goods;
import com.qf.entity.ResultData;
import com.qf.entity.User;
import com.qf.feign.IGoodsFeign;
import com.qf.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/kill")
public class SecondsKillController {

    @Autowired
    private IGoodsFeign iGoodsFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String lua = "--1.获取参数\n" +
            "local gid = KEYS[1]\n" +
            "local gnumber = tonumber(ARGV[1])\n" +
            "\n" +
            "--2.获得库存\n" +
            "local gsave = tonumber(redis.call('get','gsave_'..gid))\n" +
            "--3.判断库存\n" +
            "if gsave<gnumber then\n" +
            "--库存不足\n" +
            "return -1\n" +
            "end\n" +
            "\n" +
            "--库存充足就修改库存\n" +
            "local newSave = tonumber(redis.call('decrby'), 'gsave_'..gid, gnumber)\n" +
            "if newSave>0 then\n" +
            "--还没有抢完\n" +
            "return 1\n" +
            "else\n" +
            "--抢完了\n" +
            "return 2\n" +
            "end";

    private String lua2 = "--获得参数\n" +
            "local gid = KEYS[1]\n" +
            "local gnumber = tonumber(ARGV[1])\n" +
            "local uid = ARGV[2]\n" +
            "local now = tonumber(ARGV[3])\n" +
            "\n" +
            "--获得库存\n" +
            "local gsave = tonumber(redis.call('get', 'gsave_'..gid))\n" +
            "--判断库存\n" +
            "if gsave < gnumber then\n" +
            "\t--库存不足\n" +
            "\treturn -1\n" +
            "end\n" +
            "\n" +
            "--修改库存\n" +
            "local newSave = tonumber(redis.call('decrby', 'gsave_'..gid, gnumber))\n" +
            "--记录排队位置\n" +
            "redis.call('zadd', 'paidui_'..gid, now, uid)\n" +
            "\n" +
            "--抢购成功\n" +
            "return 1";

    /**
     * 获得秒杀的场次
     *
     * @return
     */
    @RequestMapping("/getKillTimes")
    @ResponseBody
    public ResultData<List<Date>> getKillTimes() {
        List<Date> dates = new ArrayList<>();

        //获得当前时间
        Date now = DateUtil.getNextNDate(0);
        //获得下一个小时的时间
        Date next1 = DateUtil.getNextNDate(1);
        //获得下下个小时的时间
        Date next2 = DateUtil.getNextNDate(2);
        dates.add(now);
        dates.add(next1);
        dates.add(next2);
        System.out.println(dates);

        return new ResultData<List<Date>>().setCode(ResultData.ResultCodeList.OK).setData(dates);
    }

    /***
     * 获取对应场次的秒杀商品
     * @param n
     * @return
     */
    @RequestMapping("/queryKillList")
    @ResponseBody
    public ResultData<List<Goods>> queryGoodsListByKillTime(Integer n) {
        //1.得到场次时间
        Date date = DateUtil.getNextNDate(n);
        List<Goods> goodsList = iGoodsFeign.queryKillList(date);
        return new ResultData<List<Goods>>().setCode(ResultData.ResultCodeList.OK).setData(goodsList);
    }

    /**
     * 获取服务器的当前时间
     *
     * @return
     */
    @RequestMapping("/queryNowTime")
    @ResponseBody
    public ResultData<Date> queryNowTime() {
        return new ResultData<Date>().setCode(ResultData.ResultCodeList.OK).setData(new Date());
    }

    /**
     * 抢购商品
     *
     * @param gid
     * @return
     */
    @isLogin(mustLogin = true)
    @RequestMapping("/quQiangGo")
    public String quQiangGo(Integer gid, Integer gnumber, Model model) {
        if (gnumber == null || gnumber == 0) {
            gnumber = 1;
        }
        /*String code, @CookieValue(name = "codeToken", required = false) String codeToken*/
        //判断验证码功能
     /*   String ocode = redisTemplate.opsForValue().get(codeToken);
        if(code == null || !code.equals(ocode)){
            return "error2";
        }*/

      /*  //1.拿到当前场次
       String time = (String) redisTemplate.opsForValue().get("killgoods_now");
        System.out.println("当前场次"+time);
        boolean flag = false;
        if(time != null){
            //2.判断用户抢购的商品id是否存在当前场次中
            flag = redisTemplate.opsForSet().isMember("killgoods_" + time, gid + "");
            System.out.println("flag为"+flag);
        }
      *//* Long result =redisTemplate.execute(new DefaultRedisScript<>(lua,Long.class),null,gid+"");*//*
        if(!flag){
            //秒杀商品未开始秒杀
            System.out.println("秒杀商品没有开始哦");
            return "error";
        }*/
        User user = UserHolder.getUser();//登录的用户信息
        System.out.println(user.getUsername() + "抢购了" + gid + "商品");
        //执行lua脚本
        //判定
        long result = redisTemplate.execute(
                new DefaultRedisScript<>(lua2, Long.class),
                Collections.singletonList(gid + ""),
                gnumber + "",
                user.getId() + "",
                System.currentTimeMillis() + "");
        //判断是否抢购成功
        if (result != -1) {
            Map<String, Object> map = new HashMap<>();
            map.put("gid", gid);
            map.put("gnumber", gnumber);
            map.put("uid", user.getId());

            //将商品信息放入rabbitmq中
            rabbitTemplate.convertAndSend("kill_exchange", "", map);
            model.addAttribute("gid", gid);
            return "paidui";
        }

        return "fail";
    }

    /**
     * 获得验证码
     */
    @RequestMapping("/code")
    public void getCode(HttpServletResponse response) {
        //验证码的文本
        String text = defaultKaptcha.createText();
        //根据验证码的文本生成图片
        BufferedImage image = defaultKaptcha.createImage(text);

        //将验证码的值存入redis
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, text);
        redisTemplate.expire(token, 1, TimeUnit.MINUTES);

        //将uuid写入用户的cookid
        Cookie cookie = new Cookie("codeToken", token);
        cookie.setMaxAge(60);
        cookie.setPath("/");
        response.addCookie(cookie);

        //将二维码图片设置到浏览器端
        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得当前排队位置
     *
     * @return
     */
    @RequestMapping("/getRank")
    @isLogin
    @ResponseBody
    public ResultData<String> getRank(Integer gid) {
        //获得当前的用户信息
        User user = UserHolder.getUser();

        //获得排名
        Long rank = redisTemplate.opsForZSet().rank("paidui_" + gid, user.getId() + "");
        System.out.println(user.getId() + "当前排名：" + rank);
        if (rank == null) {
            //没有排名
            return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setData("抢购成功！");
        }

        //当前正在排队
        return new ResultData<String>().setCode(ResultData.ResultCodeList.ERROR).setData((rank + 1) + "");
    }

    @RequestMapping("/tixing")
    @ResponseBody
    @isLogin(mustLogin = true)
    public ResultData<String> tixing(Integer gid, Integer flag) {
        //得到登录的用户
        User user = UserHolder.getUser();
        //根据商品id查询商品信息
        Goods goods = iGoodsFeign.queryById(gid);
        //秒杀开始时间
        Date startTime = goods.getGoodsKill().getStartTime();
        //将时间-10分钟得到提醒的时间
        //先得到一个日历对象
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.set(Calendar.MINUTE, -10);
        //得到提醒的时间
        Date tixingTime = calendar.getTime();
        //得到当天，比如200109，tingxing_200109作为key,存入redis
        String yyMMdd = DateUtil.date2String(tixingTime, "yyMMdd");
        //拿到小时和分钟做评分例如1450
        String HHmm = DateUtil.date2String(tixingTime, "HHmm");
        //提醒的内容
        Map<String, Integer> map = new HashMap<>();
        map.put("uid", user.getId());
        map.put("gid", gid);

        if (flag == 1) {
            //设置提醒
            redisTemplate.opsForZSet().add("tixing_" + yyMMdd, JSON.toJSONString(map), Double.valueOf(HHmm));

        } else {
            //取消提醒
            redisTemplate.opsForZSet().remove("tixing_" + yyMMdd, JSON.toJSONString(map));
        }

        return new ResultData<String>().setCode(ResultData.ResultCodeList.OK);

    }


}
