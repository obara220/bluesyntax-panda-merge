package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dao.MatchEventInfoDao;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.MatchEventInfoDetail;
import com.panda.merge.dto.WarningEventDTO;
import com.panda.merge.exception.Asserts;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Slf4j
@Service
public class MatchEventInfoServiceImpl implements MatchEventInfoService {

    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;

    @Autowired
    private MatchEventInfoDao matchEventInfoDao;

    @Autowired
    public RedisService redisService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Override
    public MatchEventInfo create(MatchEventInfoDTO matchEventInfoDTO, ThirdMatchInfo thirdMatchInfo, ThirdSportTeam thirdSportTeam, Long sportId, String linkId) {
        MatchEventInfo matchEventInfo = new MatchEventInfo();
        BeanUtils.copyProperties(matchEventInfoDTO, matchEventInfo);
        matchEventInfo.setId(UUIdUtils.getId());
        String extraInfo = "null".equalsIgnoreCase(matchEventInfoDTO.getExtrainfo()) ? "" : matchEventInfoDTO.getExtrainfo();
        matchEventInfo.setExtraInfo(extraInfo);
        matchEventInfo.setSourceType(Integer.valueOf(matchEventInfoDTO.getSourceType()));
        matchEventInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchEventInfo.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchEventInfo.setStandardMatchId(thirdMatchInfo.getReferenceId());
        matchEventInfo.setThirdMatchId(thirdMatchInfo.getId());
        matchEventInfo.setThirdTeamId(thirdSportTeam == null ? null : thirdSportTeam.getId());
        matchEventInfo.setStandardTeamId(thirdSportTeam == null ? null : thirdSportTeam.getReferenceId());
        matchEventInfo.setSportId(sportId);
        matchEventInfo.setLinkId(linkId);
        matchEventInfo.setSendData("N");
//        processDeleteEvent(linkId, matchEventInfoDTO, matchEventInfo,null);
        matchEventInfoMapper.insert(matchEventInfo);
        return matchEventInfo;
    }

    @Override
    public MatchEventInfo getItem(String thirdEventId, String dataSourceCode, Integer sourceType, String thirdMatchSourceId, Integer canceled) {
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andThirdEventIdEqualTo(thirdEventId)
                .andDataSourceCodeEqualTo(dataSourceCode)
                .andSourceTypeEqualTo(sourceType)
                .andThirdMatchSourceIdEqualTo(thirdMatchSourceId)
                .andCanceledEqualTo(canceled);
        matchEventInfoExample.setOrderByClause("event_time desc");
        List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
        if(CollectionUtils.isEmpty(matchEventInfos)){
            return null;
        }
        return matchEventInfos.get(0);
    }

    @Override
    public MatchEventInfo getItem(String thirdEventId, String dataSourceCode,String thirdMatchSourceId) {
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andThirdEventIdEqualTo(thirdEventId)
                .andDataSourceCodeEqualTo(dataSourceCode)
                .andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
        matchEventInfoExample.setOrderByClause("event_time desc");
        List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
        if(CollectionUtils.isEmpty(matchEventInfos)){
            return null;
        }
        return matchEventInfos.get(0);
    }

    @Override
    public List<MatchEventInfo> getMatchEventInfoByThird(Long matchPeriodId, String eventCode, String thirdMatchSourceId, String dataSoureCode,Integer canceled) {
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria()
                .andMatchPeriodIdEqualTo(matchPeriodId)
                .andEventCodeEqualTo(eventCode)
                .andThirdMatchSourceIdEqualTo(thirdMatchSourceId)
                .andDataSourceCodeEqualTo(dataSoureCode)
                .andCanceledEqualTo(canceled)
                .andSendDataEqualTo(YesNoEnum.Y.name());
        List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
        return matchEventInfos;
    }

