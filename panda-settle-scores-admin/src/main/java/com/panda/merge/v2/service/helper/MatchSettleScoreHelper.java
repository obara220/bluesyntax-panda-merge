package com.panda.merge.v2.service.helper;

import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MatchSettleScoreHelper {

    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;

    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;

    public void setBkInScoreTag(List<MatchSettleScoreDto> matchSettleScoreDtos, Long standardMatchId){
        List<MatchSettleThirdScore> list =matchSettleThirdScoreRepository.getModelByStandardMatchIdAndSettleNum(standardMatchId, null);
        matchSettleScoreDtos.forEach(matchSettleScoreDto ->{
            if (BasketBallSettleScoreUtils.IN_SETTLE_NUM_LIST.contains(matchSettleScoreDto.getSettleNum())&&null!=matchSettleScoreDto.getT1()&&null!=matchSettleScoreDto.getT2()){
                list.forEach(matchSettleThirdScore->{
                    boolean tag = false;
                    if (matchSettleThirdScore.getSettleNum().equals(matchSettleScoreDto.getSettleNum())){
                        if (null==matchSettleThirdScore.getT1()||null==matchSettleThirdScore.getT2()){
                            tag = true;
                        }else  {
                            if (matchSettleThirdScore.getT1()!=matchSettleScoreDto.getT1()||matchSettleThirdScore.getT2() != matchSettleScoreDto.getT2()){
                                tag = true;
                            }
                        }
                    }
                    if (tag){
                        matchSettleScoreDto.setScoreCheckTag(3);
                    }
                });
            }
        });
    }

    public void verifyScoresIsSame(Long standardMatchId){
        //查出這場所有已結算比分
        List<MatchSettleScore> matchSettleScores =  matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(null,standardMatchId, Arrays.asList(3));

        MatchSettleScore scoreQ104 = null; //第一節
        MatchSettleScore scoreQ204 = null;//第二節
        MatchSettleScore score1Ht = null; //上半場
        MatchSettleScore scoreQ304 = null;//第三節
        MatchSettleScore scoreQ404 = null;//第四節
        MatchSettleScore score2Ht = null; //下半場
        MatchSettleScore scoreEt = null; //加時賽
        MatchSettleScore scoreHtEt = null; //下半場含加時賽
        MatchSettleScore scoreFtRg = null; //全場常規不含加時賽
        MatchSettleScore scoreFtEt = null; //全場比分含加時賽
        if (!CollectionUtils.isEmpty(matchSettleScores)){
            for (MatchSettleScore score :matchSettleScores){
                if (score.getSettleNum().equals("bk_q104")){
                    scoreQ104=score;
                }
                if (score.getSettleNum().equals("bk_q204")){
                    scoreQ204=score;
                }
                if (score.getSettleNum().equals("bk_1ht")){
                    score1Ht=score;
                }
                if (score.getSettleNum().equals("bk_q304")){
                    scoreQ304=score;
                }
                if (score.getSettleNum().equals("bk_q404")){
                    scoreQ404=score;
                }
                if (score.getSettleNum().equals("bk_2ht")){
                    score2Ht=score;
                }if (score.getSettleNum().equals("bk_et")){
                    scoreEt=score;
                }
                if (score.getSettleNum().equals("bk_2htet")){
                    scoreHtEt=score;
                }
                if (score.getSettleNum().equals("bk_ft_rg")){
                    scoreFtRg=score;
                }
                if (score.getSettleNum().equals("bk_ft_et")){
                    scoreFtEt=score;
                }

            }
        }

        if (null!=score1Ht){
            int home1Ht = 0;
            int away1Ht = 0;
            if (null==scoreQ104||null==scoreQ204){ //第一節,第二節其中有沒有結算
                score1Ht.setScoreCheckTag(0);
            }else {
                home1Ht = scoreQ104.getT1()+scoreQ204.getT1();
                away1Ht = scoreQ104.getT2()+scoreQ204.getT2();
                if (home1Ht!=score1Ht.getT1()||away1Ht!=score1Ht.getT2()){
                    score1Ht.setScoreCheckTag(1);
                }else {
                    score1Ht.setScoreCheckTag(0);
                }
            }
            matchSettleScoreRepository.updateById(score1Ht);
        }

        if (null!=score2Ht){
            int home2Ht = 0;
            int away2Ht = 0;
            if (null==scoreQ304||null==scoreQ404){ //第3節,第4節其中有沒有結算
                score2Ht.setScoreCheckTag(0);
            }else {
                home2Ht = scoreQ304.getT1()+scoreQ404.getT1();
                away2Ht = scoreQ304.getT2()+scoreQ404.getT2();
                if (home2Ht!=score2Ht.getT1()||away2Ht!=score2Ht.getT2()){
                    score2Ht.setScoreCheckTag(1);
                }else {
                    score2Ht.setScoreCheckTag(0);
                }
            }
            matchSettleScoreRepository.updateById(score2Ht);
        }

        if (null!=scoreHtEt){
            int homeHtEt = 0;
            int awayHtEt = 0;
            if (null==score2Ht){ //第3節,第4節,加時賽其中有沒有結算
                scoreHtEt.setScoreCheckTag(0);
            }else {
                if (null==scoreEt){
                    homeHtEt = score2Ht.getT1();
                    awayHtEt =score2Ht.getT2();
                }else {
                    homeHtEt = score2Ht.getT1()+scoreEt.getT1();
                    awayHtEt =score2Ht.getT2()+scoreEt.getT2();
                }

                if (homeHtEt!=scoreHtEt.getT1()||awayHtEt!=scoreHtEt.getT2()){
                    scoreHtEt.setScoreCheckTag(1);
                }else {
                    scoreHtEt.setScoreCheckTag(0);
                }
            }
            matchSettleScoreRepository.updateById(scoreHtEt);
        }

        if (null!=scoreFtRg){
            int homeFtRg = 0;
            int awayFtRg = 0;
            if (null==score1Ht||null==score2Ht){ //第一節,第二節,第3節,第4節其中有沒有結算
                scoreFtRg.setScoreCheckTag(0);
            }else {
                homeFtRg = score1Ht.getT1()+score2Ht.getT1();
                awayFtRg = score1Ht.getT2()+score2Ht.getT2();
                if (homeFtRg!=scoreFtRg.getT1()||awayFtRg!=scoreFtRg.getT2()){
                    scoreFtRg.setScoreCheckTag(1);
                }else {
                    scoreFtRg.setScoreCheckTag(0);
                }
            }
            matchSettleScoreRepository.updateById(scoreFtRg);
        }

        if (null!=scoreFtEt){
            int homeFtEt = 0;
            int awayFtEt = 0;
            if (null==score1Ht||null==score2Ht||null==scoreEt){ //第一節,第二節,第3節,第4節,加時賽其中有沒有結算
                scoreFtEt.setScoreCheckTag(0);
            }else {
                homeFtEt = score1Ht.getT1()+score2Ht.getT1()+scoreEt.getT1();
                awayFtEt = score1Ht.getT2()+score2Ht.getT2()+scoreEt.getT2();
                if (homeFtEt!=scoreFtEt.getT1()||awayFtEt!=scoreFtEt.getT2()){
                    scoreFtEt.setScoreCheckTag(1);
                }else {
                    scoreFtEt.setScoreCheckTag(0);
                }
            }
            matchSettleScoreRepository.updateById(scoreFtEt);
        }

    }

    /*
        1、删除事件只是不结算对应删除的小节不结算，不卡全部小节阶段，其他小节阶段还是可以录入比分结算（手动/自动）
        第一节卡住后 只卡第一节
        第二节卡住后 上半场阶段Q1+Q2同步卡住
        第三节卡住后 只卡第三节
        第四节卡住后 下半场阶段Q3+Q4与全场常规阶段同步卡住
        OT阶段卡住后 下半场结算Q3+Q4+OT与全场比分（含加时）同步卡住
        2、有删除事件时，数据商开关关闭，还支持1个审核员+数据商权重100触发结算
        3、谁先达X分和6分钟玩法不受影响
        4、二节制赛事与四节制赛事受影响
     */
    public boolean validateBasketSettle(MatchSettleScore matchSettleScore,String linkId){
        //足球不走此逻辑
        if(matchSettleScore.getSportId()==1){
            return false;
        }
        log.info("{}:当前校验的阶段是:{},赛事是:{}",linkId,matchSettleScore.getSettleNum(),matchSettleScore.getStandardMatchId());
        List<String> comparaSettleNum = Arrays.asList("bk_q104","bk_q204","bk_q304","bk_q404","bk_1ht","bk_2ht","bk_2htet","bk_ft_rg","bk_ft_et","bk_et");
        if (!comparaSettleNum.contains(matchSettleScore.getSettleNum())){
            return false;
        }
        //获取当前结算的结算
        String settleNum = matchSettleScore.getSettleNum();
        //假如当前结算的是 第一节，第二节，第三节，第四节，加时赛，则只需要判断当前阶段是否有删除事件
        if (settleNum.equals("bk_q104")||settleNum.equals("bk_q204")||settleNum.equals("bk_q304")||settleNum.equals("bk_q404")||settleNum.equals("bk_et")){
            if (null!=matchSettleScore.getHasDeleteEvent()&&matchSettleScore.getHasDeleteEvent()==1){
                return true;
            }else {
                return false;
            }
        }
        List<String> settleNUmList = new ArrayList<>();
        List<MatchSettleScore> matchSettleScores ;
        //假如当前结算的是上半场
        if (settleNum.equals("bk_1ht")){
            settleNUmList.add("bk_q104");
            settleNUmList.add("bk_q204");
            settleNUmList.add("bk_1ht");//二节制

        }
        //假如结算的是下半场
        if (settleNum.equals("bk_2ht")){
            settleNUmList.add("bk_q304");
            settleNUmList.add("bk_q404");
            settleNUmList.add("bk_2ht");
        }
        //假如结算的是下半场+加时
        if (settleNum.equals("bk_2htet")){
            settleNUmList.add("bk_q304");
            settleNUmList.add("bk_q404");
            settleNUmList.add("bk_2ht");
            settleNUmList.add("bk_et");
        }
        //假如是全场常规
        if (settleNum.equals("bk_ft_rg")){
            settleNUmList.add("bk_q104");
            settleNUmList.add("bk_q204");
            settleNUmList.add("bk_1ht");
            settleNUmList.add("bk_q304");
            settleNUmList.add("bk_q404");
            settleNUmList.add("bk_2ht");
        }
        //全场含加时
        if (settleNum.equals("bk_ft_et")){
            settleNUmList.add("bk_q104");
            settleNUmList.add("bk_q204");
            settleNUmList.add("bk_1ht");
            settleNUmList.add("bk_q304");
            settleNUmList.add("bk_q404");
            settleNUmList.add("bk_2ht");
            settleNUmList.add("bk_et");
            settleNUmList.add("bk_2htet");
            settleNUmList.add("bk_ft_rg");
        }
        boolean flag = false;
        matchSettleScores = matchSettleScoreRepository.getModelsByItemsAndSettleNums(matchSettleScore.getStandardMatchId(),null,null,null,settleNUmList);
        if (!matchSettleScores.isEmpty()){
            log.info("{}:校验的阶段集合长度:{}",linkId,matchSettleScores.size());
            for (int i = 0; i < matchSettleScores.size(); i++) {
                if (null!=matchSettleScores.get(i).getHasDeleteEvent()&&matchSettleScores.get(i).getHasDeleteEvent()==1){
                    flag = true;
                }
            }
        }

        return flag;
    }
}
