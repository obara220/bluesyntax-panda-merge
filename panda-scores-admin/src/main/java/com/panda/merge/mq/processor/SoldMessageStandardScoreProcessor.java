package com.panda.merge.mq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * @author warren
 * @since 2025/01/03 16:29:52
 */
@Slf4j
@Validated
@Component
public class SoldMessageStandardScoreProcessor {
    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private MessageBuilderUtils messageBuilderUtils;

    @Autowired
    private ScoresProducer scoresProducer;

    @Autowired
    private ThirdMatchInfoRepository thirdMatchInfoRepository;

    @Autowired
    private MatchScoreInfoRepository matchScoreInfoRepository;

    @Autowired
    private StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

//    @Async("SoldMessageStandardScoreThreadPool")
    public void execute(Request<SoldMessage> soldMessageRequest) {
        SoldMessage soldMessage = soldMessageRequest.getData();
        String linkId = soldMessageRequest.getLinkId();
//        log.info("::{}::比分中心:soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(soldMessageRequest));
        Long matchId = soldMessage.getMatchId();
//        StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//        example.createCriteria().andMatchInfoIdEqualTo(matchId);
//        example.setOrderByClause("id desc limit 1");
//        List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(example);
//        if (CollectionUtils.isEmpty(standardSportMarketSellList)) {
//            log.info("::{}::比分中心-开售赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
//            return;
//        }
//        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellList.get(0);

        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(matchId);
        if (standardSportMarketSell==null) {
            log.info("::{}::比分中心-开售赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(matchId);
        if (standardMatchInfo.getMatchPeriodId() != null && standardMatchInfo.getMatchPeriodId() == 0L) {
            log.info("::{}::比分中心-赛事未开赛-阶段为0,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        if (SellStatusEnum.UNSOLD.getValue().equals(standardSportMarketSell.getLiveMatchSellStatus()) && SellStatusEnum.UNSOLD.getValue().equals(standardSportMarketSell.getPreMatchSellStatus())) {
            log.info("::{}::比分中心-赛事未开售,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        String businessEvent = standardSportMarketSell.getBusinessEvent();
        Long sportId = standardSportMarketSell.getSportId();

        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(matchId, businessEvent);
        if (thirdMatchInfo == null) {
            log.info("::{}::比分中心-开售:三方赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        Integer dataSourceType = SourceTypeEnum.LIVE_DATA.getCode();
        if (DataSourceCodeEnum.BC.code.equals(soldMessageRequest.getDataSourceCode())) {
            Long sourceType = matchScoreInfoRepository.checkB02ScoresSource(sportId);
            dataSourceType = sourceType == 1 ? 0 : 1;
        }
//        log.info("::{}::比分中心:thirdMatchInfo-soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(thirdMatchInfo));
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), dataSourceType);
        if(matchScoresInfo==null || matchScoresInfo.getScoresJson()==null){
            log.info("::{}::比分中心-开售:比分不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
//        log.info("::{}::比分中心:matchScoresInfo-soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(matchScoresInfo));
        // 数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, matchScoresInfo);
//        log.info("::{}::比分中心:commonScoresDto-soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(commonScoresDto));
        commonScoresDto.setLinkedId(linkId);
        scoresProducer.sendStandardMatchScores(commonScoresDto);
    }
}
