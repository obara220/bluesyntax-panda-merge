package com.panda.merge.v2.service.assemble;

import com.panda.merge.config.RedisService;
import com.panda.merge.dto.BasketBallSearchScoreCompareDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.IBasketballInSettleService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import com.panda.merge.utils.MatchEventInfoSettleUtils;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.service.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class MatchSettleScoreAssemble {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;

    @Autowired
    private MatchSettleCheckInfoHelper matchSettleCheckInfoHelper;

    @Autowired
    private MatchSettleRollBackInfoHelper matchSettleRollBackInfoHelper;

    @Autowired
    private MatchDelaySettleInfoHelper matchDelaySettleInfoHelper;

    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;

    @Autowired
    private MatchSettleScoreHelper matchSettleScoreHelper;

    @Autowired
    private MentionStatusHelper mentionStatusHelper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Lazy
    @Autowired
    private IBasketballInSettleService basketballInSettleService;

    public List<MatchSettleScoreDto> searchFootballMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
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
        List<MatchSettleScore> list =matchSettleScoreRepository.getModelByMatchIdAndEventCodeOrderBySettleNum(settleScoreSearchDto.getStandardMatchId(),eventCodes);

        Map<String, Integer> deleteStatusMap = new HashMap<>();
        Map<String, Integer> dataMismatchMap = new HashMap<>();
        mentionStatusHelper.obtainDetailInfo(settleScoreSearchDto, deleteStatusMap, dataMismatchMap);
        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
        for (MatchSettleScore matchSettleScore : list) {
            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            // deleteStatusMap 和 dataMismatchMap 的 key 可能是 matchSettleScoreId 或 matchSettleEventId
            // 对于 MatchSettleScore，主要使用 matchSettleScoreId 查找
            String scoreIdKey = String.valueOf(matchSettleScore.getId());
            Integer deleteStatus = deleteStatusMap.get(scoreIdKey);
            matchSettleScoreDto.setHasDeleteEvent(deleteStatus != null ? deleteStatus : matchSettleScore.getHasDeleteEvent());
            Integer dataMismatchStatus = dataMismatchMap.get(scoreIdKey);
            matchSettleScoreDto.setHasDataMismatchEvent(dataMismatchStatus != null ? dataMismatchStatus : 0);
            matchSettleScoreDto.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
            matchSettleScoreDto.setIsGrey(matchSettleScore.getIsGrey());
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
        //查询 当前用户的 阶段比分的明细的审核状态
        matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
        matchSettleRollBackInfoHelper.setRollBackStatusScores(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
        //查询比分的倒计时秒数
        matchDelaySettleInfoHelper.setDelaySettleSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
        matchSettleScoreDtos = matchSettleInfoHelper.setFiveMinList(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
        //查询回滚状态
        return matchSettleScoreDtos;
    }

    public List<MatchSettleScoreDto> searchBasketballMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        log.info("searchBasketballMatchSettleScores参数:"+settleScoreSearchDto);
        List<MatchSettleScore> list = matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleScoreSearchDto.getStandardMatchId(),settleScoreSearchDto.getEventCode());
        List<MatchSettleScoreDto> matchSettleScoreDtos= new ArrayList<>();
        //查询是否有篮球即时比分，如果没有则新增，而且加redis锁
        boolean hasInScore = false;
        boolean has2HTET = false;
        for (MatchSettleScore matchSettleScore : list) {
            if(matchSettleScore.getSettleNum().equals("bk_in_rg")){
                hasInScore=true;
            }
            if(matchSettleScore.getSettleNum().equals("bk_in_2htet")){
                has2HTET=true;
            }
        }
//        //如果没有初始化则重查一遍，先初始化
        if(!hasInScore){
            addInitInSettleScore(settleScoreSearchDto.getStandardMatchId());
            list =matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleScoreSearchDto.getStandardMatchId(),settleScoreSearchDto.getEventCode());
        }
        if(!has2HTET){
            add2HTETSettleScore(settleScoreSearchDto.getStandardMatchId());
            list =matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleScoreSearchDto.getStandardMatchId(),settleScoreSearchDto.getEventCode());
        }
        for (MatchSettleScore matchSettleScore : list) {
            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        list.sort(new Comparator<MatchSettleScore>() {
            @Override
            public int compare(MatchSettleScore o1, MatchSettleScore o2) {
                BasketBallSearchScoreCompareDto compareDto1=new BasketBallSearchScoreCompareDto();
                BeanUtils.copyProperties(o1,compareDto1);
                BasketBallSearchScoreCompareDto compareDto2=new BasketBallSearchScoreCompareDto();
                BeanUtils.copyProperties(o2,compareDto2);
                return compareDto1.compareTo(compareDto2);
            }
        });
//        //查询 当前用户的 阶段比分的明细的审核状态
        matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
        matchSettleRollBackInfoHelper.setRollBackStatusScores(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
        matchSettleScoreHelper.setBkInScoreTag(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
        //查询比分的倒计时秒数
        matchDelaySettleInfoHelper.setDelaySettleSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);

        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleScoreSearchDto.getStandardMatchId());
        //查询即时结算开关
        boolean realtimeOnOff = basketballInSettleService.getRealtimeSwitchOfLevel(2L, standardMatchInfo.getStandardTournamentId());
        if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(matchSettleScoreDtos)){
            matchSettleScoreDtos.forEach(matchSettleScoreDto -> {
                matchSettleScoreDto.setRealTimeOnOff(realtimeOnOff);
            });
        }

        //查询回滚状态
        return matchSettleScoreDtos;
    }

    private void addInitInSettleScore(Long standardMatchId) {
        String key = "addInitInSettleScore:"+standardMatchId;
        try{
            if(redisService.tryLock(key,key,2,3)){
                //重查一遍
                List<MatchSettleScore> list= BasketBallSettleScoreUtils.createBasketInSettleScore(standardMatchId);
                if (!CollectionUtils.isEmpty(list)){
                    matchSettleScoreRepository.saveOrUpdateBatch(list);
                }
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-v2-addInitInSettleScore:",e);
        }finally {
            redisService.unLock(key,key);
        }
    }

    private void add2HTETSettleScore(Long standardMatchId) {
        String key = "add2HTETSettleScore:"+standardMatchId;
        try{
            if(redisService.tryLock(key,key,2,3)){
                //重查一遍
                List<MatchSettleScore> list= BasketBallSettleScoreUtils.createBasket2HTETSettleScore(standardMatchId);
                if (!CollectionUtils.isEmpty(list)){
                    matchSettleScoreRepository.saveOrUpdateBatch(list);
                }
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-v2-add2HTETSettleScore:",e);
        }finally {
            redisService.unLock(key,key);
        }
    }



    public List<MatchSettleScoreDto> searchFootballMatchSettleScoresv3(MatchSettleScoreSearchDto settleScoreSearchDto) {
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
        List<MatchSettleScore> list =matchSettleScoreRepository.getModelByMatchIdAndEventCodeOrderBySettleNum(settleScoreSearchDto.getStandardMatchId(),eventCodes);

        Map<String, Integer> deleteStatusMap = new HashMap<>();
        Map<String, Integer> dataMismatchMap = new HashMap<>();
        mentionStatusHelper.obtainDetailInfo(settleScoreSearchDto, deleteStatusMap, dataMismatchMap);
        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
        for (MatchSettleScore matchSettleScore : list) {
            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            // deleteStatusMap 和 dataMismatchMap 的 key 可能是 matchSettleScoreId 或 matchSettleEventId
            // 对于 MatchSettleScore，主要使用 matchSettleScoreId 查找
            String scoreIdKey = String.valueOf(matchSettleScore.getId());
            Integer deleteStatus = deleteStatusMap.get(scoreIdKey);
            matchSettleScoreDto.setHasDeleteEvent(deleteStatus != null ? deleteStatus : matchSettleScore.getHasDeleteEvent());
            Integer dataMismatchStatus = dataMismatchMap.get(scoreIdKey);
            matchSettleScoreDto.setHasDataMismatchEvent(dataMismatchStatus != null ? dataMismatchStatus : 0);
            matchSettleScoreDto.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
            matchSettleScoreDto.setIsGrey(matchSettleScore.getIsGrey());
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
        //查询 当前用户的 阶段比分的明细的审核状态
        matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
        matchSettleRollBackInfoHelper.setRollBackStatusScores(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
        //查询比分的倒计时秒数
        matchDelaySettleInfoHelper.setDelaySettleSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
        matchSettleScoreDtos = matchSettleInfoHelper.setFiveMinList(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
        //查询回滚状态
        return matchSettleScoreDtos;
    }

    public List<MatchSettleScoreDto> searchBasketballMatchSettleScoresV3(MatchSettleScoreSearchDto settleScoreSearchDto) {
        log.info("searchBasketballMatchSettleScores参数:"+settleScoreSearchDto);
        List<MatchSettleScore> list = matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleScoreSearchDto.getStandardMatchId(),settleScoreSearchDto.getEventCode());
        List<MatchSettleScoreDto> matchSettleScoreDtos= new ArrayList<>();
        //查询是否有篮球即时比分，如果没有则新增，而且加redis锁
        boolean hasInScore = false;
        boolean has2HTET = false;
        for (MatchSettleScore matchSettleScore : list) {
            if(matchSettleScore.getSettleNum().equals("bk_in_rg")){
                hasInScore=true;
            }
            if(matchSettleScore.getSettleNum().equals("bk_in_2htet")){
                has2HTET=true;
            }
        }
//        //如果没有初始化则重查一遍，先初始化
        if(!hasInScore){
            addInitInSettleScore(settleScoreSearchDto.getStandardMatchId());
            list =matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleScoreSearchDto.getStandardMatchId(),settleScoreSearchDto.getEventCode());
        }
        if(!has2HTET){
            add2HTETSettleScore(settleScoreSearchDto.getStandardMatchId());
            list =matchSettleScoreRepository.getByStandardMatchIdAndEventCode(settleScoreSearchDto.getStandardMatchId(),settleScoreSearchDto.getEventCode());
        }
        for (MatchSettleScore matchSettleScore : list) {
            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        list.sort(new Comparator<MatchSettleScore>() {
            @Override
            public int compare(MatchSettleScore o1, MatchSettleScore o2) {
                BasketBallSearchScoreCompareDto compareDto1=new BasketBallSearchScoreCompareDto();
                BeanUtils.copyProperties(o1,compareDto1);
                BasketBallSearchScoreCompareDto compareDto2=new BasketBallSearchScoreCompareDto();
                BeanUtils.copyProperties(o2,compareDto2);
                return compareDto1.compareTo(compareDto2);
            }
        });
//        //查询 当前用户的 阶段比分的明细的审核状态
        matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
        matchSettleRollBackInfoHelper.setRollBackStatusScores(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
        matchSettleScoreHelper.setBkInScoreTag(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
        //查询比分的倒计时秒数
        matchDelaySettleInfoHelper.setDelaySettleSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);

        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleScoreSearchDto.getStandardMatchId());
        //查询即时结算开关
        boolean realtimeOnOff = basketballInSettleService.getRealtimeSwitchOfLevel(2L, standardMatchInfo.getStandardTournamentId());
        if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(matchSettleScoreDtos)){
            matchSettleScoreDtos.forEach(matchSettleScoreDto -> {
                matchSettleScoreDto.setRealTimeOnOff(realtimeOnOff);
            });
        }

        //查询回滚状态
        return matchSettleScoreDtos;
    }
}
