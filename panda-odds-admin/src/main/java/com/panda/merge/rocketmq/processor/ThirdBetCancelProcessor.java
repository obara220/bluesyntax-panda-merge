package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.dto.ThirdBetCancelItemDTO;
import com.panda.merge.dto.message.StandardBetCancelItemMessage;
import com.panda.merge.model.StandardRelationNewStandard;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.StandardBetCancelProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 盘口取消 对应上游bet-cancel事件
 *
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.processor
 * @description : TODO
 * @date: 2020-09-09 14:12
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Component
@Slf4j
@Validated
public class ThirdBetCancelProcessor extends BaseProcessor {
    @Autowired
    private ThirdBetOperationLogService thirdBetOperationLogService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardBetCancelProducer standardBetCancelProducer;

    @Autowired
    private ThirdSportTypeService thirdSportTypeService;

    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;

    public void thirdBetCancel(@Valid Request<ThirdBetCancelDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::盘口取消对应上游bet-cancel事件={}", linkId, JSON.toJSONString(request));
        validateLinkId(Constant.PUT_BET_CANCEL,request);
        ThirdBetCancelDTO thirdBetCancelDTO = request.getData();
        String dataSourceCode = thirdBetCancelDTO.getDataSourceCode();
        String thirdSourceMatchId = thirdBetCancelDTO.getThirdSourceMatchId();

        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdSourceMatchId);
        if (thirdMatchInfo == null) {
            log.error("::{}::thirdBetCancel 非法的三方赛事源Id, thirdMatchSourceId :{}, dataSourceCode :{}", linkId, thirdSourceMatchId, dataSourceCode);
            return;
        }
        Long referenceId = thirdMatchInfo.getReferenceId();
        if (null == referenceId || referenceId == 0) {
            log.error("::{}::thirdBetCancel 未绑定标准赛事ID, thirdMatchInfoId :{}, dataSourceCode :{}", linkId, thirdMatchInfo.getId(), dataSourceCode);
            return;
        }
        List<StandardBetCancelItemMessage> standardBetCancelItemMessages = getStandardBetCancelItemMessages(linkId, referenceId, thirdBetCancelDTO);
        if (standardBetCancelItemMessages.size() == 0) {
            log.info("::{}::thirdBetCancel - getStandardBetCancelItemMessages 数据源盘口数据与标准数据匹配失败", linkId);
            return;
        }
        //入库：记录数据源状态数据
        thirdBetOperationLogService.thirdBetCancelCreate(thirdBetCancelDTO);

        //推送数据到下游
        standardBetCancelProducer.sendStandardBetCancel(linkId, referenceId, thirdMatchInfo.getSportId(), thirdBetCancelDTO, standardBetCancelItemMessages);
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(referenceId);
        if (null == standardRelationNewStandard)
        {
            return;
        }
        else
        {
            List<StandardBetCancelItemMessage> standardBetCancelItemMessages1 = getStandardBetCancelItemMessages(linkId, standardRelationNewStandard.getNewStandardId(), thirdBetCancelDTO);
            if (standardBetCancelItemMessages1.size() == 0) {
                log.info("::{}::thirdBetCancel - getStandardBetCancelItemMessages1 数据源盘口数据与标准数据匹配失败", linkId+"_new_match");
                return;
            }
            //推送数据到下游
            standardBetCancelProducer.sendStandardBetCancel(linkId+"_new_match", standardRelationNewStandard.getNewStandardId(), thirdMatchInfo.getSportId(), thirdBetCancelDTO, standardBetCancelItemMessages1);
        }
    }

    /**
     * 数据源盘口数据与标准数据进行匹配
     *
     * @param linkId
     * @param thirdBetCancelDTO 数据源
     * @return 标准盘口数据
     */
    private List<StandardBetCancelItemMessage> getStandardBetCancelItemMessages(String linkId, Long standardMatchId, ThirdBetCancelDTO thirdBetCancelDTO) {
        List<ThirdBetCancelItemDTO> marketData = thirdBetCancelDTO.getMarkets();
        String dataSourceCode = thirdBetCancelDTO.getDataSourceCode();
        List<String> strList = marketData.stream().map(ThirdBetCancelItemDTO::getThirdSourceMarketId).collect(Collectors.toList());
        log.info("::{}::thirdBetCancel - betCancelItem  thirdSourceMarketId List, strList :: {}, dataSourceCode :{}", linkId, strList, dataSourceCode);
        List<StandardSportMarket> standardSportMarketList = standardSportMarketService.getItemByThirdMarketSourceIdsAndDataSourceCode(strList, dataSourceCode, standardMatchId);
        Map<String, Long> stringLongMap = standardSportMarketList.stream().collect(Collectors.toMap(StandardSportMarket::getThirdMarketSourceId, StandardSportMarket::getRelationMarketId));
        List<StandardBetCancelItemMessage> standardBetCancelItemMessages = new ArrayList<>();
        for (ThirdBetCancelItemDTO thirdBetCancelMessage : marketData) {
            if (!stringLongMap.containsKey(thirdBetCancelMessage.getThirdSourceMarketId())) {
                continue;
            }
            StandardBetCancelItemMessage standardBetCancelItemMessage = new StandardBetCancelItemMessage();
            BeanUtils.copyProperties(thirdBetCancelMessage, standardBetCancelItemMessage);
            standardBetCancelItemMessage.setMarketId(stringLongMap.get(thirdBetCancelMessage.getThirdSourceMarketId()));
            standardBetCancelItemMessages.add(standardBetCancelItemMessage);
        }
        return standardBetCancelItemMessages;
    }


}
