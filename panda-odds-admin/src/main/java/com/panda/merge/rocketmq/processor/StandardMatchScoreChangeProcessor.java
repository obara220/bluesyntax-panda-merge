package com.panda.merge.rocketmq.processor;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.scores.StandardMatchScoreChangeDTO;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.CategoryCodeProducer;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class StandardMatchScoreChangeProcessor extends BaseProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private CategoryCodeProducer categoryCodeProducer;

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    public void execute(@Valid Request<StandardMatchScoreChangeDTO> request){
        log.info("::{}::StandardMatchScoreChangeProcessor，逻辑处理开始，request={}", request.getLinkId(), JSON.toJSONString(request));
        String linkId = request.getLinkId();
        Long standardMatchInfoId = request.getData().getMatchId();
        boolean isOutRight = false;
        //获取标准赛事
        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight,
                standardMatchInfoId);
        if (standardMatchInfo == null) {
            log.info("::{}::StandardMatchScoreChangeProcessor,标准赛事未找到，标准赛事id:{}", linkId, standardMatchInfoId);
            return ;
        }
        if (standardMatchInfo.getSportId() !=1){
            log.info("::{}::StandardMatchScoreChangeProcessor,非足球不处理，标准赛事id:{}", linkId, standardMatchInfoId);
            return;
        }
        //获取开售信息
        StandardSportMarketSell standardSportMarketSell =
                thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, standardMatchInfoId);
        if (standardSportMarketSell == null) {
            log.info("::{}::StandardMatchScoreChangeProcessor,标准赛事未开售，标准赛事id:{}", linkId, standardMatchInfoId);
            return ;
        }
        //收集本次有改变的玩法
        Set<Long> marketCategoryIdSet = MarginCategoryConfig.STANDARD_MATCH_SCORE_CHANGE.get(request.getData().getType());
        //模式判断
        Set<Long> changeCategoryIdSet = new HashSet<>();
        marketCategoryIdSet.forEach(categoryId -> {
            ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(linkId, standardMatchInfo.getId(), categoryId);
            Integer tradeTypeDB = 0;
            if (null != configTradeType) {
                tradeTypeDB = configTradeType.getTradeType();
            }
            if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeDB)) {
                log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", linkId, standardMatchInfoId, categoryId);
                return;
            }
            changeCategoryIdSet.add(categoryId);
        });
        if (CollectionUtils.isEmpty(changeCategoryIdSet)) {
            log.info("::{}::StandardMatchScoreChangeProcessor,下发玩法不存在", linkId);
            return ;
        }
        //下发当前最新赔率
        //获取缓存中的所有盘口
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(changeCategoryIdSet, linkId, standardMatchInfo, standardSportMarketSell);
        //--------操盘后台操作开关封锁,异步处理-----------
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, changeCategoryIdSet,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
    }
}
