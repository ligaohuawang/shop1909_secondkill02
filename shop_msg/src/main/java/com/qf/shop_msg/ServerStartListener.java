package com.qf.shop_msg;

import com.qf.handler.WebSocketHeartHandler;
import com.qf.handler.WebSocketInitHandler;
import com.qf.handler.WebSocketMsgHandler;
import com.qf.handler.WebSocketOutMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ServerStartListener implements CommandLineRunner {
    @Value("${server.port}")
    private int port;
    @Autowired
    private WebSocketMsgHandler webSocketMsgHandler;

    @Autowired
    private WebSocketHeartHandler webSocketHeartHandler;

    @Autowired
    private WebSocketOutMsgHandler webSocketOutMsgHandler;

    //CommandLineRunner命令行启动
    @Override
    public void run(String... args) throws Exception {
        System.out.println("一启动这个msg工程，这个方法就会触发");
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(master, slave)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/msg"));
                        //客户端连接超时处理器，哪个客户端超过5秒钟没有给服务器发送任何消息，就断开该客户端
                        pipeline.addLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS));
                        pipeline.addLast(webSocketOutMsgHandler);
                        pipeline.addLast(webSocketMsgHandler);
                        pipeline.addLast(new WebSocketInitHandler());
                        pipeline.addLast(webSocketHeartHandler);
                    }
                });
        serverBootstrap.bind(port).sync();
        System.out.println("服务器已经启动，端口：" + port);
    }
}
