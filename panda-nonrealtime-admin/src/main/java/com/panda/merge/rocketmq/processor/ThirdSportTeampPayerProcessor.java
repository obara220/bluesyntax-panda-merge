package com.panda.merge.rocketmq.processor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.LanguageTypeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.*;
import com.panda.merge.exception.ApiException;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.common.TransactionalProcessor;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.ThirdSportPlayerProducer;
import com.panda.merge.service.ThirdSportPlayerService;
import com.panda.merge.service.ThirdSportTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @Description   三方数据源投递的球队球员数据处理
 * @author        tell
 * @Date          2020年9月6日10:20:16
 * */
@Slf4j
@Validated
@Component
public class ThirdSportTeampPayerProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportTeamService thirdSportTeamService;
    @Autowired
    private ThirdSportPlayerService thirdSportPlayerService;
    @Autowired
    private TransactionalProcessor transactionalProcessor;
    @Autowired
    private ThirdSportPlayerProducer thirdSportPlayerProducer;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    /**
     * 处理三方球队球员数据
     * @param request  三方数据源赛事入参
     * */
    @Async("getTeamPayerThreadPool")
    @ExceptionHelper
    public Response processPayerData(@Valid Request<List<ThirdSportTeamDTO>> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try{
            log.info("【"+ PROJECT_ID_NOREALTIME+" ："+ THIRD_SPORT_TEAM_API+"】【::"+request.getLinkId()+"::】第三方球队球员数据接收开始...");
            //简单校验,主要判断linkId和Data不能为空,条数上限为100
            simpleValidateParam(request,THIRD_SPORT_TEAM_API,HUNDRED);
            //球队列表（包含球员数据）
            List<ThirdSportTeamDTO> thirdSportTeamDTOList = request.getData();
            //获取当前第三方数据源
            Set<String> dataSourceCodes = thirdSportTeamDTOList.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
            /** 01 校验dataSourceCode是否合法*/
            DataSource dataSource = simpleValidateDataSourceCodes(request, dataSourceCodes);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_TEAM_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方球队球员数据处理开始");
            //本次传入的三方运动类型列表
            Set<String> thirdSportIds = thirdSportTeamDTOList.stream().map(obj -> obj.getSportId().toString()).collect(Collectors.toSet());
            /** 02 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联*/
            Map<String, Long> thirdSportId2referenceId = validateSportIds(dataSource.getCode(), thirdSportIds);
            //校验球队&球员数据并处理入库
            process2Database(request.getLinkId(),dataSource,thirdSportTeamDTOList,thirdSportId2referenceId);
        }catch (Exception e){
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        }finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(),nonrealtime,THIRD_SPORT_TEAM_API,"三方球队球员信息接入",
                            stopWatch.getTotalTimeMillis(),Integer.parseInt(String.valueOf(response.getCode())),response.getMsg())
            );
        }
        log.info("【"+ PROJECT_ID_NOREALTIME+" ："+ THIRD_SPORT_TEAM_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方球队球员数据处理结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    /**
     * 球队球员数据处理入库
     * @param linkId                         线路ID
     * @param dataSource                     数据来源
     * @param thirdSportTeamDTOList          传入球队&球员数据
     * @param thirdSportId2referenceId       三方数据源运动类型和标准运动类型关系
     */
    private void process2Database(String linkId,DataSource dataSource,List<ThirdSportTeamDTO> thirdSportTeamDTOList,Map<String, Long> thirdSportId2referenceId) {
        /** 统一处理区域，球员信息，球员多语言 筛选出需要修改或者新增的数据*/
        Map<String, ThirdSportRegion> upThirdRegionId2Obj = new LinkedHashMap<>();
        Map<String, ThirdSportPlayerDetail> upThirdSourcePlayerId2Player = new LinkedHashMap<>();
        Map<Long, List<LanguageInternation>> upNameCode2Language = new LinkedHashMap<>();
        for (ThirdSportTeamDTO thirdSportTeamDTO : thirdSportTeamDTOList) {
            final Long sportId = thirdSportId2referenceId.get(String.valueOf(thirdSportTeamDTO.getSportId()));
            //获取库中球队信息是否存在
            ThirdSportTeam oldThirdSportTeam = validateThirdSportTeam(dataSource,sportId,thirdSportTeamDTO.getThirdTeamSourceId());
//            ThirdSportTeam oldThirdSportTeam = thirdSportTeamService.getOneItem(dataSource.getCode(), sportId, thirdSportTeamDTO.getThirdTeamSourceId());
            //传入三方球队下球员数据
            List<ThirdSportPlayerDTO> thirdSportPlayerDTOList = thirdSportTeamDTO.getThirdSportPlayerList();
            if (CollectionUtils.isEmpty(thirdSportPlayerDTOList)) {
                continue;
            }
            //据三方库球队查询球队下球员数据
            Map<String, ThirdSportPlayerDetail> unique2Player = thirdSportPlayerService.getUnique2ItemByTeamId(oldThirdSportTeam);
            for (ThirdSportPlayerDTO thirdSportPlayerDTO : thirdSportPlayerDTOList) {
                //获取球员唯一key
                String playerLockKey = thirdSportPlayerDTO.getDataSourceCode()+FIX+sportId+FIX+thirdSportPlayerDTO.getThirdSourcePlayerId();
                //校验球员信息--国际化
                List<I18nItemDTO> nameI18nList = thirdSportPlayerDTO.getNameI18nList();
                I18nItemDTO i18nItemDTO = validateI18nItemDTOs(nameI18nList, LanguageTypeEnum.zs.name());
                thirdSportPlayerDTO.setName(i18nItemDTO.getText());
                //============球员区域开始=======================
                //获取并设值需要编辑的球员区域
                ThirdSportRegion playerRegion = getThirdSportRegion(upThirdRegionId2Obj,dataSource.getCode(), sportId, thirdSportPlayerDTO.getThirdRegionId(), thirdSportPlayerDTO.getThirdRegionName());
                //获取并设值需要编辑的国籍区域
                ThirdSportRegion countryRegion = getThirdSportRegion(upThirdRegionId2Obj,dataSource.getCode(), sportId, thirdSportPlayerDTO.getCountryId(), thirdSportPlayerDTO.getCountryName());
                //============球员区域结束=======================
                //获取库中该球员信息
                ThirdSportPlayerDetail oldThirdSportPlayer = null;
                if(null != oldThirdSportTeam){
                    oldThirdSportPlayer = unique2Player.get(playerLockKey);
                }
                if(null == oldThirdSportPlayer){
                    oldThirdSportPlayer = thirdSportPlayerService.getItem(dataSource.getCode(),sportId,thirdSportPlayerDTO.getThirdSourcePlayerId());
                }
                //获取需要新增或者修改的球员信息
                ThirdSportPlayerDetail thirdSportPlayer = getThirdSportPlayer(linkId,oldThirdSportPlayer, thirdSportPlayerDTO, playerRegion.getReferenceId(), countryRegion.getReferenceId());
                thirdSportPlayer.setPlayerRegion(playerRegion);
                thirdSportPlayer.setCountryRegion(countryRegion);
                thirdSportPlayer.setSportId(sportId);
                //============球队球员多语言开始=======================
                //当前球员需要新增或修改的多语言
                List<LanguageInternation> languageInternationList = new LinkedList<>();
                List<I18nItemDTO> teamNameList = thirdSportPlayerDTO.getNameI18nList();
                Long playerNameCode = processLanguageNameCode(languageInternationList, teamNameList, dataSource, thirdSportPlayer.getNameCode(),linkId, true);
                //设置球队英文名称
                thirdSportPlayer.setNameSpell(validateI18nItemDTOs(teamNameList,LanguageTypeEnum.en.name()).getText());
                thirdSportPlayer.setNameCode(playerNameCode);
                //全语言名称Jons格式
                thirdSportPlayer.setAllLanguageName(getLanguageType2Text2Json(teamNameList));
                //============球队多语言结束=======================
                //当前需要新增或修改的数据 如果和数据库数据不一致 则允许编辑
                if(!EntityEqualsUtils.equalsIsObjToString(thirdSportPlayer,oldThirdSportPlayer)){
                    thirdSportPlayer.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                }
                //============球队球员关系开始=======================
                if(null != oldThirdSportTeam){
                    //三方库中球队球员关系
                    ThirdTeamPlayerRelation oldThirdTeamPlayerRelation = thirdSportPlayer.getTeamPlayerRelation();
                    //获取需要新增或者修改的球队和球员关联关系
                    ThirdTeamPlayerRelation thirdTeamPlayerRelation = getThirdTeamPlayerRelationData(oldThirdTeamPlayerRelation, thirdSportPlayerDTO, oldThirdSportTeam.getId(), thirdSportPlayer.getId());
                    thirdSportPlayer.setTeamPlayerRelation(thirdTeamPlayerRelation);
                }
                //============球队球员关系结束=======================
                //需要修改的球员相关信息
                upThirdSourcePlayerId2Player.put(playerLockKey,thirdSportPlayer);
                //球员多语言信息
                upNameCode2Language.put(thirdSportPlayer.getNameCode(),languageInternationList);
            }
        }
        /** 区域信息入库*/
        for (String key: upThirdRegionId2Obj.keySet()) {
            thirdSportRegionService.saveOrupdate(upThirdRegionId2Obj.get(key));
        }
        /** 数据统一入库球队球员关系，球队球员，球员国际化*/
        for (String playerKey: upThirdSourcePlayerId2Player.keySet()) {
            ThirdSportPlayerDetail thirdSportPlayer = upThirdSourcePlayerId2Player.get(playerKey);
            try{
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_TEAM_API+"】【"+dataSource.getCode()+" ::"+linkId+"::】本次处理的三方球员信息:{}",thirdSportPlayer.getThirdSourcePlayerId());
                JSONObject affectedObject = new JSONObject();
                transactionalProcessor.saveOrupdateThirdPlayer(thirdSportPlayer,upNameCode2Language.get(thirdSportPlayer.getNameCode()),linkId,affectedObject);
                thirdSportPlayerProducer.pushQueuePlayer(linkId,dataSource,thirdSportPlayer);

                //4066【比分网】比分网后台
                if (affectedObject.get("affected") != null && affectedObject.getInteger("affected")>0 && StandardSportTypeEnum.FootBall.getCode().equals(thirdSportPlayer.getSportId())) {
                    thirdSportPlayerProducer.pushThirdPlayerPLS(linkId,dataSource,thirdSportPlayer);
                }
            }catch (DuplicateKeyException e){
                log.error("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_TEAM_API+"】【"+dataSource.getCode()+" : "+linkId+"】，唯一主键冲突，Exception:",e);
            }
        }
    }

    /**
     * 校验球队是否存在
     * @param dataSource                  本次传入数据来源
     * @param standardSportId             本次传入的运动类型转换后的标准运动类型
     * @param thirdTeamSourceId           本次传入的三方数据源球队D
     * @return   ThirdSportTeam
     */
    private ThirdSportTeam validateThirdSportTeam(DataSource dataSource, Long standardSportId, String thirdTeamSourceId) {
        ThirdSportTeam oldThirdSportTeam = thirdSportTeamService.getOneItem(dataSource.getCode(),standardSportId,thirdTeamSourceId);
        if(null == oldThirdSportTeam){
            throw new ApiException("三方库不存在传入的球队数据【三方数据源球队ID："+thirdTeamSourceId+"】，请检查！");
        }
        return oldThirdSportTeam;
    }


    /**
     * 获取需要新增或者修改的球员信息
     * @param oldThirdSportPlayer      三方库中球员信息
     * @param thirdSportPlayerDTO      传入球员参数信息
     * @param playerRegionId           球员的区域
     * @param playerCountryId          球员国籍
     * */
    private ThirdSportPlayerDetail getThirdSportPlayer(String linkId,ThirdSportPlayerDetail oldThirdSportPlayer, ThirdSportPlayerDTO thirdSportPlayerDTO, Long playerRegionId, Long playerCountryId) {
        ThirdSportPlayerDetail thirdSportPlayer = new ThirdSportPlayerDetail();
        BeanUtils.copyProperties(thirdSportPlayerDTO, thirdSportPlayer);
        //如果不存在则生成球员名称NameCode
        if (Objects.isNull(oldThirdSportPlayer)) {
            thirdSportPlayer.setId(UUIdUtils.getId());
            thirdSportPlayer.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_TEAM_API+"】【"+thirdSportPlayerDTO.getDataSourceCode()+" ::"+linkId+"::】本次需要新增的三方球员信息:{}",thirdSportPlayerDTO.getThirdSourcePlayerId());
        }else{
            thirdSportPlayer.setCreateTime(null);
            thirdSportPlayer.setId(oldThirdSportPlayer.getId());
            thirdSportPlayer.setReferenceId(oldThirdSportPlayer.getReferenceId());
            thirdSportPlayer.setNameCode(oldThirdSportPlayer.getNameCode());
            thirdSportPlayer.setTeamPlayerRelation(oldThirdSportPlayer.getTeamPlayerRelation());
        }
        thirdSportPlayer.setRegionId(playerRegionId);
        thirdSportPlayer.setCountryId(playerCountryId);
        return thirdSportPlayer;
    }

    /**
     * 获取需要新增或者修改的球队和球员关联关系
     * @param oldThirdTeamPlayerRelation    三方库中球队球员关系
     * @param thirdSportPlayerDTO        传入三方球员参数
     * @param teamId                    三方球队ID
     * @param playersId                 三方球队人员ID
     */
    private ThirdTeamPlayerRelation getThirdTeamPlayerRelationData(ThirdTeamPlayerRelation oldThirdTeamPlayerRelation,ThirdSportPlayerDTO thirdSportPlayerDTO,Long teamId,Long playersId) {
        ThirdTeamPlayerRelation thirdTeamPlayerRelation = new ThirdTeamPlayerRelation();
        if(Objects.isNull(oldThirdTeamPlayerRelation)){
            thirdTeamPlayerRelation.setId(UUIdUtils.getId());
            thirdTeamPlayerRelation.setTeamId(teamId);
            thirdTeamPlayerRelation.setPlayerId(playersId);
            thirdTeamPlayerRelation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }else{
            BeanUtils.copyProperties(oldThirdTeamPlayerRelation, thirdTeamPlayerRelation);
            thirdTeamPlayerRelation.setCreateTime(null);
        }
        thirdTeamPlayerRelation.setJerseyNumber(thirdSportPlayerDTO.getJerseyNumber());
        thirdTeamPlayerRelation.setPosition(thirdSportPlayerDTO.getTeamPosition());
        //当前需要新增或修改的数据 如果和数据库数据不一致 则允许编辑
        if(!EntityEqualsUtils.equalsIsObjToString(thirdTeamPlayerRelation,oldThirdTeamPlayerRelation)){
            thirdTeamPlayerRelation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }
        return thirdTeamPlayerRelation;
    }


}
