package com.panda.merge.v2.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.MatchFreezeDto;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.SettleEventDeleteRequest;
import com.panda.merge.dto.settle.UpdateMatchSettleScoreDto;
import com.panda.merge.model.*;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleOperateLogEntity;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.panda.merge.constant.RepositoryConstant.REDIS_THREE_TIME;
import static com.panda.merge.constant.RepositoryConstant.TEMPLATE_RELATION;

public interface IMatchSettleOperateLogService {

    public void delTemplateRelationByExample(MatchSettleTemplateRelationExample example);


    public void batchInsertTemplateRelationToRedis(Integer level);

    void updateMatchSettleScoreAddLog(UpdateMatchSettleScoreDto matchSettleOperateLogDto, String forwScore, MatchSettleScore matchSettleScore, StandardMatchInfo standardMatchInfo, String OperateType);
    void matchSettleScoreAddLog(MatchSettleScore matchSettleScore, String operatorName, OperateLogTypeEnum scoreSettle, String  beforeText, String  ipAddress);

    void matchSettleScoreAddLog(MatchSettleScoreEntity matchSettleScore,MatchSettleScore newMatchSettleScore, String operatorName, String type,String  linkId,String  ipAddress);
    void deleteSettleAlertLog(Object matchSettleScoreEventInfo, MatchSettleSwitcherDto matchSettleSwitcherDto);
     void matchSettleCheckScoreAddLog(MatchSettleCheckInfoEntity oIdInfo, MatchSettleCheckInfoEntity newInfo,
                                            UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                            String settleNum, Integer checkNumber);

     void categoryReSettleAddLog(SettleQueryDTO settleQueryDTO, String forwText);

    void matchFreezeAddLog(StandardMatchInfo standardMatchInfo ,MatchSettleInfo matchSettleInfo, String forwText, MatchFreezeDto matchFreezeDto) ;
    void matchReSettleAddLog(SettleQueryDTO settleQueryDTO);

     void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent, String operatorName, String code, String before, String ipAddress);

    void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent, MatchSettleEvent newMatchSettleEvent, String operatorName, OperateLogTypeEnum type, String ipAddress);

    /**
     * 记录核对事件日志
     * @param oIdInfo     原核对信息
     * @param newInfo     新核对信息
     * @param dto         操作入参
     * @param enums       操作类型
     * @param settleNum   结算阶段（用于匹配阶段/玩法）
     * @param checkNumber 第几位审核员
     * @param eventType   事件类型：1=次序（只记录比分），3=时段（只记录5/15分钟）
     */
    void matchSettleCheckEventAddLog(MatchSettleCheckInfoEntity oIdInfo, MatchSettleCheckInfoEntity newInfo,
                                     UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                     String settleNum, Integer checkNumber, Integer eventType);
    void settleMentionLog(Object object, SettleEventDeleteRequest settleEventDeleteRequest);
}
