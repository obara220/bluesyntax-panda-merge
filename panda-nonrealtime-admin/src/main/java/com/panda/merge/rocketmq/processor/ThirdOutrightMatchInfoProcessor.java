package com.panda.merge.rocketmq.processor;


import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.LanguageTypeEnum;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.*;
import com.panda.merge.exception.ApiException;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.common.TransactionalProcessor;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.service.I18nnamesOutrightMatchNameService;
import com.panda.merge.service.StandardOutrightMatchInfoService;
import com.panda.merge.service.ThirdOutrightMatchInfoService;
import com.panda.merge.service.ThirdSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 三方数据源投递的冠军赛事数据处理 <br>
 * @author        tell
 * @Date          2020年9月5日10:05:33
 * */
@Slf4j
@Validated
@Component
public class ThirdOutrightMatchInfoProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;
    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;
    @Autowired
    private I18nnamesOutrightMatchNameService i18nnamesOutrightMatchNameService;
    @Autowired
    private StandardOutrightMatchInfoService standardOutrightMatchInfoService;
    @Autowired
    private TransactionalProcessor transactionalProcessor;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    /**
     * 处理赛事数据
     * @param request  三方数据源赛事入参
     * */
    @ExceptionHelper
    public Response processMatchData(@Valid Request<List<ThirdOutrightMatchInfoDTO>> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try{
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_OUTRIGHT_MATCH_INFO_API+"】【::"+request.getLinkId()+"::】第三方冠军赛事数据接收开始...");
            //简单校验,主要判断linkId和Data不能为空,条数上限为1
            simpleValidateParam(request,THIRD_OUTRIGHT_MATCH_INFO_API,1);
            //获取赛事参数列表
            List<ThirdOutrightMatchInfoDTO> thirdMatchInfoDtoList = request.getData();
            //获取当前第三方数据源
            Set<String> dataSourceCodes = thirdMatchInfoDtoList.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
            /** 01 校验dataSourceCode是否合法*/
            DataSource dataSource = simpleValidateDataSourceCodes(request, dataSourceCodes);
            //本次传入的三方运动类型列表
            Set<String> thirdSportIds = thirdMatchInfoDtoList.stream().map(obj -> obj.getSportId().toString()).collect(Collectors.toSet());
            /** 02 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联*/
            Map<String, Long> thirdSportId2referenceId = validateSportIds(dataSource.getCode(), thirdSportIds);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_OUTRIGHT_MATCH_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方冠军赛事数据处理开始");
            //数据处理入库
            process2Database(request.getLinkId(),dataSource,thirdMatchInfoDtoList,thirdSportId2referenceId);
        }catch (Exception e){
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        }finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(),nonrealtime,THIRD_OUTRIGHT_MATCH_INFO_API,"三方冠军赛事信息接入",
                            stopWatch.getTotalTimeMillis(),Integer.parseInt(String.valueOf(response.getCode())),response.getMsg())
            );
        }
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ PROJECT_ID_NOREALTIME+" ："+ THIRD_OUTRIGHT_MATCH_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方冠军赛事数据处理结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }


    /**
     * 赛事数据处理入库
     * @param linkId                     线路ID
     * @param dataSource                 本次传入数据来源
     * @param thirdMatchInfoDtoList      本次传入的赛事列表
     * @param thirdSportId2referenceId   三方数据源运动类型和标准运动类型关系
     * */
    private void process2Database(String linkId,DataSource dataSource, List<ThirdOutrightMatchInfoDTO> thirdMatchInfoDtoList,Map<String, Long> thirdSportId2referenceId) {
        /** 03 统一处理赛事,赛事多语言 筛选出需要修改或者新增的数据*/
        for (ThirdOutrightMatchInfoDTO thirdMatchInfoDTO: thirdMatchInfoDtoList) {
            //获取标准运动类型ID
            final Long sportId = thirdSportId2referenceId.get(String.valueOf(thirdMatchInfoDTO.getSportId()));
            //校验赛事中联赛是否存在
            ThirdSportTournament oldThirdSportTournament = validateThirdSportTournament(dataSource, sportId, thirdMatchInfoDTO.getTournamentId());
            String thirdMatchSourceId = thirdMatchInfoDTO.getThirdOutrightSourceId();
            //根据 标准运动类型+三方数据源赛事ID 获取三方库中赛事信息
            ThirdOutrightMatchInfo oldThirdMatchInfo = thirdOutrightMatchInfoService.getItem(dataSource.getCode(),thirdMatchSourceId);
            ThirdOutrightMatchInfo thirdMatchInfo = new ThirdOutrightMatchInfo();
            BeanUtils.copyProperties(thirdMatchInfoDTO, thirdMatchInfo);
            thirdMatchInfo.setSportId(sportId);
            //获取三方区域信息
            ThirdSportRegion odlThirdSportRegion = getThirdSportRegionByUniqueStr(dataSource.getCode(),thirdMatchInfo.getSportId(),thirdMatchInfoDTO.getRegionId());
            if(!Objects.isNull(odlThirdSportRegion)){
                thirdMatchInfo.setRegionId(odlThirdSportRegion.getReferenceId());
            }
            //设值三方库联赛ID
            thirdMatchInfo.setTournamentId(oldThirdSportTournament.getId());
            //=============================多语言信息开始=============================
            //设值冠军赛事多语言
            List<I18nItemDTO> matchNameList = thirdMatchInfoDTO.getMatchNameList();
            //设置冠军赛事中英文名称
            List<I18nnamesOutrightMatchName> languageInternationList = new LinkedList<>();
            thirdMatchInfo.setThirdMatchNameCn(validateI18nItemDTOs(matchNameList, LanguageTypeEnum.zs.name()).getText());
            thirdMatchInfo.setThirdMatchNameEn(validateI18nItemDTOs(matchNameList, LanguageTypeEnum.en.name()).getText());
            Long id = processMatchLanguageNameCode(languageInternationList, matchNameList, dataSource, thirdMatchInfo.getId());
            //=============================多语言信息结束=============================
            //不存在新增的赛事
            if(Objects.isNull(oldThirdMatchInfo)){
                thirdMatchInfo.setId(id);
                thirdMatchInfo.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            }else{
                thirdMatchInfo.setId(oldThirdMatchInfo.getId());
                thirdMatchInfo.setCreateTime(null);
                //已经关联标准赛事
                if(null != oldThirdMatchInfo.getReferenceId() && !Long.valueOf(ZERO).equals(oldThirdMatchInfo.getReferenceId())){
                    StandardOutrightMatchInfo standardOutrightMatchInfo = new StandardOutrightMatchInfo();
                    standardOutrightMatchInfo.setId(oldThirdMatchInfo.getReferenceId());
                    standardOutrightMatchInfo.setSeasonId(thirdMatchInfo.getSeasonId());
                    standardOutrightMatchInfo.setStandardOutrightYear(thirdMatchInfo.getThirdOutrightYear());
//                    standardOutrightMatchInfo.setRegionId(thirdMatchInfo.getRegionId());
                    standardOutrightMatchInfo.setThirdOutrightMatchId(oldThirdMatchInfo.getId());
                    //设置冠军赛事中英文名称
                    standardOutrightMatchInfo.setStandardOutrightNameCn(thirdMatchInfo.getThirdMatchNameCn());
                    standardOutrightMatchInfo.setStandardOutrightNameEn(thirdMatchInfo.getThirdMatchNameEn());
                    standardOutrightMatchInfo.setStandrdOutrightMatchBegionTime(thirdMatchInfo.getThirdOutrightBeginTime());
                    standardOutrightMatchInfo.setStandrdOutrightMatchEndTime(thirdMatchInfo.getThirdOutrightEndTime());
                    standardOutrightMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    standardOutrightMatchInfoService.updateByPrimaryKeySelective(standardOutrightMatchInfo);
                }
            }
            thirdMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            /** 冠军赛事，赛事多语言入库*/
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_OUTRIGHT_MATCH_INFO_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次处理的三方冠军赛事信息:{}",thirdMatchInfo.getThirdOutrightSourceId());
            transactionalProcessor.saveOrupdateOutrightMatch(thirdMatchInfo,languageInternationList);
        }
    }


    /**
     * 筛选出需要新增或入库的多语言列表并返回NameCode
     * @param languageInternationList  需要新增或修改的多语言列表
     * @param i18nList                 本次传入的单个nameCode多语言列表
     * @param dataSource               数据来源对象
     * @param matchId                 当前多语言nameCode（当前赛事的赛事ID）
     */
    public Long processMatchLanguageNameCode(List<I18nnamesOutrightMatchName> languageInternationList, List<I18nItemDTO> i18nList, DataSource dataSource, Long matchId) {
        //库中单个nameCode的多语言类型和对象的关联
        Map<String, I18nnamesOutrightMatchName> oldLanguageType2Obj = new LinkedHashMap<>();
        if(null != matchId){
            oldLanguageType2Obj = i18nnamesOutrightMatchNameService.getLanguageType2Item(dataSource.getCode(), matchId);
        }else{
            matchId = UUIdUtils.getId();
        }
        for(I18nItemDTO i18nItemDTO : i18nList){
            I18nnamesOutrightMatchName oldLanguageInternation = oldLanguageType2Obj.get(i18nItemDTO.getLanguageType());
            I18nnamesOutrightMatchName languageInternation = new I18nnamesOutrightMatchName();
            BeanUtils.copyProperties(i18nItemDTO, languageInternation);
            // 不存在，insert
            if (Objects.isNull(oldLanguageInternation)) {
                languageInternation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                languageInternation.setFlag(ONE);
                languageInternation.setType(ONE);
            }else{
                languageInternation.setCreateTime(null);
                languageInternation.setId(oldLanguageInternation.getId());
            }
            languageInternation.setMatchCategoryFiled(matchId);
            languageInternation.setDataSourceCode(dataSource.getCode());
            if(!EntityEqualsUtils.equalsIsObjToString(languageInternation,oldLanguageInternation)){
                languageInternation.setModfiyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            }
            languageInternationList.add(languageInternation);
        }
        return matchId;
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
