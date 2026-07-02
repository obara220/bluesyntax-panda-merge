package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.DataMerchantBaffleProducer;
import com.panda.merge.rocketmq.producer.StandardClearCategoryDiffProducer;
import com.panda.merge.service.ConfigTradeMarketLogService;
import com.panda.merge.service.PdMatchScoreLogService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Component
@Slf4j
@Validated
public class LSSendRscMatchStatusProcessor extends BaseProcessor {
    @Autowired
    private StandardClearCategoryDiffProducer standardClearCategoryDiffProducer;
    @Autowired
    ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private ConfigTradeMarketLogService configTradeMarketLogService;
    @Autowired
    private PdMatchScoreLogService pdMatchScoreLogService;
    @Autowired
    private DataMerchantBaffleProducer dataMerchantBaffleProducer;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    public void processor(Request<TradeMarketConfigDTO> request) {
        TradeMarketConfigDTO tradeMarketConfigDTO = request.getData();
        //判断赛事类型
        boolean isOutRight = StringUtils.equals("1", tradeMarketConfigDTO.getMatchType());
        ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfo(isOutRight,
                tradeMarketConfigDTO.getAddition1(), tradeMarketConfigDTO.getTargetId());
        if (thirdMatchInfo == null) {
            log.info("::{}::LSSendRscMatchStatusProcessor,数据源数据TargetID对应的三方赛事未找到，三方赛事id:{}", request.getLinkId(),
                    tradeMarketConfigDTO.getTargetId());
            return;
        }
        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight,
                thirdMatchInfo.getReferenceId());
        if (standardMatchInfo == null) {
            log.info("::{}::LSSendRscMatchStatusProcessor,数据源数据TargetID对应的标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    thirdMatchInfo.getReferenceId());
            return;
        }

        //保存configTradeMarketLog，该表只记录，不会作为业务使用
        configTradeMarketLogService.create(request.getLinkId(), tradeMarketConfigDTO);
        pdMatchScoreLogService.updateMarketStatusLog(tradeMarketConfigDTO);

        List<Long> clearDiffList = new ArrayList<>();
        Boolean isChangeTx = Boolean.FALSE;
        if (DataSourceCodeEnum.LS.code.equals(tradeMarketConfigDTO.getAddition1()) && DataSourceCodeEnum.LS.code.equals(tradeMarketConfigDTO.getAddition4())) {
            //根据当前赛事状态区分赛前滚球，查出缓存中是否存在TX玩法
            Integer matchStatus = standardMatchInfo.getMatchStatus();
            Integer changeMarketType = isOddsLive(standardMatchInfo.getId());
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + changeMarketType;
            Map<String, String> changeCategoryMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(changeCategoryMap)) {
                isChangeTx = changeCategoryMap.containsValue(DataSourceCodeEnum.LS.code);
                if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                    if (isChangeTx) {
                        changeCategoryMap.forEach((k, v) -> {
                            if (v.equals(DataSourceCodeEnum.LS.code)) {
                                clearDiffList.add(Long.valueOf(k));
                            }
                        });
                    }
                }
            }
        }
        //通知风控清除玩法水差
        if (!CollectionUtils.isEmpty(clearDiffList)) {
            log.info("::{}::标准赛事ID:{},TX/LS切换通知风控清除玩法水差:{}", request.getLinkId(), standardMatchInfo.getId(), clearDiffList);
            delDiffByMatchIdAndCategoryList(request.getLinkId(), standardMatchInfo.getId(), clearDiffList, standardMatchInfo.getSportId().intValue());
            standardClearCategoryDiffProducer.sendStandardClearCategoryDiffRisk(request.getLinkId(), standardMatchInfo, clearDiffList);
            dataMerchantBaffleProducer.switchDataSourceSendRiskMQ(request.getLinkId(), standardMatchInfo.getId(), standardMatchInfo.getSportId());

            //查询赛事的开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
            if (standardSportMarketSell == null) {
                return;
            }
            Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(new HashSet<Long>(clearDiffList),request.getLinkId(), standardMatchInfo, standardSportMarketSell);
            //异步处理
            thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, new HashSet<Long>(clearDiffList),
                    standardMarketDataMessageMap, System.currentTimeMillis(), standardSportMarketSell, new HashMap<>());
        }
    }
}
