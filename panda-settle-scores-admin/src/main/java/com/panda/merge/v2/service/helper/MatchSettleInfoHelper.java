package com.panda.merge.v2.service.helper;

import cn.hutool.core.collection.CollectionUtil;
import com.panda.merge.common.enums.MatchPeriodEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.FootballPeriodValidateEnum;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.dto.CheckPeriodEventEquileDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.v2.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;
import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.NOT_EDIT;

@Component
@Slf4j
public class MatchSettleInfoHelper {
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    private MatchSettleFactorCheckInfoRepository matchSettleFactorCheckInfoRepository;
    @Autowired
    private RedisService redisService;

    @Value("${match.settle.refresh.redis.settle.info.limit:1000}")
    private Integer settleInfoLimit;

    private static final MatchSettleCheckInfo constantCheckInfo = new MatchSettleCheckInfo();
    private static final List<String> allMins15Codes = Arrays.asList(MatchPeriodEnum.GOAL_2.getCode().toString(),
            MatchPeriodEnum.GOAL_3.getCode().toString(),MatchPeriodEnum.GOAL_4.getCode().toString(),MatchPeriodEnum.GOAL_6.getCode().toString(),
            MatchPeriodEnum.GOAL_7.getCode().toString(),MatchPeriodEnum.GOAL_8.getCode().toString());

    /**
     * 半场阶段比分（不依赖 5/15 分钟阶段是否已结算）
     * - 进球：105(1HT), 109(2HT)
     * - 角球：201(1HT CR), 202(2HT CR)
     * - 罚牌：304(BK 1HT), 308(BK 2HT)
     */
    private boolean isHalfPeriodSettleNum(String settleNum) {
        if (settleNum == null) {
            return false;
        }
        return settleNum.equals(MatchPeriodEnum.GOAL_5.getCode().toString())
                || settleNum.equals(MatchPeriodEnum.GOAL_9.getCode().toString())
                || settleNum.equals(MatchPeriodEnum.Corner_1.getCode().toString())
                || settleNum.equals(MatchPeriodEnum.Corner_2.getCode().toString())
                || settleNum.equals(MatchPeriodEnum.BOOKINGS_4.getCode().toString())
                || settleNum.equals(MatchPeriodEnum.BOOKINGS_8.getCode().toString());
    }
    public void updateMatchCurrentEventStatus(Long standardMatchId){
        try {
            int deleteGoal=0;
            int grayGoal=0;

            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
            if (matchSettleInfo!=null && matchSettleInfo.getSportId()!=null && (matchSettleInfo.getSportId().intValue()!=1 && matchSettleInfo.getSportId().intValue()!=2)){
                return;
            }
            List<MatchSettleEvent> goalEventList =matchSettleEventRepository.getModelByStandardMatchIdAndNotStatus(standardMatchId, 3);
            for (MatchSettleEvent matchSettleEvent : goalEventList) {
                if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
                    grayGoal=1;
                }
                if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
                    deleteGoal=1;
                }
            }
            List<MatchSettleScore> goalScoreList = matchSettleScoreRepository.getModelStandardMatchIdAndNotStatusAndIsGrey(standardMatchId, 3, null);

