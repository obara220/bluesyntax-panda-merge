package com.panda.merge.v2.service.assemble;

import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleThirdEvent;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.utils.MatchEventInfoSettleUtils;
import com.panda.merge.v2.repository.MatchSettleThirdEventRepository;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MatchSettleThirdEventAssemble {

    @Autowired
    private MatchSettleThirdEventRepository matchSettleThirdEventRepository;
    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;

    public ThirdMatchSettleEventDto searchFootballThirdMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto) {
        //1.根据标准赛事等条件查询到相关事件
        List<Long> periods=new ArrayList<>();
        List<String> eventCodes =new ArrayList<>();
        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
            eventCodes.add("yellow_card");
            eventCodes.add("red_card");eventCodes.add("fa_card");
        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
            eventCodes.add("goal");eventCodes.add("no goal");
        }else {
            eventCodes.add("corner");
        }
        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
        List<MatchSettleThirdEvent> list =matchSettleThirdEventRepository.getModelByItemsOrderBySettleNum(
                settleScoreSearchDto.getStandardMatchId(),eventCodes,periods,null,null);
        List<MatchSettleEventDto> matchSettleScoreDtos=new ArrayList<>();
        for (MatchSettleThirdEvent matchSettleScore : list) {
            MatchSettleEventDto matchSettleScoreDto =new MatchSettleEventDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            // 设置 eventType
            matchSettleScoreDto.setEventType(matchSettleScore.getEventType());
            // 转换秒数为 23:20 格式
            if (matchSettleScore.getSecondFromStart() != null) {
                matchSettleScoreDto.setSecondFromStart(formatSecondsToTime(matchSettleScore.getSecondFromStart()));
            }
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        if(settleScoreSearchDto.getEventCode().equals("corner")){
            //查询根据settleNum
            List<String> settleNumCorner=new ArrayList<>();
            settleNumCorner.add("201"); settleNumCorner.add("202"); settleNumCorner.add("203");
            settleNumCorner.add("206"); settleNumCorner.add("207"); settleNumCorner.add("208");
            List<MatchSettleThirdScore> matchSettleScores =matchSettleThirdScoreRepository.getModelByMatchIdAndEventCodeAndSettleNum(
                    settleScoreSearchDto.getStandardMatchId(), Arrays.asList("corner"),settleNumCorner);
            for (MatchSettleThirdScore matchSettleScore : matchSettleScores) {
                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
                matchSettleEvent.setId(matchSettleScore.getId().toString());
                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                matchSettleScoreDtos.add(matchSettleEvent);
            }
        }
        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
            //查询根据settleNum
            List<String> settleNumFaCard=new ArrayList<>();
            settleNumFaCard.add("304"); settleNumFaCard.add("308"); settleNumFaCard.add("309");
            settleNumFaCard.add("3013"); settleNumFaCard.add("3017"); settleNumFaCard.add("3018");
            List<MatchSettleThirdScore> matchSettleScores =matchSettleThirdScoreRepository.getModelByMatchIdAndEventCodeAndSettleNum(
                    settleScoreSearchDto.getStandardMatchId(), Arrays.asList("fa_card"),settleNumFaCard);
            for (MatchSettleThirdScore matchSettleScore : matchSettleScores) {
                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
                matchSettleEvent.setId(matchSettleScore.getId().toString());
                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                // MatchSettleThirdScore 没有 secondFromStart 字段，只有 MatchSettleThirdEvent 才有
                // 这里的 matchSettleScore 是 MatchSettleThirdScore 类型，不需要处理 secondFromStart
                matchSettleScoreDtos.add(matchSettleEvent);
            }
        }
        //2.根据事件进行分组
        Map<String,List<MatchSettleEventDto>> map= matchSettleScoreDtos.stream().collect(Collectors.groupingBy(MatchSettleEventDto::getDataSourceCode));
        Map<String,List<MatchSettleEventExtryInfoDto>> Infomap =new HashMap<>();
        for (Map.Entry<String, List<MatchSettleEventDto>> entry : map.entrySet()) {
            List<MatchSettleEventExtryInfoDto>  l = Infomap.get(entry.getKey());
            if(l==null){
                l= new ArrayList<>();
                Infomap.put(entry.getKey(),l);
            }
            for (MatchSettleEventDto matchSettleEventDto : entry.getValue()) {
                MatchSettleEventExtryInfoDto infoDto =new MatchSettleEventExtryInfoDto();
                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEventDto);
                BeanUtils.copyProperties(matchSettleEventDto,infoDto);
                // 显式设置 eventType 和 secondFromStart，确保正确传递
                infoDto.setEventType(matchSettleEventDto.getEventType());
                infoDto.setSecondFromStart(matchSettleEventDto.getSecondFromStart());
                l.add(infoDto);
            }
        }
        //3.组装事件数据
        ThirdMatchSettleEventDto thirdMatchSettleEventDto =new ThirdMatchSettleEventDto();
        thirdMatchSettleEventDto.setEventCode(settleScoreSearchDto.getEventCode());
        thirdMatchSettleEventDto.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
        thirdMatchSettleEventDto.setThirdMatchEventMap(Infomap);
        //4.返回前端
        return thirdMatchSettleEventDto;
    }

    /**
     * 将秒数转换为 23:20 格式
     * @param seconds 秒数
     * @return 格式化的时间字符串，如 "23:20"
     */
    private String formatSecondsToTime(Integer seconds) {
        if (seconds == null) {
            return null;
        }
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
}
