package com.panda.merge.config;


import cn.hutool.db.Session;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyConfig {
    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public final static ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 本地存储一份map <channel, 1>
     */
    public final static Map<String,String> LOCALCHANNELMAP = new ConcurrentHashMap<>();

    /**
     * 本地存储一份map <tokey, channel>  送达到key，channel
     */
    public final static Map<String, List<String>> LOCALCHANNELLISTMAP = new ConcurrentHashMap<>();


    /**
     * 本地的channelId对应的请求参数的保存
     */
    public final static Map<Object, Object> LOCALCHANNELRRQUEST = new ConcurrentHashMap<>();
}
