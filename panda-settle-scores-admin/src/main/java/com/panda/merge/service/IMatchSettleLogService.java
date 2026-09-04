package com.panda.merge.service;

import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.dto.LimitSwitchDto;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.*;
import com.panda.merge.v2.entity.*;

import java.util.List;

/**
 * 比分结算日志
 * */
public interface IMatchSettleLogService {
    //比分结算增加日志
    void matchSettleScoreAddLog(MatchSettleScore matchSettleScore, String operatorName, OperateLogTypeEnum scoreSettle,String  beforeText,String  ipAddress);

    List<MatchSettleOperateLog> batchMatchSettleScoreAddLog(StandardMatchInfo standardMatchInfo, MatchSettleScore matchSettleScore, String operatorName, OperateLogTypeEnum scoreSettle, String  beforeText, String  ipAddress);

    void matchSettleScoreAddLog(MatchSettleScore matchSettleScore,MatchSettleScore newMatchSettleScore, String operatorName, String type,String  linkId,String  ipAddress);

    void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent, String operatorName, String type,String before,String  ipAddress);

    void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent,MatchSettleEvent newMatchSettleEvent, String operatorName, OperateLogTypeEnum type,String  ipAddress);
   //结算切换增加日志
    void settleSwitcherAddLog(MatchSettleInfo matchSettleInfo, MatchSettleSwitcherDto matchSettleSwitcherDto, Integer operateForw);
    //比分新增/更新/编辑增加日志
    void updateMatchSettleScoreAddLog(UpdateMatchSettleScoreDto matchSettleOperateLogDto,String forwScore, MatchSettleScore matchSettleScore,StandardMatchInfo standardMatchInfo,String OperateType);
    //比分阶段(次序)冻结 操作日志
    void scoresPeriodOrderFreeze(StandardMatchInfo standardMatchInfo,MatchSettleEvent matchSettleEvent, String forwText, ScoresPeriodOrderFreezeDto freezeDto);
    //比分阶段冻结 操作日志
    void scoresPeriodFreezeAddLog(StandardMatchInfo standardMatchInfo,MatchSettleScore matchSettleScore, String forwText, ScoresPeriodFreezeDto scoresPeriodFreezeDto);
   //赛事冻结 操作日志
    void matchFreezeAddLog(StandardMatchInfo standardMatchInfo  ,MatchSettleInfo matchSettleInfo, String rearText, MatchFreezeDto matchFreezeDto);
    //赛事级重跑操作日志
    void matchReSettleAddLog(SettleQueryDTO settleQueryDTO);
    //玩法级重跑或冻结操作日志
    void categoryReSettleAddLog(SettleQueryDTO settleQueryDTO,String forwText);


    //新结算比分信息日志
    void matchSettleCheckScoreAddLog(MatchSettleCheckInfo oIdInfo, MatchSettleCheckInfo newInfo,
                                     UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                     String settleNum,Integer checkNumber);

    //新结算事件次序日志
    void matchSettleCheckEventAddLog(MatchSettleCheckInfo oIdInfo, MatchSettleCheckInfo newInfo,
                                     UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                     String settleNum,Integer checkNumber);
    //新结算事件次序日志
    void matchSettleCheckEventAddLog(MatchSettleEvent matchSettleEvent,MatchSettleEvent newMatchSettleEvent,
                                     UpdateMatchSettleScoreDto dto, OperateLogTypeEnum type,Integer checkNumber);

    //回滚回调更新操作日志
    void upLog(Long evenRollBackId, String matchId,String info );

    //结算顺序操作日志
    void setSettleOrderClosedAddLog(MatchSettleInfo oIdMatchSettleInfo, MatchSettleInfo matchSettleInfo, MatchSettleOrderClosedDTO dto, OperateLogTypeEnum type);

    //五分钟玩法操作日志
    void setFiveMinSwitchLog(MatchSettleInfo oIdMatchSettleInfo, MatchSettleInfo matchSettleInfo, MatchSettleFiveMinSwitchDTO dto, OperateLogTypeEnum type);

    //删除阶段报警日志
    void deleteSettleAlertLog(Object matchSettleScoreEventInfo,MatchSettleSwitcherDto matchSettleSwitcherDto);

    void settleMentionLog(Object matchSettleScoreEventInfo, SettleEventDeleteRequest settleEventDeleteRequest);

    /**
     * 更新15分钟&5分钟数据上灰色区时间设置
     * @param grayIntervalDto
     * @param dbGray
     */
