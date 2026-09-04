package com.panda.merge.v2.service.assemble;

import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.dto.settle.ThirdMatchSettleScoresDto;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.utils.MatchEventInfoSettleUtils;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MatchSettleThirdScoreAssemble {

    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;

    public ThirdMatchSettleScoresDto searchFootballThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        //1.查询所有条件符合的三方比分
        List<String> eventCodes =new ArrayList<>();
        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
            eventCodes.add("fa_card");
            //2975 --add red_card
            eventCodes.add("red_card");
        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
            eventCodes.add("goal");eventCodes.add("kick_off");
        }else {
            eventCodes.add("corner");
        }
        List<MatchSettleThirdScore> list =matchSettleThirdScoreRepository.getModelByMatchIdAndEventCodeOrderBySettleNum(settleScoreSearchDto.getStandardMatchId(),eventCodes);
        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
        for (MatchSettleThirdScore matchSettleScore : list) {
            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            //查询阶段比分的时候要过滤角球 阶段比分 界面查询展示效果变更
            if(settleScoreSearchDto.getEventCode().equals("corner")&&(
                    matchSettleScore.getSettleNum().equals("201")||matchSettleScore.getSettleNum().equals("202")
                            ||matchSettleScore.getSettleNum().equals("203")||matchSettleScore.getSettleNum().equals("206")||matchSettleScore.getSettleNum().equals("207")
                            ||matchSettleScore.getSettleNum().equals("208"))){
                continue;
            }
            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        //2.根据数据商比分组
        Map<String,List<MatchSettleScoreDto>> map = matchSettleScoreDtos.stream().collect(Collectors.groupingBy(MatchSettleScoreDto::getDataSourceCode));
        //3.组装数据返回前端
        ThirdMatchSettleScoresDto thirdMatchSettleScoresDto =new ThirdMatchSettleScoresDto();
        thirdMatchSettleScoresDto.setEventCode(settleScoreSearchDto.getEventCode());
        thirdMatchSettleScoresDto.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
        thirdMatchSettleScoresDto.setThirdMatchScoresMap(map);
        //4.log日志记录异常报错以及耗时
        return thirdMatchSettleScoresDto;
    }

    public ThirdMatchSettleScoresDto searchBasketballThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        List<MatchSettleThirdScore> list =matchSettleThirdScoreRepository.getModelByStandardMatchIdAndSettleNum(settleScoreSearchDto.getStandardMatchId(), null);
        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
        for (MatchSettleThirdScore matchSettleScore : list) {
            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        //2.根据数据商比分组
        Map<String,List<MatchSettleScoreDto>> map = matchSettleScoreDtos.stream().collect(Collectors.groupingBy(MatchSettleScoreDto::getDataSourceCode));
        //3.组装数据返回前端
        ThirdMatchSettleScoresDto thirdMatchSettleScoresDto =new ThirdMatchSettleScoresDto();
        thirdMatchSettleScoresDto.setEventCode(settleScoreSearchDto.getEventCode());
        thirdMatchSettleScoresDto.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
        thirdMatchSettleScoresDto.setThirdMatchScoresMap(map);
        //4.log日志记录异常报错以及耗时
        return thirdMatchSettleScoresDto;
    }

}
