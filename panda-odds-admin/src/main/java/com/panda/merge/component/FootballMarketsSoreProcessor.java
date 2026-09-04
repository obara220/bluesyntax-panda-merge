package com.panda.merge.component;

import com.panda.merge.cache.CommonItem;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MarketTypeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.NacosParameterConfig;
import com.panda.merge.dto.FootballCacheScores;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.odds.MergeMarketStatusEnum;
import com.panda.merge.dto.odds.StandardMarketScoreModification;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.odds.cache.FootballScoreCacheService;
import com.panda.merge.odds.enums.MarketScoreTypeEnum;
import com.panda.merge.odds.model.CategoryMarketMessageData;
import com.panda.merge.odds.model.MatchMarketMessageData;
import com.panda.merge.service.StandardSportMarketNewService;
import com.panda.merge.service.StandardSportMarketOddsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.panda.merge.odds.constants.CategoryConstant.*;

/**
 * 开盘的盘口比分校验
 */
@Slf4j
@Component
public class FootballMarketsSoreProcessor extends BaseProcessor {

    @Autowired
    private NacosParameterConfig nacosParameterConfig;

    @Autowired
    private FootballScoreCacheService footballScoreCacheService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketNewService;

    @Autowired
    private StandardSportMarketOddsService standardSportMarketOddsService;

    public <T extends StandardMarketScoreModification> void check(String linkId,
                                                                  StandardMatchInfo standardMatchInfo,
                                                                  List<T> standardMarketMessageList) {
        List<T> standardMarketMessages = filter(linkId, standardMatchInfo, standardMarketMessageList);
        if (CollectionUtils.isEmpty(standardMarketMessages)) {
            return;
        }
        FootballCacheScores footballCacheScores = footballScoreCacheService.getCache(linkId, standardMatchInfo.getId());
        checkScore(linkId, standardMatchInfo, footballCacheScores, standardMarketMessages);
    }

    public void check(CategoryMarketMessageData categoryData) {

        String linkId = categoryData.matchData.linkId;
        StandardMatchInfo standardMatchInfo = categoryData.matchData.standardMatchInfo;
        List<StandardMarketMessage> standardMarketMessages =
                filter(linkId, standardMatchInfo, categoryData.marketMessages);
        if (CollectionUtils.isEmpty(standardMarketMessages)) {
            return;
        }
        FootballCacheScores footballCacheScores = footballScoreCacheService.getCache(linkId, standardMatchInfo.getId());
        checkScore(linkId, standardMatchInfo, footballCacheScores, standardMarketMessages);
    }

    private <T extends StandardMarketScoreModification> void checkScore(String linkId,
                                                                        StandardMatchInfo standardMatchInfo,
                                                                        FootballCacheScores footballCacheScores,
                                                                        List<T> standardMarketMessages) {
        if (Objects.isNull(footballCacheScores)) {
            return;
        }
        for (T standardMarketMessage : standardMarketMessages) {
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            //盘口比分

            String marketScore = standardMarketMessage.score();
            if (StringUtils.isEmpty(marketScore)) {
                continue;
            }

            String cacheScore = getCacheScore(footballCacheScores, marketCategoryId);
            if (StringUtils.isEmpty(cacheScore)) {
                continue;
            }

            if (!StringUtils.equals(marketScore, cacheScore)) {
                standardMarketMessage.scoreClose(cacheScore);
                log.error("::{}::赛事ID:{},比分校验不通过，玩法id:{},缓存比分:{}，数据商关盘：{}",
                          linkId,
                          standardMatchInfo.getId(),
                          marketCategoryId,
                          cacheScore,
                          standardMarketMessage.getId());
            }
        }
    }

