package com.qf.handler;

import com.qf.entity.WsMsgEntity;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

/**
 * 心跳消息处理器,出站消息的处理器
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHeartHandler extends SimpleChannelInboundHandler<WsMsgEntity> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WsMsgEntity wsMsgEntity) throws Exception {
        if (wsMsgEntity.getType() == 2) {
            channelHandlerContext.writeAndFlush(wsMsgEntity);
        } else {
            //继续往后传递
            channelHandlerContext.fireChannelRead(wsMsgEntity);
        }
    }
}
