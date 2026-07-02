package com.panda.merge.component;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 波胆赔率联动
 * 主：10008-7   全场正确比分 ，从 10083-1100487 全场正确比分（自选），10050-344 全场正确比分（多重投注）
 * 主：20008-341 半场正确比分， 从 20034-1100488 半场正确比分（自选）
 * 主：60006-342 下半场正确比分，从 60029-1100489 下半场正确比分（自选）
 */
@Component
@Slf4j
public class CorrectScorelOddsLinkageProcessor {

    @Autowired
    RedisService redisService;

    public static List<Long> CATEGORY = Lists.newArrayList(7L, 1100487L, 341L, 1100488L, 342L, 1100489L);
    public static Map<Long, List<Long>> STANDARD_CATEGORY_ID_AND_AO_CATEGORY = new LinkedHashMap<Long, List<Long>>() {{
        //全场正确比分
        put(7L, Lists.newArrayList(1100487L));
        //半场正确比分
        put(341L, Lists.newArrayList(1100488L));
        //下半场正确比分
        put(342L, Lists.newArrayList(1100489L));
    }};

    public void oddsLinkage(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        try {
            if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
                return;
            }
            //需要处理的玩法
            Map<Long, StandardMarketMessage> standardMarketMap = standardMarketMessageList.stream()
                    .filter(m -> CATEGORY.contains(m.getMarketCategoryId()) && m.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)
                            && !CollectionUtils.isEmpty(m.getMarketOddsList())).collect(Collectors.toMap(StandardMarketMessage::getMarketCategoryId, thi -> thi));
            //所有主玩法
            Map<Long, StandardMarketMessage> standardMarketMainMap = standardMarketMap.values().stream()
                    .filter(m -> STANDARD_CATEGORY_ID_AND_AO_CATEGORY.keySet().contains(m.getMarketCategoryId()))
                    .collect(Collectors.toMap(StandardMarketMessage::getMarketCategoryId, thi -> thi));

            //全场正确比分
            oddsMainViceLinkage(linkId, standardMatchInfo, standardMarketMap, 7l, standardMarketMainMap, standardMarketMessageList);

            //半场正确比分
            oddsMainViceLinkage(linkId, standardMatchInfo, standardMarketMap, 341L, standardMarketMainMap, standardMarketMessageList);

            //下半场正确比分
            oddsMainViceLinkage(linkId, standardMatchInfo, standardMarketMap, 342L, standardMarketMainMap, standardMarketMessageList);

        } catch (Exception e) {
            log.error("::" + linkId + "::波胆赔率联动,出现异常", e);
        }

    }

    /**
     * @param standardMarketMap           所有需要处理的玩法
     * @param correctScoreDataMessageMain 主
     * @param fTCorrectScoreViceMap       从
     */
    private void oddsMainViceLinkage(String linkId, StandardMatchInfo standardMatchInfo, Map<Long, StandardMarketMessage> standardMarketMap,
                                     Long categoryId, Map<Long, StandardMarketMessage> standardMarketMainMap, List<StandardMarketMessage> standardMarketMessageList) {
        String lastMarketOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        // 主
        StandardMarketMessage correctScoreDataMessageMain = standardMarketMainMap.get(categoryId);
        //从
        Map<Long, StandardMarketMessage> fTCorrectScoreViceMap = standardMarketMap.values().stream()
                .filter(m -> STANDARD_CATEGORY_ID_AND_AO_CATEGORY.get(categoryId).contains(m.getMarketCategoryId()))
                .collect(Collectors.toMap(StandardMarketMessage::getMarketCategoryId, thi -> thi));
        log.info("::{}::波胆赔率联动,赛事id:{},开始处理：{}", linkId, standardMatchInfo.getId(), categoryId);
        if (null != correctScoreDataMessageMain || MapUtils.isNotEmpty(fTCorrectScoreViceMap)) {
            //主不存在取最新缓存
            if (null == correctScoreDataMessageMain) {
                Object obj = redisService.hGet(lastMarketOddsKey, categoryId.toString());
                if (ObjectUtil.isNotEmpty(obj)) {
                    List<StandardMarketMessage> list = (List<StandardMarketMessage>) obj;
                    correctScoreDataMessageMain = list.get(0);
                }
            }
            //主不存在不处理
            if (null == correctScoreDataMessageMain) {
                log.info("::{}::波胆赔率联动,赛事id:{},主玩法不存在不处理：{}", linkId, standardMatchInfo.getId(), categoryId);
                return;
            }
            Map<String, StandardMarketOddsMessage> standardMarketOddsDataMessageMain = correctScoreDataMessageMain.getMarketOddsList()
                    .stream().collect(Collectors.toMap(StandardMarketOddsMessage::getOddsType, thi -> thi));
            log.info("::{}::波胆赔率联动,赛事id:{},主玩法：{}，standardMarketOddsDataMessageMain：{}", linkId, standardMatchInfo.getId(), categoryId, JSONObject.toJSONString(standardMarketOddsDataMessageMain));

            //从数据
            STANDARD_CATEGORY_ID_AND_AO_CATEGORY.get(categoryId).forEach(viceCategoryId -> {
                Boolean isTrue = Boolean.FALSE;
                StandardMarketMessage standardMarketDataMessageVice = standardMarketMap.get(viceCategoryId);
                //从玩法不存在，查缓存
                if (null == standardMarketDataMessageVice) {
                    log.info("::{}::波胆赔率联动,赛事id:{},从不存在查缓存：{}", linkId, standardMatchInfo.getId(), viceCategoryId);
                    Object obj = redisService.hGet(lastMarketOddsKey, viceCategoryId.toString());
                    if (ObjectUtil.isNotEmpty(obj)) {
                        log.info("::{}::波胆赔率联动,赛事id:{},从存在查缓存存在：{}", linkId, standardMatchInfo.getId(), viceCategoryId);
                        List<StandardMarketMessage> list = (List<StandardMarketMessage>) obj;
                        standardMarketDataMessageVice = list.get(0);
                        isTrue = Boolean.TRUE;
                    }
                }
                if (null == standardMarketDataMessageVice) {
                    log.info("::{}::波胆赔率联动,赛事id:{},主玩法：{}，次要玩法不存在不处理：{}", linkId, standardMatchInfo.getId(), categoryId, viceCategoryId);
                    return;
                }
                standardMarketDataMessageVice.getMarketOddsList().stream().forEach(oddsVice -> {
                    StandardMarketOddsMessage standardMarketOddsMessageMain = standardMarketOddsDataMessageMain.get(oddsVice.getOddsType());
                    if (null != standardMarketOddsMessageMain) {
                        oddsVice.setPaOddsValue(standardMarketOddsMessageMain.getPaOddsValue());
                        oddsVice.setActive(standardMarketOddsMessageMain.getActive());
                    }
                });
                if (isTrue) {
                    log.info("::{}::波胆赔率联动,赛事id:{},从缓存存在，当前不存在，加入到当前赔率下发：{}", linkId, standardMatchInfo.getId(), viceCategoryId);
                    standardMarketMessageList.add(standardMarketDataMessageVice);
                }
            });
        }
    }

}
