package com.panda.merge.v2.service.impl;

import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.service.IMatchSettleInfoService;
import com.panda.merge.v2.service.helper.MatchSettleInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MatchSettleInfoServiceImpl implements IMatchSettleInfoService {

    @Autowired
    private MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;

    @Override
    public void updateMatchCurrentEventStatus(Long standardMatchId) {
        matchSettleInfoHelper.updateMatchCurrentEventStatus(standardMatchId);
    }
    @Override
    public boolean updateMatchGrayStatus(Long standardMatchId) {
        return matchSettleInfoHelper.updateMatchGrayStatus(standardMatchId);
    }
    @Override
    public boolean checkBasketPeriodScoreOrder(MatchSettleScore matchSettleScore) {
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            return true;
        }
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
        if (matchSettleInfo == null) {
            return true;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (matchSettleInfo.getSettleOrderClosed() != null &&
                matchSettleInfo.getSettleOrderClosed() != 0) {
            return true;
        }
        List<Integer > statusList =new ArrayList<>();
        statusList.add(1);   statusList.add(0);   statusList.add(2);  statusList.add(4);
        //1.根据当前结算编码得到他之前的结算编码
        List<String> settleNumList = SettleNumUtils.countBasketballScoreSettleNumBefore(matchSettleScore.getSettleNum(),standardMatchInfo.getMatchLength());
        if(settleNumList.size()==0){
            return true;
        }
        List<MatchSettleScore> list =matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, statusList);

        //2.判断之前的结算编码是否已经结算，如果没有结算则不能结算返回false
        if(list.size()!=0){
            return false;
        }
        return true;
    }

    /**
     * 根据赛事id判断该赛事是否可以数据商结算
     *
     * @param standardMatchId
     * @return
     */
    public boolean matchIsAutoSettle(long standardMatchId) {
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
        if (matchSettleInfo != null) {
            if (matchSettleInfo.getIsAutoSettleDataSource() == null || matchSettleInfo.getIsAutoSettleDataSource() == 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
