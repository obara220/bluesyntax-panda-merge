package com.panda.merge.task;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.StandardSportMarketNewService;
import com.panda.merge.service.StandardSportMarketService;
import com.panda.merge.service.ThirdSportMarketNewService;
import com.panda.merge.service.ThirdSportMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class OutrightMarketAlarmTask extends BaseTask {
    @Autowired
    RedisService redisService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;
    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;
    String HOST_ADDRESS = "";
    String KEY = Constant.REDIS_KEY.RONGHE_STANDARD_OUTRIGHT_MARKETALARMTASK;

    @Scheduled(initialDelay = 1000, fixedRate = 900000)
    //@Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        try {
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
                log.info("冠军警报开始");
                redisService.set(KEY, HOST_ADDRESS, RedisConfig.REDIS_SIXTY_SECOND);
                Set<String> keys = null;
                keys = (Set<String>) redisService.get(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM_K);
                if (null != keys) {
                    for (String key : keys) {
                        Map<String, Object> alarmMap = redisService.hGetAll(key);
                        if (!CollectionUtils.isEmpty(alarmMap)) {
                            String linkId = String.valueOf(alarmMap.get("linkId"));
                            String param = String.valueOf(alarmMap.get("param"));
                            long standardMatchInfoId = Long.valueOf(String.valueOf(alarmMap.get("standardMatchInfoId")));
                            //获取第三方盘口ID
                            String thirdMarketSourceId = key.replace(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM, "");
                            ThirdSportMarket thirdSportMarket = thirdSportMarketService.getItem(thirdMarketSourceId);
                            if (null != thirdSportMarket) {
                                StandardSportMarket standardSportMarket = standardSportMarketService.getItemNoCache(thirdSportMarket.getDataSourceCode(), thirdMarketSourceId, standardMatchInfoId);
                                if (2 == thirdSportMarket.getMarketType() && (
                                        (StringUtils.isNotBlank(thirdSportMarket.getAddition3()) && StringUtils.isNotBlank(standardSportMarket.getAddition3()) && !thirdSportMarket.getAddition3().equals(standardSportMarket.getAddition3())) ||
                                                ((StringUtils.isNotBlank(thirdSportMarket.getAddition1()) && StringUtils.isNotBlank(standardSportMarket.getAddition1()) && !thirdSportMarket.getAddition1().equals(standardSportMarket.getAddition1())))
                                )
                                ) {
                                    standardMarketOddsProducer.sendChampionMarketCloseWarn(linkId, param, standardMatchInfoId);
                                } else {
                                    if (null != keys) {
                                        redisService.del(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM + thirdSportMarket.getThirdMarketSourceId());
                                        keys.remove(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM + thirdSportMarket.getThirdMarketSourceId());
                                        redisService.set(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM_K, keys);
                                    }
                                }
                            }
                        }
                    }
                }

                log.info("冠军警报执行结束");
            }
        } catch (Exception e) {
            log.error("冠军警报异常：" + e.getMessage());
        }

    }

    @PostConstruct
    public void initAddress() {
        if (StringUtils.isEmpty(HOST_ADDRESS)) {
            InetAddress address = getLocalHostExactAddress();
            HOST_ADDRESS = address.getHostAddress();
            redisService.set(KEY, HOST_ADDRESS, RedisConfig.REDIS_SIXTY_SECOND);
        }
    }
}
