package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.OutrightTradeTypeConfigDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.message.OutrightMarketSoldMessage;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.ConfigOutrightTradeType;
import com.panda.merge.odds.service.ChampionMarketService;
import com.panda.merge.service.OutrightTradeTypeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author raulvii<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/10/17 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class OutrightMarketSoldProcessor extends BaseProcessor {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;
    @Autowired
    private ChampionMarketService championMarketService;

    public void processOutrightMarketSold(@Valid Request<OutrightMarketSoldMessage> request) {
        String linkId = request.getLinkId();
        validateLinkId("processOutrightMarketSold", request);
        log.info("::{}::processOutrightMarketSold,req:{}", linkId, JSON.toJSONString(request));
        OutrightMarketSoldMessage outrightMarketSoldMessage = request.getData();
        List<Long> marketIdList = outrightMarketSoldMessage.getMarketIdList();
        Long matchId = outrightMarketSoldMessage.getStandardOutrightMatchId();
        String dataSourceCode = outrightMarketSoldMessage.getDataSourceCode();
        //获取当前数据源缓存中所有的盘口
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + matchId + "_" + dataSourceCode;
        log.info("::{}:: processOutrightMarketSold redisKey={} ", linkId, redisKey);
        Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(redisKey);
        if (CollectionUtils.isEmpty(standardMarketMessageMap)) {
            // 缓存为空的补充查询
            standardMarketMessageMap = championMarketService.championMarketCacheExecute( linkId, matchId, dataSourceCode);
            if ( CollectionUtils.isEmpty(standardMarketMessageMap) ) {
                log.info("::{}:: processOutrightMarketSold standardMarketMessageMap is null", linkId);
                return;
            }
        }
        //过滤出开售的盘口
        Map<String, StandardMarketDataMessage> sendMap = standardMarketMessageMap.entrySet().stream()
                .filter(map -> marketIdList.contains(map.getValue().getRelationMarketId()))
                .collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));
        if (CollectionUtils.isEmpty(sendMap)) {
            log.info("::{}:: processOutrightMarketSold sendMap is null", linkId);
            return;
        }

        //下发当前最新赔率
        Set<Long> marketIdSet = sendMap.values().stream().map(StandardMarketDataMessage::getRelationMarketId).collect(Collectors.toSet());
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(true, matchId);
        thirdMatchMarketProcessor.processOddsByOutright(linkId, standardMatchInfo, marketIdSet, sendMap, System.currentTimeMillis(), new HashMap<>());

        //只有冠军的才能改操盘方式
        if (DataSourceCodeEnum.PA.name().equals(standardMatchInfo.getDataSourceCode())) {
            //931【手动创建盘口】,盘口下发后 设置操盘模式为M
            OutrightTradeTypeConfigDTO tradeTypeConfigDTO = new OutrightTradeTypeConfigDTO();
            tradeTypeConfigDTO.setStandardMarketId(marketIdList.get(0));
            tradeTypeConfigDTO.setStandardMatchId(matchId);
            tradeTypeConfigDTO.setTradeType(Constant.OUTRIGHT_ONE);
            tradeTypeConfigDTO.setLevel(Constant.OUTRIGHT_ZERO);
            ConfigOutrightTradeType configOutrightTradeType = outrightTradeTypeConfigService.selectItem(tradeTypeConfigDTO);
            if(null == configOutrightTradeType){
                outrightTradeTypeConfigService.insertItem(request.getLinkId(),tradeTypeConfigDTO);
            }else {
                configOutrightTradeType.setTradeType(tradeTypeConfigDTO.getTradeType());
                configOutrightTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                outrightTradeTypeConfigService.updateItem(configOutrightTradeType);
            }
        }
        log.info("::{}:: processOutrightMarketSold process success", linkId);
    }

}

