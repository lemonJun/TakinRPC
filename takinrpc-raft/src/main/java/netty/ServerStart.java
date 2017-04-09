package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by ywjay on 17/2/23.
 */
public class ServerStart {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();

        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.option(ChannelOption.SO_BACKLOG, 100);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new LoggingHandler());
        bootstrap.childHandler(new ChannelInitializer<ServerChannel>() {
            protected void initChannel(ServerChannel serverChannel) throws Exception {
                serverChannel.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                serverChannel.pipeline().addLast(new ObjectEncoder());
            }
        });

        ChannelFuture future = null;
        try {
            future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            group.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
