package com.panda.merge.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.dto.MatchSettleEventCompareDto;
import com.panda.merge.dto.MatchSettleEventFiveMinCompareDto;
import com.panda.merge.dto.MatchSettleScoresBasketballCompareDto;
import com.panda.merge.dto.MatchSettleScoresCompareDto;
import com.panda.merge.dto.settle.UpdateBasketBallSettleScoreDto;
import com.panda.merge.dto.settle.UpdateMatchSettleScoreDto;
import com.panda.merge.model.*;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettleCheckUtils {
    /**
     * 数据商三方比分编辑核对记录
     * */
    public static void copyMatchSettleScores(MatchSettleThirdScore matchSettleThirdScore,    MatchSettleCheckInfo matchSettleCheckInfo){
        matchSettleCheckInfo.setT1(matchSettleThirdScore.getT1());
        matchSettleCheckInfo.setT2(matchSettleThirdScore.getT2());
        matchSettleCheckInfo.setStandardMatchId(matchSettleThirdScore.getStandardMatchId());
        matchSettleCheckInfo.setDataSourceCode(matchSettleThirdScore.getDataSourceCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleThirdScore.getExtryInfo());
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
        matchSettleCheckInfo.setGoWaterStatus(MatchSettleCheckConstant.GoWaterStatus.NOT_GO_WATER);
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
    }
    /**
     *数据商三方比分初始化核对记录
     * */
    public static MatchSettleCheckInfo initMatchSettleScores(MatchSettleScore matchSettleScore, MatchSettleThirdScore matchSettleThirdScore) {
        MatchSettleCheckInfo matchSettleCheckInfo =new MatchSettleCheckInfo();
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
        matchSettleCheckInfo.setDataSourceCode(matchSettleThirdScore.getDataSourceCode());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.DATA_SOURCE);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleScore.getId());
        matchSettleCheckInfo.setStandardMatchId(matchSettleScore.getStandardMatchId());
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.NOT_EDIT);
        //数据商没有推送用户操作的通知消息 所以没有通知消息的当前用户核对顺序
        matchSettleCheckInfo.setCheckNumber(0);
        matchSettleCheckInfo.setEventCode(matchSettleThirdScore.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleThirdScore.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleThirdScore.getT1());
        matchSettleCheckInfo.setT2(matchSettleThirdScore.getT2());
        matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdScore.getId());
        matchSettleCheckInfo.setId(IdWorker.getId());
        //数据商不会推送走水
        matchSettleCheckInfo.setGoWaterStatus(MatchSettleCheckConstant.GoWaterStatus.NOT_GO_WATER);
        return matchSettleCheckInfo;
    }
    /**
     * 阶段比分类型核对分组方法
     * */
    public static  Map<String,List<MatchSettleCheckInfo>> groupBySettleCheck(List<MatchSettleCheckInfo> list ) {
        Map<String,List<MatchSettleCheckInfo>> group =new HashMap<>();
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            //1.计算比对Key 相同的key可以认定比分相同
            String key = countSettleCheckGroupKey(matchSettleCheckInfo);
            //2.将相同的key的数据集合起来
            List<MatchSettleCheckInfo> checkInfoList =group.get(key);
            if(checkInfoList==null){
                checkInfoList =new ArrayList<>();
                checkInfoList.add(matchSettleCheckInfo);
                group.put(key,checkInfoList);
            } else {
                checkInfoList.add(matchSettleCheckInfo);
            }
            /*checkInfoList.add(matchSettleCheckInfo);
            group.put(key,checkInfoList);*/
        }
        return group;
    }

    public static  Map<String,List<MatchSettleCheckInfo>> groupByFiveMinSettleCheck(List<MatchSettleCheckInfo> list ) {
        Map<String,List<MatchSettleCheckInfo>> group =new HashMap<>();
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            //1.计算比对Key 相同的key可以认定比分相同
            MatchSettleEventFiveMinCompareDto compareDto =new MatchSettleEventFiveMinCompareDto();
            BeanUtils.copyProperties(matchSettleCheckInfo,compareDto);
//            if(StringUtils.isEmpty(compareDto.getExtryInfo())){
//                compareDto.setExtryInfo("");
//            }
            String key = JSONObject.toJSONString(compareDto);
            //2.将相同的key的数据集合起来
            List<MatchSettleCheckInfo> checkInfoList =group.get(key);
            if(checkInfoList==null){
                checkInfoList =new ArrayList<>();
                checkInfoList.add(matchSettleCheckInfo);
                group.put(key,checkInfoList);
            } else {
                checkInfoList.add(matchSettleCheckInfo);
            }

        }
        return group;
    }
    /**
     * 通过 比分对比的KEY 来进行分组 和比分核对
     * */
    public static String countSettleCheckGroupKey(MatchSettleCheckInfo matchSettleCheckInfo) {
        if(matchSettleCheckInfo.getCheckType()!=null&&matchSettleCheckInfo.getCheckType()==MatchSettleCheckConstant.CheckType.PERIOD_SCORE){
            return countSettleScoreCompareKey(matchSettleCheckInfo);
        }else {
            return countSettleEventCompareKey(matchSettleCheckInfo);
        }
    }

    public static String countSettleCheckGroupBasketballKey(MatchSettleCheckInfo matchSettleCheckInfo) {
        MatchSettleScoresBasketballCompareDto compareDto =new MatchSettleScoresBasketballCompareDto();
        BeanUtils.copyProperties(matchSettleCheckInfo,compareDto);
        String jsonKey = JSONObject.toJSONString(compareDto);
        return jsonKey;
    }

    /**
     * 通过 比分对比的KEY 来进行分组 和比分核对
     * */
    public static String countSettleCheckGroupKey(MatchSettleScore matchSettleScore) {
        MatchSettleScoresCompareDto compareDto =new MatchSettleScoresCompareDto();
        BeanUtils.copyProperties(matchSettleScore,compareDto);
        /*if(StringUtils.isEmpty(compareDto.getExtryInfo())){
            compareDto.setExtryInfo("");
        }*/
        String jsonKey = JSONObject.toJSONString(compareDto);
        return jsonKey;
    }
    public static String countSettleEventCompareKey(MatchSettleEvent matchSettleEvent) {
        MatchSettleEventCompareDto compareDto =new MatchSettleEventCompareDto();
        BeanUtils.copyProperties(matchSettleEvent,compareDto);
//        if(StringUtils.isEmpty(compareDto.getExtryInfo())){
//            compareDto.setExtryInfo("");
//        }
        String jsonKey = JSONObject.toJSONString(compareDto);
        return jsonKey;
    }

    /**
     * 比分核对
     * */
    private static String countSettleScoreCompareKey(MatchSettleCheckInfo matchSettleCheckInfo) {
        MatchSettleScoresCompareDto compareDto =new MatchSettleScoresCompareDto();
        BeanUtils.copyProperties(matchSettleCheckInfo,compareDto);
        /*if(StringUtils.isEmpty(compareDto.getExtryInfo())){
            compareDto.setExtryInfo("");
        }*/
        String jsonKey = JSONObject.toJSONString(compareDto);
        return jsonKey;
    }




    /**
     * 事件核对
     * */
    public static String countSettleEventCompareKey(MatchSettleCheckInfo matchSettleCheckInfo) {
        MatchSettleEventCompareDto compareDto =new MatchSettleEventCompareDto();
        BeanUtils.copyProperties(matchSettleCheckInfo,compareDto);
//        if(StringUtils.isEmpty(compareDto.getExtryInfo())){
//            compareDto.setExtryInfo("");
//        }
        String jsonKey = JSONObject.toJSONString(compareDto);
        return jsonKey;
    }

    /**
     *5分钟区间事件核对
     * @param matchSettleCheckInfo
     * @return
     */
    public static String countSettleEventFiveMinCompareKey(MatchSettleCheckInfo matchSettleCheckInfo) {
        MatchSettleEventFiveMinCompareDto compareDto =new MatchSettleEventFiveMinCompareDto();
        BeanUtils.copyProperties(matchSettleCheckInfo,compareDto);
        return JSONObject.toJSONString(compareDto);
    }


    public static void copyMatchSettleEvent(MatchSettleThirdEvent matchSettleThirdEvent, MatchSettleCheckInfo matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleThirdEvent.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleThirdEvent.getExtryInfo());
        matchSettleCheckInfo.setEventOrder(matchSettleThirdEvent.getEventOrder());
        matchSettleCheckInfo.setHomeAway(matchSettleThirdEvent.getHomeAway());
        matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdEvent.getId());
        matchSettleCheckInfo.setT1(matchSettleThirdEvent.getT1());
        matchSettleCheckInfo.setT2(matchSettleThirdEvent.getT2());
        matchSettleCheckInfo.setFirstT1(matchSettleThirdEvent.getFirstT1());
        matchSettleCheckInfo.setFirstT2(matchSettleThirdEvent.getFirstT2());
        matchSettleCheckInfo.setSecondT1(matchSettleThirdEvent.getSecondT1());
        matchSettleCheckInfo.setSecondT2(matchSettleThirdEvent.getSecondT2());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);

    }

    public static MatchSettleCheckInfo initMatchSettleEvent(MatchSettleEvent matchSettleEvent, MatchSettleThirdEvent matchSettleThirdEvent) {
        MatchSettleCheckInfo matchSettleCheckInfo =new MatchSettleCheckInfo();
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
        matchSettleCheckInfo.setDataSourceCode(matchSettleThirdEvent.getDataSourceCode());

        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.DATA_SOURCE);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleEvent.getId());
        matchSettleCheckInfo.setStandardMatchId(matchSettleEvent.getStandardMatchId());
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.NOT_EDIT);
        //数据商没有推送用户操作的通知消息 所以没有通知消息的当前用户核对顺序
        matchSettleCheckInfo.setCheckNumber(0);
        matchSettleCheckInfo.setEventCode(matchSettleThirdEvent.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleThirdEvent.getExtryInfo());
        matchSettleCheckInfo.setFirstT1(matchSettleThirdEvent.getFirstT1());
        matchSettleCheckInfo.setFirstT2(matchSettleThirdEvent.getFirstT2());
        matchSettleCheckInfo.setSecondT1(matchSettleThirdEvent.getSecondT1());
        matchSettleCheckInfo.setSecondT2(matchSettleThirdEvent.getSecondT2());
        matchSettleCheckInfo.setT1(matchSettleThirdEvent.getT1());
        matchSettleCheckInfo.setT2(matchSettleThirdEvent.getT2());
        matchSettleCheckInfo.setEventOrder(matchSettleThirdEvent.getEventOrder());
        matchSettleCheckInfo.setHomeAway(matchSettleThirdEvent.getHomeAway());
        matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdEvent.getId());
        matchSettleCheckInfo.setId(IdWorker.getId());
        //数据商不会推送走水
        matchSettleCheckInfo.setGoWaterStatus(MatchSettleCheckConstant.GoWaterStatus.NOT_GO_WATER);
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);
        return matchSettleCheckInfo;
    }

    /**
     *人工录入比分初始化核对记录   注：人工录入没有数据商编码和源三方结算比分事件ID
     * */
    public static MatchSettleCheckInfo initManualMatchSettleScores(MatchSettleScore matchSettleScore) {
        MatchSettleCheckInfo matchSettleCheckInfo =new MatchSettleCheckInfo();
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
        //比分或事件id
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleScore.getId());
        //赛事id
        matchSettleCheckInfo.setStandardMatchId(matchSettleScore.getStandardMatchId());
        //核对状态
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
        //用户名称
        matchSettleCheckInfo.setUserName(matchSettleScore.getOperater());
        //事件编码
        matchSettleCheckInfo.setEventCode(matchSettleScore.getEventCode());
        //附加字段
        matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
        //主队比分
        matchSettleCheckInfo.setT1(matchSettleScore.getT1());
        //客队比分
        matchSettleCheckInfo.setT2(matchSettleScore.getT2());
        //ID
        matchSettleCheckInfo.setId(IdWorker.getId());
        //是否灰色区间： 1 是 0 不是
        matchSettleCheckInfo.setIsGrey(MatchSettleCheckConstant.IsGrey.IS_NOT_GREY);
        //走水状态
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScore.getGoWaterStatus());
        //次序
        matchSettleCheckInfo.setCheckNumber(matchSettleScore.getCheckNumber());
        return matchSettleCheckInfo;
    }
    public static MatchSettleCheckInfoEntity initManualMatchSettleScoresv2(MatchSettleScore matchSettleScore) {
        MatchSettleCheckInfoEntity matchSettleCheckInfo =new MatchSettleCheckInfoEntity();
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
        //比分或事件id
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleScore.getId());
        //赛事id
        matchSettleCheckInfo.setStandardMatchId(matchSettleScore.getStandardMatchId());
        //核对状态
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
        //用户名称
        matchSettleCheckInfo.setUserName(matchSettleScore.getOperater());
        //事件编码
        matchSettleCheckInfo.setEventCode(matchSettleScore.getEventCode());
        //附加字段
        matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
        //主队比分
        matchSettleCheckInfo.setT1(matchSettleScore.getT1());
        //客队比分
        matchSettleCheckInfo.setT2(matchSettleScore.getT2());
        //ID
        matchSettleCheckInfo.setId(IdWorker.getId());
        //是否灰色区间： 1 是 0 不是
        matchSettleCheckInfo.setIsGrey(MatchSettleCheckConstant.IsGrey.IS_NOT_GREY);
        //走水状态
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScore.getGoWaterStatus());
        //次序
        matchSettleCheckInfo.setCheckNumber(matchSettleScore.getCheckNumber());
        return matchSettleCheckInfo;
    }

    /**
     * 人工录入更新核对记录
     * @param matchSettleScoreDto
     * @param matchSettleCheckInfo
     */
    public static void copyManualMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto, MatchSettleCheckInfo matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }

    /**
     * 人工录入更新核对记录
     * @param matchSettleScoreDto
     * @param matchSettleCheckInfo
     */
    public static void copyManualMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto, MatchSettleCheckInfo matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }
    public static void copyManualMatchSettleScoreV2(UpdateBasketBallSettleScoreDto matchSettleScoreDto, MatchSettleCheckInfoEntity matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }

    /**
     * 初始化数据
     * */
    public static MatchSettleCheckInfo initCheckMatchSettleEvent(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfo matchSettleCheckInfo) {
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setDataSourceCode(matchSettleEvent.getDataSourceCode());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleEvent.getId());
        matchSettleCheckInfo.setStandardMatchId(matchSettleEvent.getStandardMatchId());
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
        //数据商没有推送用户操作的通知消息 所以没有通知消息的当前用户核对顺序 TODO 次序计算不对
        matchSettleCheckInfo.setEventCode(matchSettleEvent.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleEvent.getExtryInfo());
        matchSettleCheckInfo.setFirstT1(matchSettleEvent.getFirstT1());
        matchSettleCheckInfo.setFirstT2(matchSettleEvent.getFirstT2());
        matchSettleCheckInfo.setSecondT1(matchSettleEvent.getSecondT1());
        matchSettleCheckInfo.setSecondT2(matchSettleEvent.getSecondT2());
        matchSettleCheckInfo.setT1(matchSettleEvent.getT1());
        matchSettleCheckInfo.setT2(matchSettleEvent.getT2());
        matchSettleCheckInfo.setEventOrder(matchSettleEvent.getEventOrder());
        matchSettleCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
        //数据商不会推送走水
        matchSettleCheckInfo.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
        matchSettleCheckInfo.setFiveMinSection(matchSettleEvent.getFiveMinSection());
        return matchSettleCheckInfo;
    }

    /**
     * 初始化数据
     * */
    public static MatchSettleCheckInfo initCheckPaniltyEvent(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfo matchSettleCheckInfo) {
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setDataSourceCode(matchSettleEvent.getDataSourceCode());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleEvent.getId());
        matchSettleCheckInfo.setStandardMatchId(matchSettleEvent.getStandardMatchId());
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.NOT_EDIT);
        matchSettleCheckInfo.setEventCode(matchSettleEvent.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleEvent.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleEvent.getT1());
        matchSettleCheckInfo.setT2(matchSettleEvent.getT2());
        matchSettleCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
        //数据商不会推送走水
        matchSettleCheckInfo.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
        return matchSettleCheckInfo;
    }
    public static MatchSettleCheckInfoEntity initCheckPaniltyEventV2(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfoEntity matchSettleCheckInfo) {
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setDataSourceCode(matchSettleEvent.getDataSourceCode());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleEvent.getId());
        matchSettleCheckInfo.setStandardMatchId(matchSettleEvent.getStandardMatchId());
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.NOT_EDIT);
        matchSettleCheckInfo.setEventCode(matchSettleEvent.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleEvent.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleEvent.getT1());
        matchSettleCheckInfo.setT2(matchSettleEvent.getT2());
        matchSettleCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
        //数据商不会推送走水
        matchSettleCheckInfo.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
        return matchSettleCheckInfo;
    }
    public static void initCheckMatchSettleScore(MatchSettleScore matchSettleScoreDto, MatchSettleCheckInfo matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }
    public static void initCheckMatchSettleScoreV2(MatchSettleScore matchSettleScoreDto, MatchSettleCheckInfoEntity matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }

    public static Integer getCheckNumber(Integer settleCheckNumber){
        if(settleCheckNumber==null){
            return 1;
        }else {
            return settleCheckNumber;
        }
    }

    /**
     * 核对记录属性复制到比分
     * @param matchSettleScore
     * @param matchSettleCheckInfo
     */
    public static void copyCheckInfoToMatchSettleScore(MatchSettleCheckInfo matchSettleCheckInfo,MatchSettleScore matchSettleScore) {

        matchSettleScore.setEventCode(matchSettleCheckInfo.getEventCode());
        matchSettleScore.setExtryInfo(matchSettleCheckInfo.getExtryInfo());
        matchSettleScore.setT1(matchSettleCheckInfo.getT1());
        matchSettleScore.setT2(matchSettleCheckInfo.getT2());
        matchSettleScore.setModifyTime(System.currentTimeMillis());
        matchSettleScore.setGoWaterStatus(matchSettleCheckInfo.getGoWaterStatus());
    }

    /**
     * 核对记录属性复制到事件
     * @param matchSettleCheckInfo
     * @param matchSettleEvent
     */
    public static void copyCheckInfoToMatchSettleEvent(MatchSettleCheckInfo matchSettleCheckInfo,MatchSettleEvent matchSettleEvent) {
        //点球大战次序事件只复制比分和是否进球
        if(matchSettleEvent.getSettleNum().equals("1030")){
            matchSettleEvent.setT1(matchSettleCheckInfo.getT1());
            matchSettleEvent.setT2(matchSettleCheckInfo.getT2());
            matchSettleEvent.setExtryInfo(matchSettleCheckInfo.getExtryInfo());
            matchSettleEvent.setGoWaterStatus(matchSettleCheckInfo.getGoWaterStatus());
        }else {
            matchSettleEvent.setEventCode(matchSettleCheckInfo.getEventCode());
            matchSettleEvent.setExtryInfo(matchSettleCheckInfo.getExtryInfo());
            matchSettleEvent.setEventOrder(matchSettleCheckInfo.getEventOrder());
            matchSettleEvent.setHomeAway(matchSettleCheckInfo.getHomeAway());
            matchSettleEvent.setT1(matchSettleCheckInfo.getT1());
            matchSettleEvent.setT2(matchSettleCheckInfo.getT2());
            matchSettleEvent.setFirstT1(matchSettleCheckInfo.getFirstT1());
            matchSettleEvent.setFirstT2(matchSettleCheckInfo.getFirstT2());
            matchSettleEvent.setSecondT1(matchSettleCheckInfo.getSecondT1());
            matchSettleEvent.setSecondT2(matchSettleCheckInfo.getSecondT2());
            matchSettleEvent.setGoWaterStatus(matchSettleCheckInfo.getGoWaterStatus());
        }
    }

    public static void copyProperties(MatchSettleCheckInfo matchSettleCheckInfo, MatchSettleCheckInfo nextCheckInfo) {
        BeanUtils.copyProperties(matchSettleCheckInfo,nextCheckInfo);
        MatchSettleCheckInfo newCheckInfo=new MatchSettleCheckInfo();
        nextCheckInfo.setEventCode(newCheckInfo.getEventCode());
        nextCheckInfo.setExtryInfo(newCheckInfo.getExtryInfo());
        nextCheckInfo.setEventOrder(newCheckInfo.getEventOrder());
        nextCheckInfo.setHomeAway(newCheckInfo.getHomeAway());
        nextCheckInfo.setT1(newCheckInfo.getT1());
        nextCheckInfo.setT2(newCheckInfo.getT2());
        nextCheckInfo.setFirstT1(newCheckInfo.getFirstT1());
        nextCheckInfo.setFirstT2(newCheckInfo.getFirstT2());
        nextCheckInfo.setSecondT1(newCheckInfo.getSecondT1());
        nextCheckInfo.setSecondT2(newCheckInfo.getSecondT2());
        nextCheckInfo.setId(IdWorker.getId());
        nextCheckInfo.setCreateTime(System.currentTimeMillis());
        nextCheckInfo.setModifyTime(System.currentTimeMillis());
        nextCheckInfo.setCheckStatus(0);
        nextCheckInfo.setCheckType(matchSettleCheckInfo.getCheckType());
        nextCheckInfo.setDataSourceCode("PA");
    }
    public static List<String> getEventCodesByCode(Long periodId,String eventCode){
        List<String> eventCodes=new ArrayList<>();

        if(eventCode.equals("goal")||eventCode.equals("penalty_missed")){
            eventCodes.add("goal"); eventCodes.add("no goal");
            if(periodId.equals(50l)){
                eventCodes.add("penalty_missed");
            }
        }else if(eventCode.equals("corner")){
            eventCodes.add("corner");
        }else {
            eventCodes.add("yellow_card");eventCodes.add("red_card");eventCodes.add("fa_card");
        }
        return eventCodes;
    }

    public static Map<String, List<MatchSettleCheckInfo>> groupByBasketBallCheck(List<MatchSettleCheckInfo> list) {
        Map<String,List<MatchSettleCheckInfo>> group =new HashMap<>();
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            //1.计算比对Key 相同的key可以认定比分相同
            String key = countSettleCheckGroupBasketballKey(matchSettleCheckInfo);
            //2.将相同的key的数据集合起来
            List<MatchSettleCheckInfo> checkInfoList =group.get(key);
            if(checkInfoList==null){
                checkInfoList =new ArrayList<>();
                checkInfoList.add(matchSettleCheckInfo);
                group.put(key,checkInfoList);
            } else {
                checkInfoList.add(matchSettleCheckInfo);
            }
            /*checkInfoList.add(matchSettleCheckInfo);
            group.put(key,checkInfoList);*/
        }
        return group;
    }

    public static void copyManualMatchSettleScoreV2(UpdateMatchSettleScoreDto matchSettleScoreDto, MatchSettleCheckInfoEntity matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }

    public static MatchSettleCheckInfoEntity initCheckMatchSettleEventV2(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfoEntity matchSettleCheckInfo) {
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setDataSourceCode(matchSettleEvent.getDataSourceCode());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.EVENT_SCORE);
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleEvent.getId());
        matchSettleCheckInfo.setStandardMatchId(matchSettleEvent.getStandardMatchId());
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
        //数据商没有推送用户操作的通知消息 所以没有通知消息的当前用户核对顺序 TODO 次序计算不对
        matchSettleCheckInfo.setEventCode(matchSettleEvent.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleEvent.getExtryInfo());
        matchSettleCheckInfo.setFirstT1(matchSettleEvent.getFirstT1());
        matchSettleCheckInfo.setFirstT2(matchSettleEvent.getFirstT2());
        matchSettleCheckInfo.setSecondT1(matchSettleEvent.getSecondT1());
        matchSettleCheckInfo.setSecondT2(matchSettleEvent.getSecondT2());
        matchSettleCheckInfo.setT1(matchSettleEvent.getT1());
        matchSettleCheckInfo.setT2(matchSettleEvent.getT2());
        matchSettleCheckInfo.setEventOrder(matchSettleEvent.getEventOrder());
        matchSettleCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
        //数据商不会推送走水
        matchSettleCheckInfo.setGoWaterStatus(matchSettleEvent.getGoWaterStatus());
        matchSettleCheckInfo.setFiveMinSection(matchSettleEvent.getFiveMinSection());
        return matchSettleCheckInfo;
    }
}
