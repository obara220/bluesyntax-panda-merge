package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelRollbackDTO;
import com.panda.merge.dto.ThirdBetCancelRollbackItemDTO;
import com.panda.merge.dto.message.StandardBetCancelItemMessage;
import com.panda.merge.dto.message.StandardBetCancelRollbackItemMessage;
import com.panda.merge.model.StandardRelationNewStandard;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportType;
import com.panda.merge.rocketmq.producer.StandardBetCancelRollbackProcessor;
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
 * @description : 回滚盘口取消操作时调用，对应上游rollback bet cancel
 * @date: 2020-09-09 18:53
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Component
@Slf4j
@Validated
public class ThirdBetCancelRollbackProcessor extends BaseProcessor {
    @Autowired
    private ThirdBetOperationLogService thirdBetOperationLogService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardBetCancelRollbackProcessor standardBetCancelRollbackProcessor;
    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;

    public void thirdBetCancelRollback(@Valid Request<ThirdBetCancelRollbackDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::thirdBetCancelRollback ={}", linkId, JSON.toJSONString(request));
        validateLinkId(Constant.PUT_BET_CANCEL_ROLLBACK,request);
        ThirdBetCancelRollbackDTO betCancelRollbackDTO = request.getData();
        String dataSourceCode = betCancelRollbackDTO.getDataSourceCode();
        String thirdSourceMatchId = betCancelRollbackDTO.getThirdSourceMatchId();

        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdSourceMatchId);
        if (thirdMatchInfo == null) {
            log.error("::{}::thirdBetCancelRollback 非法的三方赛事源Id, thirdMatchSourceId :{}, dataSourceCode :{}", linkId, thirdSourceMatchId, dataSourceCode);
            return;
        }
        Long referenceId = thirdMatchInfo.getReferenceId();
        if (referenceId == 0) {
            log.error("::{}::thirdBetCancelRollback 未绑定标准赛事ID, thirdMatchInfoId :{}, dataSourceCode :{}", linkId, thirdMatchInfo.getId(), dataSourceCode);
            return;
        }
        List<StandardBetCancelRollbackItemMessage> standardBetCancelRollbackItemMessages = getStandardBetCancelRollbackItemMessages(linkId, referenceId, betCancelRollbackDTO);
        if (standardBetCancelRollbackItemMessages.size() == 0) {
            log.info("::{}::thirdBetCancelRollback - getStandardBetCancelRollbackItemMessages 数据源盘口数据与标准数据匹配失败", linkId);
            return;
        }
        //入库：记录数据源状态数据
        thirdBetOperationLogService.betCancelRollbackCreate(betCancelRollbackDTO);
        //推送下游
        standardBetCancelRollbackProcessor.sendStandardBetCancelRollback(linkId, referenceId, thirdMatchInfo.getSportId(), betCancelRollbackDTO, standardBetCancelRollbackItemMessages);
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(referenceId);
        if (null == standardRelationNewStandard)
        {
            return;
        }
        else
        {
            List<StandardBetCancelRollbackItemMessage> standardBetCancelRollbackItemMessages1 = getStandardBetCancelRollbackItemMessages(linkId, standardRelationNewStandard.getNewStandardId(), betCancelRollbackDTO);
            if (standardBetCancelRollbackItemMessages1.size() == 0) {
                log.info("::{}::thirdBetCancelRollback - getStandardBetCancelRollbackItemMessages1 数据源盘口数据与标准数据匹配失败", linkId+"_new_match");
                return;
            }
            //推送下游
            standardBetCancelRollbackProcessor.sendStandardBetCancelRollback(linkId+"_new_match", standardRelationNewStandard.getNewStandardId(), thirdMatchInfo.getSportId(), betCancelRollbackDTO, standardBetCancelRollbackItemMessages1);
        }
    }


    private List<StandardBetCancelRollbackItemMessage> getStandardBetCancelRollbackItemMessages(String linkId, Long standardMatchId, ThirdBetCancelRollbackDTO thirdBetCancelRollbackDTO) {
        List<ThirdBetCancelRollbackItemDTO> marketData = thirdBetCancelRollbackDTO.getMarkets();
        String dataSourceCode = thirdBetCancelRollbackDTO.getDataSourceCode();
        List<String> strList = marketData.stream().map(ThirdBetCancelRollbackItemDTO::getThirdSourceMarketId).collect(Collectors.toList());
        log.info("::{}::thirdBetCancelRollback - thirdSourceMarketId List, strList :: {}, dataSourceCode :{}", linkId, strList, dataSourceCode);

        List<StandardSportMarket> standardSportMarketList = standardSportMarketService.getItemByThirdMarketSourceIdsAndDataSourceCode(strList, dataSourceCode, standardMatchId);
        Map<String, Long> stringLongMap = standardSportMarketList.stream().collect(
                Collectors.toMap(StandardSportMarket::getThirdMarketSourceId, StandardSportMarket::getRelationMarketId));
        List<StandardBetCancelRollbackItemMessage> standardBetCancelItemMessages = new ArrayList<>();
        for (ThirdBetCancelRollbackItemDTO thirdBetCancelRollbackItemDTO : marketData) {
            if (!stringLongMap.containsKey(thirdBetCancelRollbackItemDTO.getThirdSourceMarketId())) {
                continue;
            }
            StandardBetCancelRollbackItemMessage standardBetCancelRollbackItemMessage = new StandardBetCancelRollbackItemMessage();
            BeanUtils.copyProperties(thirdBetCancelRollbackItemDTO, standardBetCancelRollbackItemMessage);
            standardBetCancelRollbackItemMessage.setMarketId(stringLongMap.get(thirdBetCancelRollbackItemDTO.getThirdSourceMarketId()));
            standardBetCancelItemMessages.add(standardBetCancelRollbackItemMessage);
        }
        return standardBetCancelItemMessages;
    }


}
