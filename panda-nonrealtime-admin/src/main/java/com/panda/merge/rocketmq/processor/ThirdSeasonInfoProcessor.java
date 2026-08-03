package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.*;
import com.panda.merge.exception.ApiException;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.common.TransactionalProcessor;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.ThirdSportSeasonProducer;
import com.panda.merge.service.ThirdSportSeasonService;
import com.panda.merge.service.ThirdSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @Author Kepa
 * @Date 2021/2/5 20:56
 * @Version 1.0
 */
@Slf4j
@Validated
@Component
public class ThirdSeasonInfoProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;
    @Autowired
    private ThirdSportSeasonService thirdSportSeasonService;
    @Autowired
    private TransactionalProcessor transactionalProcessor;
    @Autowired
    private ThirdSportSeasonProducer thirdSportSeasonProducer;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    @ExceptionHelper
    public Response processSeasonData(@Valid Request<ThirdSeasonInfoDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try{
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_SEASON_INFO_API +"】【::" + request.getLinkId() + "::】第三方赛季数据接收开始...");
            //对linkid进行校验防止数据的重复接入
            validateLinkId(THIRD_SEASON_INFO_API, request);
            ThirdSeasonInfoDTO thirdSeasonDTO = request.getData();
            /** 01 校验dataSourceCode是否合法*/
            DataSource dataSource = simpleValidateDataSourceCode(request, thirdSeasonDTO.getDataSourceCode());
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SEASON_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方赛季数据处理开始");
            /** 02 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联*/
            Long sportId = validateSportId(dataSource.getCode(), String.valueOf(thirdSeasonDTO.getSportId()));
            //数据处理入库
            process2Database(request.getLinkId(), dataSource, thirdSeasonDTO,sportId);
        }catch (Exception e){
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        }finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(),nonrealtime,THIRD_SEASON_INFO_API,"三方赛季信息接入",
                            stopWatch.getTotalTimeMillis(),Integer.parseInt(String.valueOf(response.getCode())),response.getMsg())
            );
        }
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+PROJECT_ID_NOREALTIME+" ："+ THIRD_SEASON_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方赛季数据处理结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }

    /**
     * 保存数据
     * @param linkId          线路ID
     * @param dataSource      数据来源
     * @param thirdSeasonDTO  入参
     * @param sportId         panda运动类型
     */
    private void process2Database(String linkId, DataSource dataSource, ThirdSeasonInfoDTO thirdSeasonDTO,Long sportId){
        //判断联赛是否存在
        ThirdSportTournament oldTournament = this.validateThirdSportTournament(dataSource, sportId, thirdSeasonDTO.getThirdTournamentId());
        //查看赛季id是否存在
        ThirdSportSeason thirdSportSeason = new ThirdSportSeason();
        BeanUtils.copyProperties(thirdSeasonDTO, thirdSportSeason);
        thirdSportSeason.setThirdSourceSeasonId(thirdSeasonDTO.getThirdSeasonId());
        thirdSportSeason.setSeasonName(thirdSeasonDTO.getThirdSeasonName());

        ThirdSportSeason oldThirdSportSeason = thirdSportSeasonService.getOneItem(thirdSeasonDTO.getDataSourceCode(),thirdSeasonDTO.getSportId(),thirdSeasonDTO.getThirdSeasonId() );
        if(Objects.isNull(oldThirdSportSeason)){
            thirdSportSeason.setId(UUIdUtils.getId());
            thirdSportSeason.setThirdTournamentId(oldTournament.getId());
            thirdSportSeason.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //StandardSportSeason standardSportSeason =  this.createStandardSeasonData(thirdSeasonDTO, thirdSportSeason, oldTournament, dataSource);
        } else {
            thirdSportSeason.setId(oldThirdSportSeason.getId());
            thirdSportSeason.setReferenceId(oldThirdSportSeason.getReferenceId());
            thirdSportSeason.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }

        //赛季对应的多语言
        List<LanguageInternation> languageInternationList = new LinkedList<>();
        List<I18nItemDTO> seasonNameList = thirdSeasonDTO.getSeasonNameList();
        if (!CollectionUtils.isEmpty(seasonNameList)) {
            Long seasonNameCode = processLanguageNameCode(languageInternationList, seasonNameList, dataSource, thirdSportSeason.getNameCode(),linkId,false);
            thirdSportSeason.setNameCode(seasonNameCode);
        }
        //保存数据
        transactionalProcessor.saveOrupdateThirdSeason(thirdSportSeason,languageInternationList,linkId);
        //if (Objects.isNull(oldThirdSportSeason)) {
        /** 入库完毕推送MQ*/
        thirdSportSeasonProducer.pushQueueSeason(linkId,dataSource,thirdSportSeason);
        //}
    }

    /**
     * 生成标准的赛季
     */
    private StandardSportSeason createStandardSeasonData(ThirdSeasonInfoDTO thirdSeasonDTO, ThirdSportSeason thirdSportSeason, ThirdSportTournament oldTournament, DataSource dataSource,String linkId) {
        StandardSportSeason standardSportSeason = new StandardSportSeason();
        if (oldTournament.getReferenceId()>0) {
            standardSportSeason.setStandardTournamentId(oldTournament.getReferenceId());
        }
        BeanUtils.copyProperties(thirdSeasonDTO, standardSportSeason);
        standardSportSeason.setId(UUIdUtils.getId());
        standardSportSeason.setSeasonName(thirdSeasonDTO.getThirdSeasonName());
        standardSportSeason.setThirdSeasonId(thirdSportSeason.getId());
        thirdSportSeason.setReferenceId(standardSportSeason.getId());
        standardSportSeason.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());

        //标准赛季对应的多语言
        List<LanguageInternation> languageInternationList = Lists.newLinkedList();
        List<I18nItemDTO> seasonNameList = thirdSeasonDTO.getSeasonNameList();
        if (!CollectionUtils.isEmpty(seasonNameList)) {
            Long seasonNameCode = processLanguageNameCode(languageInternationList, seasonNameList, dataSource, thirdSportSeason.getNameCode(),linkId, false);
            standardSportSeason.setNameCode(seasonNameCode);
        }
        transactionalProcessor.saveStandardSeasonAndInternation(standardSportSeason, languageInternationList,linkId,dataSource);
        return standardSportSeason;
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

}
