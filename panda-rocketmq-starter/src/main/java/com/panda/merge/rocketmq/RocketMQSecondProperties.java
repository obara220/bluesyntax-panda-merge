package com.panda.merge.rocketmq;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RocketMQSecondProperties
 *
 * @description: 第二mq集群属性
 * @date: 2/8/2025
 **/
@Component
@Data
public class RocketMQSecondProperties {
    /** 集群转换开关 1 主集群 2 备用集群 默认主集群 **/
    @NacosValue(value = "${panda.data.mq.gateway.market:1}", autoRefreshed = true)
    private Integer clusterSwitch;

    /** 走备用集群的比赛id列表 **/
    private List<Long> matchIds;


    @NacosValue(value = "${panda.data.mq.gateway.matchId:}", autoRefreshed = true)
    public void setMatchIds(String rawMatchIds) {
        if (StringUtils.isNotBlank(rawMatchIds)) {
            this.matchIds = Arrays
                    .stream(rawMatchIds.trim().split(","))
                    .filter(StringUtils::isNumeric)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
    }
}