    @Override
    public void processDeleteEvent(String linkId, MatchEventInfoDTO matchEventInfoDTO, MatchEventInfo matchEventInfo, List<MatchEventInfo> matchEventInfoList, Boolean pandaDbIsError, ThirdMatchInfo thirdMatchInfo) {
        if (EventTypeEnum.DELETE_EVENT.name().equalsIgnoreCase(matchEventInfoDTO.getEventCode()) || matchEventInfoDTO.getCanceled() == 1) {
            if (StringUtils.isBlank(matchEventInfoDTO.getExtrainfo())) {
                Asserts.fail("processDeleteEvent,第三方赛事体育删除事件非法,Extrainfo不能为空");
            }
            log.info("linkId=【{}】processDeleteEvent,删除事件入参,{}", linkId, JSON.toJSONString(matchEventInfoDTO));

            MatchEventInfo matchEventInfoLoad = new MatchEventInfo();
            //106533 【日常】【生产】删除事件比进球事件先下发200毫秒，事件并发导致拦截被删除事件失效
            boolean eventDeleteLockFlag = false;
            String deleteEventLock = String.format(ConstantSystem.getDeleteEventLockKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getExtrainfo());
            try {
                eventDeleteLockFlag = redisService.tryLock(deleteEventLock, deleteEventLock, 2, 3);
                //单号 85728 数据入库延迟导致没有查到需要被删除的比分 兼容处理，先查缓存中足球比分事件缓存
                String thirdMatchScoresEventKey = String.format(ConstantSystem.getThirdMatchScoresEventKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getExtrainfo());
                Object redisEventCodeObj = redisService.get(thirdMatchScoresEventKey);
                if(redisEventCodeObj != null){
                    log.info("linkId=【{}】processDeleteEvent,获取到redis中被删除的事件信息,{}", linkId, JSON.toJSONString(matchEventInfoDTO));
                    JSONObject jsonObject = JSON.parseObject(redisEventCodeObj.toString());
                    matchEventInfoLoad.setEventCode(jsonObject.getString("eventCode"));
                    matchEventInfoLoad.setMatchPeriodId(jsonObject.getLong("matchPeriodId"));
                }else{
                    if(!pandaDbIsError){
                        matchEventInfoLoad = getItem(matchEventInfoDTO.getExtrainfo(), matchEventInfoDTO.getDataSourceCode(), Integer.parseInt(matchEventInfoDTO.getSourceType()), matchEventInfoDTO.getThirdMatchSourceId(), 0);
                    }
                }
                //单号：54926 【日常】【生产】R01赛事进球后没有删除  问题兜底（可能需要删除的事件还在本次批量处理的事件列表里面，还未入库，所以数据库查询不到）
                if(matchEventInfoLoad == null && !CollectionUtils.isEmpty(matchEventInfoList)){
                    Map<String, MatchEventInfo> thirdEventId2Item = matchEventInfoList.stream().collect(Collectors.toMap(MatchEventInfo::getThirdEventId, obj -> obj, (oldValue, newValue) -> newValue));
                    matchEventInfoLoad = thirdEventId2Item.get(matchEventInfoDTO.getExtrainfo());
                }
                if (matchEventInfoLoad == null) {
                    //优化单：81636 删除事件处理先于进球事件，导致删除无效,需要缓存删除事件
                    String deleteEventKey = String.format(ConstantSystem.getDeleteEventKey(), matchEventInfoDTO.getDataSourceCode(),matchEventInfoDTO.getThirdMatchSourceId(),matchEventInfoDTO.getExtrainfo());
                    redisService.set(deleteEventKey,matchEventInfoDTO.getExtrainfo(), RedisConfig.REDIS_HOUR_TIME);
                    Asserts.fail("第三方赛事体育删除事件非法"+matchEventInfoDTO.getThirdEventId()+"，暂时缓存被删除的事件ID，方便后续处理，请确认数据是否正确,Extrainfo:"+matchEventInfoDTO.getExtrainfo());
                }
            } finally {
                if(eventDeleteLockFlag){
                    redisService.unLock(deleteEventLock,deleteEventLock);
                }
            }

            //单号：84170 跨阶段删除事件特殊标识
            boolean acrossStagesFlag1 = false; // 删除事件和被删除事件阶段不同
            boolean acrossStagesFlag2 = false; // 删除事件和被删除事件阶段相同,当前三方赛事阶段和被删除事件阶段不同
            if(
                    !matchEventInfoDTO.getMatchPeriodId().equals(matchEventInfoLoad.getMatchPeriodId()) &&
                    BasketBallPeroidEnum.getCrossPeriod().contains(String.valueOf(matchEventInfoDTO.getMatchPeriodId())) &&
                    BasketBallPeroidEnum.getCrossPeriod().contains(String.valueOf(matchEventInfoLoad.getMatchPeriodId()))
            ){
                matchEventInfo.setAddition5(ONE+"");
                acrossStagesFlag1 = true;
            } else {
                if (
                        !Objects.equals(thirdMatchInfo.getMatchPeriod(), matchEventInfo.getMatchPeriodId().toString()) &&
                        BasketBallPeroidEnum.getCrossPeriod().contains(thirdMatchInfo.getMatchPeriod()) &&
                        BasketBallPeroidEnum.getCrossPeriod().contains(String.valueOf(matchEventInfo.getMatchPeriodId()))
                ) {
                    acrossStagesFlag2 = true;
                }
            }

            //103715 转优化处理 当事件源下发跨阶段修改比分或删除比分时，将在嘀嘀群组预警
            try {
                boolean acrossWarnFlag = StandardSportTypeEnum.Basketball.code.equals(matchEventInfo.getSportId()) &&
                        DataSourceCodeEnum.getCrossPeriodScoreChangedCode().contains(matchEventInfo.getDataSourceCode()) &&
                        matchEventInfo.getStandardMatchId() != null && matchEventInfo.getStandardMatchId() != 0L &&
                        EventCodeEnum.SCORE_CHANGE.code.equals(matchEventInfoLoad.getEventCode());

                if (  (acrossStagesFlag1 || acrossStagesFlag2) && acrossWarnFlag) {
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchEventInfo.getStandardMatchId());
                    if (standardMatchInfo != null) {
                        WarningEventDTO warningEventDTO = new WarningEventDTO();
                        warningEventDTO.setLinkId(linkId);
                        warningEventDTO.setWarningType(WarningTypeEnum.CROSS_PERIOD_SCORE_CHANGED);

                        Map<String, Object> warnContext = new HashMap<>();
                        warnContext.put("dataSourceCode", DataSourceCodeEnum.getDataSourceCodeEnumByCode(matchEventInfo.getDataSourceCode()).getMaskedCode());
                        warnContext.put("homeAwayInfo", standardMatchInfo.getHomeAwayInfo());
                        warnContext.put("matchManageId", standardMatchInfo.getMatchManageId());

                        BasketBallPeroidEnum curPeriod = acrossStagesFlag1 ? BasketBallPeroidEnum.getEnum(matchEventInfo.getMatchPeriodId().intValue()) : BasketBallPeroidEnum.getEnum(Integer.parseInt(thirdMatchInfo.getMatchPeriod())) ;
                        BasketBallPeroidEnum originalPeriod = BasketBallPeroidEnum.getEnum(matchEventInfoLoad.getMatchPeriodId().intValue());
                        String matchPeriodId = acrossStagesFlag1 ? String.valueOf(matchEventInfo.getMatchPeriodId()) : thirdMatchInfo.getMatchPeriod();
                        warnContext.put("curMatchPeriod", curPeriod != null ? curPeriod.getValueZh() : matchPeriodId);
                        warnContext.put("originalMatchPeriod", originalPeriod != null ? originalPeriod.getValueZh() : matchEventInfoLoad.getMatchPeriodId());
                        warnContext.put("reason","赛事跨阶段下发删除比分");
                        warningEventDTO.setContexts(Lists.newArrayList(warnContext));

                        warningService.warn(warningEventDTO);
                    }

                }
            } catch (Exception e) {
                log.error("篮球跨阶段修改比分预警异常, linkId={}", linkId, e);
            }

            matchEventInfo.setEventCode(matchEventInfoLoad.getEventCode());
            matchEventInfo.setCanceled(CanceledEnum.Canceled.value);
        }
    }

    @Override
    public List<MatchEventInfo> getItemByStandardMatchIdAndDataSoureCode(Long standardMatchInfo, String dataSoureCode) {
        return matchEventInfoDao.getItemByStandardMatchIdAndDataSoureCode(standardMatchInfo, dataSoureCode);
    }

    @Override
    public List<MatchEventInfo> getItemByThirdMatchIdAndSendData(Long thirdMatchId,String dataSource,String sendData) {
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId)
                .andDataSourceCodeEqualTo(dataSource).andSendDataEqualTo(sendData);
        matchEventInfoExample.setOrderByClause("event_time");
        return matchEventInfoMapper.selectByExample(matchEventInfoExample);
    }

    @Override
    public MatchEventInfo getMatchEventInfo(Long thirdMatchId,String dataSource,String eventCode) {
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId)
                .andDataSourceCodeEqualTo(dataSource)
                .andEventCodeEqualTo(eventCode);
        matchEventInfoExample.setOrderByClause("event_time desc");
        List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
        if(CollectionUtils.isEmpty(matchEventInfos)){
            return null;
        }
        return matchEventInfos.get(0);
    }


    @Override
    public List<MatchEventInfo> getItemByThirdMatchIdAndDataSoureCode(Long thirdMatchId,String dataSource) {
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceCodeEqualTo(dataSource);
        matchEventInfoExample.setOrderByClause("event_time");
        return matchEventInfoMapper.selectByExample(matchEventInfoExample);
    }


    @Override
    @Async("EventInfoDbThreadPool")
    public void save(MatchEventInfo matchEventInfo) {
        try{
            matchEventInfoMapper.insert(matchEventInfo);
            log.info("linkId=【{}】MatchEventInfo入库成功，三方赛事原始id:{}", matchEventInfo.getLinkId(),matchEventInfo.getThirdMatchSourceId());
        }catch (DataAccessException e) {
            log.error("linkId=【"+matchEventInfo.getLinkId()+"】save三方盘中事件入库异常0,matchEventInfo："+ JSON.toJSONString(matchEventInfo) +",Exception:", e);
        }catch (Exception e){
            log.error("linkId=【"+matchEventInfo.getLinkId()+"】save三方盘中事件入库异常,matchEventInfo："+ JSON.toJSONString(matchEventInfo) +",Exception:", e);
        }
    }

    @Override
