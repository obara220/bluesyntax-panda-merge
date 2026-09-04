package com.panda.merge.rocketmq.processor;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketOddsPreResultDTO;
import com.panda.merge.dto.ThirdMarketPreResultDTO;
import com.panda.merge.dto.ThirdMatchPreResultDTO;
import com.panda.merge.dto.message.StandardMatchMarketOddsPreResultMessage;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.StandardMatchPreResultProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消费数据源盘口新提前结算信息
 *
 * @author bevan
 */
@Component
@Slf4j
@Validated
@Async("ThirdMarketPreResultThreadPool")
public class ThirdMarketPreResultNewProcessor extends BaseProcessor {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    private StandardMatchPreResultProducer standardMatchPreResultProducer;

    @ExceptionHelper
    public void thirdMarketPreResultApi(@Valid Request<ThirdMatchPreResultDTO> request) {
        String linkId = request.getLinkId();
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("新提前结算处理开始");
        log.info("::{}::数据源新提前结算处理开始", linkId);
        ThirdMatchPreResultDTO thirdMatchPreResultDTO = request.getData();
        Long dataSourceTime = request.getDataSourceTime();
        String dataSourceCode = thirdMatchPreResultDTO.getDataSourceCode();
        String thirdMatchId = thirdMatchPreResultDTO.getThirdMatchId();
        String requestType = thirdMatchPreResultDTO.getRequestType();
        List<ThirdMarketPreResultDTO> marketResultList = thirdMatchPreResultDTO.getMarketResultList();
        if (CollectionUtils.isEmpty(marketResultList)) {
            log.info("::{}::新提前结算,概率盘口不存在,三方赛事id={}", linkId, thirdMatchId);
            sw.stop();
            return;
        }
        //查找三方赛事
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchId);
        if (null == thirdMatchInfo) {
            sw.stop();
            log.info("::{}::新提前结算,查询三方赛事为空,三方赛事id={}", linkId, thirdMatchId);
            return;
        }
        Long standardMatchId = thirdMatchInfo.getReferenceId();
        //查询标准赛事
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (null == standardMatchInfo) {
            log.info("::{}::新提前结算,未找到标准赛事信息,标准赛事id={}", linkId, standardMatchId);
            sw.stop();
            return;
        }
        int marketType = isOddsLive(standardMatchInfo.getId());
        //查找开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        if (null == standardSportMarketSell) {
            log.info("::{}::新提前结算,未找到预开售信息,标准赛事id={}", linkId, standardMatchId);
            sw.stop();
            return;
        }
        if (marketType == 0) {
            if (!StringUtils.equals(standardSportMarketSell.getLiveMatchSellStatus(), SaleMatchSellStausEnum.Sold.name())) {
                log.info("::{}::新提前结算,滚球未开售,标准赛事id={}", linkId, standardMatchId);
                sw.stop();
                return;
            }
        } else {
            if (!StringUtils.equals(standardSportMarketSell.getPreMatchSellStatus(), SaleMatchSellStausEnum.Sold.name())) {
                log.info("::{}::新提前结算,赛前未开售,标准赛事id={}", linkId, standardMatchId);
                sw.stop();
                return;
            }
        }
        Map<String, List<ThirdMarketPreResultDTO>> thirdMarketPreResultMap = marketResultList.stream().collect(Collectors.groupingBy(ThirdMarketPreResultDTO::getThirdMarketCategorySourceId));
        //处理三方盘口新提前结算信息
        thirdConvertStandardMarketNew(linkId, standardMatchInfo, thirdMarketPreResultMap, dataSourceTime, dataSourceCode, thirdMatchId,requestType);
        sw.stop();
        log.info("::{}::新提前结算处理耗时{}ms,处理三方盘口条数:{},标准盘口条数:{}," + sw.prettyPrint(), linkId, sw.getTotalTimeMillis(), marketResultList.size());
    }

    /**
     * 三方盘口转标准
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdMatchPreResultMap
     * @param dataSourceTime
     * @param dataSourceCode
     * @param thirdMatchId
     * @return
     */
    private List<StandardMatchMarketPreResultMessage> thirdConvertStandardMarketNew(String linkId, StandardMatchInfo standardMatchInfo, Map<String, List<ThirdMarketPreResultDTO>> thirdMatchPreResultMap, Long dataSourceTime, String dataSourceCode, String thirdMatchId,String requestType) {
        List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = Collections.synchronizedList(new ArrayList());
        try {
            //处理三方盘口新提前结算信息
            for (Map.Entry<String, List<ThirdMarketPreResultDTO>> entry : thirdMatchPreResultMap.entrySet()) {
                String thirdCategorySourceId = entry.getKey();
                //查询标准玩法ID
                Long marketCategoryId = getMarketCategoryId(linkId, dataSourceCode, thirdCategorySourceId);
                if (null == marketCategoryId) {
                    continue;
                }
                //处理盘口信息
                List<ThirdMarketPreResultDTO> thirdMarketPreResults = entry.getValue();
                for (ThirdMarketPreResultDTO thirdMarket : thirdMarketPreResults) {
                    thirdMarket.setThirdMatchId(thirdMatchId);
                    //标准盘口ID生成
                    Long relationMarketId = thirdSportMarketService.getRelationMarketId(linkId, standardMatchInfo.getId(), marketCategoryId, thirdMarket.getAddition1(), thirdMarket.getAddition2(), thirdMarket.getAddition3(), thirdMarket.getAddition4(), thirdMarket.getAddition5(), 0, thirdMarket.getThirdMarketId());
                    //转换标准盘口信息
                    StandardMatchMarketPreResultMessage marketMessage = new StandardMatchMarketPreResultMessage();
                    BeanUtils.copyProperties(thirdMarket, marketMessage);
                    marketMessage.setId(relationMarketId);
                    marketMessage.setMarketCategoryId(marketCategoryId);
                    //重构不需要下发给风控，默认给null
                    marketMessage.setThirdSportSendTime(null);
                    marketMessage.setMatchPreStatus(null);
                    marketMessage.setMatchPreStatusRisk(null);
                    marketMessage.setCategoryPreStatus(null);
                    marketMessage.setCashOutMargin(null);
                    marketMessage.setSpread(null);
                    //转换标准投注项
                    List<StandardMatchMarketOddsPreResultMessage> marketOddsMessageList = new ArrayList<>();
                    List<ThirdMarketOddsPreResultDTO> marketOddsResultList = thirdMarket.getMarketOddsResultList();
                    if (!CollectionUtils.isEmpty(marketOddsResultList)) {
                        for (ThirdMarketOddsPreResultDTO thirdMarketOdds : marketOddsResultList) {
                            StandardMatchMarketOddsPreResultMessage marketOddsPreResultMessage = new StandardMatchMarketOddsPreResultMessage();
                            BeanUtils.copyProperties(thirdMarketOdds, marketOddsPreResultMessage);
                            //标准投注项ID生成
                            Long relationMarketOddsId = thirdSportMarketOddsService.getRelationMarketOddsId(relationMarketId, thirdMarketOdds.getOddsType(), thirdMarketOdds.getThirdOddsFieldSourceId(), thirdMarketOdds.getAddition1(), marketCategoryId);
                            marketOddsPreResultMessage.setId(relationMarketOddsId);
                            marketOddsMessageList.add(marketOddsPreResultMessage);
                        }
                        marketMessage.setMarketOddsPreResultMessages(marketOddsMessageList);
                    }
                    marketPreResultMessageList.add(marketMessage);
                }
            }
        } catch (Exception e) {
            log.error("::{}::新提前结算出现异常，", linkId, e);
            e.printStackTrace();
        }
        //下发数据
        if (!CollectionUtils.isEmpty(marketPreResultMessageList)) {
            standardMatchPreResultProducer.sendStandardMatchPreResultNew(linkId, standardMatchInfo, marketPreResultMessageList, dataSourceTime, dataSourceCode,requestType);
        } else {
            log.info("::{}::新提前结算,marketPreResultMessageList为空,标准赛事id:{}", linkId, standardMatchInfo.getId());
        }
        return marketPreResultMessageList;
    }

    /**
     * 查询标准玩法ID
     *
     * @param linkId
     * @param dataSourceCode
     * @param thirdCategorySourceId
     * @return
     */
    private Long getMarketCategoryId(String linkId, String dataSourceCode, String thirdCategorySourceId) {
        //查询标准玩法ID
        ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCode, thirdCategorySourceId);
        if (thirdMarketCategory == null) {
            log.info("::{}::新提前结算,未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
            return null;
        }
        //标准盘口ID
        Long marketCategoryId = thirdMarketCategory.getReferenceId();
        if (null == marketCategoryId || 0L == marketCategoryId) {
            log.info("::{}::新提前结算,三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
            return null;
        }
//        //提前计算支持玩法
//        if (!MarginCategoryConfig.PRE_STANDARD_CATEGORY.contains(marketCategoryId)) {
//            log.info("::{}::新提前结算,不在本期玩法集合内，不处理,三方玩法id:{},标准玩法id：{}", linkId, thirdCategorySourceId, marketCategoryId);
//            return null;
//        }
        return marketCategoryId;
    }

}
