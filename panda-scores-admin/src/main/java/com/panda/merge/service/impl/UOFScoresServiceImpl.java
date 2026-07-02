package com.panda.merge.service.impl;

import com.panda.merge.calculation.CalculationService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.model.*;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchScoresSourceTypeRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.IUOFScoresService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UOFScoresServiceImpl implements IUOFScoresService {
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;

    @Autowired
    IScoresService scoresService;
//    @Autowired
//    IMatchScoreSearchService matchScoreSearchService;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    MatchScoresSourceTypeRepository matchScoresSourceTypeRepository;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    ScoresRedisHelp scoresRedisHelp;

    @Override
    public void processUofScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data,StandardMatchInfo standardMatchInfo){
        CalculationService calculationService = scoresService.getCalculationService(matchScoresInfo.getSportId());
        log.info("saveScores,serviceName:{},数据源赛事ID:{}",calculationService==null?"比分计算服务接口服务名为空":calculationService.getClass().getSimpleName(),data.getThirdMatchSourceId());
        if(calculationService == null){
            return;
        }
        //计算阶段比分
        calculationService.saveMatchStatisticsScores(matchScoresInfo,data,standardMatchInfo);
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    @Override
    public MatchScoresInfo checkScores(ThirdMatchInfo thirdMatchInfo, MatchStatisticsInfoDTO data,MatchScoresInfo matchScoresInfo ) throws Exception {
        try {
            //1.查询 UOF 的比分
            matchScoresInfo.setEventTime(System.currentTimeMillis());
            matchScoresInfo.setPeriod(data.getPeriod().longValue());
            if (data.getSecondsMatchStart() != null)
                matchScoresInfo.setSecondsMatchStart(data.getSecondsMatchStart().longValue());
            if (data.getRemainingTime() != null)
                matchScoresInfo.setRemainingTime(data.getRemainingTime().longValue());
            if (matchScoresInfo.getSportId().equals(2L) && thirdMatchInfo.getMatchType() != null && thirdMatchInfo.getMatchType() == 3) {
                if (data.getRemainingTime() != null)
                    matchScoresInfo.setRemainingTime(data.getRemainingTime().longValue());
                if (data.getSecondsMatchStart() != null)
                    matchScoresInfo.setSecondsMatchStart(data.getSecondsMatchStart().longValue());
            }
            //UOF 取  getRemainingTime
            if (matchScoresInfo.getSportId().equals(2L)){
                if (data.getRemainingTime() != null)
                    matchScoresInfo.setSecondsMatchStart(data.getRemainingTime().longValue());
            }
            //比分暂时不入库，流程后段统一入库
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            MatchTimeInfo matchTimeInfo = matchTimeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
            if (matchTimeInfo == null) {
                return matchScoresInfo;
            }
            matchTimeInfo.setEventTime(System.currentTimeMillis());
            matchTimeInfo.setModifyTime(System.currentTimeMillis());
            if (data.getPeriod() != null) {
                matchTimeInfo.setPeriod(data.getPeriod().longValue());
            }
            if (data.getRemainingTime() != null) {
                matchTimeInfo.setRemainingTime(data.getRemainingTime().longValue());
            }
            if (data.getSecondsMatchStart() != null) {
                matchTimeInfo.setSecondFromStart(data.getSecondsMatchStart().longValue());
            }
            if (matchScoresInfo.getSportId().equals(2L) && thirdMatchInfo.getMatchType() != null && thirdMatchInfo.getMatchType() == 3) {
                if (data.getRemainingTime() != null)
                    matchTimeInfo.setRemainingTime(data.getRemainingTime().longValue());
                if (data.getSecondsMatchStart() != null)
                    matchTimeInfo.setSecondFromStart(data.getSecondsMatchStart().longValue());
            }
            //UOF 取  getRemainingTime
            if (matchScoresInfo.getSportId().equals(2L)){
                if (data.getRemainingTime() != null)
                    matchTimeInfo.setSecondFromStart(data.getRemainingTime().longValue());
            }
            //判断是否暂停
            matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
            return matchScoresInfo;
        }catch (Exception e){
            log.error("checkScores_error:thirdMatchId:{},统计data:{},error:",thirdMatchInfo.getId(),data,e);
            throw  new Exception(e);
        }
    }
}
