package com.panda.merge.job;

import cn.hutool.core.util.IdUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.HeartMessage;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.producer.A99HeartbeatProducer;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Slf4j
@Component
@JobHandler(value = "A99HeartbeatJob")
public class A99HeartbeatJob extends IJobHandler {

    String HOST_ADDRESS = "";
    String KEY = Constant.REDIS_KEY.RONGHE_A99_HEARTBEAT;

    @Autowired
    RedisService redisService;

    @Autowired
    private A99HeartbeatProducer a99HeartbeatProducer;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始执行调度任务===>A99HeartbeatJob!,param=" + param);
            HeartMessage heartMessage = new HeartMessage();
            heartMessage.setDataSourceCode("A99");
            heartMessage.setTimestamp(System.currentTimeMillis());
            Request<HeartMessage> request = new Request<>();
            request.setData(heartMessage);
            String linkId = "A99_" + IdUtil.simpleUUID();
            request.setLinkId(linkId);
            request.setDataSourceTime(System.currentTimeMillis());
            a99HeartbeatProducer.sendA99Heartbeat(request);
        XxlJobLogger.log("结束执行调度任务===>A99HeartbeatJob!");
        return ReturnT.SUCCESS;
    }


   /* @Scheduled(initialDelay = 1000, fixedRate = 3000)
    public void execute() {
        if (StringUtils.isEmpty(HOST_ADDRESS)) {
            InetAddress address = null;
            try {
                address = getLocalHostExactAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HOST_ADDRESS = address.getHostAddress();
        }
        Object oldAddress = redisService.get(KEY);
        if (oldAddress == null || StringUtils.equals((String) oldAddress, HOST_ADDRESS)) {
            log.info("准备模拟A99心跳:{}", oldAddress);
            redisService.set(KEY, HOST_ADDRESS, RedisConfig.REDIS_SIXTY_SECOND);
            HeartMessage heartMessage = new HeartMessage();
            heartMessage.setDataSourceCode("A99");
            heartMessage.setTimestamp(System.currentTimeMillis());
            Request<HeartMessage> request = new Request<>();
            request.setData(heartMessage);
            String linkId = "A99_" + IdUtil.simpleUUID();
            request.setLinkId(linkId);
            request.setDataSourceTime(System.currentTimeMillis());
            a99HeartbeatProducer.sendA99Heartbeat(request);
        }
    }*/


}