//    @Async("EventInfoThreadPool")
    public void saveBatch(List<MatchEventInfo> matchEventInfoList,String linkId) {
        //因为涉及到根据dataSourceCode分表，所以需要按照数据源编码分组入库
        Map<String, List<MatchEventInfo>> dataSourceCode2List = matchEventInfoList.stream().collect(Collectors.groupingBy(obj -> obj.getDataSourceCode()));
        for (String dataSourceCode: dataSourceCode2List.keySet()) {
            MatchEventInfoDetail addMatchEventInfoDetail = new MatchEventInfoDetail();
            addMatchEventInfoDetail.setTableName("match_event_info_"+dataSourceCode.toLowerCase(Locale.ROOT));
            List<MatchEventInfo> data = new ArrayList<>();
            for (MatchEventInfo item: dataSourceCode2List.get(dataSourceCode)) {
                //2S内不允许重复入库
                String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:DbLock:%s", item.getId());
                //如果是新增,避免同时新增出现唯一索引异常
                if(redisService.tryLockOnce(lockKey,lockKey,REDIS_FIVE_MINS_TIME)){
                    data.add(item);
                }else{
                    log.info("linkId={},saveBatch,MatchEventInfo,数据源编码={}，源事件ID={},2S内不允许重复入库",item.getLinkId(),item.getDataSourceCode(),item.getThirdEventId());
                }
            }
            try{
                if(!CollectionUtils.isEmpty(data)){
                    for (MatchEventInfo item : data) {
                        String curlinkId = item.getLinkId();
                        if (curlinkId != null && curlinkId.length() > 60) {
                            String subLinkId = curlinkId.substring(0, 60);
                            item.setLinkId(subLinkId);
                            log.info("saveBatch MatchEventInfo linkId 截取, 原linkId={},截取后={}", curlinkId, subLinkId);
                        }
                    }
                    addMatchEventInfoDetail.setData(data);
                    matchEventInfoDao.saveBatch(addMatchEventInfoDetail);
                }
            }catch (DataAccessException e) {
                log.error("linkId="+linkId+",saveBatch,MatchEventInfo,批量入库事件异常0,Exception:",e);
            }catch (Exception e){
                log.error("linkId="+linkId+",saveBatch,MatchEventInfo,批量入库事件异常,Exception:",e);
            }
        }
    }

    @Override
    @Async("EventInfoDbThreadPool")
    public void upOrSaveBatch(List<MatchEventInfo> matchEventInfoList,String linkId){
        //创建时间为空表示需要新增，修改时间为空表示需要修改
        List<MatchEventInfo> insertList = new ArrayList<>();
        List<MatchEventInfo> updateList = new ArrayList<>();
        for (MatchEventInfo matchEventInfo: matchEventInfoList) {
            //表示需要新增
            if(matchEventInfo.getCreateTime() == null){
                matchEventInfo.setCreateTime(System.currentTimeMillis());
                matchEventInfo.setModifyTime(System.currentTimeMillis());
                insertList.add(matchEventInfo);
            }else{
                matchEventInfo.setModifyTime(System.currentTimeMillis());
                updateList.add(matchEventInfo);
            }
        }
        if(!CollectionUtils.isEmpty(insertList)){
            try{
                saveBatch(insertList,linkId);
            }catch (DataAccessException e) {
                log.error("linkId=【"+linkId+"】upOrSaveBatch三方盘中事件saveBatch入库异常0,matchEventInfo："+ JSON.toJSONString(insertList) +",Exception:", e);
            }catch (Exception e){
                log.error("linkId=【"+linkId+"】upOrSaveBatch三方盘中事件saveBatch入库异常,matchEventInfo："+ JSON.toJSONString(insertList) +",Exception:", e);
            }
        }
        if(!CollectionUtils.isEmpty(updateList)){
            try{
                updateBatch(updateList);
            }catch (Exception e){
                log.error("linkId=【"+linkId+"】upOrSaveBatch三方盘中事件updateBatch入库异常,matchEventInfo："+ JSON.toJSONString(updateList) +",Exception:", e);
            }
        }
    }

    @Override