            for (MatchSettleScore matchSettleScore : goalScoreList) {
                if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
                    grayGoal=1;
                }
                if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
                    deleteGoal=1;
                }
            }
            if(deleteGoal==1 && grayGoal==1){
                matchSettleInfo.setIsGray(1);
                matchSettleInfo.setHasDeleteEvent(1);
                matchSettleInfo.setCurrentEventStatus(1);
            }else if(deleteGoal==1 && grayGoal==0){
                matchSettleInfo.setIsGray(0);
                matchSettleInfo.setHasDeleteEvent(1);
                matchSettleInfo.setCurrentEventStatus(2);
            }else if(deleteGoal==0 && grayGoal==1){
                matchSettleInfo.setIsGray(1);
                matchSettleInfo.setHasDeleteEvent(0);
                matchSettleInfo.setCurrentEventStatus(1);
            }else{
                matchSettleInfo.setIsGray(0);
                matchSettleInfo.setHasDeleteEvent(0);
                matchSettleInfo.setCurrentEventStatus(0);
            }
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        }catch (Exception e){
            log.error("{标准赛事Id:"+standardMatchId+",修改灰色区间标识出错:",e);
        }
    }

    public boolean updateMatchGrayStatus(Long standardMatchId) {
        try {
            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
            List<MatchSettleEvent> events = matchSettleEventRepository.getModelByStandardMatchIdAndNotStatusAndEventTypeAndIsGrey(standardMatchId,SETTLED,1,1 );
            if (events.size() != 0) {
                matchSettleInfo.setIsGray(1);
                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                return true;
            }
            List<MatchSettleScore> scores = matchSettleScoreRepository.getModelStandardMatchIdAndNotStatusAndIsGrey(standardMatchId,SETTLED,1);
            if (scores.size() != 0) {
                matchSettleInfo.setIsGray(1);
                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                return true;
            }
            matchSettleInfo.setIsGray(0);
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
            return false;
        }catch (Exception e){

        }
        return false;
    }
    public boolean checkSettleScoreAndAutoSettleNonEvent(MatchSettleInfo matchSettleInfo, MatchSettleScore matchSettleScore,MatchSettleCheckInfo checkInfo, String linkedId, Map<String, MatchSettleScore> settleNumMap) {
        try {
            if(checkInfo!=null){
                matchSettleScore.setT1(checkInfo.getT1());
                matchSettleScore.setT2(checkInfo.getT2());
                matchSettleScore.setGoWaterStatus(checkInfo.getGoWaterStatus());
            }
            //LS跟BFZX没有次序
            if(checkInfo.getDataSourceCode().equals("LS")||checkInfo.getDataSourceCode().equals("BFZX")){
                return true;
            }
            //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
            if (matchSettleInfo.getSettleOrderClosed() != null && matchSettleInfo.getSettleOrderClosed() == 1) {
                return this.normalCheckAutoSettleNonEvent(matchSettleScore, matchSettleInfo);
            }
            //2 阶段事件比分是否一致 不一致则 返回 false 不一致
            Integer x= isPeriodScoreEquile(matchSettleScore, checkInfo, matchSettleInfo, linkedId, settleNumMap);
            if ( x!= 0) {
                log.error("linkedId::{} 阶段比分结算拦截1: {}-{} 赛事id:{},原因 x:{}",linkedId, matchSettleScore.getEventName(),matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(),x);
                return false;
            }
            if (isHalfPeriodSettleNum(matchSettleScore.getSettleNum())) {
                return true;
            } else {
                //3 判断事件和比分是否一致，不一致则返回 事件结算和比分不一致
                CheckPeriodEventEquileDto checkPeriodEventEquileDto = isPeriodEventEquile(matchSettleScore);
                if (!checkPeriodEventEquileDto.isPassCheck()) {
                    log.info("linkedId::{} 阶段比分结算拦截2: {}-{} 赛事id:{}, CheckPeriodEvent:{}", linkedId, matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), checkPeriodEventEquileDto);
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            log.error("linkedId::{} checkSettleScoreAndAutoSettleNonEvent error:",linkedId, e);
            return false;
        }
    }

    public boolean checkSettleScoreAndAutoSettleNonEvent(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo) {
        try {
            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(matchSettleScore.getStandardMatchId());
            if (checkInfo != null) {
                matchSettleScore.setT1(checkInfo.getT1());
                matchSettleScore.setT2(checkInfo.getT2());
                matchSettleScore.setGoWaterStatus(checkInfo.getGoWaterStatus());
            }
            //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
            if (matchSettleInfo.getSettleOrderClosed() != null && matchSettleInfo.getSettleOrderClosed() == 1) {
                return normalCheckAutoSettleNonEvent(matchSettleScore, matchSettleInfo);
            }
            //2 阶段事件比分是否一致 不一致则 返回 false 不一致
            Integer x = isPeriodScoreEquile(matchSettleScore, checkInfo, matchSettleInfo);
            if (x != 0) {
                log.info("阶段比分结算拦截1: {}-{} 赛事id:{},原因 x:{}", matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), x);
                return false;
            }
            if (isHalfPeriodSettleNum(matchSettleScore.getSettleNum())) {
                return true;
            } else {
                //3 判断事件和比分是否一致，不一致则返回 事件结算和比分不一致
                CheckPeriodEventEquileDto checkPeriodEventEquileDto = isPeriodEventEquile(matchSettleScore);
                if (!checkPeriodEventEquileDto.isPassCheck()) {
                    log.info("阶段比分结算拦截2: {}-{} 赛事id:{}, CheckPeriodEvent:{}", matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), checkPeriodEventEquileDto);
                    return false;
                }
            }
            //5.返回 true
            return true;
        } catch (Exception e) {
            log.error("checkSettleScoreAndAutoSettleNonEvent error:", e);
            return false;
        }
    }

    public boolean normalCheckAutoSettleNonEvent(MatchSettleScore matchSettleScore, MatchSettleInfo matchSettleInfo) {
        //只需要校验 全场结算的时候 全场= 上半场 +下半场
        if(matchSettleScore.getEventCode().equals("goal")){
            if(!matchSettleScore.getSettleNum().equals("1010")){
                return true;
            }
        }
        if(matchSettleScore.getEventCode().equals("corner")){
            if(!matchSettleScore.getSettleNum().equals("203")){
                return true;
            }
        }
        if(matchSettleScore.getEventCode().equals("fa_card")){
            if(!matchSettleScore.getSettleNum().equals("309")){
                return true;
            }
        }
        if(isPeriodScoreEquile(matchSettleScore, constantCheckInfo, matchSettleInfo)==0){
            return true;
        }
        return false;
    }
    private Integer isPeriodScoreEquile(MatchSettleScore matchSettleScore,MatchSettleCheckInfo checkInfo, MatchSettleInfo matchSettleInfo, String linkedId, Map<String, MatchSettleScore> settleNumMap) {
        //1.根据当前传入足球的阶段比分的 结算编码 settleNum 得到需要核对的 15分钟比分 或者 5分钟比分 或者半场比分 的结算编码 settleNum
        //1.2 需要核对的结算编码settleNum list.size ==0 return 0 成功该类型比分无需核对
        //2.根据上面的 settleNum 和 标准赛事ID  查询 结算阶段比分表已经结算的比分
        //3.检查 返回已结算的 list size 是否等于  1 步骤的 settleNum list size  不相等则返回结算失败 记 1 还有比分未结算
        //4.检查已结算的比分之和 是否和  待结算的传参的 比分的主客队是否相等   不相等 返回 2  比分不一致
        //5. 上述校验通过返回  0  成功
        /**
         * 查询当前编辑的比分之前已结算的比分
         * 判断3个15分钟区间是否都已结算,
         * 1,未全部结算:如果是上下半场,计算灰色区间进球结算因子,是否等于上下半场比分.
         * 2,全部结算:核对已经结算的3个15分钟区间比分和是否一致
         */
        List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(matchSettleScore.getSettleNum());
        if (allMins15Codes.contains(matchSettleScore.getSettleNum()) && (checkInfo != null || matchSettleInfo.getFiveMinSwitch() == 0)){
            settleNumList = null;
        }

        // 对于5/15分钟阶段，都不需要进行校验，直接返回成功
        // 对于上下半场结算（105/109），其settleNumList会包含15分钟阶段（102,103,104或106,107,108），也需要过滤掉
        if (settleNumList != null && !settleNumList.isEmpty()) {
            List<String> all5SettleNums = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040",
                    "1041", "1042", "1043", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1053");
            List<String> all15SettleNums = Arrays.asList("102", "103", "104", "106", "107", "108", "2011",
                    "2012", "2013", "2014", "2015", "2016", "301", "302", "303", "305", "306", "307");
            // 过滤掉5/15分钟阶段的 settleNum（包括上下半场结算时包含的15分钟阶段）
            settleNumList = settleNumList.stream()
                    .filter(settleNum -> !all5SettleNums.contains(settleNum) && !all15SettleNums.contains(settleNum))
                    .collect(Collectors.toList());
        }

        // 半场阶段结算：如果过滤后 settleNumList 为空（通常只包含 15 分钟阶段且被过滤），直接返回成功
        if (settleNumList == null || settleNumList.isEmpty()) {
            return 0;
        }
        if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
            return 2;
        }
        // 5/15分钟校验
        if (checkInfo == null) {
            if ((!(allMins15Codes.contains(matchSettleScore.getSettleNum()) && matchSettleInfo.getFiveMinSwitch() == 0)) && (!validGoalSettle(matchSettleScore))) {
                return 2;
            }
        }
        List<MatchSettleScore> grayList = matchSettleScoreRepository.getModelStandardMatchIdAndSettleNumAndIsGrey(matchSettleScore.getStandardMatchId(),settleNumList,NOT_CONFIRM);

        grayList = addUnsettledScoreToList(grayList, settleNumList, linkedId, settleNumMap, true);

        if (!grayList.isEmpty()) {
            if (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) || matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
                BigDecimal inputScoreT1 = new BigDecimal(matchSettleScore.getT1());
                BigDecimal inputScoreT2 = new BigDecimal(matchSettleScore.getT2());
                BigDecimal sumSettleScoreT1 = BigDecimal.ZERO;
                BigDecimal sumSettleScoreT2 = BigDecimal.ZERO;
                // 查询出灰色区间结算因子总比分
                List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoList = matchSettleFactorCheckInfoRepository.matchSettleFactorCheckInfoListCaseTwo(matchSettleScore.getStandardMatchId(),settleNumList);
                for (MatchSettleFactorCheckInfo matchSettleFactorCheckInfo : matchSettleFactorCheckInfoList) {
                    if (matchSettleFactorCheckInfo.getT1() != null) {
                        sumSettleScoreT1 = sumSettleScoreT1.add(matchSettleFactorCheckInfo.getT1());
                    }
                    if (matchSettleFactorCheckInfo.getT2() != null) {
                        sumSettleScoreT2 = sumSettleScoreT2.add(matchSettleFactorCheckInfo.getT2());
                    }
                }
                //判断输入的上下半场比分,不能小于结算因子
                if (inputScoreT1.compareTo(sumSettleScoreT1) < NOT_EDIT || inputScoreT2.compareTo(sumSettleScoreT2) < NOT_EDIT) {
                    return 2;
                }
                //输入的上下半场比分等于结算因子,输入比分大于结算因子的情况是:一个数据源,3个15分钟区间有正常进球，当一个数据源加人工比分一致. 可以触发结算
                if (inputScoreT1.compareTo(sumSettleScoreT1) >= NOT_EDIT && inputScoreT2.compareTo(sumSettleScoreT2) >= NOT_EDIT) {
                    return 0;
                }
            }
            return 1;
        } else {
            List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,matchSettleScore.getStandardMatchId(), Arrays.asList(SETTLED));

            list = addUnsettledScoreToList(list, settleNumList, linkedId, settleNumMap, false);
            if (list.isEmpty() || list.size() != settleNumList.size()) {
                // 半场阶段结算不依赖 5/15 分钟是否已结算：放行
                if (isHalfPeriodSettleNum(matchSettleScore.getSettleNum())) {
                    return 0;
                }
                return 1;
            }
            Integer sumScoreT1 = 0;
            Integer sumScoreT2 = 0;
            for (MatchSettleScore settleScore : list) {
                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
                    sumScoreT1 += settleScore.getT1();
                }
                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
                    sumScoreT2 += settleScore.getT2();
                }
            }
            if (matchSettleScore.getT1() != null && matchSettleScore.getT1().equals(sumScoreT1) && matchSettleScore.getT2() != null && matchSettleScore.getT2().equals(sumScoreT2)) {
                return 0;
            } else {
                return 2;
            }
        }
    }
    public Integer isPeriodScoreEquile(MatchSettleScore matchSettleScore,MatchSettleCheckInfo checkInfo, MatchSettleInfo matchSettleInfo) {
        //1.根据当前传入足球的阶段比分的 结算编码 settleNum 得到需要核对的 15分钟比分 或者 5分钟比分 或者半场比分 的结算编码 settleNum
        //1.2 需要核对的结算编码settleNum list.size ==0 return 0 成功该类型比分无需核对
        //2.根据上面的 settleNum 和 标准赛事ID  查询 结算阶段比分表已经结算的比分
        //3.检查 返回已结算的 list size 是否等于  1 步骤的 settleNum list size  不相等则返回结算失败 记 1 还有比分未结算
        //4.检查已结算的比分之和 是否和  待结算的传参的 比分的主客队是否相等   不相等 返回 2  比分不一致
        //5. 上述校验通过返回  0  成功
        /**
         * 查询当前编辑的比分之前已结算的比分
         * 判断3个15分钟区间是否都已结算,
         * 1,未全部结算:如果是上下半场,计算灰色区间进球结算因子,是否等于上下半场比分.
         * 2,全部结算:核对已经结算的3个15分钟区间比分和是否一致
         */
        List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(matchSettleScore.getSettleNum());
        if (allMins15Codes.contains(matchSettleScore.getSettleNum()) && (checkInfo != null || matchSettleInfo.getFiveMinSwitch() == 0)){
            settleNumList = null;
        }

        // 对于5/15分钟阶段，都不需要进行校验，直接返回成功
        // 对于上下半场结算（105/109），其settleNumList会包含15分钟阶段（102,103,104或106,107,108），也需要过滤掉
        if (settleNumList != null && !settleNumList.isEmpty()) {
            List<String> all5SettleNums = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040",
                    "1041", "1042", "1043", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1053");
            List<String> all15SettleNums = Arrays.asList("102", "103", "104", "106", "107", "108", "2011",
                    "2012", "2013", "2014", "2015", "2016", "301", "302", "303", "305", "306", "307");
            // 过滤掉5/15分钟阶段的 settleNum（包括上下半场结算时包含的15分钟阶段）
            settleNumList = settleNumList.stream()
                    .filter(settleNum -> !all5SettleNums.contains(settleNum) && !all15SettleNums.contains(settleNum))
                    .collect(Collectors.toList());
        }

        // 半场阶段结算：如果过滤后 settleNumList 为空（通常只包含 15 分钟阶段且被过滤），直接返回成功
        if (settleNumList == null || settleNumList.isEmpty()) {
            return 0;
        }
        if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
            return 2;
        }
        // 5/15分钟校验
        if (checkInfo == null) {
            if ((!(allMins15Codes.contains(matchSettleScore.getSettleNum()) && matchSettleInfo.getFiveMinSwitch() == 0)) && (!validGoalSettle(matchSettleScore))) {
                return 2;
            }
        }
        List<MatchSettleScore> grayList = matchSettleScoreRepository.getModelStandardMatchIdAndSettleNumAndIsGrey(matchSettleScore.getStandardMatchId(),settleNumList,NOT_CONFIRM);
        if (!grayList.isEmpty()) {
            if (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) || matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
                BigDecimal inputScoreT1 = new BigDecimal(matchSettleScore.getT1());
                BigDecimal inputScoreT2 = new BigDecimal(matchSettleScore.getT2());
                BigDecimal sumSettleScoreT1 = BigDecimal.ZERO;
                BigDecimal sumSettleScoreT2 = BigDecimal.ZERO;
                // 查询出灰色区间结算因子总比分
                List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoList = matchSettleFactorCheckInfoRepository.matchSettleFactorCheckInfoListCaseTwo(matchSettleScore.getStandardMatchId(),settleNumList);
                for (MatchSettleFactorCheckInfo matchSettleFactorCheckInfo : matchSettleFactorCheckInfoList) {
                    if (matchSettleFactorCheckInfo.getT1() != null) {
                        sumSettleScoreT1 = sumSettleScoreT1.add(matchSettleFactorCheckInfo.getT1());
                    }
                    if (matchSettleFactorCheckInfo.getT2() != null) {
                        sumSettleScoreT2 = sumSettleScoreT2.add(matchSettleFactorCheckInfo.getT2());
                    }
                }
                //判断输入的上下半场比分,不能小于结算因子
                if (inputScoreT1.compareTo(sumSettleScoreT1) < NOT_EDIT || inputScoreT2.compareTo(sumSettleScoreT2) < NOT_EDIT) {
                    return 2;
                }
                //输入的上下半场比分等于结算因子,输入比分大于结算因子的情况是:一个数据源,3个15分钟区间有正常进球，当一个数据源加人工比分一致. 可以触发结算
                if (inputScoreT1.compareTo(sumSettleScoreT1) >= NOT_EDIT && inputScoreT2.compareTo(sumSettleScoreT2) >= NOT_EDIT) {
                    return 0;
                }
            }
            return 1;
        } else {
            List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,matchSettleScore.getStandardMatchId(), Arrays.asList(SETTLED));
            if (list.isEmpty() || list.size() != settleNumList.size()) {
                // 半场阶段结算不依赖 5/15 分钟是否已结算：放行
                if (isHalfPeriodSettleNum(matchSettleScore.getSettleNum())) {
                    return 0;
                }
                return 1;
            }
            Integer sumScoreT1 = 0;
            Integer sumScoreT2 = 0;
            for (MatchSettleScore settleScore : list) {
                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
                    sumScoreT1 += settleScore.getT1();
                }
                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
                    sumScoreT2 += settleScore.getT2();
                }
            }
            if (matchSettleScore.getT1() != null && matchSettleScore.getT1().equals(sumScoreT1) && matchSettleScore.getT2() != null && matchSettleScore.getT2().equals(sumScoreT2)) {
                return 0;
            } else {
                return 2;
            }
        }
    }
    private CheckPeriodEventEquileDto isPeriodEventEquile(MatchSettleScore matchSettleScore) {
        CheckPeriodEventEquileDto checkPeriodEventEquileDto=new CheckPeriodEventEquileDto();
        List<String> goalPeriodSettleNum =new ArrayList<>();
        goalPeriodSettleNum.add("105");goalPeriodSettleNum.add("109");goalPeriodSettleNum.add("1014");goalPeriodSettleNum.add("1018");
        List<String> cornerPeriodSettleNum =new ArrayList<>();
        cornerPeriodSettleNum.add("201");cornerPeriodSettleNum.add("202");cornerPeriodSettleNum.add("206");cornerPeriodSettleNum.add("207");
        List<String> facardPeriodSettleNum =new ArrayList<>();
        facardPeriodSettleNum.add("304");facardPeriodSettleNum.add("308");facardPeriodSettleNum.add("3013");facardPeriodSettleNum.add("3017");

        Long period =SettleNumUtils.countEventPeriodBySettleScore(matchSettleScore.getSettleNum());
        if(period==null){
            return checkPeriodEventEquileDto;
        }
        MatchSettleEventExample eventExample =new MatchSettleEventExample();
        Integer homeScore=0;
        Integer awayScore=0;
        Integer eventT1=0;
        Integer eventT2=0;
        //發牌
        Integer eventFirstT1=0;
        Integer eventFirstT2=0;
        Integer eventSecondT1=0;
        Integer eventSecondT2=0;
        //过滤不需要校验的阶段比分
        if(matchSettleScore.getEventCode().equals("goal")){
            //过滤不需要校验的阶段比分
            if(!goalPeriodSettleNum.contains(matchSettleScore.getSettleNum())){
                return checkPeriodEventEquileDto;
            }
            //预设置需要补充
            checkPeriodEventEquileDto.setNeedNoneEvent(true);
            List<MatchSettleEvent> goalList = matchSettleEventRepository.getModelsByItemsAndEventType(matchSettleScore.getStandardMatchId(), Arrays.asList("goal"),Arrays.asList(period),3,1);
            for (MatchSettleEvent matchSettleEvent : goalList) {
                if("home".equals(matchSettleEvent.getHomeAway())){
                    homeScore++;
                }else if("away".equals(matchSettleEvent.getHomeAway())){
                    awayScore++;
                }else {
                    //增加阶段 如果是上半场，则必须是上半场无进球 下半场 则 必须是下半场无进球 TODO

                    checkPeriodEventEquileDto.setNeedNoneEvent(false);
                }
                if(eventT1<matchSettleEvent.getT1()){
                    eventT1=matchSettleEvent.getT1();
                }
                if(eventT2<matchSettleEvent.getT2()){
                    eventT2=matchSettleEvent.getT2();
                }
            }
            checkPeriodEventEquileDto.setOrderNum(goalList.size()+1);
        }
        if(matchSettleScore.getEventCode().equals("corner")){
            //过滤不需要校验的阶段比分
            if(!cornerPeriodSettleNum.contains(matchSettleScore.getSettleNum())){
                return checkPeriodEventEquileDto;
            }
            //预设置需要补充
            checkPeriodEventEquileDto.setNeedNoneEvent(true);
            List<MatchSettleEvent> goalList = matchSettleEventRepository.getModelsByItemsAndEventType(matchSettleScore.getStandardMatchId(), Arrays.asList("corner"),Arrays.asList(period),3,1);
            for (MatchSettleEvent matchSettleEvent : goalList) {
                if("home".equals(matchSettleEvent.getHomeAway())){
                    homeScore++;
                }else if("away".equals(matchSettleEvent.getHomeAway())){
                    awayScore++;
                }else {
                    checkPeriodEventEquileDto.setNeedNoneEvent(false);
                }
                if(eventT1<matchSettleEvent.getT1()){
                    eventT1=matchSettleEvent.getT1();
                }
                if(eventT2<matchSettleEvent.getT2()){
                    eventT2=matchSettleEvent.getT2();
                }
            }
            checkPeriodEventEquileDto.setOrderNum(goalList.size()+1);
        }
        if(matchSettleScore.getEventCode().equals("fa_card")){
            //过滤不需要校验的阶段比分
            if(!facardPeriodSettleNum.contains(matchSettleScore.getSettleNum())){
                return checkPeriodEventEquileDto;
            }
            //预设置需要补充
            checkPeriodEventEquileDto.setNeedNoneEvent(true);
            List<String> bookingSettleNum =new ArrayList<>();
            bookingSettleNum.add("fa_card");bookingSettleNum.add("yellow_card");bookingSettleNum.add("red_card");
            List<MatchSettleEvent> goalList =matchSettleEventRepository.getModelsByItemsAndEventType(matchSettleScore.getStandardMatchId(), bookingSettleNum,Arrays.asList(period),3,1);
            for (MatchSettleEvent matchSettleEvent : goalList) {
                if(matchSettleEvent.getEventCode().equals("red_card")){
                    if("home".equals(matchSettleEvent.getHomeAway())){
                        homeScore+=2;
                    }else if("away".equals(matchSettleEvent.getHomeAway())){
                        awayScore+=2;
                    }
                }else if(matchSettleEvent.getEventCode().equals("yellow_card")){
                    if("home".equals(matchSettleEvent.getHomeAway())){
                        homeScore++;
                    }else if("away".equals(matchSettleEvent.getHomeAway())){
                        awayScore++;
                    }else {
                        checkPeriodEventEquileDto.setNeedNoneEvent(false);
                    }
                }
                if(eventT1<matchSettleEvent.getT1()){
                    eventT1=matchSettleEvent.getT1();
                }
                if(eventT2<matchSettleEvent.getT2()){
                    eventT2=matchSettleEvent.getT2();
                }
                if(eventFirstT1<matchSettleEvent.getFirstT1()){
                    eventFirstT1=matchSettleEvent.getFirstT1();
                }
                if(eventFirstT2<matchSettleEvent.getFirstT2()){
                    eventFirstT2=matchSettleEvent.getFirstT2();
                }
                if(eventSecondT1<matchSettleEvent.getSecondT1()){
                    eventSecondT1=matchSettleEvent.getSecondT1();
                }
                if(eventSecondT2<matchSettleEvent.getSecondT2()){
                    eventSecondT2=matchSettleEvent.getSecondT2();
                }
            }
            checkPeriodEventEquileDto.setOrderNum(goalList.size()+1);
        }
        checkPeriodEventEquileDto.setEventT1(eventT1);
        checkPeriodEventEquileDto.setEventT2(eventT2);
        checkPeriodEventEquileDto.setEventFirstT1(eventFirstT1);
        checkPeriodEventEquileDto.setEventFirstT2(eventFirstT2);
        checkPeriodEventEquileDto.setEventSecondT1(eventSecondT1);
        checkPeriodEventEquileDto.setEventSecondT2(eventSecondT2);
        checkPeriodEventEquileDto.setPeriod(period);
        if(matchSettleScore.getT1()!=null&&matchSettleScore.getT2()!=null){
            if(homeScore==matchSettleScore.getT1()&&awayScore==matchSettleScore.getT2()){
                return checkPeriodEventEquileDto;
            }else {
                checkPeriodEventEquileDto.setPassCheck(false);
                return checkPeriodEventEquileDto;
            }
        }
        return checkPeriodEventEquileDto;
    }
    private List<MatchSettleScore> addUnsettledScoreToList(List<MatchSettleScore> list, List<String> settleNumList, String linkedId, Map<String, MatchSettleScore> settleNumMap, boolean isGray){
        Map<String, MatchSettleScore> MatchSettleScoreMap = list.stream().collect(Collectors.toMap(MatchSettleScore::getSettleNum, Function.identity(), (v1, v2)->v1));
        for(String settleNum : settleNumList) {
            if (!settleNumMap.containsKey(settleNum)) {
                continue;
            }
            MatchSettleScore settleScore = settleNumMap.get(settleNum);
            log.info("linkedId::{} settleNum::{} 更新settleNum", linkedId, settleNum);
            if (isGray){
                if (settleScore.getIsGrey() != null && settleScore.getIsGrey() == 1) {
                    MatchSettleScoreMap.put(settleNum, settleScore);
                }
            } else {
                MatchSettleScoreMap.put(settleNum, settleScore);
            }
        }
        return new ArrayList<>(MatchSettleScoreMap.values());
    }
    public boolean validGoalSettle(MatchSettleScore matchSettleScore) {
        if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
            return false;
        }
        List<String> childSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(matchSettleScore.getSettleNum());
        String parentSettleNum = FootballPeriodValidateEnum.getParentSettleNumList(matchSettleScore.getSettleNum());
        List<String> brotherSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(parentSettleNum);
        if (CollectionUtil.isEmpty(childSettleNumList) && parentSettleNum == null) {
            return true;
        }
        List<MatchSettleScore> settleScores = matchSettleScoreRepository.getModelsByItems(matchSettleScore.getStandardMatchId(),Arrays.asList(SettleEventCodeEnum.FOOTBALL_GOAL.getValue()),null,SETTLED,null,null);
        Map<String, MatchSettleScore> settleScoreMap = settleScores.stream().collect(Collectors.toMap(MatchSettleScore::getSettleNum, t->t, (v1, v2)->v1));

        // valid child nodes
        int sumScoreT1 = 0;
        int sumScoreT2 = 0;
        for(String settleNum : childSettleNumList) {
            MatchSettleScore settleScore = settleScoreMap.getOrDefault(settleNum, null);
            if (settleScore == null) {
                continue;
            }
            if (settleScore.getT1() != null && settleScore.getT1() > 0) {
                sumScoreT1 += settleScore.getT1();
            }
            if (settleScore.getT2() != null && settleScore.getT2() > 0) {
                sumScoreT2 += settleScore.getT2();
            }
        }
        if (sumScoreT1 > matchSettleScore.getT1() || sumScoreT2 > matchSettleScore.getT2()) {
            return false;
        }
        // valid parent nodes
        if (parentSettleNum == null || !settleScoreMap.containsKey(parentSettleNum)) {
            return true;
        }
        sumScoreT1 = matchSettleScore.getT1();
        sumScoreT2 = matchSettleScore.getT2();
        for(String settleNum : brotherSettleNumList){
            MatchSettleScore settleScore = settleScoreMap.getOrDefault(settleNum, null);
            if (settleScore == null) {
                continue;
            }
            if (settleScore.getT1() != null && settleScore.getT1() > 0) {
                sumScoreT1 += settleScore.getT1();
            }
            if (settleScore.getT2() != null && settleScore.getT2() > 0) {
                sumScoreT2 += settleScore.getT2();
            }
        }
        if(sumScoreT1 > settleScoreMap.get(parentSettleNum).getT1() || sumScoreT2 > settleScoreMap.get(parentSettleNum).getT2()){
            return false;
        }
        return true;
    }

    public List<MatchSettleScoreDto> setFiveMinList(Long matchId, List<MatchSettleScoreDto> matchSettleScoreDtos){
        MatchSettleInfo settleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(matchId);
        List<MatchSettleScoreDto> scoreList = new ArrayList<>();
        if (settleInfo!=null){

            if(settleInfo.getFiveMinSwitch() != null && settleInfo.getFiveMinSwitch() == 1){
                List<MatchSettleScoreDto> oneList = new ArrayList<>();
                List<MatchSettleScoreDto> twoList = new ArrayList<>();
                List<MatchSettleScoreDto> threeList = new ArrayList<>();
                List<MatchSettleScoreDto> fourList = new ArrayList<>();
                List<MatchSettleScoreDto> fiveList = new ArrayList<>();
                List<MatchSettleScoreDto> sixList = new ArrayList<>();
                for (int i=0;i<matchSettleScoreDtos.size();i++) {
                    MatchSettleScoreDto matchSettleScoreDto = matchSettleScoreDtos.get(i);
                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036){
                        oneList.add(matchSettleScoreDto);
                    }else if(settleNum == 1037 || settleNum == 1038 || settleNum == 1039){
                        twoList.add(matchSettleScoreDto);
                    }else if(settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043){
                        threeList.add(matchSettleScoreDto);
                    }else if(settleNum == 1044 || settleNum == 1045 || settleNum == 1046){
                        fourList.add(matchSettleScoreDto);
                    }else if(settleNum == 1047 || settleNum == 1048 || settleNum == 1049){
                        fiveList.add(matchSettleScoreDto);
                    }else if(settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
                        sixList.add(matchSettleScoreDto);
                    }
                }
                for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
                    if(settleNum == 102){
                        matchSettleScoreDto.setFiveMinList(oneList);
                    }
                    if(settleNum == 103){
                        matchSettleScoreDto.setFiveMinList(twoList);
                    }
                    if(settleNum == 104){
                        matchSettleScoreDto.setFiveMinList(threeList);
                    }
                    if(settleNum == 106){
                        matchSettleScoreDto.setFiveMinList(fourList);
                    }
                    if(settleNum == 107){
                        matchSettleScoreDto.setFiveMinList(fiveList);
                    }
                    if(settleNum == 108){
                        matchSettleScoreDto.setFiveMinList(sixList);
                    }
                }

                for (MatchSettleScoreDto score:matchSettleScoreDtos) {
                    MatchSettleScoreDto matchSettleScoreDto = score;
                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036
                            || settleNum == 1037 || settleNum == 1038 || settleNum == 1039
                            || settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043
                            ||settleNum == 1044 || settleNum == 1045 || settleNum == 1046
                            || settleNum == 1047 || settleNum == 1048 || settleNum == 1049
                            || settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){

                    } else {
                        scoreList.add(score);
                    }
                }
            } else {
                for (MatchSettleScoreDto score:matchSettleScoreDtos) {
                    MatchSettleScoreDto matchSettleScoreDto = score;
                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036
                            || settleNum == 1037 || settleNum == 1038 || settleNum == 1039
                            || settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043
                            ||settleNum == 1044 || settleNum == 1045 || settleNum == 1046
                            || settleNum == 1047 || settleNum == 1048 || settleNum == 1049
                            || settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){

                    } else {
                        scoreList.add(score);
                    }
                }
            }
        }

        return scoreList;
    }

    @Async("PushStandardSettleEventThreadPool")
    public void deleteAllCacheBasedIdKeys(){
        log.info("deleteAllCacheBasedIdKeys start!");
        Long curId = 0l;
        while (true) {
            List<MatchSettleInfo> matchSettleInfos = matchSettleInfoRepository.selectByCurIdAndLimit(curId, settleInfoLimit);
            if (CollectionUtils.isEmpty(matchSettleInfos)) {
                log.info("deleteAllCacheBasedIdKeys end!");
                return;
            }
            List<String> redisKeys = matchSettleInfos.stream().map(t-> RepositoryConstant.MATCH_SETTLE_INFO + t.getId()).collect(Collectors.toList());
            log.info("deleteAllCacheBasedIdKeys redisKeys size:{}", redisKeys.size());
            // 删除 Redis 中的这些键
            redisService.del(redisKeys);
            log.info("deleteAllCacheBasedIdKeys 删除key");
            curId = matchSettleInfos.get(matchSettleInfos.size()-1).getId();
        }
    }
    public List<String> getSettleNumByStarTimeAndEventCode(Long timeFromStar,String eventCode,Long periodId){
        List<String> settleNumList = new ArrayList<>();
        String settleNum = null;
        if (eventCode.equals("goal")){
            if (periodId==6){
                if (timeFromStar>=0&&timeFromStar<300){
                    settleNum = "1034";
                }
                if (timeFromStar>=300&&timeFromStar<600){
                    settleNum = "1035";
                }
                if (timeFromStar>=600&&timeFromStar<900){
                    settleNum = "1036";
                }if (timeFromStar>=900&&timeFromStar<1200){
                    settleNum = "1037";
                }if (timeFromStar>=1200&&timeFromStar<1500){
                    settleNum = "1038";
                }if (timeFromStar>=1500&&timeFromStar<1800){
                    settleNum = "1039";
                }if (timeFromStar>=1800&&timeFromStar<2100){
                    settleNum = "1040";
                }if (timeFromStar>=2100&&timeFromStar<2400){
                    settleNum = "1041";
                }if (timeFromStar>=2400&&timeFromStar<2700){
                    settleNum = "1042";
                }if (timeFromStar>=2700){
                    settleNum = "1043";
                }
            } else  if (periodId==7){
                if (timeFromStar>=2700&&timeFromStar<3000){
                    settleNum = "1044";
                }
                if (timeFromStar>=3000&&timeFromStar<3300){
                    settleNum = "1045";
                }
                if (timeFromStar>=3300&&timeFromStar<3600){
                    settleNum = "1046";
                }if (timeFromStar>=3600&&timeFromStar<3900){
                    settleNum = "1047";
                }if (timeFromStar>=3900&&timeFromStar<4200){
                    settleNum = "1048";
                }if (timeFromStar>=4200&&timeFromStar<4500){
                    settleNum = "1049";
                }if (timeFromStar>=4500&&timeFromStar<4800){
                    settleNum = "1050";
                }if (timeFromStar>=4800&&timeFromStar<5100){
                    settleNum = "1051";
                }if (timeFromStar>=5100&&timeFromStar<5400){
                    settleNum = "1052";
                }if (timeFromStar>=5400){
                    settleNum = "1053";
                }
            } else  {
                if (timeFromStar<=6300){
                    settleNum = "1014";
                }else {
                    settleNum = "1018";
                }

            }

        }
        if (eventCode.equals("corner")){
            if (periodId==6) {
                if (timeFromStar>=0&&timeFromStar<900){
                    settleNum = "2011";
                }
                if (timeFromStar>=900&&timeFromStar<1800){
                    settleNum = "2012";
                }if (timeFromStar>=1800){
                    settleNum = "2013";
                }
            }else if (periodId==7){
                if (timeFromStar>=2700&&timeFromStar<3600){
                    settleNum = "2014";
                }if (timeFromStar>=3600&&timeFromStar<4500){
                    settleNum = "2015";
                }if (timeFromStar>=4500){
                    settleNum = "2016";
                }
            }else {
                if (timeFromStar<=6300){
                    settleNum = "206";
                }else {
                    settleNum = "207";
                }
            }

        }
        settleNumList.add(settleNum);
        if (settleNum.equals("1034")||settleNum.equals("1035")||settleNum.equals("1036")){
            settleNumList.add("102");
        }
        if (settleNum.equals("1037")||settleNum.equals("1038")||settleNum.equals("1039")){
            settleNumList.add("103");
        }
        if (settleNum.equals("1040")||settleNum.equals("1041")||settleNum.equals("1042")||settleNum.equals("1043")){
            settleNumList.add("104");
        }
        if (settleNum.equals("1044")||settleNum.equals("1045")||settleNum.equals("1046")){
            settleNumList.add("106");
        }
        if (settleNum.equals("1047")||settleNum.equals("1048")||settleNum.equals("1049")){
            settleNumList.add("107");
        }
        if (settleNum.equals("1050")||settleNum.equals("1051")||settleNum.equals("1052")||settleNum.equals("1053")){
            settleNumList.add("108");
        }
        return settleNumList;
    }

}
