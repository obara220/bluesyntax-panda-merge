//package com.panda.merge.config;
//
//
//import com.panda.merge.constant.Constants;
//import com.panda.merge.handler.MergeWebsocketHandler;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpServerCodec;
//import io.netty.handler.stream.ChunkedWriteHandler;
//
//
//public class MergeWebsocketInitializer extends ChannelInitializer<SocketChannel> {
//
//    @Override
//    protected void initChannel(SocketChannel ch) throws Exception {
//        ch.pipeline().addLast(Constants.HTTP_CODEC, new HttpServerCodec());
//        ch.pipeline().addLast(Constants.AGGREGATOR, new HttpObjectAggregator(Constants.MAX_CONTENT_LENGTH));
//        ch.pipeline().addLast(Constants.HTTP_CHUNKED, new ChunkedWriteHandler());
//        ch.pipeline().addLast(Constants.HANDLER, new MergeWebsocketHandler());
//    }
//
//}