//    void updateDataSourceGrayIntervalLog(DataSourceGrayIntervalDto grayIntervalDto, MatchGrayInterval dbGray,List<MatchSettleOperateLogEntity> operateLogEntityList );

   /**
    * 更新联赛等级对应的结算数据源的开关列表
    * @param matchSettleDataSourceDto
    * @param oldStatus
    */
   void updateLeagueMatchSettleDataSourceLog(MatchSettleDataSourceDto matchSettleDataSourceDto, Integer oldStatus);

    /**
     * 新增自定义联赛模版设置日志
     * @param template
     */
    void addSettleTemplateLog(MatchSettleTemplateDto template);

    /**
     * 批量修改联赛模版设置日志
     * @param settleTemplateUpdateDto
     */
    void templateBatchUpdateLog(SettleTemplateBatchUpdateDto settleTemplateUpdateDto);

    /**
     * 单条修改联赛模版设置日志
     * @param settleTemplateUpdateDto
     */
    void templateBatchSingleUpdateLog(SettleTemplateBatchUpdateDto settleTemplateUpdateDto);

    /**
     * 删除自定义联赛模版设置接口
     * @param settleTemplateUpdateDto
     */
    void deleteTemplateLog(SettleTemplateBatchUpdateDto settleTemplateUpdateDto);

    /**
     * 编辑数据商权重模板日志
     * @param matchSettleTemplateOld
     * @param matchSettleTemplateNew
     */
    void editWeightTemplateLog(MatchSettleTemplateDto matchSettleTemplateOld,SettleWeightTemplateUpdateDto matchSettleTemplateNew);

   /**
    * 编辑数据商灰色区间模板日志
    * @param matchSettleTemplateOld
    * @param matchSettleTemplateNew
    */
   void editGrayAreaTemplateLog(MatchSettleTemplateDto matchSettleTemplateOld,SettleGrayTemplateUpdateDto matchSettleTemplateNew);

 void editDownTemplateLog(MatchSettleTemplateDto matchSettleTemplateOld,SettleDownTemplateUpdateDto settleDownTemplateUpdateDto);


    /**
     * 编辑数据商权重日志
     * @param matchSettleTemplateOld
     * @param matchSettleTemplateNew
     */
    void editMatchSettleDataSourceWeightLog(MatchSettleTemplateDto matchSettleTemplateOld,SettleWeightTemplateUpdateDto matchSettleTemplateNew,String dataSource, List<MatchSettleOperateLogEntity> willUpdateMatchSettleOperateLogList);

    /**
     * 编辑数据商灰色区间日志
     * @param matchSettleTemplateOld
     * @param matchSettleTemplateNew
     */
    void editMatchSettleDataSourceGrayAreaLog(MatchSettleTemplateDto matchSettleTemplateOld,SettleGrayTemplateUpdateDto matchSettleTemplateNew,String dataSource,List<MatchSettleOperateLogEntity> willUpdateGrayMatchSettleOperateLogList);

 /**
  * 编辑数据商开关日志
  * @param oldSwitch
  * @param newSwitch
  */
 void editMatchSettleDataSourceSwitchLog(MatchSettleDataSourceSwitch oldSwitch,MatchSettleDataSourceSwitchDto newSwitch);

 /**
  * 新增/删除数据商日志
  * @param matchSettleDataSourceWeightAndSwitchDto
  * @param tag 0新增  1删除
  */
 void addOrDelDataSourceLog(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto ,Integer tag);

 /**
  * 修改数据商编码日志
  * @param dataSourceWeightUpdateDto
  */
 void updateDataSourceCodeLog(DataSourceWeightUpdateDto dataSourceWeightUpdateDto);

 void editMatchDataSourceWeightConfigLog(MatchSettleDataSourceWeightConfig oldConfig,MatchSettleDataSourceWeightConfigDto newConfig,List<MatchSettleOperateLogEntity> matchSettleOperateLogList);

 void spOddsResultAddLog(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto,MatchSettleSpOdds oddsBefore, MatchSettleSpOdds odds, StandardMatchInfo standardMatchInfo, String type, List<MatchSettleOperateLogEntity> operateLogEntityList);

 void spOddsResultAddLog(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto, MatchSettleSpOddsEntity oddsBefore, MatchSettleSpOddsEntity odds, StandardMatchInfo standardMatchInfo, String type, List<MatchSettleOperateLogEntity> operateLogEntityList);

 void editBasketBallRealTimeConfigLog(LimitSwitchDto oldConfig, LimitSwitchDto newConfig, SettleTimeLimitDto dto, List<MatchSettleOperateLogEntity> willSaveOperateLogList) ;

    void editBasketBallTimeLimitConfigLog(LimitSwitchDto oldConfig, LimitSwitchDto newConfig, SettleTimeLimitDto dto,List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList);
 void editBasketBallSetUpConfigLog(LimitSwitchDto oldConfig,LimitSwitchDto newConfig,SettleTimeLimitDto dto, List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList);
}
