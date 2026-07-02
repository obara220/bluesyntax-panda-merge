package com.panda.merge.service.impl;

import com.panda.merge.calculation.CalculationService;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchScores;
import com.panda.merge.service.ILiveDataScoresService;
import com.panda.merge.service.IScoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LIVEdata事件比分服务
 */
@Service
public class LiveDataScoresServiceImpl implements ILiveDataScoresService {

    @Autowired
    IScoresService scoresService;


    /**
     * 计算比分入库
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculation( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        CalculationService calculationService = scoresService.getCalculationService(matchScoresInfo.getSportId());
        if (null != calculationService) {
            //计算阶段比分
            calculationService.calculationMatchScores(matchScoresInfo, data);
        }
    }

    /**
     * 计算标准比分
     * @param matchScoresInfo
     * @param standardMatchScores
     * @throws Exception
     */
    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores standardMatchScores,MatchEventInfo data) throws Exception {
        CalculationService calculationService = scoresService.getCalculationService(matchScoresInfo.getSportId());
        if (null != calculationService) {
            //计算阶段比分
            calculationService.calcStandardMatchScores(matchScoresInfo, standardMatchScores,data);
        }
    }


    /**
     * 事件取消
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data,Boolean isReissue) throws Exception {
        CalculationService calculationService =scoresService.getCalculationService(matchScoresInfo.getSportId());
        if (null != calculationService) {
            //计算阶段比分
            calculationService.cancelEvent(matchScoresInfo,data,false,isReissue);
        }
    }

    @Override
    public void canceledGoal(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        CalculationService calculationService =scoresService.getCalculationService(matchScoresInfo.getSportId());
        if (null != calculationService) {
            calculationService.canceledGoal(matchScoresInfo,data);
        }
    }

}
