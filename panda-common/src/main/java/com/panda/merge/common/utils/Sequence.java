package com.panda.merge.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author :  Jimmy
 * @Project Name :  panda_data_realtime_marketodds
 * @Package Name :  com.panda.sport.data.realtime.utils
 * @Description :  TODO
 * @Date: 2020-02-12 17:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class Sequence {

    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private final long twepoch = 1288834974657L;
    /**
     * 机器标识位数
     */
    private final long workerIdBits = 10L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 毫秒内自增位
     */
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;

    /**
     * 时间戳左移动位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private final long workerId;

    /**
     * 并发控制
     */
    private long sequence = 0L;
    /**
     * 上次生产 ID 时间戳
     */
    private long lastTimestamp = -1L;

    public Sequence() {
        this.workerId = getWorkId(maxWorkerId);
        log.info("Sequence workId is 1 {}", this.workerId);
    }

    public Sequence(long workerId) {
        if (workerId == 1025) {
            this.workerId = getWorkId(maxWorkerId);
        } else {
            this.workerId = workerId;
        }
        log.info("Sequence workId is 2 {}", this.workerId);
    }

    /**
     * 获取 maxWorkerId
     */
    protected static long getWorkId(long maxWorkerId) {
        long id = 0L;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String serverIpStr = addr.getHostAddress().replace(".","");
            long serverIpNum = Long.valueOf(serverIpStr);
            id = serverIpNum % (maxWorkerId + 1);
        } catch (Exception e) {
            log.warn(" getDataCenterId: " + e);
        }
        return id;
    }

    /**
     * 获取下一个 ID
     *
     * @return 下一个 ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        //闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;
        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        long id = ((timestamp - twepoch) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence ;
        return id;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }


    /**获取Linux的IP*/
    public static String getLinuxLocalIp() {
        String ip ="";
        try {
            ip = System.getenv("POD_ID");
            if(StringUtils.isNotBlank(ip)){
                log.info("由POD_ID获取ip:{}",ip);
                return ip;
            }
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            ip = "127.0.0.1";
            log.error("获取ip异常",ex);
        }
        return ip;
    }
}