//    @Async("EventInfoDbThreadPool")
    public void updateBatch(List<MatchEventInfo> matchEventInfoList) {
        long currentTimeMillis = System.currentTimeMillis();
        List<MatchEventInfoDetail> upMatchEventInfoList = new ArrayList<>();
        for (MatchEventInfo item: matchEventInfoList) {
            MatchEventInfoDetail upMatchEventInfo = new MatchEventInfoDetail();
            BeanUtils.copyProperties(item, upMatchEventInfo);
            upMatchEventInfo.setTableName("match_event_info_"+item.getDataSourceCode().toLowerCase(Locale.ROOT));
            upMatchEventInfoList.add(upMatchEventInfo);
        }
        if(!CollectionUtils.isEmpty(upMatchEventInfoList)){
            MatchEventInfoDetail item = upMatchEventInfoList.get(0);
            matchEventInfoDao.updateBatch(upMatchEventInfoList);
            log.info("linkId=【{}】updateBatch 批量修改成功，三方赛事原始id:{}，耗时={}", item.getLinkId(),item.getThirdMatchSourceId(),System.currentTimeMillis() - currentTimeMillis);
        }

    }

    @Override
//    @Async("EventInfoDbThreadPool")
    public void updateById(MatchEventInfo item) {
        MatchEventInfoExample example = new MatchEventInfoExample();
        //分表字段，在修改的时候必须带上
        example.createCriteria().andIdEqualTo(item.getId()).andDataSourceCodeEqualTo(item.getDataSourceCode());
        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        try{
            matchEventInfoMapper.updateByExample(item,example);
            log.info("linkId=【{}】MatchEventInfo修改成功，三方赛事原始id:{}", item.getLinkId(),item.getThirdMatchSourceId());
        }catch (Exception e){
            log.error("linkId=【"+item.getLinkId()+"】updateById三方盘中事件修改异常,matchEventInfo："+ JSON.toJSONString(item) +",Exception:", e);
        }
    }

    @Override
    public List<MatchEventInfo> getEventHistoryByEndEvent(MatchEventInfo matchEventInfo) {
        List<String> eventCodes=new ArrayList<>();
        eventCodes.add("goal");eventCodes.add("corner");eventCodes.add("yellow_card");eventCodes.add("red_card");
        MatchEventInfoExample example =new MatchEventInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(matchEventInfo.getThirdMatchId()).andDataSourceCodeEqualTo(matchEventInfo.getDataSourceCode())
                .andEventCodeIn(eventCodes).andSourceTypeEqualTo(1);
        example.setOrderByClause("event_time");
        List<MatchEventInfo> list= matchEventInfoMapper.selectByExample(example);
        return list;
    }

    @Override
    public MatchEventInfo getMatchEventInfo(MatchEventInfo matchEventInfo) {
        MatchEventInfoExample example = new MatchEventInfoExample();
        example.createCriteria().andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId()).andDataSourceCodeEqualTo(matchEventInfo.getDataSourceCode())
                .andHomeAwayEqualTo(matchEventInfo.getHomeAway()).andEventCodeEqualTo(matchEventInfo.getEventCode());
        example.setOrderByClause("create_time desc");
        List<MatchEventInfo> list = matchEventInfoMapper.selectByExample(example);
        return !CollectionUtils.isEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<MatchEventInfo> getEventHistoryByEventTime(MatchEventInfoDetail matchEventInfo){
        return matchEventInfoDao.getEventHistoryByEventTime(matchEventInfo);
    }


    @Override
    public List<MatchEventInfo> getMatchEvenIdsByDayDateTime(MatchEventInfoDetail matchEventInfo){
        return matchEventInfoDao.getMatchEvenIdsByDayDateTime(matchEventInfo);
    }

    @Override
    public Integer deleteMatchEvenIdsByDayDateTime(MatchEventInfoDetail matchEventInfo){
        return matchEventInfoDao.deleteMatchEvenIdsByDayDateTime(matchEventInfo);
    }


    @Override
    public void matchEvent2StandardMatch(String linkId, ThirdMatchInfo thirdMatchInfo){
        long currentTimeMillis = System.currentTimeMillis();
        MatchEventInfoDetail item = new MatchEventInfoDetail();
        try{
             item.setTableName("match_event_info_"+thirdMatchInfo.getDataSourceCode().toLowerCase(Locale.ROOT));
            item.setStandardMatchId(thirdMatchInfo.getReferenceId());
            item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            item.setLinkId(linkId);
            item.setThirdMatchId(thirdMatchInfo.getId());
            item.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
            item.setDataSourceCode(thirdMatchInfo.getDataSourceCode());

            matchEventInfoDao.matchEvent2StandardMatch(item);
            log.info("linkId=【{}】matchEvent2StandardMatch 修改成功，三方赛事原始id:{},耗时={}", item.getLinkId(),item.getThirdMatchSourceId(),System.currentTimeMillis() - currentTimeMillis);
        }catch (Exception e){
            log.error("linkId=【"+item.getLinkId()+"】matchEvent2StandardMatch 三方盘中事件修改异常,matchEventInfo："+ JSON.toJSONString(item) +",Exception:", e);
        }
    }

    @Override
    public void matchEvent2StandardEvent(String linkId,ThirdMatchInfo thirdMatchInfo) {
        MatchEventInfoDetail item = new MatchEventInfoDetail();
        try{
            item.setTableName("match_event_info_"+thirdMatchInfo.getDataSourceCode().toLowerCase(Locale.ROOT));
            item.setStandardMatchId(thirdMatchInfo.getReferenceId());
            item.setSendData(YesNoEnum.Y.name());
            item.setLinkId(linkId);
            item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            item.setThirdMatchId(thirdMatchInfo.getId());
            item.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
            item.setDataSourceCode(thirdMatchInfo.getDataSourceCode());

            matchEventInfoDao.matchEvent2StandardEvent(item);
            log.info("linkId=【{}】matchEvent2StandardEvent 修改成功，三方赛事原始id:{}", item.getLinkId(),item.getThirdMatchSourceId());
        }catch (Exception e){
            log.error("linkId=【"+linkId+"】matchEvent2StandardEvent 三方盘中事件修改异常,matchEventInfo："+ JSON.toJSONString(item) +",Exception:", e);
        }
    }


}
