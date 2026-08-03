package com.panda.merge.rocketmq.processor;


import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.LanguageTypeEnum;
import com.panda.merge.common.enums.MatchTypeEnum;
import com.panda.merge.common.enums.PandaErrorCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.*;
import com.panda.merge.exception.ApiException;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.ThirdSportRegion;
import com.panda.merge.model.ThirdSportTournament;
import com.panda.merge.rocketmq.common.TransactionalProcessor;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.ThirdSportTournamentProducer;
import com.panda.merge.service.ThirdSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 三方数据源投递的联赛数据处理 <br>
 * @author        tell
 * @createDate   2020年9月2日19:29:38
 * */
@Slf4j
@Validated
@Component
public class ThirdSportTournamentProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;
    @Autowired
    private TransactionalProcessor transactionalProcessor;
    @Autowired
    private ThirdSportTournamentProducer thirdSportTournamentProducer;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    /**
     * 处理联赛数据
     * @param request  三方数据源联赛入参
     * */
    @Async("getTournamentThreadPool")
    @ExceptionHelper
    public Response processTournamentData(@Valid Request<List<ThirdSportTournamentDTO>> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try{
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_TOURNAMENT_API+"】【::"+request.getLinkId()+"::】第三方联赛数据接收开始...");
            //简单校验,主要判断linkId和Data不能为空,条数上限为1
            simpleValidateParam(request,THIRD_TOURNAMENT_API,1);
            //获取联赛参数列表
            List<ThirdSportTournamentDTO> thirdSportTournamentDTOList = request.getData();
            //获取当前第三方数据源
            Set<String> dataSourceCodes = thirdSportTournamentDTOList.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
            /** 01 校验dataSourceCode是否合法*/
            DataSource dataSource = simpleValidateDataSourceCodes(request, dataSourceCodes);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_TOURNAMENT_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方联赛数据处理开始");
            //本次传入的三方运动类型列表
            Set<String> thirdSportIds = thirdSportTournamentDTOList.stream().map(obj -> obj.getSportId().toString()).collect(Collectors.toSet());
            /** 02 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联*/
            Map<String, Long> thirdSportId2referenceId = validateSportIds(dataSource.getCode(), thirdSportIds);
            //数据处理入库
            process2Database(request.getLinkId(),dataSource,thirdSportTournamentDTOList,thirdSportId2referenceId);
        }catch (Exception e){
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        }finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(),nonrealtime,THIRD_TOURNAMENT_API,"三方联赛信息接入",
                            stopWatch.getTotalTimeMillis(),Integer.parseInt(String.valueOf(response.getCode())),response.getMsg())
            );
        }
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+PROJECT_ID_NOREALTIME+" ："+ THIRD_TOURNAMENT_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方联赛数据处理结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    /**
     * 联赛数据处理入库
     * @param linkId         线路ID
     * @param dataSource     本次传入数据来源
     * @param thirdSportTournamentDTOList    本次传入的联赛列表
     * @param thirdSportId2referenceId       三方数据源运动类型和标准运动类型关系
     * */
    private void process2Database(String linkId,DataSource dataSource, List<ThirdSportTournamentDTO> thirdSportTournamentDTOList,Map<String, Long> thirdSportId2referenceId) {
        /** 03 查询数据库中本次传入已存在的数据 方便统一过滤数据
         *    ①:库中已存在的联赛数据
         *    ②:库中已存在的区域数据
         *    ③:库中已存在的多语言数据（联赛名称多语言，和赛季多语言）
         * */
        //本次传入的全部三方数据源联赛ID
        Set<String> thirdTournamentSourceIds = thirdSportTournamentDTOList.stream().map(obj -> obj.getThirdTournamentSourceId()).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(thirdTournamentSourceIds)){
            throw new ApiException(PandaErrorCodeEnum.TOURNAMENT_ID_IS_NOTNULL.getErrorMsg());
        }
        //本次传入的全部三方数据源区域ID
        Set<String> thirdRegionIds = thirdSportTournamentDTOList.stream().map(obj -> obj.getSportRegionId()).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(thirdRegionIds)){
            throw new ApiException(PandaErrorCodeEnum.REGION_ID_IS_NOTNULL.getErrorMsg());
        }
        /** 04 统一处理联赛，区域，联赛多语言，赛季多语言参数，筛选出需要修改或者新增的数据*/
        //需要新增或修改的区域
        Map<String, ThirdSportRegion> upThirdRegionId2Obj = new LinkedHashMap<>();
        for (ThirdSportTournamentDTO tournamentDto:thirdSportTournamentDTOList) {
            //参数信息校验
            validateThirdSportTournament(tournamentDto);
            //获取标准运动类型ID
            final Long sportId = thirdSportId2referenceId.get(String.valueOf(tournamentDto.getSportId()));
            //创建联赛redis分布式锁
            String tournamentLockKey = tournamentDto.getDataSourceCode() + sportId + tournamentDto.getThirdTournamentSourceId();
            boolean tournamentLockFlag = false;
            try{
                tournamentLockFlag = redisService.tryLock(tournamentLockKey,tournamentLockKey,5,3);
                //对是子联赛进行相关的判断
                validateFatherTournament(linkId, sportId, tournamentDto);
                //创建三方库联赛对象并填充参数
                ThirdSportTournament tournament = new ThirdSportTournament();
                BeanUtils.copyProperties(tournamentDto, tournament, "tournamentNameList");
                tournament.setSportId(sportId);
                tournament.setCurrentRoundName(tournamentDto.getTournamentRoundName());
                tournament.setCurrentSeasonId(tournamentDto.getThirdSeasonSourceId());
                tournament.setSimpleFlage(Integer.parseInt(tournamentDto.getSimpleFlage()==null?"0":tournamentDto.getSimpleFlage()));
                //=============================获取三方区域信息开始=============================
                //获取并设值需要编辑的球队区域
                ThirdSportRegion thirdSportRegion = getThirdSportRegion(upThirdRegionId2Obj,dataSource.getCode(), tournament.getSportId(), tournamentDto.getSportRegionId(), tournamentDto.getSportRegionName());
                //=============================获取三方区域信息结束=============================
                //=============================获取三方联赛信息开始=============================
                ThirdSportTournament oldTournament = thirdSportTournamentService.getOneItem(dataSource.getCode(), tournament.getSportId(), tournamentDto.getThirdTournamentSourceId());
                //三方联赛信息不存在 则新增
                if(Objects.isNull(oldTournament)){
                    tournament.setId(UUIdUtils.getId());
                    tournament.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_TOURNAMENT_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次需要新增的三方联赛信息:{}",tournamentDto.getThirdTournamentSourceId());
                }else{
                    tournament.setCreateTime(null);
                    tournament.setId(oldTournament.getId());
                    tournament.setReferenceId(oldTournament.getReferenceId());
                    tournament.setNameCode(oldTournament.getNameCode());
                    tournament.setSeasonNameCode(oldTournament.getSeasonNameCode());
                    //tournament.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                }
                //=============================联赛名称，赛事多语言开始=============================
                //当前联赛需要新增或修改的多语言
                List<LanguageInternation> languageInternationList = new LinkedList<>();
                //联赛名称多语言
                List<I18nItemDTO> i18nItemDtos = tournamentDto.getTournamentNameList();
                //处理联赛多语言国际化 返回nameCode
                Long tournamentNameCode = processLanguageNameCode(languageInternationList, i18nItemDtos, dataSource, tournament.getNameCode(),linkId,true);
                //设值英文名称
                tournament.setNameSpell(validateI18nItemDTOs(i18nItemDtos, LanguageTypeEnum.en.name()).getText());
                tournament.setNameCode(tournamentNameCode);
                //赛季多语言
                List<I18nItemDTO> seasonNameList = tournamentDto.getSeasonNameList();
                if (!CollectionUtils.isEmpty(seasonNameList)) {
                    Long seasonNameCode = processLanguageNameCode(languageInternationList, seasonNameList, dataSource, tournament.getSeasonNameCode(),linkId,false);
                    tournament.setSeasonNameCode(seasonNameCode);
                    //将赛季信息转换为json存储
                    tournament.setSeason(getLanguageType2Text2Json(seasonNameList));
                }
                //=============================联赛名称，赛事多语言开始=============================
                //设置标准区域ID
                tournament.setRegionId(thirdSportRegion.getReferenceId());
                //当前需要新增或修改的数据 如果和数据库数据不一致 则允许编辑
                if(!EntityEqualsUtils.equalsIsObjToString(tournament,oldTournament)){
                    tournament.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                }
                /** 数据统一入库联赛，多语言*/
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_TOURNAMENT_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次处理的三方联赛信息:{}",tournament.getThirdTournamentSourceId());
                transactionalProcessor.saveOrupdateThirdTournament(tournament,languageInternationList,linkId);
                /** 入库完毕推送MQ*/
                thirdSportTournamentProducer.pushQueueTournament(linkId,dataSource,tournament);
                //3803比分网后台推送三方联赛
                pushThirdTournamentPLS(linkId, dataSource, sportId, tournament);
                //=============================获取三方联赛信息结束=============================
            }finally {
                if(tournamentLockFlag){
                    redisService.unLock(tournamentLockKey,tournamentLockKey);
                }
            }
        }
        /** 区域信息入库*/
        for (String key: upThirdRegionId2Obj.keySet()) {
            thirdSportRegionService.saveOrupdate(upThirdRegionId2Obj.get(key));
        }
    }

    public void pushThirdTournamentPLS(String linkId, DataSource dataSource, Long sportId, ThirdSportTournament tournament) {
        if (tournament == null) {
            return;
        }
        ThirdSportTournamentDetail thirdSportTournamentDetail = new ThirdSportTournamentDetail();
        BeanUtils.copyProperties(tournament,thirdSportTournamentDetail);
        if (thirdSportTournamentDetail.getMatchType()==null) {
            thirdSportTournamentDetail.setMatchType(MatchTypeEnum.NORMAL.getCode());
        }
        if (StandardSportTypeEnum.FootBall.getCode().equals(sportId) && MatchTypeEnum.NORMAL.getCode().equals(thirdSportTournamentDetail.getMatchType())) {
            if (thirdSportTournamentDetail.getNameCode() != null && thirdSportTournamentDetail.getNameCode()!=0) {
                Map<String, LanguageInternation> languageInternationMap = languageInternationService.getLanguageType2Item(thirdSportTournamentDetail.getDataSourceCode(), thirdSportTournamentDetail.getNameCode());
                if (MapUtil.isNotEmpty(languageInternationMap)) {
                    thirdSportTournamentDetail.setTournamentNameList(new ArrayList<>(languageInternationMap.values()));
                }
            }
            if (thirdSportTournamentDetail.getSeasonNameCode() !=null && thirdSportTournamentDetail.getSeasonNameCode() !=0) {
                Map<String, LanguageInternation> languageInternationMap = languageInternationService.getLanguageType2Item(thirdSportTournamentDetail.getDataSourceCode(), thirdSportTournamentDetail.getSeasonNameCode());
                if (MapUtil.isNotEmpty(languageInternationMap)) {
                    thirdSportTournamentDetail.setSeasonNameList(new ArrayList<>(languageInternationMap.values()));
                }
            }
            thirdSportTournamentProducer.pushThirdTournamentPLS(linkId, dataSource,thirdSportTournamentDetail);
        }
    }

    /**
     *联赛校验
     * */
    private void validateFatherTournament(String linkId, Long sportId, ThirdSportTournamentDTO tournamentDto) {
        //包含子联赛的情况下
        if (tournamentDto.getSimpleFlage() != null && String.valueOf(ONE).equals(tournamentDto.getSimpleFlage())) {
            if (StringUtils.isEmpty(tournamentDto.getFatherTournamentId())) {
                throw new ApiException("::"+linkId+":: 子联赛【"+tournamentDto.getThirdTournamentSourceId()+",运动类型："+sportId+"】的父联赛id为空!");
            }
            ThirdSportTournament fatherTournament = thirdSportTournamentService.getOneItem(tournamentDto.getDataSourceCode(), sportId, tournamentDto.getFatherTournamentId());
            if ( null == fatherTournament ) {
                throw new ApiException("::"+linkId+":: 子联赛【"+tournamentDto.getThirdTournamentSourceId()+",运动类型："+sportId+"】的父联赛不存在!");
            }
            //要求的父联赛id为融合库id
            tournamentDto.setFatherTournamentId(String.valueOf(fatherTournament.getId()));
        }
    }

    /**
     * 联赛参数校验
     * @param   thirdSportTournamentDTO  联赛信息
     */
    private void validateThirdSportTournament(ThirdSportTournamentDTO thirdSportTournamentDTO) {
        //校验联赛国际化信息 并 设值联赛名称为联赛中文名称
        List<I18nItemDTO> tournamentNameList = thirdSportTournamentDTO.getTournamentNameList();
        I18nItemDTO zsI18nItemDTO = validateI18nItemDTOs(tournamentNameList,LanguageTypeEnum.zs.name());
        thirdSportTournamentDTO.setName(zsI18nItemDTO.getText());
    }

}
