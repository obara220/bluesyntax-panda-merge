package com.panda.merge.rocketmq.processor;

import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketOddsResultDTO;
import com.panda.merge.dto.ThirdMatchResultDTO;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.StandardMatchResultProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/29 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class ThirdMarketResultProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;

    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;

    @Autowired
    private StandardMatchResultProducer standardMatchResultProducer;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;
    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;

    /**
     * 全部投注项的赛果都是  赢半，输半， 输，赢 或者 走水时，不把该玩法的该盘口值赛果下发至业务侧
     */
    private Set<Long> RESULT_SET = new HashSet<Long>(){{add(2L);add(3L);add(4L);add(5L);add(6L);}};
    /**
     * 数据源不走 ，全部投注项的赛果都是  赢半，输半， 输，赢 或者 走水时，不把该玩法的该盘口值赛果下发至业务侧
     */
    private List<String> RESULT_SET_DATASOURCE_CODE = Lists.newArrayList("BE","OD","F01","SR");


    public void thirdMarketResultApi(@Valid Request<ThirdMatchResultDTO> request) {
        StopWatch stopWatch = new StopWatch("thirdMarketResultApi_"+ UUIdUtils.getId());
        stopWatch.start();
        String linkId = request.getLinkId();
        log.info("::{}::数据源赛果处理开始", linkId);
        ThirdMatchResultDTO thirdMatchResultDTO = request.getData();
        boolean isOutRight = 1 == thirdMatchResultDTO.getMatchType();
        //查找三方赛事
        ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfo(isOutRight, thirdMatchResultDTO.getDataSourceCode(), thirdMatchResultDTO.getThirdMatchId());
        if (null == thirdMatchInfo) {
            log.info("::{}::查询三方赛事为空,三方赛事id={}", linkId, thirdMatchResultDTO.getThirdMatchId());
            return;
        }
        //查找开售信息
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
        if (null == standardSportMarketSell) {
            log.info("::{}::未找到预开售信息,标准赛事id={}", linkId, thirdMatchInfo.getReferenceId());
            return;
        }
        //获取三方盘口数据
        List<ThirdSportMarket> thirdSportMarketList = thirdSportMarketService.getItemList(thirdMatchInfo.getId());
        if (CollectionUtils.isEmpty(thirdSportMarketList)) {
            log.info("::{}::未找到三方盘口,标准赛事id={}", linkId, thirdMatchInfo.getReferenceId());
            return;
        }
        Map<String, ThirdSportMarket> thirdSportMarketMap = thirdSportMarketList.stream().collect(Collectors.toMap(ThirdSportMarket::getThirdMarketSourceId, Function.identity()));
        //获取标准盘口数据
        List<StandardSportMarket> standardSportMarketList = standardSportMarketService.getItemList(thirdMatchInfo.getReferenceId());
        if (CollectionUtils.isEmpty(standardSportMarketList)) {
            log.info("::{}::未找到标准盘口,标准赛事id={}", linkId, thirdMatchInfo.getReferenceId());
            return;
        }
        Map<String, StandardSportMarket> standardSportMarketMap = standardSportMarketList.stream().collect(Collectors.toMap(StandardSportMarket::getThirdMarketSourceId, Function.identity()));
        //按盘口处理赛果
        thirdMatchResultDTO.getMarketResultList().forEach(thirdMarketResultDTO -> {
            //三方投注项结果
            List<ThirdMarketOddsResultDTO> marketOddsResultList = thirdMarketResultDTO.getMarketOddsResultList();
            //--------------更新三方投注项数据结果--------------
            //三方盘口
            ThirdSportMarket thirdSportMarket = thirdSportMarketMap.get(thirdMarketResultDTO.getThirdMarketId());
            if(thirdSportMarket == null){
                log.error("::{}::三方盘口未找到三方盘口原始id:{},", linkId, thirdMarketResultDTO.getThirdMarketId());
                return;
            }
            //查询三方投注项赔率数据
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketOddsService.getItemList(thirdMatchResultDTO.getDataSourceCode(),thirdSportMarket.getId());
            if (CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                log.error("::{}::三方投注项赔率数据未找到三方盘口原始id:{},", linkId, thirdSportMarket.getId());
                return;
            }
            Map<String, ThirdSportMarketOdds> thirdSportMarketOddsMap = thirdSportMarketOddsList.stream().collect(Collectors.toMap(thi -> thi.getThirdOddsFieldSourceId(), thi -> thi,(oldValue,newValue)->newValue));
            //更新三方投注项结果
            for (ThirdMarketOddsResultDTO thirdMarketOddsResultDTO : marketOddsResultList) {
                ThirdSportMarketOdds thirdSportMarketOdds = thirdSportMarketOddsMap.get(thirdMarketOddsResultDTO.getMarketOddsId());
                if (thirdSportMarketOdds != null) {
                    thirdSportMarketOdds.setSettlementResult(thirdMarketOddsResultDTO.getSettlementResult());
                    thirdSportMarketOdds.setBetSettlementCertainty(thirdMarketOddsResultDTO.getBetSettlementCertainty());
                    thirdSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    thirdSportMarketOddsService.updateByPrimaryKeySelective(thirdMatchResultDTO.getDataSourceCode(),thirdSportMarketOdds);
                }
            }
            //------------更新标准投注项数据结果--------------
            //标准盘口
            StandardSportMarket standardSportMarket = standardSportMarketMap.get(thirdMarketResultDTO.getThirdMarketId());
            if(standardSportMarket == null){
                log.error("::{}::DB标准盘口未找到三方盘口原始id:{},", linkId, thirdMarketResultDTO.getThirdMarketId());
                standardSportMarket = standardSportMarketService.getItem(thirdMatchInfo.getDataSourceCode(), thirdMarketResultDTO.getThirdMarketId(), thirdMatchInfo.getReferenceId());
                if (null == standardSportMarket) {
                    log.error("::{}::redis标准盘口未找到三方盘口原始id:{},", linkId, thirdMarketResultDTO.getThirdMarketId());
                    return;
                }
            }
            //查询标准投注项赔率数据
            List<StandardSportMarketOdds> standardSportMarketOddsList = standardSportMarketOddsService.getItemList(standardSportMarket.getId());
            if (CollectionUtils.isEmpty(standardSportMarketOddsList)) {
                log.error("::{}::标准投注项赔率数据未找到标准盘口id:{},", linkId, standardSportMarket.getId());
                return;
            }
            Map<String, StandardSportMarketOdds> standardSportMarketOddsMap = standardSportMarketOddsList.stream().collect(Collectors.toMap(thi -> thi.getThirdOddsFieldSourceId(), thi -> thi,(oldValue,newValue)->newValue));
            //更新标准投注项结果

            List<StandardSportMarketOdds> standardSettledList = new ArrayList<>();
            for (ThirdMarketOddsResultDTO thirdMarketOddsResultDTO : marketOddsResultList) {
                StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMap.get(thirdMarketOddsResultDTO.getMarketOddsId());
                if (standardSportMarketOdds != null) {
                    standardSportMarketOdds.setSettlementResult(thirdMarketOddsResultDTO.getSettlementResult());
                    standardSportMarketOdds.setBetSettlementCertainty(thirdMarketOddsResultDTO.getBetSettlementCertainty());
                    standardSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    standardSportMarketOddsService.updateByPrimaryKeySelective(standardSportMarketOdds);
                    standardSettledList.add(standardSportMarketOdds);
                }else {
                    standardSportMarketOdds = standardSportMarketOddsService.getItem(standardSportMarket.getDataSourceCode(), thirdMarketOddsResultDTO.getMarketOddsId(), standardSportMarket.getId());
                    log.error("::{}::redis标准盘口赔率未找到，重新查询redis,三方盘口原始id:{},", linkId, thirdMarketResultDTO.getThirdMarketId());
                    if (null != standardSportMarketOdds) {
                        standardSportMarketOdds.setSettlementResult(thirdMarketOddsResultDTO.getSettlementResult());
                        standardSportMarketOdds.setBetSettlementCertainty(thirdMarketOddsResultDTO.getBetSettlementCertainty());
                        standardSettledList.add(standardSportMarketOdds);
                    }
                }
            }
            //全部投注项的赛果都是 赢半，输半， 输 或者 赢时，不把该玩法的该盘口值赛果下发至业务侧
            boolean isNeedSend = true;
            if (!CollectionUtils.isEmpty(marketOddsResultList) &&
                    !RESULT_SET_DATASOURCE_CODE.contains(thirdMatchInfo.getDataSourceCode()))
            {
                Set<Long> resultSet = new HashSet<>();
                for (ThirdMarketOddsResultDTO thirdMarketOddsResultDTO : marketOddsResultList) {
                    String tempResult = thirdMarketOddsResultDTO.getSettlementResult();
                    resultSet.add(Long.valueOf(tempResult));
                }
                log.info("::{}::标准赔率各个投注项赛果 resultSet :{},", linkId, resultSet);
                if (resultSet.size() == 1 && RESULT_SET.containsAll(resultSet))
                {
                    isNeedSend = false;
                }
            }
            //下发数据到topic
            standardMatchResultProducer.pushStandardMatchResultInfo(linkId, thirdMatchResultDTO.getMatchType(), standardSettledList, standardSportMarket, thirdMatchInfo ,thirdMarketResultDTO,thirdMatchInfo.getReferenceId());
            /*if (isNeedSend)
            {
                standardMatchResultProducer.pushStandardMatchResultInfo(linkId, thirdMatchResultDTO.getMatchType(), standardSettledList, standardSportMarket, thirdMatchInfo ,thirdMarketResultDTO,thirdMatchInfo.getReferenceId());
            }
            else
            {
                log.info("::{}::标准赔率各个投注项赛果一致，不下发赛果:{},", linkId, thirdMarketResultDTO);
            }*/
            //冠军玩法赛果统计
            if(MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(standardSportMarket.getMarketCategoryId())){
                StandardOutrightMatchCategory standardOutrightMatchCategory = new StandardOutrightMatchCategory();
                //标准玩法id 是standard_market_category.id  还是 standard_sport_market_category.id ???????
                standardOutrightMatchCategory.setId(standardSportMarket.getId());
                standardOutrightMatchCategory.setMatchResultStatus(standardSettledList.size());
                //暂未测试,使用放开
                //standardOutrightMatchCategoryService.updateByPrimaryKeySelective(standardOutrightMatchCategory);
            }
        });
        //测试联赛赛果处理
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(thirdMatchInfo.getReferenceId());
        if (null != standardRelationNewStandard)
        {
            List<StandardSportMarket> standardSportMarketList1 = standardSportMarketService.getItemList(standardRelationNewStandard.getNewStandardId());
            if (CollectionUtils.isEmpty(standardSportMarketList1)) {
                log.info("::{}::未找到标准盘口,标准赛事id={}", linkId+"_new_match", standardRelationNewStandard.getNewStandardId());
                return;
            }
            Map<String, StandardSportMarket> standardSportMarketMap1 = standardSportMarketList1.stream().collect(Collectors.toMap(StandardSportMarket::getThirdMarketSourceId, Function.identity()));
            //按盘口处理赛果
            thirdMatchResultDTO.getMarketResultList().forEach(thirdMarketResultDTO -> {
                //------------更新标准投注项数据结果--------------
                //标准盘口
                StandardSportMarket standardSportMarket = standardSportMarketMap1.get(thirdMarketResultDTO.getThirdMarketId());
                if(standardSportMarket == null){
                    log.error("::{}::标准盘口未找到三方盘口原始id:{},", linkId+"_new_match", thirdMarketResultDTO.getThirdMarketId());
                    return;
                }
                //查询标准投注项赔率数据
                List<StandardSportMarketOdds> standardSportMarketOddsList = standardSportMarketOddsService.getItemList(standardSportMarket.getId());
                if (CollectionUtils.isEmpty(standardSportMarketOddsList)) {
                    log.error("::{}::标准投注项赔率数据未找到标准盘口id:{},", linkId+"_new_match", standardSportMarket.getId());
                    return;
                }
                Map<String, StandardSportMarketOdds> standardSportMarketOddsMap = standardSportMarketOddsList.stream().collect(Collectors.toMap(thi -> thi.getThirdOddsFieldSourceId(), thi -> thi,(oldValue,newValue)->newValue));
                //更新标准投注项结果
                //三方投注项结果
                List<ThirdMarketOddsResultDTO> marketOddsResultList = thirdMarketResultDTO.getMarketOddsResultList();
                List<StandardSportMarketOdds> standardSettledList = new ArrayList<>();
                for (ThirdMarketOddsResultDTO thirdMarketOddsResultDTO : marketOddsResultList) {
                    StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMap.get(thirdMarketOddsResultDTO.getMarketOddsId());
                    if (standardSportMarketOdds != null) {
                        standardSportMarketOdds.setSettlementResult(thirdMarketOddsResultDTO.getSettlementResult());
                        standardSportMarketOdds.setBetSettlementCertainty(thirdMarketOddsResultDTO.getBetSettlementCertainty());
                        standardSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        standardSportMarketOddsService.updateByPrimaryKeySelective(standardSportMarketOdds);
                        standardSettledList.add(standardSportMarketOdds);
                    }
                }
                //全部投注项的赛果都是 赢半，输半， 输 或者 赢时，不把该玩法的该盘口值赛果下发至业务侧
                boolean isNeedSend = true;
                if (!CollectionUtils.isEmpty(marketOddsResultList) &&
                        !RESULT_SET_DATASOURCE_CODE.contains(thirdMatchInfo.getDataSourceCode()))
                {
                    Set<Long> resultSet = new HashSet<>();
                    for (ThirdMarketOddsResultDTO thirdMarketOddsResultDTO : marketOddsResultList) {
                        String tempResult = thirdMarketOddsResultDTO.getSettlementResult();
                        resultSet.add(Long.valueOf(tempResult));
                    }
                    log.info("::{}::标准赔率各个投注项赛果 resultSet :{},", linkId, resultSet);
                    if (resultSet.size() == 1 && RESULT_SET.containsAll(resultSet))
                    {
                        isNeedSend = false;
                    }
                }
                //下发数据到topic
                if (isNeedSend)
                {
                    //下发数据到topic
                    standardMatchResultProducer.pushStandardMatchResultInfo(linkId+"_new_match", thirdMatchResultDTO.getMatchType(), standardSettledList, standardSportMarket, thirdMatchInfo ,thirdMarketResultDTO,standardRelationNewStandard.getNewStandardId());
                }
            });
        }
        //统计处理耗时
        stopWatch.stop();
        paDataServiceLogProducer.sendPaDataServiceLog(
                getPaDataServiceLogDTO(request.getLinkId(),"odds-admin","THIRD_MARKET_RESULT_API","赛果信息topic",
                        stopWatch.getTotalTimeMillis(),200,null)
        );
    }
}
