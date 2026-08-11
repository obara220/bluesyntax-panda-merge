package com.panda.merge.rocketmq.processor;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.exception.ApiException;
import com.panda.merge.exception.Asserts;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.common.TransactionalProcessor;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.ThirdMatchInfoProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 三方数据源投递的赛事数据处理 <br>
 * @author        tell
 * @Date          2020年9月5日10:05:33
 * */
@Slf4j
@Validated
@Component
public class ThirdMatchInfoProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;
    @Autowired
    private ThirdSportTeamService thirdSportTeamService;
    @Autowired
    private StandardSportTeamService standardSportTeamService;
    @Autowired
    private StandardMatchTeamRelationService standardMatchTeamRelationService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private TransactionalProcessor transactionalProcessor;
    @Autowired
    private ThirdMatchInfoProducer thirdMatchInfoProducer;

    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;
    @Resource
    private StandardSportRegionService standardSportRegionService;
    @Resource
    private ThirdSportTournamentProcessor thirdSportTournamentProcessor;

    /** 自动开售数据源*/
    @NacosValue(value = "${sale.auto.sources:0}", autoRefreshed = true)
    private String autoSaleSources;

    /** 测试环境专用，自动开售的赛种*/
    @NacosValue(value = "${sale.auto.sport.ids:0}", autoRefreshed = true)
    private String autoSaleSportIds;


    /**
     * 处理赛事数据
     * @param request  三方数据源赛事入参
     * */
    @Async("getMatchThreadPool")
    @ExceptionHelper
    public Response processMatchData(@Valid Request<List<ThirdMatchInfoDTO>> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try{
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【::"+request.getLinkId()+"::】第三方赛事数据接收开始...");
            //简单校验,主要判断linkId和Data不能为空,条数上限为1
            simpleValidateParam(request,THIRD_MATCH_INFO_API,1);
            //获取赛事参数列表
            List<ThirdMatchInfoDTO> thirdMatchInfoDTOList = request.getData();
            //获取当前第三方数据源
            Set<String> dataSourceCodes = thirdMatchInfoDTOList.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
            /** 01 校验dataSourceCode是否合法*/
            DataSource dataSource = simpleValidateDataSourceCodes(request, dataSourceCodes);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方赛事数据处理开始");
            //本次传入的三方运动类型列表
            Set<String> thirdSportIds = thirdMatchInfoDTOList.stream().map(obj -> String.valueOf(obj.getSportId())).collect(Collectors.toSet());
            /** 02 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联*/
            Map<String, Long> thirdSportId2referenceId = validateSportIds(dataSource.getCode(), thirdSportIds);
            //数据处理入库
            process2Database(request.getLinkId(),dataSource,thirdMatchInfoDTOList,thirdSportId2referenceId);
        }catch (Exception e){
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        }finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(),nonrealtime,THIRD_MATCH_INFO_API,"三方赛事信息接入",
                            stopWatch.getTotalTimeMillis(),Integer.parseInt(String.valueOf(response.getCode())),response.getMsg())
            );
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_MATCH_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方赛事数据处理结束,返回结果 ：{}" ,JSON.toJSONString(response));
        }
        return response;
    }


    /**
     * 赛事数据处理入库
     * @param linkId                     线路ID
     * @param dataSource                 本次传入数据来源
     * @param thirdMatchInfoDTOList      本次传入的赛事列表
     * @param thirdSportId2referenceId   三方数据源运动类型和标准运动类型关系
     * */
    private void process2Database(String linkId,DataSource dataSource, List<ThirdMatchInfoDTO> thirdMatchInfoDTOList,Map<String, Long> thirdSportId2referenceId) {
        /** 03 统一处理赛事，球队，赛事球队关系，球队多语言，场地信息多语言，筛选出需要修改或者新增的数据*/
        //需要新增或修改的区域
        Map<String, ThirdSportRegion> upThirdRegionId2Obj = new LinkedHashMap<>();
        for (ThirdMatchInfoDTO thirdMatchInfoDTO: thirdMatchInfoDTOList) {
            //获取标准运动类型ID
            final Long sportId = thirdSportId2referenceId.get(String.valueOf(thirdMatchInfoDTO.getSportId()));
            if(null == sportId || sportId == 0){
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource+" ::"+linkId+"::】第三方赛事{}运动类型不能为空或0，当前sportId：{}",thirdMatchInfoDTO.getThirdMatchSourceId(),sportId);
                continue;
            }
            //创建赛事redis锁
            String matchLockKey = thirdMatchInfoDTO.getDataSourceCode() + sportId + thirdMatchInfoDTO.getThirdMatchSourceId();
            boolean matchLockFlag = false;
            try{
                matchLockFlag = redisService.tryLock(matchLockKey, matchLockKey, 5, 3);
                //校验赛事中联赛是否存在
                ThirdSportTournament oldThirdSportTournament = validateThirdSportTournament(dataSource, sportId, thirdMatchInfoDTO.getSourceTournamentId());
                //校验赛事信息
                validateThirdMatchInfoDTO(thirdMatchInfoDTO);
                //根据 标准运动类型+三方数据源赛事ID 获取三方库中赛事信息
                ThirdMatchInfoDetail oldThirdMatchInfo = thirdMatchInfoService.getItemDetail(dataSource.getCode(),thirdMatchInfoDTO.getThirdMatchSourceId());
                //如果三方赛事不为空，三方赛事中赛事球队关系为空，则重新查询三方赛事球队关系（容错处理）
                if(null != oldThirdMatchInfo){
                    if(null == oldThirdMatchInfo.getMtRelationList() || oldThirdMatchInfo.getMtRelationList().size() != TWO){
                        Map<String, ThirdMatchTeamRelation> matchIdAndPosition2Relation = thirdMatchTeamRelationService.getMatchIdAndPosition2ItemByMatchId(oldThirdMatchInfo.getId());
                        if(!CollectionUtils.isEmpty(matchIdAndPosition2Relation)){
                            oldThirdMatchInfo.setMtRelationList(Lists.newArrayList(matchIdAndPosition2Relation.values()));
                        }
                    }
                    //获取缓存中的三方赛事信息
                    ThirdMatchInfo item = thirdMatchInfoService.getItem(dataSource.getCode(), thirdMatchInfoDTO.getThirdMatchSourceId());
                    if(null != item){
                        //忽略空值拷贝（避免ThirdMatchInfoDetail中缓存未被刷新，重新赋值最新赛事缓存信息）
                        BeanUtil.copyProperties(item,oldThirdMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                    }
                }
                //设值赛事信息
                ThirdMatchInfoDetail thirdMatchInfo = new ThirdMatchInfoDetail();
                BeanUtils.copyProperties(thirdMatchInfoDTO, thirdMatchInfo);
                thirdMatchInfo.setSportId(sportId);
                //获取三方区域信息
                ThirdSportRegion odlThirdSportRegion = getThirdSportRegionByUniqueStr(dataSource.getCode(),thirdMatchInfo.getSportId(),thirdMatchInfoDTO.getThirdRegionId());
                if(!Objects.isNull(odlThirdSportRegion)){
                    thirdMatchInfo.setRegionId(odlThirdSportRegion.getReferenceId());
                }
                //转换赛事状态
                if(StringUtils.isNotBlank(thirdMatchInfoDTO.getMatchStatus())){
                    thirdMatchInfo.setMatchStatus(Integer.valueOf(thirdMatchInfoDTO.getMatchStatus()));
                }
                //设值三方库联赛ID
                thirdMatchInfo.setTournamentId(oldThirdSportTournament.getId());
                //设置赛事的类型
                thirdMatchInfo.setMatchType(thirdMatchInfoDTO.getMatchType());
                //是否新增三方赛事标识
                Boolean isNewThirdMatch = Boolean.FALSE;
                //不存在新增的赛事
                if(Objects.isNull(oldThirdMatchInfo)){
                    thirdMatchInfo.setId(UUIdUtils.getId());
                    thirdMatchInfo.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    isNewThirdMatch = Boolean.TRUE;
                    log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次需要新增的三方赛事信息:{}",thirdMatchInfoDTO.getThirdMatchSourceId());
                }else{
                    thirdMatchInfo.setId(oldThirdMatchInfo.getId());
                    thirdMatchInfo.setMatchPositionNameCode(oldThirdMatchInfo.getMatchPositionNameCode());
                    thirdMatchInfo.setCreateTime(null);
                    //场地类型 默认值100
                    if(Objects.isNull(thirdMatchInfo.getSiteType())){
                        thirdMatchInfo.setSiteType(oldThirdMatchInfo.getSiteType());
                    }
                    //处理需要修改的赛事业务逻辑
                    processUpdateMatchInfo(dataSource.getCode(),thirdMatchInfo,oldThirdMatchInfo ,linkId);
                    //如果赛事状态为1(滚球),2(暂停),10(中断) ，传下来的开赛时间比之前的开赛时间小，就不需要更新赛事开赛时间
                    if (MatchStatusEnum.Live.value.equals(thirdMatchInfo.getMatchStatus()) || MatchStatusEnum.Suspended.value.equals(thirdMatchInfo.getMatchStatus())
                            || MatchStatusEnum.Interrupted.value.equals(thirdMatchInfo.getMatchStatus())) {
                        if(oldThirdMatchInfo.getBeginTime() > thirdMatchInfo.getBeginTime()){
                            thirdMatchInfo.setBeginTime(oldThirdMatchInfo.getBeginTime());
                        }
                    }
                }
                //=============================球队信息开始=============================
                /** 球队，球队多语言*/
                /*  球队信息处理逻辑 返回 单个赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询 主队在前，客队在后
                 *  包含三方联赛信息变更后，三方赛事、标准赛事对应的字段(联赛是否变更)处理
                 */
                process2TeamData(linkId,thirdMatchInfoDTO, thirdMatchInfo,oldThirdMatchInfo, dataSource, upThirdRegionId2Obj);
                //=============================球队信息结束=============================
                //=============================场地信息开始=============================
                //设值场地多语言
                List<I18nItemDTO> psitionNameDtoList = thirdMatchInfoDTO.getMatchPositionNameList();
                if (!CollectionUtils.isEmpty(psitionNameDtoList)) {
                    List<LanguageInternation> psitionNameList = new LinkedList<>();
                    //处理场地多语言国际化 返回nameCode
                    thirdMatchInfo.setMatchPositionNameCode(processLanguageNameCode(psitionNameList, psitionNameDtoList, dataSource, thirdMatchInfo.getMatchPositionNameCode(),linkId,false));
                    thirdMatchInfo.setPsitionNameList(psitionNameList);
                }
                //=============================场地信息结束=============================
                /**统一入库赛事，赛事球队关系，场地信息多语言入库*/
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次处理的三方赛事信息:{},开赛时间={},liveEventSource={}",thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getBeginTime(), thirdMatchInfoDTO.getLiveEventSource());
                //108326 【日常】【生产】L01篮球，三方赛事结束了，阶段却显示0
                try {
                    Long.valueOf(thirdMatchInfo.getMatchPeriod());
                } catch (Exception e) {
                    log.info("【linkId="+linkId+",源赛事ID={}】,三方赛事阶段转换异常,原始值:{},转换为'0'", thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getMatchPeriod());
                    thirdMatchInfo.setMatchPeriod("0");
                }
                if (DataSourceCodeEnum.LS.getCode().equals(thirdMatchInfo.getDataSourceCode())) {
                    if ("0".equals(thirdMatchInfo.getMatchPeriod()) && oldThirdMatchInfo != null && StringUtils.isNotBlank(oldThirdMatchInfo.getMatchPeriod())) {
                        thirdMatchInfo.setMatchPeriod(null);
                    }
                }
                transactionalProcessor.saveOrupdateThirdMatch(linkId, thirdMatchInfo, isNewThirdMatch, thirdMatchInfoDTO.getSportId());
                thirdMatchInfoProducer.pushQueueMatch(linkId,dataSource,thirdMatchInfo);
                //如果为0则不开启自动开售
                if(!String.valueOf(ZERO).equals(autoSaleSources)){
                    List<String> autoSaleSourceList = Arrays.asList(autoSaleSources.split(","));
                    /** BE默认自动开售 42835 单 ，F01电子赛事自动开售*/
                    if(TWO.equals(thirdMatchInfo.getMatchType()) && autoSaleSourceList.contains(thirdMatchInfo.getDataSourceCode())) {
                        thirdMatchInfoProducer.pushReplayMatch(linkId, dataSource, thirdMatchInfo);
                        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】成功发送重播赛事自动开售处理");
                    }else{
                        /** ---------------测试环境专用开始，需要自动开售的赛种---------------*/
                        List<String> autoSaleSportIdList = Arrays.asList(autoSaleSportIds.split(","));
                        if(autoSaleSourceList.contains(thirdMatchInfo.getDataSourceCode()) && autoSaleSportIdList.contains(thirdMatchInfo.getSportId()+"")) {
                            thirdMatchInfoDTO.setReplayMatch(ConstantSystem.ONE);
                        }
                        /** ---------------测试环境专用结束，需要自动开售的赛种---------------*/
                        /** 重播自动开售*/
                        if(ConstantSystem.ONE.equals(thirdMatchInfoDTO.getReplayMatch())) {
                            thirdMatchInfoProducer.pushReplayMatch(linkId, dataSource, thirdMatchInfo);
                            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】成功发送重播赛事自动开售处理");
                        }
                    }
                }
                //3803【比分网】比分网后台-联赛管理 推送三方赛事
                if (StandardSportTypeEnum.FootBall.getCode().equals(sportId) && MatchTypeEnum.NORMAL.getCode().equals(thirdMatchInfo.getMatchType())) {
                    if (CollectionUtils.isEmpty(thirdMatchInfo.getPsitionNameList()) && thirdMatchInfo.getMatchPositionNameCode()!=null && thirdMatchInfo.getMatchPositionNameCode()!=0) {
                        Map<String, LanguageInternation> languageInternationMap = languageInternationService.getLanguageType2Item(dataSource.getCode(), thirdMatchInfo.getMatchPositionNameCode());
                        if (MapUtil.isNotEmpty(languageInternationMap)) {
                            thirdMatchInfo.setPsitionNameList(new ArrayList<>(languageInternationMap.values()));
                        }
                    }
                    if (odlThirdSportRegion != null) {
                        thirdMatchInfo.setThirdSportRegion(odlThirdSportRegion);

                        StandardSportRegion oldStandardSportRegion = standardSportRegionService.getStandardSportRegion(odlThirdSportRegion.getReferenceId());
                        if (oldStandardSportRegion!=null) {
                            StandardSportRegionDetail standardSportRegionDetail = new StandardSportRegionDetail();
                            BeanUtils.copyProperties(oldStandardSportRegion,standardSportRegionDetail);
                            thirdMatchInfo.setStandardSportRegion(standardSportRegionDetail);
                            if (oldStandardSportRegion != null && oldStandardSportRegion.getNameCode() != null && oldStandardSportRegion.getNameCode() != 0) {
                                Map<String, LanguageInternation> languageInternationMap = languageInternationService.getLanguageType2Item(null, oldStandardSportRegion.getNameCode());
                                if (MapUtil.isNotEmpty(languageInternationMap)) {
                                    standardSportRegionDetail.setIl8nNameList(new ArrayList<>(languageInternationMap.values()));
                                }
                            }
                        }

                    }
                    if (CollectionUtil.isNotEmpty(thirdMatchInfo.getTeamList())) {
                        for (ThirdSportTeamDetail thirdSportTeamDetail : thirdMatchInfo.getTeamList()) {
                            if (thirdSportTeamDetail.getNameCode() != null && thirdSportTeamDetail.getNameCode() != 0) {
                                Map<String, LanguageInternation> languageInternationMap = languageInternationService.getLanguageType2Item(thirdSportTeamDetail.getDataSourceCode(), thirdSportTeamDetail.getNameCode());
                                if (MapUtil.isNotEmpty(languageInternationMap)) {
                                    thirdSportTeamDetail.setIl8nNameList(new ArrayList<>(languageInternationMap.values()));
                                }
                            }
                        }
                    }
                    String tournamentLockKey = dataSource.getCode() + sportId +thirdMatchInfoDTO.getSourceTournamentId();
                    boolean tournamentLockFlag = false;
                    try {
                        tournamentLockFlag = redisService.tryLock(tournamentLockKey,tournamentLockKey,5,3);
                        ThirdSportTournament thirdSportTournament = thirdSportTournamentService.getOneItem(dataSource.getCode(), sportId, thirdMatchInfoDTO.getSourceTournamentId());
                        thirdSportTournamentProcessor.pushThirdTournamentPLS(linkId,dataSource,sportId,thirdSportTournament);
                    } finally {
                        if(tournamentLockFlag){
                            redisService.unLock(tournamentLockKey,tournamentLockKey);
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        log.error("::" + linkId + "::下发赛事前先下发联赛延迟500毫秒 异常！Exception:", e);
                    }
                    thirdMatchInfoProducer.pushThirdMatchPLS(linkId,dataSource,thirdMatchInfo);
                }
            }finally {
                if(matchLockFlag){
                    //释放赛事redis锁
                    redisService.unLock(matchLockKey,matchLockKey);
                }
            }
        }
        /** 区域信息入库*/
        for (String key: upThirdRegionId2Obj.keySet()) {
            thirdSportRegionService.saveOrupdate(upThirdRegionId2Obj.get(key));
        }
    }

    /**
     * 处理球队相关信息（包含联赛信息是否变更处理）
     * @param   thirdMatchInfoDTO           传入三方赛事信息
     * @param   thirdMatchInfo              需要更新的三方赛事信息
     * @param   dataSource                  数据来源信息
     * @param   upThirdRegionId2Obj         需要更新的区域集合
     * */
    private Map<String,Map<String, String>> process2TeamData(String linkId,ThirdMatchInfoDTO thirdMatchInfoDTO,ThirdMatchInfoDetail thirdMatchInfo,ThirdMatchInfoDetail oldThirdMatchInfo,DataSource dataSource,
                                                             Map<String, ThirdSportRegion> upThirdRegionId2Obj){
        StandardMatchInfo standardMatchInfo = null;
        if ( null != oldThirdMatchInfo && null != oldThirdMatchInfo.getReferenceId() && !Long.valueOf(ZERO).equals(oldThirdMatchInfo.getReferenceId()) ) {
            standardMatchInfo = standardMatchInfoService.getItem(oldThirdMatchInfo.getReferenceId());
        }
        //单个赛事赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询 主队在前，客队在后
        Map<String,Map<String, String>> languageType2Text = new LinkedHashMap<>(2);
        //球队名称是否发生改变（默认否）
        boolean teamNameFlag = false;
        //当前赛事库中对应的数据源主客队ID
        String oldHomeTeamId = null;
        String oldAwayTeamId = null;
        //当前赛事传入的数据源主客队ID
        String parHomeTeamId = null;
        String parAwayTeamId = null;


        //93166 三方球队变更未更新teamName
        //当前赛事库中对应的数据源主客队ID
        String oldThirdHomeTeamId = null;
        String oldThirdAwayTeamId = null;
        //当前赛事传入的数据源主客队ID
        String parThirdHomeTeamId = null;
        String parThirdAwayTeamId = null;
        //当前传入赛事下球队列表 sportIdAndMacthId2Match
        for (ThirdMatchTeamDTO thirdMatchTeamDTO: thirdMatchInfoDTO.getMatchTeamList()) {
            //创建球队redis唯一key
            String teamLockKey = dataSource.getCode() + thirdMatchInfo.getSportId() + thirdMatchTeamDTO.getThirdTeamId();
            boolean teamLockFlag = false;
            try{
                teamLockFlag = redisService.tryLock(teamLockKey,teamLockKey, 5,3);
                //赛事球队关系
                ThirdMatchTeamRelationDTO thirdMatchTeamRelationDTO = thirdMatchTeamDTO.getMatchTeamRelation();
                //home,away,other
                String matchPosition = thirdMatchTeamRelationDTO.getMatchPosition().toLowerCase();
                //============球队区域开始=======================
                //获取并设值需要编辑的球队区域
                ThirdSportRegion thirdTeamSportRegion = getThirdSportRegion(upThirdRegionId2Obj,dataSource.getCode(), thirdMatchInfo.getSportId(), thirdMatchTeamDTO.getCountryId(), thirdMatchTeamDTO.getCountryName());
                //============球队区域结束=======================
                //获取三方库中球队信息
                ThirdSportTeam oldThirdSportTeam = thirdSportTeamService.getOneItem(dataSource.getCode(),thirdMatchInfo.getSportId(),thirdMatchTeamDTO.getThirdTeamId());
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】数据源球队ID:{}获取到的三方球队信息:{}",thirdMatchTeamDTO.getThirdTeamId(),null != oldThirdSportTeam);
                //库中赛事球队关系
                ThirdMatchTeamRelation oldThirdMatchTeamRelation = null;
                //是否需要修改标准赛事球队关系
                Boolean upStandardMatchTeamRelationFlag = false;
                //是否新增标准球队
                Boolean addStandardTeamFlag = false;
                //变更已经存在的赛事球队ID
                if(null != oldThirdMatchInfo){
                    //获取库中当前球队位置的赛事球队关系
                    List<ThirdMatchTeamRelation> mtRelationList = oldThirdMatchInfo.getMtRelationList();
                    if(!CollectionUtils.isEmpty(mtRelationList)){
                        oldThirdMatchTeamRelation = mtRelationList.stream().filter(obj -> obj.getMatchPosition().equals(matchPosition)).collect(Collectors.toList()).get(0);
                    }
                    //如果该赛事关联了标准赛事并修改球队id的操作
                    if(null != oldThirdMatchTeamRelation && null != oldThirdMatchInfo.getReferenceId() && !Long.valueOf(ZERO).equals(oldThirdMatchInfo.getReferenceId())){
                        ThirdSportTeam oldTeam = thirdSportTeamService.getItem(oldThirdMatchTeamRelation.getTeamId());
                        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】" +
                                "三方球队ID:{}获取到的三方球队信息:{}",oldThirdMatchTeamRelation.getTeamId(),null != oldTeam ? oldTeam.getThirdTeamSourceId():null);
                        if(null != oldTeam && !thirdMatchTeamDTO.getThirdTeamId().equals(oldTeam.getThirdTeamSourceId())){
                            if(HOME.equals(matchPosition)){
                                oldHomeTeamId = oldTeam.getThirdTeamSourceId();
                                parHomeTeamId = thirdMatchTeamDTO.getThirdTeamId();
                            }
                            if(AWAY.equals(matchPosition)){
                                oldAwayTeamId = oldTeam.getThirdTeamSourceId();
                                parAwayTeamId = thirdMatchTeamDTO.getThirdTeamId();
                            }
                            //刷新缓存
                            standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(oldThirdMatchInfo.getReferenceId());
                            //根据主数据源判断
                            if(null == standardMatchInfo){
                                throw new ApiException("未找到三方赛事ID : "+oldThirdMatchInfo.getId()+"对应的标准赛事ID : "+oldThirdMatchInfo.getReferenceId()+"，请检查！");
                            }else{
                                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】球队是否发生改变信息，三方 VS 标准 {} : {},MatchManageId:{}"
                                        ,oldThirdMatchInfo.getDataSourceCode(),standardMatchInfo.getDataSourceCode(),standardMatchInfo.getMatchManageId());
                            }
                            if(oldThirdMatchInfo.getDataSourceCode().equalsIgnoreCase(standardMatchInfo.getDataSourceCode()) && StringUtils.isNotBlank(standardMatchInfo.getMatchManageId())){
                                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】" +
                                        "三方数据源赛事ID【"+thirdMatchInfoDTO.getThirdMatchSourceId()+"】下三方数据源球队ID发生改变【当前ID VS 以前ID】:【"+thirdMatchTeamDTO.getThirdTeamId()+" VS "+oldTeam.getThirdTeamSourceId()+"】");
                                upStandardMatchTeamRelationFlag = true;
                                //如果变更数据源球队，并当前数据源球队以前不存在，创建三方球队 和 修改三方赛事球队关系 后需要额外 创建对应的标准球队 和 修改标准赛事和球队关系
                                if(null == oldThirdSportTeam){
                                    addStandardTeamFlag = true;
                                }
                            }
                        }
                    }
                    //93166 三方球队变更未更新teamName
                    if(null != oldThirdMatchTeamRelation ){
                        ThirdSportTeam oldTeam = thirdSportTeamService.getItem(oldThirdMatchTeamRelation.getTeamId());
                        if(null != oldTeam && !thirdMatchTeamDTO.getThirdTeamId().equals(oldTeam.getThirdTeamSourceId())){
                            if(HOME.equals(matchPosition)){
                                oldThirdHomeTeamId = oldTeam.getThirdTeamSourceId();
                                parThirdHomeTeamId = thirdMatchTeamDTO.getThirdTeamId();
                            }
                            if(AWAY.equals(matchPosition)){
                                oldThirdAwayTeamId = oldTeam.getThirdTeamSourceId();
                                parThirdAwayTeamId = thirdMatchTeamDTO.getThirdTeamId();
                            }
                        }
                    }
                }
                ThirdSportTeam thirdSportTeam = new ThirdSportTeam();
                BeanUtils.copyProperties(thirdMatchTeamDTO, thirdSportTeam);
                thirdSportTeam.setThirdTeamSourceId(thirdMatchTeamDTO.getThirdTeamId());
                thirdSportTeam.setSportId(thirdMatchInfo.getSportId());
                //当前球队多语言信息
                Map<String, LanguageInternation> oldLanguageType2Obj = new LinkedHashMap<>();
                //不存在 则新增(球队 & 赛事和球队关系)
                if(Objects.isNull(oldThirdSportTeam)){
                    thirdSportTeam.setId(UUIdUtils.getId());
                    thirdSportTeam.setNameCode(thirdSportTeam.getId());
                }else{
                    thirdSportTeam.setId(oldThirdSportTeam.getId());
                    if(Long.valueOf(ZERO).equals(oldThirdSportTeam.getNameCode())){
                        thirdSportTeam.setNameCode(UUIdUtils.getId());
                    }else{
                        thirdSportTeam.setNameCode(oldThirdSportTeam.getNameCode());
                    }
                    if(Objects.isNull(thirdSportTeam.getType())){
                        thirdSportTeam.setType(oldThirdSportTeam.getType());
                    }
                    //获取球队多语言
                    oldLanguageType2Obj = languageInternationService.getLanguageType2Item(dataSource.getCode(), oldThirdSportTeam.getNameCode());
                }
                //需要新增或修改的三方球队信息
                thirdSportTeam.setThirdTeamSourceId(thirdMatchTeamDTO.getThirdTeamId());
                thirdSportTeam.setRegionId(thirdTeamSportRegion.getReferenceId());
                thirdSportTeam.setCountryId(thirdTeamSportRegion.getReferenceId());
                thirdSportTeam.setDataSourceCode(dataSource.getCode());
                //============赛事球队关系开始=======================
                //获取赛事球队关系
                getThirdMatchTeamRelationList(oldThirdMatchTeamRelation,thirdMatchInfo,thirdSportTeam,thirdMatchTeamRelationDTO);
                //============赛事球队关系结束=======================
                //============球队多语言开始=======================
                List<I18nItemDTO> teamNameList = thirdMatchTeamDTO.getTeamNameList();

                //处理球队多语言国际化
                Map<String, LanguageInternation> upLanguageType2Obj = processLanguageNameCode(oldLanguageType2Obj, teamNameList, dataSource, thirdSportTeam.getNameCode(),linkId);
                //设置球队英文名称
                thirdSportTeam.setNameSpell(validateI18nItemDTOs(teamNameList, LanguageTypeEnum.en.name()).getText());
                languageType2Text.put(thirdMatchTeamRelationDTO.getMatchPosition().toLowerCase(),getLanguageType2Text(teamNameList));
                //============球队多语言结束=======================
                //获取多语言是否需要更新，如果需要更新则为true，相应的球队信息也需要更新
                boolean flag = !CollectionUtils.isEmpty(Lists.newArrayList(upLanguageType2Obj.values()).stream()
                        .filter(obj -> !Objects.isNull(obj.getCreateTime()) || !Objects.isNull(obj.getModifyTime())).collect(Collectors.toList()));
                if(flag){
                    teamNameFlag = true;
                }
                //如果需要新增标准球队
                if(addStandardTeamFlag){
                    StandardSportTeamDetail standardSportTeam  = new StandardSportTeamDetail();
                    standardSportTeam.setSportId(thirdSportTeam.getSportId());
                    standardSportTeam.setThirdTeamId(thirdSportTeam.getId());
                    standardSportTeam.setNameCode(UUIdUtils.getId());
                    standardSportTeam.setName(thirdSportTeam.getName());
                    standardSportTeam.setNameSpell(thirdSportTeam.getNameSpell());
                    standardSportTeam.setRegionId(thirdSportTeam.getRegionId());
                    standardSportTeam.setCountryId(thirdSportTeam.getCountryId());
                    standardSportTeam.setType(thirdSportTeam.getType());
                    standardSportTeam.setCoach(thirdSportTeam.getCoach());
                    standardSportTeam.setStatium(thirdSportTeam.getStatium());
                    standardSportTeam.setRemark(thirdSportTeam.getRemark());
                    standardSportTeam.setBetRadarId(thirdSportTeam.getBetRadarId());
                    standardSportTeam.setDataSourceCode(dataSource.getCode());
                    standardSportTeam.setRelatedDataSourceCoderNum(ONE);
                    standardSportTeam.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    standardSportTeam.setModifyTime(standardSportTeam.getCreateTime());
                    standardSportTeam.setIl8nNameList(Lists.newArrayList(upLanguageType2Obj.values()));
                    standardSportTeamService.saveItem(standardSportTeam,linkId);
                    log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】新增标准球队信息成功:{}",JSON.toJSONString(standardSportTeam));
                    //新增成功后赋值给三方球队
                    thirdSportTeam.setReferenceId(standardSportTeam.getId());
                }
                //如果需要修改标准赛事球队关系
                if(upStandardMatchTeamRelationFlag){
                    //获取开售信息,如果移入了预开售则不修改标准赛事球队关系 BUG：38187
                    standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(oldThirdMatchInfo.getReferenceId());
                    if(null != standardMatchInfo && OUT.equalsIgnoreCase(standardMatchInfo.getSoldFlag())){
                        Map<String, StandardMatchTeamRelation> position2Item = standardMatchTeamRelationService.getPosition2ItemByStandardMatchId(oldThirdMatchInfo.getReferenceId());
                        if(!CollectionUtils.isEmpty(position2Item)){
                            StandardMatchTeamRelation standardMatchTeamRelation = position2Item.get(oldThirdMatchTeamRelation.getMatchPosition());
                            //查询库中三方球队信息
                            ThirdSportTeam teamItem = thirdSportTeamService.getItemByPrimaryKey(thirdSportTeam.getId());
                            if(null != standardMatchTeamRelation && null != teamItem){
                                standardMatchTeamRelation.setStandardTeamId(teamItem.getReferenceId());
                                standardMatchTeamRelation.setTeamName(thirdSportTeam.getName());
                                standardMatchTeamRelationService.updateItem(standardMatchTeamRelation);
                                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】修改标准赛事球队关系成功:{}",JSON.toJSONString(standardMatchTeamRelation));
                            }
                        }
                    }
                }
                //3803 球队列表
                ThirdSportTeamDetail thirdSportTeamDetail = new ThirdSportTeamDetail();
                BeanUtils.copyProperties(thirdSportTeam,thirdSportTeamDetail);
                if (thirdMatchInfo.getTeamList() == null) {
                    thirdMatchInfo.setTeamList(Lists.newArrayList(thirdSportTeamDetail));
                } else {
                    thirdMatchInfo.getTeamList().add(thirdSportTeamDetail);
                }
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次处理的三方球队信息:{},多语言数据条数：{}",
                        thirdSportTeam.getThirdTeamSourceId(),upLanguageType2Obj.size());
                thirdSportTeamService.saveOrupdate(thirdSportTeam);
                languageInternationService.saveOrupdateList(Lists.newArrayList(upLanguageType2Obj.values()),linkId);
            }finally {
                if(teamLockFlag){
                    redisService.unLock(teamLockKey,teamLockKey);
                }
            }
        }

        //设值主客队关系
        if(thirdMatchInfo.getHomeRelation() != null && thirdMatchInfo.getAwayRelation() != null){
            thirdMatchInfo.setMtRelationList(Lists.newArrayList(thirdMatchInfo.getHomeRelation(),thirdMatchInfo.getAwayRelation()));
        }

        //93166 三方球队变更未更新teamName
        Integer teamChangeFlag = ZERO;
        if(StringUtils.isNotBlank(oldThirdHomeTeamId) && StringUtils.isNotBlank(oldThirdAwayTeamId)){
            if(oldThirdAwayTeamId.equals(parThirdHomeTeamId) && oldThirdHomeTeamId.equals(parThirdAwayTeamId)){
                teamChangeFlag = ONE;
            }
        }
        //在teamChangeStatus不等于1的情况才需要判断主客队是否变更
        if(!Objects.equals(ONE,teamChangeFlag)){
            if(StringUtils.isNotBlank(oldThirdHomeTeamId) && !oldThirdHomeTeamId.equals(parThirdHomeTeamId)){
                teamChangeFlag = TWO;
            }
            if(StringUtils.isNotBlank(oldThirdAwayTeamId) && !oldThirdAwayTeamId.equals(parThirdAwayTeamId)){
                teamChangeFlag = TWO;
            }
        }

        //设置赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询 主队在前，客队在后
        thirdMatchInfo.setTeamName(JSON.toJSONString(Lists.newArrayList(languageType2Text.get(HOME), languageType2Text.get(AWAY))));
        if(!teamNameFlag && null != oldThirdMatchInfo && StringUtils.isNotBlank(oldThirdMatchInfo.getTeamName()) && !Objects.equals(TWO,teamChangeFlag)){
            thirdMatchInfo.setTeamName(oldThirdMatchInfo.getTeamName());
        }

        /**
         * BUG：38187 如果数据商赛事主客队对调需要额外标识
         * 0:未变更(默认），1:主客队对调，2:主队或者客队变更
         * */
        Integer teamChangeStatus = ZERO;
        if(StringUtils.isNotBlank(oldHomeTeamId) && StringUtils.isNotBlank(oldAwayTeamId)){
            if(oldAwayTeamId.equals(parHomeTeamId) && oldHomeTeamId.equals(parAwayTeamId)){
                teamChangeStatus = ONE;
            }
        }
        //在teamChangeStatus不等于1的情况才需要判断主客队是否变更
        if(!Objects.equals(ONE,teamChangeStatus)){
            if(StringUtils.isNotBlank(oldHomeTeamId) && !oldHomeTeamId.equals(parHomeTeamId)){
                teamChangeStatus = TWO;
            }
            if(StringUtils.isNotBlank(oldAwayTeamId) && !oldAwayTeamId.equals(parAwayTeamId)){
                teamChangeStatus = TWO;
            }
        }

        //需要修改的标准赛事信息
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        if(!Objects.equals(ZERO,teamChangeStatus)){
            //赋值球队是否变更
            thirdMatchInfo.setTeamChangeStatus(teamChangeStatus);
            if(null != standardMatchInfo){
                upStandardMatchInfo.setTeamChangeStatus(teamChangeStatus);
            }
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】" +
                    "三方赛事{}对应球队源ID发生改变",thirdMatchInfo.getThirdMatchSourceId());
        }

        //判断联赛信息是否有变更
        if(null != oldThirdMatchInfo
                && !Objects.equals(oldThirdMatchInfo.getTournamentId(), thirdMatchInfo.getTournamentId())){
            thirdMatchInfo.setTournamentChangeStatus(ONE);
            if(null != standardMatchInfo){
                upStandardMatchInfo.setTournamentChangeStatus(ONE);
            }
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】" +
                    "三方赛事{}对应联赛源ID发生改变",thirdMatchInfo.getThirdMatchSourceId());
        }
        //更新标准赛事信息
        if(null != upStandardMatchInfo.getTeamChangeStatus()
                || null != upStandardMatchInfo.getTournamentChangeStatus()){
            upStandardMatchInfo.setId(standardMatchInfo.getId());
            if(null != upStandardMatchInfo.getTeamChangeStatus()){
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】" +
                        "标准赛事对应球队ID发生改变:{}",JSON.toJSONString(upStandardMatchInfo));
            }
            if(null != upStandardMatchInfo.getTournamentChangeStatus()){
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】" +
                        "标准赛事对应联赛发生改变:{}",JSON.toJSONString(upStandardMatchInfo));
            }
            standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
        }
        return languageType2Text;
    }


    /**
     * 校验赛事中联赛是否存在
     * @param dataSource                  本次传入数据来源
     * @param standardSportId             本次传入的运动类型转换后的标准运动类型
     * @param thirdSourceTournamentId     本次传入的三方数据源联赛ID
     * @return   ThirdSportTournament
     */
    private ThirdSportTournament validateThirdSportTournament(DataSource dataSource, Long standardSportId, String thirdSourceTournamentId) {
        ThirdSportTournament oldThirdSportTournament = thirdSportTournamentService.getOneItem(dataSource.getCode(),standardSportId,thirdSourceTournamentId);
        if(null == oldThirdSportTournament){
            throw new ApiException("三方库不存在传入的三方联赛数据【三方数据源联赛ID："+thirdSourceTournamentId+",运动类型："+standardSportId+"】，请检查！");
        }
        return oldThirdSportTournament;
    }


    /**
     * 赛事参数校验
     * @param thirdMatchInfoDTO
     */
    private void validateThirdMatchInfoDTO(ThirdMatchInfoDTO thirdMatchInfoDTO) {
        //校验场地类型
        if (!Objects.isNull(thirdMatchInfoDTO.getSiteType())) {
            List<String> values = systemItemDictService.getListByParentTypeId(SystemTypeDictEnum.POSITION_TYPE.getCode()).stream().map(obj -> obj.getValue()).collect(Collectors.toList());
            Asserts.validateEnumForEmpty(thirdMatchInfoDTO.getSiteType()+"","场地类型[siteType]值非法,请检查!", values);
        }
        String homeName = null;
        String awayName = null;
        //校验球队名称国际化信息
        for (ThirdMatchTeamDTO thirdMatchTeamDTO : thirdMatchInfoDTO.getMatchTeamList()) {
            List<I18nItemDTO> teamNameList = thirdMatchTeamDTO.getTeamNameList();
            I18nItemDTO zsI18nItemDTO = validateI18nItemDTOs(teamNameList,LanguageTypeEnum.zs.name());
            thirdMatchTeamDTO.setName(zsI18nItemDTO.getText());
            if(!Objects.isNull(thirdMatchTeamDTO.getType())){
                List<String> values = systemItemDictService.getListByParentTypeId(SystemTypeDictEnum.SPORT_TEAM_TYPE.getCode()).stream().map(obj -> obj.getValue()).collect(Collectors.toList());
                Asserts.validateEnumForEmpty(thirdMatchTeamDTO.getType()+"","球队类型[type]值非法,请检查!", values);
            }
            if(HOME.equalsIgnoreCase(thirdMatchTeamDTO.getMatchTeamRelation().getMatchPosition())){
                homeName = thirdMatchTeamDTO.getName();
            }
            if(AWAY.equalsIgnoreCase(thirdMatchTeamDTO.getMatchTeamRelation().getMatchPosition())){
                awayName = thirdMatchTeamDTO.getName();
            }
        }
        thirdMatchInfoDTO.setHomeAwayInfo(homeName+VS+awayName);
        //场地名称国际化
//        List<I18nItemDTO> matchPositionNameList = thirdMatchInfoDTO.getMatchPositionNameList();
//        if(!CollectionUtils.isEmpty(matchPositionNameList)){
//            I18nItemDTO zsI18nItemDTO = validateI18nItemDTOs(matchPositionNameList,LanguageTypeEnum.zs.name());
//            thirdMatchInfoDTO.setMatchPositionName(zsI18nItemDTO.getText());
//        }
    }

    /**
     * 处理需要修改的赛事业务逻辑
     * @param dataSourceCode     数据来源
     * @param thirdMatchInfo     更新的三方赛事信息
     * @param oldThirdMatchInfo  库中存在的三方赛事信息
     * */
    private void processUpdateMatchInfo(String dataSourceCode,ThirdMatchInfo thirdMatchInfo,ThirdMatchInfo oldThirdMatchInfo,String linkId){
        //如果是泰森赛事并关联了标准赛事，更新标准赛事竞彩编号
        if (dataSourceCode.equals(DataSourceCodeEnum.TS.getCode())) {
            if(null != oldThirdMatchInfo.getReferenceId() && !Long.valueOf(ZERO).equals(oldThirdMatchInfo.getReferenceId())){
                String lotteryNumber = thirdMatchInfo.getLotteryNumber();
                String oldLotteryNumber = oldThirdMatchInfo.getLotteryNumber();
                //如果传入为空，则取库中编号
                if(StringUtils.isBlank(lotteryNumber)){
                    lotteryNumber = oldLotteryNumber;
                }
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_MATCH_INFO_API+"】【"+dataSourceCode+" : "+linkId+"】传入彩票编号：{},库中彩票编号：{}",thirdMatchInfo.getLotteryNumber(),oldLotteryNumber);
                if(StringUtils.isNotBlank(lotteryNumber) && !lotteryNumber.equals(oldLotteryNumber)){
                    try{
                        StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
                        standardMatchInfo.setId(oldThirdMatchInfo.getReferenceId());
                        standardMatchInfo.setLotteryNumber(lotteryNumber);
                        StandardMatchInfo upStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(standardMatchInfo);
                        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_MATCH_INFO_API+"】【"+dataSourceCode+" : "+linkId+"】" + "修改后的标准赛事彩票编号信息：{}",JSON.toJSONString(upStandardMatchInfo));
                    }catch (Exception e){
                        log.error("【"+ PROJECT_ID_NOREALTIME+" ："+ THIRD_MATCH_INFO_API+"】【"+dataSourceCode+" : "+linkId+"】" + "修改后的标准赛事彩票编号信息异常，Exception",e);
                    }
                }
            }
        }
        //如果是商业数据源 推送赛事状态为/3完赛/4结束 则延用原赛事状态
        List<Integer> mathcStatusList = Arrays.asList(MatchStatusEnum.Ended.value,MatchStatusEnum.Closed.value);
        boolean flag = mathcStatusList.contains(oldThirdMatchInfo.getMatchStatus()) && getDataSourceCodes(DataSourceCommerceEnum.COMMERCE.getCode()).contains(dataSourceCode);
        if (flag) {
            //如果原赛事是完赛,是可以修改成结束状态的
            if(!(MatchStatusEnum.Ended.value.equals(oldThirdMatchInfo.getMatchStatus()) && MatchStatusEnum.Closed.value.equals(thirdMatchInfo.getMatchStatus()))){
                //状态还原
                thirdMatchInfo.setMatchStatus(oldThirdMatchInfo.getMatchStatus());
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_MATCH_INFO_API+"】【"+dataSourceCode+" : "+linkId+"】" +
                        "状态不可逆触发,原赛事状态：{"+thirdMatchInfo.getMatchStatus()+"},传入赛事状态：{"+oldThirdMatchInfo.getMatchStatus()+"}" );
            }
        }else{
            //如果上游推送的状态为未开赛，则无需修改赛事状态
            if (MatchStatusEnum.Not_Started.value.equals(thirdMatchInfo.getMatchStatus())) {
                thirdMatchInfo.setMatchStatus(null);
            }
        }
    }

    /**
     * 设值需要新增和修改的赛事球队关系
     * @param oldThirdMatchTeamRelation        三方库赛事球队关系
     * @param thirdMatchInfo                   编辑的赛事
     * @param thirdSportTeam                   编辑的球队
     * @param thirdMatchTeamRelationDTO        传入的赛事球队关系信息
     * */
    private void getThirdMatchTeamRelationList(ThirdMatchTeamRelation oldThirdMatchTeamRelation,ThirdMatchInfoDetail thirdMatchInfo,ThirdSportTeam thirdSportTeam,ThirdMatchTeamRelationDTO thirdMatchTeamRelationDTO){
        //三方库赛事和球队关系
        ThirdMatchTeamRelation thirdMatchTeamRelation = new ThirdMatchTeamRelation();
        BeanUtils.copyProperties(thirdMatchTeamRelationDTO, thirdMatchTeamRelation);
        //三方赛事球队关系中的三方赛事ID
        thirdMatchTeamRelation.setMatchId(thirdMatchInfo.getId());
        //三方赛事球队关系中的三方球队ID
        thirdMatchTeamRelation.setTeamId(thirdSportTeam.getId());
        if(Objects.isNull(oldThirdMatchTeamRelation)){
            thirdMatchTeamRelation.setId(UUIdUtils.getId());
            thirdMatchTeamRelation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }else{
            //设值库中ID
            thirdMatchTeamRelation.setId(oldThirdMatchTeamRelation.getId());
            thirdMatchTeamRelation.setCreateTime(null);
        }
        //如果和数据库数据一致 标记无需修改，修改时间不设置
        if(!EntityEqualsUtils.equalsIsObjToString(thirdMatchTeamRelation,oldThirdMatchTeamRelation)){
            thirdMatchTeamRelation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }
        if(null == thirdMatchInfo.getMtRelationList()){
            thirdMatchInfo.setMtRelationList(Lists.newArrayList(thirdMatchTeamRelation));
        }else{
            thirdMatchInfo.getMtRelationList().add(thirdMatchTeamRelation);
        }
        if(HOME.equals(thirdMatchTeamRelation.getMatchPosition())){
            thirdMatchInfo.setHomeRelation(thirdMatchTeamRelation);
        }
        if(AWAY.equals(thirdMatchTeamRelation.getMatchPosition())){
            thirdMatchInfo.setAwayRelation(thirdMatchTeamRelation);
        }
    }

}
