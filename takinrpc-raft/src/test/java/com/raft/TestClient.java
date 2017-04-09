//package com.raft;
//
//import com.raft.handler.ClientHandler;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.serialization.ClassResolvers;
//import io.netty.handler.codec.serialization.ObjectDecoder;
//import io.netty.handler.codec.serialization.ObjectEncoder;
//
//import java.net.InetSocketAddress;
//
///**
// * Created by Administrator on 2016/10/31.
// */
//public class TestClient {
//    public static void main(String[] args) {
//        Bootstrap client = new Bootstrap();
//        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//        try {
//            client.group(eventLoopGroup);
//            client.channel(NioSocketChannel.class);
//            client.handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    socketChannel.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
//                    socketChannel.pipeline().addLast(new ObjectEncoder());
//                    socketChannel.pipeline().addLast(new TestHandler());
//                }
//            });
//
//            ChannelFuture future = client.connect(new InetSocketAddress("127.0.0.1", 1001)).sync();
//
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            eventLoopGroup.shutdownGracefully();
//        }
//    }
//}
