package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardOutrightMatchDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

/**
 * @Author Kepa
 * @Date 2021/7/14 10:17
 * @Version 1.0
 */
@Component
@Slf4j
@Validated
public class SelfBuildMarketOperateProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private SoldMessageToOddsProcessor soldMessageToOddsProcessor;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    private OutrightMatchLogService outrightMatchLogService;

    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;


    public void processBuildOutRightMarket(@Valid Request<StandardOutrightMatchDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::processBuildOutRightMarket 处理手动创建冠军盘口消息,req:{}", linkId, JSON.toJSONString(request));
        validateLinkId("processBuildOutRightMarket", request);
        StandardOutrightMatchDTO standardOutrightMatchDTO = request.getData();
        Long thirdMatchId = standardOutrightMatchDTO.getThirdOutrightMatchId();
        Long standardMatchId = standardOutrightMatchDTO.getStandardOutrightMatchId();
        String dataSourceCode = standardOutrightMatchDTO.getDataSourceCode();
        String thirdMarketSourceId = standardOutrightMatchDTO.getThirdMarketSourceId();

        // 0:创建  1:修改  2:并列
        Integer operateType = standardOutrightMatchDTO.getOperateType();

        //查询三方盘口
        List<ThirdSportMarket> thirdSportMarketList = Lists.newLinkedList();
        if (Constant.OUTRIGHT_ZERO == operateType) {
            thirdSportMarketList = thirdSportMarketService.getItemList(thirdMatchId, standardMatchId);
        } else {
            thirdSportMarketList = thirdSportMarketService.getItemList(thirdMatchId);
        }
        if (CollectionUtils.isEmpty(thirdSportMarketList)) {
            log.info("::{}::processBuildOutRightMarket thirdMatchId:{}, standardMatchId:{},三方盘口列表为空", linkId, thirdMatchId, standardMatchId);
            return;
        }

        ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfoByMatchId(true, standardMatchId, dataSourceCode);
        if (null == thirdMatchInfo) {
            log.info("::{}::processBuildOutRightMarket standardMatchId:{},未找到三方冠军赛事", linkId, standardMatchId);
            return;
        }
        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(true, standardMatchId);
        if (null == standardMatchInfo) {
            log.info("::{}::processBuildOutRightMarket standardMatchId:{},未找到标准赛事", linkId, standardMatchId);
            return;
        }
        //Step1:生成标准盘口
        Map<String, StandardMarketDataMessage> marketDataMessageMap = soldMessageToOddsProcessor.processMarketBySold(linkId,standardMatchInfo, thirdSportMarketList);
        log.info("::{}::processBuildOutRightMarket marketDataMessageMap:{}", linkId, JSON.toJSONString(marketDataMessageMap) );

        //过滤下发数据的操盘方式
        Map<String, StandardMarketDataMessage> newMarketDataMessageMap = new HashMap<>();

        String operateText = "盘口id:";
        Iterator iterator = marketDataMessageMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, StandardMarketDataMessage> entry = (Map.Entry<String, StandardMarketDataMessage>)iterator.next();
            StandardMarketDataMessage marketDataMessage = new StandardMarketDataMessage();
            StandardMarketDataMessage oldmarketData = marketDataMessageMap.get(entry.getKey());
            BeanUtils.copyProperties(oldmarketData, marketDataMessage);

            if (DataSourceCodeEnum.PA.name().equals(oldmarketData.getDataSourceCode())) {
                marketDataMessage.setTradeType(Constant.OUTRIGHT_ONE);
                operateText += marketDataMessage.getRelationMarketId();
            }
            newMarketDataMessageMap.put(entry.getKey(),marketDataMessage);
        }
        log.info("::{}::processBuildOutRightMarket newMarketDataMessageMap:{}", linkId, JSON.toJSONString(newMarketDataMessageMap) );

        //Step2:初始化盘口开售表
        String operateTitle = "盘口编辑";
        if (Constant.OUTRIGHT_ZERO == operateType) {
            operateTitle = "盘口创建";

            if (!CollectionUtils.isEmpty(newMarketDataMessageMap)) {
                log.info("::{}::processBuildOutRightMarket 初始化盘口开售表,标准赛事id:{}, size:{}", linkId, standardMatchId, newMarketDataMessageMap.size());
                List<StandardOutrightMarket> standardOutrightMarketList = new ArrayList<>();
                newMarketDataMessageMap.forEach((k, v) -> {
                    StandardOutrightMarket standardOutrightMarket = new StandardOutrightMarket();
                    standardOutrightMarket.setId(v.getRelationMarketId());
                    standardOutrightMarket.setStandardMatchId(standardMatchId);
                    standardOutrightMarket.setMarketCategoryId(v.getMarketCategoryId());
                    standardOutrightMarket.setMarketStatus(v.getStatus());
                    standardOutrightMarket.setNameCode(v.getNameCode());
                    standardOutrightMarket.setLinkId(linkId);
                    standardOutrightMarket.setMarketSellStatus(SellStatusEnum.UNSOLD.value);
                    standardOutrightMarketList.add(standardOutrightMarket);
                });
                standardOutrightMarketService.saveBatch(standardOutrightMarketList);
            }
            log.info("::{}:: processBuildOutRightMarket success.size:{}", linkId, newMarketDataMessageMap.size());
        }
