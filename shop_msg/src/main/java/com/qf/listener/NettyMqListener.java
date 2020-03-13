package com.qf.listener;

import com.qf.entity.WsMsgEntity;
import com.qf.util.ChannelUtil;
import io.netty.channel.Channel;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NettyMqListener {
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "netty_queue_${server.port}", durable = "true"),
                    exchange = @Exchange(name = "netty_exchange", type = "fanout", durable = "true")
            )
    )
    public void mesHandler(WsMsgEntity wsMsgEntity) {
        Integer toid = wsMsgEntity.getToid();
        System.out.println("有一个消息要发送给id为" + toid + "的用户");
        Channel channel = ChannelUtil.getChannel(toid);
        if (channel != null) {
            channel.writeAndFlush(wsMsgEntity);
        }
    }
}
