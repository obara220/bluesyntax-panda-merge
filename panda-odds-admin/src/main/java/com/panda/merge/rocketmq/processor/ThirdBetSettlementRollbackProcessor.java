package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetSettlementRollbackDTO;
import com.panda.merge.dto.ThirdBetSettlementRollbackItemDTO;
import com.panda.merge.dto.message.StandardBetSettlementRollbackItemMessage;
import com.panda.merge.model.StandardRelationNewStandard;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.StandardBetSettlementRollbackProducer;
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
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.processor
 * @description : TODO
 * @date: 2020-09-09 20:27
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */

@Slf4j
@Component
@Validated
public class ThirdBetSettlementRollbackProcessor extends BaseProcessor {
    @Autowired
    private ThirdBetOperationLogService thirdBetOperationLogService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardBetSettlementRollbackProducer standardBetSettlementRollbackProducer;

    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;
    public void thirdBetSettlementRollback(@Valid Request<ThirdBetSettlementRollbackDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::thirdBetSettlementRollback ={}", linkId, JSON.toJSONString(request));
        validateLinkId(Constant.PUT_BET_SETTLEMENT_ROLLBACK,request);
        ThirdBetSettlementRollbackDTO thirdBetSettlementRollbackDTO = request.getData();
        String dataSourceCode = thirdBetSettlementRollbackDTO.getDataSourceCode();
        String thirdSourceMatchId = thirdBetSettlementRollbackDTO.getThirdSourceMatchId();

        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdSourceMatchId);
        if (thirdMatchInfo == null) {
            log.error("::{}::thirdBetSettlementRollback 非法的三方赛事源Id, thirdMatchSourceId :{}, dataSourceCode :{}", linkId, thirdSourceMatchId, dataSourceCode);
            return;
        }
        Long referenceId = thirdMatchInfo.getReferenceId();
        if (null == referenceId || referenceId == 0) {
            log.error("::{}::thirdBetSettlementRollback 未绑定标准赛事ID, thirdMatchInfoId :{}, dataSourceCode :{}", linkId, thirdMatchInfo.getId(), dataSourceCode);
            return;
        }
        List<StandardBetSettlementRollbackItemMessage> settlementRollbackItemMessages = getStandardBetSettlementRollbackItemMessages(linkId, referenceId, thirdBetSettlementRollbackDTO);
        if (settlementRollbackItemMessages.size() == 0) {
            log.info("::{}::thirdBetSettlementRollback - getStandardBetSettlementRollbackItemMessages 数据源盘口数据与标准数据匹配失败", linkId);
            return;
        }

        //入库：记录数据源状态数据
        thirdBetOperationLogService.betBetSettlementRollbackCreate(thirdBetSettlementRollbackDTO);

        //推送数据到下游
        standardBetSettlementRollbackProducer.sendstandardBetSettlementRollback(linkId, referenceId, thirdMatchInfo.getSportId(), thirdBetSettlementRollbackDTO, settlementRollbackItemMessages);
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(referenceId);
        if (null == standardRelationNewStandard)
        {
            return;
        }
        else
        {
            List<StandardBetSettlementRollbackItemMessage> settlementRollbackItemMessages1 = getStandardBetSettlementRollbackItemMessages(linkId, standardRelationNewStandard.getNewStandardId(), thirdBetSettlementRollbackDTO);
            if (settlementRollbackItemMessages1.size() == 0) {
                log.info("::{}::thirdBetSettlementRollback - getStandardBetSettlementRollbackItemMessages1 数据源盘口数据与标准数据匹配失败", linkId+"_new_match");
                return;
            }
            //推送数据到下游
            standardBetSettlementRollbackProducer.sendstandardBetSettlementRollback(linkId+"_new_match", standardRelationNewStandard.getNewStandardId(), thirdMatchInfo.getSportId(), thirdBetSettlementRollbackDTO, settlementRollbackItemMessages1);
        }
    }

    private List<StandardBetSettlementRollbackItemMessage> getStandardBetSettlementRollbackItemMessages(String linkId, Long standardMatchId, ThirdBetSettlementRollbackDTO thirdBetSettlementRollbackDTO) {
        List<ThirdBetSettlementRollbackItemDTO> marketData = thirdBetSettlementRollbackDTO.getMarkets();
        String dataSourceCode = thirdBetSettlementRollbackDTO.getDataSourceCode();
        List<String> strList = marketData.stream().map(ThirdBetSettlementRollbackItemDTO::getThirdSourceMarketId).collect(Collectors.toList());
        log.info("::{}::thirdBetSettlementRollback - thirdSourceMarketId List, strList :: {}, dataSourceCode :{}", linkId, strList, dataSourceCode);

        List<StandardSportMarket> standardSportMarketList = standardSportMarketService.getItemByThirdMarketSourceIdsAndDataSourceCode(strList, dataSourceCode, standardMatchId);
        Map<String, Long> stringLongMap = standardSportMarketList.stream().collect(
                Collectors.toMap(StandardSportMarket::getThirdMarketSourceId, StandardSportMarket::getRelationMarketId));

        List<StandardBetSettlementRollbackItemMessage> standardBetCancelItemMessages = new ArrayList<>();
        for (ThirdBetSettlementRollbackItemDTO betSettlementRollbackItemDTO : marketData) {
            if (!stringLongMap.containsKey(betSettlementRollbackItemDTO.getThirdSourceMarketId())) {
                continue;
            }
            StandardBetSettlementRollbackItemMessage standardBetSettlementRollbackItemMessage = new StandardBetSettlementRollbackItemMessage();
            BeanUtils.copyProperties(betSettlementRollbackItemDTO, standardBetSettlementRollbackItemMessage);
            standardBetSettlementRollbackItemMessage.setMarketId(stringLongMap.get(betSettlementRollbackItemDTO.getThirdSourceMarketId()));
            standardBetCancelItemMessages.add(standardBetSettlementRollbackItemMessage);
        }
        return standardBetCancelItemMessages;
    }


}
