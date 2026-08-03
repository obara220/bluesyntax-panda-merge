//package com.panda.merge.component;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.alibaba.nacos.client.naming.NacosNamingService;
//import com.panda.merge.common.enums.Constant;
//import com.panda.merge.config.RedisService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.MapUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//public class NacosInstances {
//
//    private final NacosNamingService nacosNamingService;
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    @Autowired
//    private RedisService redisService;
//
//    public NacosInstances(NacosNamingService nacosNamingService) {
//        this.nacosNamingService = nacosNamingService;
//    }
//
//    @PostConstruct
//    public void init() throws NacosException {
//        String key = Constant.REDIS_KEY.RONGE_SERVE_IP + applicationName;
//        log.info("NacosInstances，key:{}", key);
//        List<Instance> allInstances = nacosNamingService.getAllInstances(applicationName);
//        log.info("NacosInstances:{}", JSONObject.toJSONString(allInstances));
//        Map<String, Integer> ipcacheMap = redisService.hGetAll(key);
//        //缓存不存在
//        if (MapUtils.isEmpty(ipcacheMap)) {
//            log.info("NacosInstances,缓存不存在:{}", JSONObject.toJSONString(allInstances));
//            cacheIPAndWorkerId(key);
//            return;
//        }
//        //nacos注册服务器根据ip分组 ,删除缓存不存在的ip
//        Map<String, Instance> allInstancesMap = allInstances.stream().collect(Collectors.toMap(Instance::getIp, thi -> thi));
//        for (String ipCacheKey : ipcacheMap.keySet()) {
//            //nacos注册服务器，在缓存中不存在代表旧数据，直接删除
//            Instance instance = allInstancesMap.get(ipCacheKey);
//            if (null == instance) {
//                redisService.hDel(key, ipCacheKey);
//                log.info("NacosInstances,nacos注册服务器，在缓存中不存在代表旧数据，直接删除:{}", ipCacheKey);
//            }
//        }
//    }
//
//    /**
//     * 缓存当前节点ip 设置 WorkerId
//     */
//    private void cacheIPAndWorkerId(String key) {
//        //获取缓存ip WorkerId
//        Map<String, Integer> ipcacheMap = redisService.hGetAll(key);
//        log.info("NacosInstances,key:{},ipcacheMap缓存信息：{}", key, ipcacheMap);
//        //获取当前ip
//        InetAddress address = getLocalHostExactAddress();
//        //ip获取异常不处理，获取WorkerId随机
//        if (null == address) {
//            log.info("NacosInstances,ip获取异常不处理，获取WorkerId随机");
//            return;
//        }
//        //循环0-31 ，对比 ipcacheMap是否存在，不存在当前节点ip就是i
//        for (int i = 0; i < 31; i++) {
//            boolean isTrue = ipcacheMap.containsValue(i);
//            if (!isTrue) {
//                redisService.hSet(key, address.getHostAddress(), i);
//                log.info("NacosInstances,缓存ip:{},和WorkerId:{}", address.getHostAddress(), i);
//                return;
//            }
//        }
//    }
//
//    /**
//     * 获取ip地址
//     *
//     * @return
//     */
//    public static InetAddress getLocalHostExactAddress() {
//        try {
//            InetAddress candidateAddress = null;
//            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
//            while (networkInterfaces.hasMoreElements()) {
//                NetworkInterface iface = networkInterfaces.nextElement();
//                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
//                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
//                    InetAddress inetAddr = inetAddrs.nextElement();
//                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
//                    if (!inetAddr.isLoopbackAddress()) {
//                        if (inetAddr.isSiteLocalAddress()) {
//                            // 如果是site-local地址，就是它了 就是我们要找的
//                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
//                            return inetAddr;
//                        }
//
//                        // 若不是site-local地址 那就记录下该地址当作候选
//                        if (candidateAddress == null) {
//                            candidateAddress = inetAddr;
//                        }
//
//                    }
//                }
//            }
//            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
//            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
