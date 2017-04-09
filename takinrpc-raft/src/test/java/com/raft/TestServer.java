//package com.raft;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.serialization.ClassResolvers;
//import io.netty.handler.codec.serialization.ObjectDecoder;
//import io.netty.handler.codec.serialization.ObjectEncoder;
//
///**
// * Created by Administrator on 2016/10/31.
// */
//public class TestServer {
//    public static void main(String[] args) {
//        final ServerBootstrap server = new ServerBootstrap();
//        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//        try {
//            server.group(eventLoopGroup);
//            server.channel(NioServerSocketChannel.class);
//            server.childHandler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    socketChannel.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
//                    socketChannel.pipeline().addLast(new ObjectEncoder());
//                    socketChannel.pipeline().addLast(new TestHandler());
//                }
//            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
//
//            ChannelFuture future = server.bind("127.0.0.1", 1001).sync();
//
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            eventLoopGroup.shutdownGracefully();
//        }
//    }
//}
