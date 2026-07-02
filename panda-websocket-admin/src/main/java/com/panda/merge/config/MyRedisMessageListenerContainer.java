//package com.panda.merge.config;
//
//import com.panda.merge.handler.PDSubcribe;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//
//@Configuration
//public class MyRedisMessageListenerContainer {
//    @Bean
//    MessageListenerAdapter messageListener() {
//        return new MessageListenerAdapter(new PDSubcribe());
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisContainer(RedisConnectionFactory factory) {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(factory);
//        container.addMessageListener(messageListener(), new ChannelTopic("PD_FOOTBALL_SCORE"));
//        container.addMessageListener(messageListener(), new ChannelTopic("PD_FOOTBALL_EVENT"));
//        return container;
//    }
//
//}