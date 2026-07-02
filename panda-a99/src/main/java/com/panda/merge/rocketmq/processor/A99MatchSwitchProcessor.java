package com.panda.merge.rocketmq.processor;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.A99ParamConfig;
import com.panda.merge.dto.A99MatchSwitchDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.job.common.A99MarketOddsCommon;
import com.panda.merge.rocketmq.producer.A99ThirdSportMarketMergeProducer;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Slf4j
@Validated
@Component
public class A99MatchSwitchProcessor extends BaseProcessor {

    @Autowired
    private A99MarketOddsCommon marketOddsCommon;

    @Autowired
    private A99ThirdMatchMarketProcessor a99ThirdMatchMarketProcessor;

    @Autowired
    private A99ThirdSportMarketMergeProducer a99ThirdSportMarketMergeProducer;
   @Autowired
   private A99ParamConfig a99ParamConfig;
    public void execute(Request<A99MatchSwitchDTO> request){
        A99MatchSwitchDTO dto = request.getData();
        log.info("{}::接收A99赛事开关,赛事id:{},开关:{},早滚:{}", request.getLinkId(), dto.getMatchId(), dto.getStatus(), dto.getMatchType());
        if (dto.getMatchType() == 1) {
            cacheA99Match(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS, dto);
        } else {
            cacheA99Match(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS, dto);
        }
    }

    private void cacheA99Match(String redisKey, A99MatchSwitchDTO dto){
        if (redisService.hHasKey(redisKey, dto.getMatchId().toString())) {
            try {
                redisService.tryLock(redisKey, dto.getMatchId().toString(), 3, 3);
                Object cacheObj = redisService.hGet(redisKey, dto.getMatchId().toString());
                log.info("::缓存A99赛事开关，赛事id:{}，获取赛事缓存:{}", dto.getMatchId(), cacheObj);
                if (ObjectUtil.isNotEmpty(cacheObj)) {
                    String categorySetIds = (String) cacheObj;
                    //status 0:关  1:开
                    if (dto.getStatus() == 0) {
                        //如果关闭玩法集，需从缓存中移除
                        if (categorySetIds.contains(dto.getCategorySetId())) {
                            String newCategorySetIds = removeFromCommaString(categorySetIds, dto.getCategorySetId());
                            if(!StringUtil.isNullOrEmpty(newCategorySetIds)){
                                log.info("::缓存A99赛事开关，赛事id:{},玩法集3:{}", dto.getMatchId(), newCategorySetIds);
                                redisService.hSet(redisKey, dto.getMatchId().toString(), newCategorySetIds);
                            }else {
                                redisService.hDel(redisKey,dto.getMatchId().toString());
                            }
                        }
                        //给融合下发A99玩法关盘数据
                        List<Integer> categoryIds = a99ParamConfig.getCategoryMap().get(dto.getCategorySetId());
                        categoryIds.forEach(categoryId -> {
                            if (redisService.hHasKey(Constant.REDIS_KEY.RONGHE_A99_PUSHED_MARKET_ODDS, categoryId.toString())) {
                                Object cacheData = redisService.hGet(Constant.REDIS_KEY.RONGHE_A99_PUSHED_MARKET_ODDS, categoryId.toString());
                                if (ObjectUtil.isNotNull(cacheData)) {
                                    List<StandardMarketDataMessage> standardMarketDataMessages = (List<StandardMarketDataMessage>)cacheData;
                                    standardMarketDataMessages.forEach(e -> e.setStatus(2));
                                    String linkId = "A99_" + IdUtil.simpleUUID() + "_CLOSE";
                                    a99ThirdSportMarketMergeProducer.sendA99OddsToRonghe(standardMarketDataMessages, linkId);
                                }
                            }
                        });
                    } else {
                        if (!categorySetIds.contains(dto.getCategorySetId())) {
                            String newCategorySetIds = categorySetIds + "," + dto.getCategorySetId();
                            log.info("::缓存A99赛事开关，赛事id:{},玩法集2:{}", dto.getMatchId(), newCategorySetIds);
                            redisService.hSet(redisKey, dto.getMatchId().toString(), newCategorySetIds);
                        }
                        //开启A99赛事，手动触发计算一次A99赔率
                        int flag = dto.getMatchType() == 0 ? 1 : 2;
                        log.info("赛事id:{},开启A99赔率,手动触发计算A99赔率,matchType:{},flag:{}",dto.getMatchId(), dto.getMatchType(), flag);
                        marketOddsCommon.calculateMarketOdds(Arrays.asList(dto.getMatchId()), dto.getMatchType(), flag);
                    }
                } else {
                    log.info("::缓存A99赛事开关，赛事id:{},玩法集1:{}", dto.getMatchId(), dto.getCategorySetId());
                    if (dto.getStatus() == 1)
                        redisService.hSet(redisKey, dto.getMatchId().toString(), dto.getCategorySetId(), 7*24*60*60);
                }
            } finally {
                redisService.unLock(redisKey, dto.getMatchId().toString());
            }
        } else {
            log.info("::缓存A99赛事开关，赛事id:{},玩法集0:{}", dto.getMatchId(), dto.getCategorySetId());
            if (dto.getStatus() == 1)
                redisService.hSet(redisKey, dto.getMatchId().toString(), dto.getCategorySetId(), 7*24*60*60);
        }

    }

    /**
     *
     * @param sourceStr  所有玩法集id组成的字符串，逗号隔开
     * @param targetStr 要移除的玩法集id
     * @return
     */
    private static String removeFromCommaString(String sourceStr, String targetStr) {
        if (sourceStr == null || sourceStr.trim().isEmpty()) {
            return "";
        }
        // 按逗号分隔字符串，同时处理可能的空格
        String[] array = sourceStr.split("\\s*,\\s*");

        StringBuilder stringBuilder = new StringBuilder();

        for (String str : array) {
            if (str.isEmpty()) {
                continue;
            }
            if (!str.equals(targetStr)) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(str);
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        System.out.println(removeFromCommaString("1001,1002","1002"));
    }

}