    private <T extends StandardMarketScoreModification> List<T> filter(String linkId,
                                                                       StandardMatchInfo standardMatchInfo,
                                                                       List<T> standardMarketMessageList) {
        int liveFlag = isOddsLive(standardMatchInfo.getId());
        log.info("linkId:{},standardMatchInfo:{},check market score, market size : {}, liveFlag:{}",
                 linkId,
                 standardMatchInfo.getId(),
                 standardMarketMessageList.size(),
                 liveFlag);
        // 只处理足球， 只处理滚球，早盘不处理
        if (standardMatchInfo.getSportId() != 1L || liveFlag == 1) {
            return null;
        }
        List<String> checkMarketsSoreCodes = nacosParameterConfig.getCheckMarketsSoreCodes();
        if (CollectionUtils.isEmpty(checkMarketsSoreCodes)) {
            log.warn("linkId:{},standardMatchInfo:{},check market score, data source config is empty",
                     linkId,
                     standardMatchInfo);
            return null;
        }
        //只处理足球主玩法 、和需要处理的数据源 ,数据源开盘的
        List<T> standardMarketMessages = standardMarketMessageList
                .stream()
                .filter(s -> checkMarketsSoreCodes.contains(s.getDataSourceCode()) &&
                        !s.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.OD.getCode()) &&
                        isTargetCategory(s.getMarketCategoryId())  &&
                        s.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(standardMarketMessages)) {
            log.info("::{}::赛事id:{},CheckMarketsSoreProcessor，checkSort，market message is empty",
                     linkId,
                     standardMatchInfo.getId());
            return null;
        }
        return standardMarketMessages;
    }

    private boolean isTargetCategory(Long categoryId) {
        if (Objects.isNull(categoryId)) {
            return false;
        }
        return CATEGORY_SCORE_TYPE_MAP.containsKey(categoryId);
    }

    private String getCacheScore(FootballCacheScores cacheScores, Long marketCategoryId) {
        MarketScoreTypeEnum marketScoreTypeEnum = CATEGORY_SCORE_TYPE_MAP.get(marketCategoryId);
        if (Objects.isNull(marketScoreTypeEnum)) {
            return null;
        }
        CommonItem score = marketScoreTypeEnum.getScore(cacheScores);
        if (Objects.isNull(score) || Objects.isNull(score.getHome()) || Objects.isNull(score.getAway())) {
            return null;
        }
        return score.getHome() + "_" + score.getAway();
    }

    private CommonItem getCacheScoreItem(FootballCacheScores cacheScores, Long marketCategoryId) {
        if (Objects.isNull(cacheScores) || Objects.isNull(marketCategoryId)) {
            return null;
        }
        MarketScoreTypeEnum marketScoreTypeEnum = CATEGORY_SCORE_TYPE_MAP.get(marketCategoryId);
        if (Objects.isNull(marketScoreTypeEnum)) {
            return null;
        }
        return  marketScoreTypeEnum.getScore(cacheScores);
    }


    /**
     * 91418
     * 1、针对玩法，全场大小（2）和半场大小（18），
     * 2、有至少2个（含2个）坑位开盘；
     * 3、找到坑位最靠前的盘口，该低赔率一边（一般是小球）抽水后为1.01，假设为坑位X满足条件
     * 4、找出来坑位X的后续坑位还有开的盘口，全部关掉；假设X+1，X+2还有开的，全部关掉
     * 5、继续判断，如果还有至少2个（含2个）坑位开盘；
     * 6、找到坑位最靠前的盘口，该低赔率一边（一般是小球）抽水后为1.02，假设为坑位Y满足条件
     * 7、坑位Y+1盘口赔率是否有和坑位Y相同的（高赔率或者低赔率有一个满足都执行），相同则将坑位Y+1及其以后的所有盘口 关盘处理
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageSendList
     */
    public void checkStandardMarketOddsValuse(String linkId, StandardMatchInfo standardMatchInfo,List<StandardMarketMessage> standardMarketMessageSendList){
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode())){
            return;
        }
        Map<Long,List<StandardMarketMessage>> map = standardMarketMessageSendList.stream().filter(e -> AO_CATEGORY_CHECK_ODDSVALUE.contains(e.getMarketCategoryId())&&e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.AO.code)).collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        if (MapUtils.isEmpty(map)){
            return;
        }
        map.forEach((k,v)->{
            List<StandardMarketMessage> activeList = v.stream().filter(e->e.getStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE) && e.getMarketOddsList()!=null && e.getMarketOddsList().size() >=2).collect(Collectors.toList());
            if (activeList.size() >= 2){
                activeList.sort(Comparator.comparingInt(StandardMarketMessage::getPlaceNum));
                /*String oddsType = activeList.get(0).getMarketOddsList().get(0).getPaOddsValue()
                        > activeList.get(0).getMarketOddsList().get(1).getPaOddsValue() ?
                        activeList.get(0).getMarketOddsList().get(1).getOddsType():
                        activeList.get(0).getMarketOddsList().get(0).getOddsType();*/
                for(int i = 1; i < activeList.size();i++){
                    StandardMarketMessage odds1 = activeList.get(i-1);
                    StandardMarketMessage odds2 = activeList.get(i);
                    Integer odds1Value = odds1.getMarketOddsList().stream().filter(e->e.getOddsType().equals("Over")).findFirst().get().getPaOddsValue();
                    Integer odds2Value = odds2.getMarketOddsList().stream().filter(e->e.getOddsType().equals("Over")).findFirst().get().getPaOddsValue();

                    Integer odds3Value = odds1.getMarketOddsList().stream().filter(e->e.getOddsType().equals("Under")).findFirst().get().getPaOddsValue();
                    Integer odds4Value = odds2.getMarketOddsList().stream().filter(e->e.getOddsType().equals("Under")).findFirst().get().getPaOddsValue();
                    Integer minOdds = Math.min(odds1Value,odds3Value);
                    Integer minOdds2 = Math.min(odds2Value,odds4Value);
                    Integer resultMinOdds = Math.min(minOdds,minOdds2);

                    Integer maxOdds = Math.max(odds1Value,odds3Value);
                    Integer maxOdds2 = Math.max(odds2Value,odds4Value);

                    if (resultMinOdds == 101000 || (resultMinOdds==102000 && maxOdds == maxOdds2)){
                        int j = i;
                        if (resultMinOdds == 101000){
                            if (odds1Value == 101000 || odds3Value == 101000){
                                j = i;
                            }else{
                                j = i + 1;
                            }
                        }
                        for (;j<activeList.size();j++){
                            activeList.get(j).setPaStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            activeList.get(j).setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            activeList.get(j).setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            activeList.get(j).setPaStatusReason("跟上一个位置最小赔相等，盘口关盘处理");
                            log.info("::{}::赛事id:{},checkStandardMarketOddsValuse,跟上一个位置最小赔相等,盘口关盘处理,relationMarketId:{}",
                                    linkId,
                                    standardMatchInfo.getId(),
                                    activeList.get(j).getRelationMarketId());
                        }
                        break;
                    }
                }
            }
        });
    }

}
