package com.panda.merge.component;

import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.constant.CategoryOppositeConfig;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.SystemDeActiveLogProducer;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * 赔率服务异步处理服务公共类
 */
@Component
@Slf4j
public class CommonAsyncService {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;
    @Autowired
    public ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    public SystemDeActiveLogProducer systemDeActiveLogProducer;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    /**
     * 根据标准盘口的开售信息找出其他数据源的三方盘口集合
     *
     * @param linkId
     * @param standardMatchInfo
     */
    @Async("PaDataServiceLogDTOThreadPool")
    public void getAllThirdSportMarketList(String linkId, StandardMatchInfo standardMatchInfo, Integer marketType, Map<Long, String> longStringHashMap) {
        log.info("::{}::开始获取三方盘口数据，标准赛事id：{}，盘口类型：{}，标准赛事玩法开售详细信息：{}", linkId, standardMatchInfo.getId(), marketType, longStringHashMap);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchInfo.getId());
        if (CollectionUtils.isEmpty(thirdMatchInfos)) {
            log.info("::{}::开始获取三方盘口数据，标准赛事id：{}，标准赛事玩法开售详细信息：{}，三方赛事信息集合为空，直接返回", linkId, standardMatchInfo.getId(), longStringHashMap);
            return;
        }
        List<ThirdSportMarket> thirdSportMarketList = Collections.synchronizedList(new ArrayList());
        longStringHashMap.forEach((k, v) -> {
            List<ThirdMatchInfo> thirdMatchInfoList = getThirdMatchInfoList(thirdMatchInfos, v);
            for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
                List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketService.getItemList(thirdMatchInfo.getId(), thirdMatchInfo.getDataSourceCode(), k, marketType);
                if (!CollectionUtils.isEmpty(thirdSportMarkets)) {
                    thirdSportMarketList.addAll(thirdSportMarkets);
                }
            }
        });
        List<ThirdSportMarketMessage> thirdSportMarketMessages = Collections.synchronizedList(new ArrayList());
        if (!CollectionUtils.isEmpty(thirdSportMarketList)) {
            thirdSportMarketList.forEach(e -> {
                ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
                BeanUtils.copyProperties(e, thirdSportMarketMessage);
                List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketOddsService.getItemList(e.getDataSourceCode(), thirdSportMarketMessage.getId());
                thirdSportMarketMessage.setThirdSportMarketOddsList(new ArrayList<>());
                if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                    thirdSportMarketMessage.getThirdSportMarketOddsList().addAll(thirdSportMarketOddsList);
                }
                thirdSportMarketMessages.add(thirdSportMarketMessage);
            });
        }
        sendMessageToRisk(linkId, standardMatchInfo, thirdSportMarketMessages);
    }

    private List<ThirdMatchInfo> getThirdMatchInfoList(List<ThirdMatchInfo> thirdMatchInfos, String dataSourceCode) {
        List<ThirdMatchInfo> thirdMatchInfoList = new ArrayList<>();
        thirdMatchInfos.forEach(e -> {
            if (!e.getDataSourceCode().equalsIgnoreCase(dataSourceCode)) {
                thirdMatchInfoList.add(e);
            }
        });
        return thirdMatchInfoList;
    }

    @Async("PaDataServiceLogDTOThreadPool")
    public void sendMessageToRisk(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages) {
        try {
            //去掉tx/ao的三方盘口数据
            List<ThirdSportMarketMessage> delList = Collections.synchronizedList(new ArrayList());
            if (!CollectionUtils.isEmpty(thirdSportMarketMessages)) {
                delList = thirdSportMarketMessages.stream().filter(e ->
                        MarginCategoryConfig.THIRD_ALL_MARKET_DATA_SOURCE_CODE.contains(e.getDataSourceCode())
                                && 0 != e.getReferenceId()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(delList)) {
                    thirdSportMarketMessages.removeAll(delList);
                }
            }
            if (!CollectionUtils.isEmpty(thirdSportMarketMessages)) {
                for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages) {
                    applyHomeAwayOppositeForThirdMarketMessage(linkId, standardMatchInfo, thirdSportMarketMessage);
                    thirdSportMarketMessage.setRelationMarketId(thirdSportMarketService.getRelationMarketId(linkId, standardMatchInfo.getId(), thirdSportMarketMessage.getMarketCategoryId(),
                            thirdSportMarketMessage.getAddition1(), thirdSportMarketMessage.getAddition2(), thirdSportMarketMessage.getAddition3(), thirdSportMarketMessage.getAddition4(), thirdSportMarketMessage.getAddition5(),
                            thirdSportMarketMessage.getMarketType(), thirdSportMarketMessage.getThirdMarketSourceId()));
                    thirdSportMarketMessage.setReferenceId(standardMatchInfo.getId());
                    if (!CollectionUtils.isEmpty(thirdSportMarketMessage.getThirdSportMarketOddsList())) {
                        thirdSportMarketMessage.getThirdSportMarketOddsList().forEach(e -> {
                            e.setMarketId(thirdSportMarketMessage.getRelationMarketId());
                            e.setId(thirdSportMarketOddsService.getRelationMarketOddsId(thirdSportMarketMessage.getRelationMarketId(), e.getOddsType(), e.getThirdOddsFieldSourceId(), e.getAddition1(), thirdSportMarketMessage.getMarketCategoryId()));
                        });
                    }
                }
                thirdSportMarketMergeProducer.sendThirdSportMarketMessageToMQ(linkId, standardMatchInfo, thirdSportMarketMessages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Async("sendDeActiveLogThreadPool")
    public void sendDeactivatedBySystemLogToRisk(StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        String linkId = IdWorker.getId() + "_DeActivated_BySystem";
        systemDeActiveLogProducer.doSendLogToRisk(linkId, standardMatchInfo, standardMarketMessage);
    }
    @Async("sendDeActiveLogThreadPool")
    public void sendDeactivatedBySystemLogToRisk(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        systemDeActiveLogProducer.doSendLogToRisk(linkId, standardMatchInfo, standardMarketMessage);
    }

    /**
     * STANDARD_THIRD_MARKET_ODDS 下发前：主客相反三方盘口/投注项翻转（与标准融合盘口径一致）
     */
    private void applyHomeAwayOppositeForThirdMarketMessage(String linkId,
                                                            StandardMatchInfo standardMatchInfo,
                                                            ThirdSportMarketMessage thirdSportMarketMessage) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            return;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), thirdSportMarketMessage.getDataSourceCode());
        if (thirdMatchInfo == null || !ONE.equals(thirdMatchInfo.getHomeAwayOpposite())) {
            return;
        }
        if (!CategoryOppositeConfig.FootBall.containsCategory(thirdSportMarketMessage.getMarketCategoryId())) {
            return;
        }
        if (thirdMatchMarketProcessor.skipHomeAwayOppositeForDataSource(thirdSportMarketMessage.getDataSourceCode())) {
            return;
        }
        ThirdSportMarket thirdSportMarket = new ThirdSportMarket();
        BeanUtils.copyProperties(thirdSportMarketMessage, thirdSportMarket);
        thirdMatchMarketProcessor.changeThirdMarketContent(linkId, thirdSportMarket);
        thirdMatchMarketProcessor.changeThirdMarketOddsContent(linkId, thirdSportMarketMessage.getThirdSportMarketOddsList(), thirdSportMarket);
        thirdSportMarketMessage.setMarketCategoryId(thirdSportMarket.getMarketCategoryId());
        thirdSportMarketMessage.setAddition1(thirdSportMarket.getAddition1());
        thirdSportMarketMessage.setAddition2(thirdSportMarket.getAddition2());
        thirdSportMarketMessage.setAddition3(thirdSportMarket.getAddition3());
        thirdSportMarketMessage.setAddition4(thirdSportMarket.getAddition4());
    }
}
