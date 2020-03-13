package com.qf.handler;

import com.alibaba.fastjson.JSON;
import com.qf.entity.WsMsgEntity;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WebSocketOutMsgHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //msg是要写出去的消息。对象的比较用instanceof
        if (msg instanceof WsMsgEntity) {
            String string = JSON.toJSONString(msg);
            //转成文本帧向外传递
            super.write(ctx, new TextWebSocketFrame(string), promise);
        } else {
            super.write(ctx, msg, promise);
        }

    }
}
