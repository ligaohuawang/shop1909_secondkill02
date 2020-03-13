package com.qf.handler;

import com.alibaba.fastjson.JSON;
import com.qf.entity.WsMsgEntity;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WebSocketMsgHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String msg = textWebSocketFrame.text();
        WsMsgEntity wsMsgEntity = JSON.parseObject(msg, WsMsgEntity.class);
        //把消息继续往后传递
        channelHandlerContext.fireChannelRead(wsMsgEntity);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端连接");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端断开连接");
    }
}
