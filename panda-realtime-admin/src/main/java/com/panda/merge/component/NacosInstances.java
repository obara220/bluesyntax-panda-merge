package com.panda.merge.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NacosInstances implements CommandLineRunner {

    private final static Integer MAX_MACHINE_NUMBER = 1024;
    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UUIdUtils uuIdUtils;

    @Override
    public void run(String... args) throws NacosException {
        String key = Constant.REDIS_KEY.RONGE_SERVE_IP + applicationName;
        String redisLocKey = "NacosInstances";
        String lockValue = UUIdUtils.getId() + "_" + System.currentTimeMillis();
        try {
            log.info("NacosInstances,准备获取锁，key:{}", key);
            redisService.tryLock(redisLocKey, lockValue, 3, 3);
            log.info("NacosInstances,准备获取锁，成功，key:{}", key);
            List<Instance> allInstances = namingService.getAllInstances(applicationName);
            Map<String, String> ipcacheMap = redisService.hGetAll(key);
            log.info("NacosInstances:{} ,ipcacheMap:{}", JSONObject.toJSONString(allInstances), JSONObject.toJSONString(ipcacheMap));
            //缓存不存在
            if (MapUtils.isEmpty(ipcacheMap)) {
                log.info("NacosInstances,缓存不存在:{}", JSONObject.toJSONString(allInstances));
                cacheIPAndWorkerId(key);
                return;
            }
            //nacos注册服务器根据ip分组 ,删除缓存不存在的ip
            Map<String, Instance> allInstancesMap = allInstances.stream().collect(Collectors.toMap(Instance::getIp, thi -> thi));
            for (Map.Entry<String, String> entry : ipcacheMap.entrySet()) {
                String ipCacheKey = entry.getKey();
                String[] split = entry.getValue().split(",");
                //小于1分钟不删除
                if (split.length == 2) {
                    Long time = Long.valueOf(split[1]);
                    if (System.currentTimeMillis() - time < 60000) {
                        log.info("NacosInstances,nacos注册服务器，小于1分钟不处理:{}", ipCacheKey);
                        continue;
                    }
                }
                //nacos注册服务器，在缓存中不存在代表旧数据，直接删除
                Instance instance = allInstancesMap.get(ipCacheKey);
                if (null == instance) {
                    redisService.hDel(key, ipCacheKey);
                    log.info("NacosInstances,nacos注册服务器，在缓存中不存在代表旧数据，直接删除:{}", ipCacheKey);
                }
            }
            cacheIPAndWorkerId(key);
        } finally {
            redisService.unLock(redisLocKey, lockValue);
            log.info("NacosInstances,准备获取锁，释放成功，key:{}", key);
        }

    }

    /**
     * 缓存当前节点ip 设置 WorkerId
     */
    private void cacheIPAndWorkerId(String key) {
        //获取缓存ip WorkerId + 时间戳
        Map<String, String> ipcacheMap = redisService.hGetAll(key);
        //转换为 缓存ip WorkerId
        Map<String, Integer> convertMap = convertMap(ipcacheMap);
        log.info("NacosInstances,key:{},ipcacheMap缓存信息：{},convertMap:{}", key, ipcacheMap, convertMap);
        //获取当前ip
        InetAddress address = getLocalHostExactAddress();
        //ip获取异常不处理，获取WorkerId随机
        if (null == address) {
            Integer randomWorkerId = getProcessId();
            uuIdUtils.setWorkerId(randomWorkerId);
            log.info("NacosInstances,ip获取异常不处理，获取WorkerId随机：{}", randomWorkerId);
            return;
        }
        //循环0-31 ，对比 ipcacheMap是否存在，不存在当前节点ip就是i
        for (int i = 0; i < MAX_MACHINE_NUMBER; i++) {
            boolean isTrue = convertMap.containsValue(i);
            if (!isTrue) {
                String value = i + "," + System.currentTimeMillis();
                redisService.hSet(key, address.getHostAddress(), value);
                uuIdUtils.setWorkerId(i);
                log.info("NacosInstances,缓存ip:{},和WorkerId:{}", address.getHostAddress(), i);
                return;
            }
        }
    }

    private static Map<String, Integer> convertMap(Map<String, String> ipcacheMap) {
        Map<String, Integer> map = new HashMap<>();
        if (MapUtils.isEmpty(ipcacheMap)) {
            return map;
        }
        for (Map.Entry<String, String> entry : ipcacheMap.entrySet()) {
            String[] value = entry.getValue().split(",");
            if (value.length == 2) {
                map.put(entry.getKey(), Integer.parseInt(value[0]));
            }
        }
        return map;
    }

    private static Integer getProcessId() {
        try {
            Random random = new Random();//默认构造方法
            int i = random.nextInt(MAX_MACHINE_NUMBER);
            log.info("NacosInstances,雪花算法随机初始化:" + i);
            return i;
        } catch (Exception e) {
            log.info("NacosInstances,雪花算法随机初始化，异常:" + 11);
            return 11;
        }
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static InetAddress getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }

                    }
                }
            }
            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
