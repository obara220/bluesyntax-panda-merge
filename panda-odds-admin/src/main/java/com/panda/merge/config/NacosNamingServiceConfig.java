//package com.panda.merge.config;
//
//import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.client.naming.NacosNamingService;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//
//@Configuration
//@AllArgsConstructor
//public class NacosNamingServiceConfig {
//
//    @Resource
//    private NacosDiscoveryProperties nacosDiscoveryProperties;
//
//
//    @Bean
//    public NacosNamingService nacosNamingService() throws NacosException {
//        return new NacosNamingService(nacosDiscoveryProperties.getNacosProperties());
//    }
//}
