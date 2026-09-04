package com.panda.merge.task;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


@Slf4j
@Component
public class HeartBeatTask {
    String HOST_ADDRESS = "";
    String KEY = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_HEARTBEAT;
    @Autowired
    RedisService redisService;
    @Lazy
    @Autowired
    StandardMarketOddsProducer standardMarketOddsProducer;

    @Lazy
    @Autowired
    ThirdMatchMarketProcessor thirdMatchMarketProcessor;

//    public void test() {
//    	monitorConfigMatchStatus("damian_test");
//    }

    //@Scheduled(cron = "*/3 * * * * ?")
    @Scheduled(initialDelay = 1000,fixedRate = 3000)
    public void execute()
    {
        if (StringUtils.isEmpty(HOST_ADDRESS))
        {
            InetAddress address = null;
            try {
                address = getLocalHostExactAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HOST_ADDRESS = address.getHostAddress();
        }
        Object oldAddress = redisService.get(KEY);
        String linkId = UUIdUtils.getId()+"_HeartBeat";
        if (oldAddress == null || StringUtils.equals((String)oldAddress,HOST_ADDRESS))
        {
            redisService.set(KEY,HOST_ADDRESS,7);
            Long time = TimeUtils.millsSecondsEast8ZoneGmt();
            log.info("::{}::赔率服务全局心跳开始发送，时间戳：{} ", linkId, time);
            standardMarketOddsProducer.standardMarketOddsHeartBeatSend(linkId,time);
            thirdMatchMarketProcessor.checkLiveTime();
        }
    }
    @PostConstruct
    public void initAddress()
    {
        if (StringUtils.isEmpty(HOST_ADDRESS))
        {
            InetAddress address = null;
            try {
                address = getLocalHostExactAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HOST_ADDRESS = address.getHostAddress();
            redisService.set(KEY,HOST_ADDRESS,7);
        }
    }

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