//        else {
//
//            List<StandardSportMarket> ssmList = standardSportMarketService.getItemList(standardMatchId).stream().filter(ssm -> ssm.getRelationMarketId() !=null ).collect(Collectors.toList());
//            log.info("::{}::processBuildOutRightMarket 遍历的盘口:{}", linkId, JSON.toJSONString(ssmList) );
//            Map<String,Long> sourceRelationIdMap = ssmList.stream().collect(Collectors.toMap(StandardSportMarket::getThirdMarketSourceId,StandardSportMarket::getRelationMarketId));
//
//            if (!sourceRelationIdMap.containsKey(thirdMarketSourceId)) {
//                log.info("::{}::processBuildOutRightMarket thirdMarketSourceId:{},未找到标准盘口", linkId, thirdMarketSourceId);
//                return;
//            }
//
//            StandardOutrightMarket standardOutrightMarket = standardOutrightMarketService.getOutrightMarketData(sourceRelationIdMap.get(thirdMarketSourceId));
//            if (SellStatusEnum.SOLD.value.equals(standardOutrightMarket.getMarketSellStatus())) {
//
//                //还需要再次的刷新盘口类型 ？？？？？
//                OutrightTradeTypeConfigDTO tradeTypeConfigDTO = new OutrightTradeTypeConfigDTO();
//                tradeTypeConfigDTO.setOperaterId(standardOutrightMatchDTO.getOperatorId());
//                tradeTypeConfigDTO.setStandardMarketId(sourceRelationIdMap.get(thirdMarketSourceId));
//                tradeTypeConfigDTO.setStandardMatchId(standardMatchId);
//                tradeTypeConfigDTO.setTradeType(Constant.OUTRIGHT_ONE);
//                tradeTypeConfigDTO.setLevel(Constant.OUTRIGHT_ZERO);
//                ConfigOutrightTradeType configOutrightTradeType = outrightTradeTypeConfigService.selectItem(tradeTypeConfigDTO);
//                if(null == configOutrightTradeType){
//                    outrightTradeTypeConfigService.insertItem(request.getLinkId(),tradeTypeConfigDTO);
//                }else {
//                    configOutrightTradeType.setTradeType(tradeTypeConfigDTO.getTradeType());
//                    configOutrightTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                    outrightTradeTypeConfigService.updateItem(configOutrightTradeType);
//                }
//
//                //发当前手动盘口最新赔率
//                Set<Long> marketIdSet = newMarketDataMessageMap.values().stream().map(StandardMarketDataMessage::getRelationMarketId).collect(Collectors.toSet());
//                thirdMatchMarketProcessor.processOddsByOutright(linkId, standardMatchInfo, marketIdSet, newMarketDataMessageMap, System.currentTimeMillis(), new HashMap<>());
//            }
//        }

        //Step4:记录操作日志
        Long operateTargetId = standardMatchInfo.getId();
        this.workRecordOfOutright (standardOutrightMatchDTO.getOperatorId(), standardOutrightMatchDTO.getOperatorName(), operateTargetId, operateTitle, operateText);


    }

    /**
     * 手动冠军赛事与盘口的操作日志的处理
     * @param operateTargetId
     * @param operateType
     * @param operateContext
     */
    public void workRecordOfOutright (Long operatorId, String operatorName, Long operateTargetId, String operateType, String operateContext) {

        OutrightMatchLog outrightMatchLog = new OutrightMatchLog();
        outrightMatchLog.setOperateTargetId(operateTargetId);   //standardOutrightMatchInfo.getId()
        outrightMatchLog.setOperatorId( operatorId );
        outrightMatchLog.setOperatorName(operatorName);
        outrightMatchLog.setOperatorNumber(UUID.randomUUID().toString().replaceAll("-",""));

        //日志内容
        List<Map<String,String>> listParamsCurrent = Lists.newArrayList();
        Map<String,String> mapsCurrnet = Maps.newHashMap();
        mapsCurrnet.put("operatorModle",  operateType );
        mapsCurrnet.put("operatorText", operateContext );
        listParamsCurrent.add(mapsCurrnet);

        //保存操作日志
        outrightMatchLogService.saveBatchOutrightMatchRecord(listParamsCurrent, outrightMatchLog);

    }

}
