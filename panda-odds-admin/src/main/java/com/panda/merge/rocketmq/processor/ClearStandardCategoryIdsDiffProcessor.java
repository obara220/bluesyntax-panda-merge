package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.Request;
import org.springframework.context.annotation.Lazy;
import com.panda.merge.dto.StandardCategoryIdsDiffDTO;
import com.panda.merge.rocketmq.producer.ClearStandardCategoryIdsDiffProducer;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Component
@Slf4j
@Validated
public class ClearStandardCategoryIdsDiffProcessor extends BaseProcessor {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ClearStandardCategoryIdsDiffProducer clearStandardCategoryIdsDiffProducer;

    public void processor(Request<StandardCategoryIdsDiffDTO> request) {
        String linkId = request.getLinkId();
        log.info("清理水差：{}",linkId);
        StandardCategoryIdsDiffDTO standardCategoryIdsDiffDTO = request.getData();
        Long aoMatchId = standardCategoryIdsDiffDTO.getAoMatchId();
        Integer sportId = standardCategoryIdsDiffDTO.getSportId();
        Long standardMatchId = standardCategoryIdsDiffDTO.getStandardMatchId();
        Set<Long> clearDiffCategoryIds = standardCategoryIdsDiffDTO.getStandardCategoryIds();
        Set<Long> sendClearDiffCategoryIds = new HashSet<>();
        //查询玩法对应数据源
        String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchId + "_" + isOddsLive(standardMatchId);
        Map<String, String> changeCategoryMap = redisService.hGetAll(categoryRedisKey);
        if (MapUtil.isNotEmpty(changeCategoryMap)) {
            clearDiffCategoryIds.forEach(standardCategoryId -> {
                if (null != changeCategoryMap.get(standardCategoryId.toString()) && changeCategoryMap.get(standardCategoryId.toString()).equals(DataSourceCodeEnum.AO.code)) {
                    sendClearDiffCategoryIds.add(standardCategoryId);
                }
            });
        }
        //通知风控清除水差
        if (!CollectionUtils.isEmpty(sendClearDiffCategoryIds)) {
            standardCategoryIdsDiffDTO.setStandardCategoryIds(sendClearDiffCategoryIds);
            log.info("通知风控清除水差：{}",linkId);
            //清除水差
            delDiffByMatchIdAndCategoryList(linkId, standardMatchId, new ArrayList<>(sendClearDiffCategoryIds), sportId);
            clearStandardCategoryIdsDiffProducer.producer(linkId, standardCategoryIdsDiffDTO);
        }

    }
}
