package com.panda.merge.rocketmq.processor;


import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.MatchPeriodForMatchOverEnum;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchOverMessage;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchEventInfoExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 接收操盘非常规结束赛事完赛
 *
 * @author idol
 * @version 1.0
 * @taskId:
 * @createDate 2021/10/13
 * @see com.panda.merge.rocketmq.processor
 */
@Slf4j
@Component
@Validated
public class LiveMatchOverProcessor {

    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;
//    @Autowired
//    private MatchEventInfoService matchEventInfoService;
    @Autowired
    private  StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private  StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private MatchEventInfoProcessor matchEventInfoProcessor;

    public void liveMatchOverProcessor(@Valid Request<MatchOverMessage> request) {
        log.info("【" + PROJECT_ID_REALTIME + " ：" + FROM_RCS_MATCH_IS_END + "】【::" + request.getLinkId() + "::】非常规结束赛事完赛开始");
        String isEnd = request.getData().getIsEnd();
        if (StringUtils.isBlank(isEnd) || String.valueOf(ZERO).equals(isEnd)) {
            log.info("【::" + request.getLinkId() + "::】非常规结束赛事完赛isEnd不为1不予处理");
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(request.getData().getMatchId());
        if (standardMatchInfo == null) {
            log.info("【::" + request.getLinkId() + "::】非常规结束赛事完赛,没有找到标准赛事不予处理,赛事id:" + request.getData().getMatchId());
            return;
        }
        //商业事件源编码(为空默认为标准赛事对应数据源)
        String businessEventCode = standardMatchInfo.getDataSourceCode();
        //获取开售信息,根据开售信息判断是否推送MQ消息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
        if (null != standardSportMarketSell) {
            //开售商业事件源编码
            if(StringUtils.isNotBlank(standardSportMarketSell.getBusinessEvent())){
                businessEventCode = standardSportMarketSell.getBusinessEvent();
            }
        }
        //查询库中事件信息
        MatchEventInfoExample example = new MatchEventInfoExample();
        example.createCriteria().andStandardMatchIdEqualTo(request.getData().getMatchId()).andDataSourceCodeEqualTo(businessEventCode)
                .andMatchPeriodIdEqualTo(MatchPeriodForMatchOverEnum.Ended999.value);
        List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(example);

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(matchEventInfos) && matchEventInfos.size()>1) {
            matchEventInfos = matchEventInfos.stream()
                    .filter(event -> EventCodeEnum.MATCH_STATUS.code.equals(event.getEventCode()))
                    .sorted(Comparator.comparing(MatchEventInfo::getEventTime))
                    .limit(1)
                    .collect(Collectors.toList());
        }
//        List<MatchEventInfo> matchEventInfos = matchEventInfoService.getItemByStandardMatchIdAndDataSoureCode(request.getData().getMatchId(), businessEventCode);
        if (CollectionUtils.isEmpty(matchEventInfos)) {
            log.info("【::" + request.getLinkId() + "::】非常规结束赛事完赛,没有找到完赛999事件不予处理,赛事id:{}，商业数据源编码：{}",standardMatchInfo.getId(),businessEventCode);
            return;
        }else{
            Set<Long> matchPeriodIds = matchEventInfos.stream().map(obj -> obj.getMatchPeriodId()).collect(Collectors.toSet());
            if(!matchPeriodIds.contains(MatchPeriodForMatchOverEnum.Ended999.value)){
                log.info("【::" + request.getLinkId() + "::】非常规结束赛事完赛,没有找到完赛999事件不予处理,赛事id:{}，商业数据源编码：{}",standardMatchInfo.getId(),businessEventCode);
                return;
            }
        }
        String thirdMatchSourceId = null;
        List<MatchEventInfoDTO> matchEventInfoDTOS = new LinkedList<>();
        for (MatchEventInfo matchEventInfo : matchEventInfos) {
            thirdMatchSourceId = matchEventInfo.getThirdMatchSourceId();
            MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
            BeanUtils.copyProperties(matchEventInfo, matchEventInfoDTO);
            matchEventInfoDTO.setExtrainfo(matchEventInfo.getExtraInfo());
            matchEventInfoDTO.setSourceType(String.valueOf(matchEventInfo.getSourceType()));
            matchEventInfoDTO.setThirdMatchSourceId(matchEventInfo.getThirdMatchSourceId());
            matchEventInfoDTO.setIsErrorEndEvent(ConstantSystem.ONE);
            matchEventInfoDTO.setDeleteNotFilterFlag(true);
            matchEventInfoDTOS.add(matchEventInfoDTO);
        }
        matchEventInfoProcessor.process2MatchEvent(request,matchEventInfoDTOS,thirdMatchSourceId,businessEventCode);
        log.info("【" + PROJECT_ID_REALTIME + " ：" + FROM_RCS_MATCH_IS_END + "】【::" + request.getLinkId() + "::】非常规结束赛事完赛结束");
    }


}
