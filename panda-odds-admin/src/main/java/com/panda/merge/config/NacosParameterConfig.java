package com.panda.merge.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Configuration
@Slf4j
public class NacosParameterConfig {

    /**
     * 联动开关 0关 1开
     */
    @NacosValue(value = "${odds.linkage.status:1}", autoRefreshed = true)
    private Integer linkageStatus;

    /**
     * 需要校验的数据源盘口比分
     */
    private List<String> checkMarketsSoreCodes;

    @NacosValue(value = "${check.markets.sore.code:}", autoRefreshed = true)
    public void setCheckMarketsSoreCodes(String checkMarketsSoreCodes) {
        if (StringUtils.isNotBlank(checkMarketsSoreCodes)) {
            this.checkMarketsSoreCodes = Arrays
                    .stream(checkMarketsSoreCodes.trim().split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            log.info("check markets soreCodes update:{}", checkMarketsSoreCodes);
        }
    }

}
