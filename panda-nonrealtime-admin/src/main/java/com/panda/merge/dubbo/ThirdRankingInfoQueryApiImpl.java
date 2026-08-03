package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.panda.merge.api.IThirdRankingInfoQueryApi;
import com.panda.merge.bo.ThirdMatchHistoryExpressionBO;
import com.panda.merge.bo.ThirdMatchSeasonStatisticsBO;
import com.panda.merge.bo.ThirdSportPlayerRankingBO;
import com.panda.merge.bo.ThirdSportTeamRankingBO;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛事分析，联赛级别数据同步
 * @author    tell
 * @since     2020年10月18日09:54:24
 */
@Slf4j
@Component
@DubboService
public class ThirdRankingInfoQueryApiImpl extends BaseProcessor implements IThirdRankingInfoQueryApi {

    @Autowired
    private ThirdSportTeamRankingService thirdSportTeamRankingService;

    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    private ThirdSportTeamService thirdSportTeamService;

    @Autowired
    private StandardSportTeamService standardSportTeamService;

    @Override
    public Response<PageModel<List<ThirdSportTeamRankingBO>>> queryThirdSportTeamRanking(Request<PageModel<QueryThirdRankingInfoDTO>> request){
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据开始,入参：{}",JSON.toJSONString(request.getData()));
        QueryThirdRankingInfoDTO data = request.getData().getData();
        if(null == data.getBeginTime() && StringUtils.isBlank(data.getSeasonId()) && StringUtils.isBlank(data.getThirdTournamentSourceId())){
            log.info("【queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            return response;
        }
        //获取库中球队榜单列表
        Page<ThirdSportTeamRanking> resPage = thirdSportTeamRankingService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据列表耗时={},条数:{}"),System.currentTimeMillis() - beginTime,resPage.size());
        if(!CollectionUtils.isEmpty(resPage)){
            //转换后的分页对象
            PageModel<List<ThirdSportTeamRankingBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //数据来源编码
            Set<String> dataSourceCodes = resPage.stream().map(thi -> thi.getDataSourceCode()).collect(Collectors.toSet());
            //获取榜单三方数据源联赛ID
            Set<String> thirdTournamentSourceIds = resPage.stream().map(thi -> thi.getThirdTournamentSourceId()).collect(Collectors.toSet());
            //获取三方联赛列表
            List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdTournamentSourceIds));
            Map<String, ThirdSportTournament> thirdTournamentSourceId2Tournament = thirdSportTournaments.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTournamentSourceId(), thi -> thi));

            //获取对应标准联赛信息
            Set<Long> standardTournamentIds = thirdSportTournaments.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            List<StandardSportTournament> standardSportTournaments = standardSportTournamentService.getItems(Lists.newArrayList(standardTournamentIds));
            Map<Long, Long> standardTournamentId2NameCode = standardSportTournaments.stream().collect(Collectors.toMap(StandardSportTournament::getId, obj -> obj.getNameCode()));

            //获取库中三方球队信息
            Set<String> thirdTeamSourceIds = resPage.stream().map(obj -> obj.getThirdTeamSourceId()).collect(Collectors.toSet());
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCodes), null, Lists.newArrayList(thirdTeamSourceIds));
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言
            List<Long> nameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Language = languageInternationService.getItemsByNameCodes(nameCodes);

            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));

            //返回的列表数据
            List<ThirdSportTeamRankingBO> resList = new LinkedList<>();
            for (ThirdSportTeamRanking oldRanking: resPage) {
                try{
                    ThirdSportTeamRankingBO boRanking = new ThirdSportTeamRankingBO();
                    BeanUtils.copyProperties(oldRanking,boRanking);
                    //获取三方联赛信息
                    ThirdSportTournament thirdSportTournament = thirdTournamentSourceId2Tournament.get(oldRanking.getDataSourceCode() + FIX + oldRanking.getSportId() + FIX + oldRanking.getThirdTournamentSourceId());
//                if(Objects.isNull(thirdSportTournament) || null == thirdSportTournament.getReferenceId() || Long.valueOf(ZERO).equals(thirdSportTournament.getReferenceId())){
//                    log.info("【queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据,当前三方联赛未关联标准联赛,{}",JSON.toJSONString(thirdSportTournament));
//                    continue;
//                }
                    if(Objects.isNull(thirdSportTournament)){
                        log.info("【queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据,当前三方联赛数据为空,联赛源ID：{}",oldRanking.getThirdTournamentSourceId());
                        continue;
                    }
                    boRanking.setStandardTournamentId(thirdSportTournament.getReferenceId());
                    boRanking.setStandardTournamentNameCode(standardTournamentId2NameCode.get(thirdSportTournament.getReferenceId()));
                    //获取三方球队信息
                    ThirdSportTeam thirdSportTeam = thirdTeamSourceId2Team.get(oldRanking.getDataSourceCode() + FIX + oldRanking.getSportId() + FIX + oldRanking.getThirdTeamSourceId());
                    if(!Objects.isNull(thirdSportTeam)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(thirdSportTeam.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            boRanking.setStandardTeamId(standardSportTeamDetail.getId());
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                boRanking.setTeamLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                boRanking.setTeamLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                            boRanking.setTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrl())){
                                boRanking.setTeamLogo(thirdSportTeam.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrlThumb())){
                                boRanking.setTeamLogoUrlThumb(thirdSportTeam.getLogoUrlThumb());
                            }
                            List<LanguageInternation> languageList = nameCode2Language.get(thirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                boRanking.setTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }
                    if(CollectionUtils.isEmpty(boRanking.getTeamNameIl8nList())){
                        //组合多语言列表
                        List<I18nItemDTO> i18nItemDTOList = getI18nItemDTOList(oldRanking.getTeamCnName(), oldRanking.getTeamEnName());
                        boRanking.setTeamNameIl8nList(getI18nItemBOList(i18nItemDTOList));
                    }
                    resList.add(boRanking);
                    log.info("【queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据={}" ,JSON.toJSONString(boRanking));
//                    log.info("【测试环境调试专用日志】【"+ PROJECT_ID_NOREALTIME +" ：queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】同步三方联赛球队榜单数据={}" ,JSON.toJSONString(boRanking));
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据异常,源联赛ID="+oldRanking.getThirdTournamentSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdSportTeamRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球队榜单数据结束,返回结果 ：{},总条数 ：{}" ,JSON.toJSONString(response),resList.size());
            pageModel.setData(resList);
        }else{
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("本次分页同步三方联赛球队榜单数据为空！");
        }
        return response;
    }


    @Autowired
    private ThirdSportPlayerRankingService thirdSportPlayerRankingService;

    @Override
    public Response<PageModel<List<ThirdSportPlayerRankingBO>>> queryThirdSportPlayerRanking(Request<PageModel<QueryThirdRankingInfoDTO>> request){
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdSportPlayerRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球员榜单数据开始,入参：{}",JSON.toJSONString(request.getData()));
        QueryThirdRankingInfoDTO data = request.getData().getData();
        if(null == data.getBeginTime() && StringUtils.isBlank(data.getSeasonId()) && StringUtils.isBlank(data.getThirdTournamentSourceId())){
            log.info("【queryThirdSportPlayerRanking】【::"+request.getLinkId()+"::】传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            return response;
        }
        //获取库中球员榜单列表
        Page<ThirdSportPlayerRanking> resPage = thirdSportPlayerRankingService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdSportPlayerRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球员榜单数据列表耗时={},条数:{}"),System.currentTimeMillis() - beginTime,resPage.size());
        //数据来源
        if(!CollectionUtils.isEmpty(resPage)){
            //转换后的分页对象
            PageModel<List<ThirdSportPlayerRankingBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());

            //数据源联赛列表
            Set<String> thirdTournamentSourceIds = resPage.stream().map(thi -> thi.getThirdTournamentSourceId()).collect(Collectors.toSet());
            //数据源球队列表
            Set<String> thirdTeamSourceIds = resPage.stream().map(obj -> obj.getThirdTeamSourceId()).collect(Collectors.toSet());
            //数据来源编码
//            Set<String> dataSourceCodes = resPage.stream().map(thi -> thi.getDataSourceCode()).collect(Collectors.toSet());
            //目前球员榜单只有V02数据源才有
            String dataSourceCode = DataSourceCodeEnum.TS.getCode();
            //获取三方联赛列表信息
            List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(Lists.newArrayList(dataSourceCode), Lists.newArrayList(thirdTournamentSourceIds));
            Map<String, ThirdSportTournament> thirdTournamentSourceId2Tournament = thirdSportTournaments.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTournamentSourceId(), thi -> thi));

            //获取库中三方球队信息
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCode), null, Lists.newArrayList(thirdTeamSourceIds));
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言
            List<Long> nameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Language = languageInternationService.getItemsByNameCodes(nameCodes);

            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));

            //返回数据列表
            List<ThirdSportPlayerRankingBO> resList = new LinkedList<>();
            for (ThirdSportPlayerRanking oldRanking: resPage) {
                try{
                    ThirdSportPlayerRankingBO boRanking = new ThirdSportPlayerRankingBO();
                    BeanUtils.copyProperties(oldRanking,boRanking);
                    //获取三方联赛信息
                    ThirdSportTournament thirdSportTournament = thirdTournamentSourceId2Tournament.get(dataSourceCode + FIX + oldRanking.getSportId() + FIX + oldRanking.getThirdTournamentSourceId());
                    if(Objects.isNull(thirdSportTournament)){
                        log.info("【queryThirdSportPlayerRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球员榜单数据,当前三方联赛数据为空,联赛源ID：{}",oldRanking.getThirdTournamentSourceId());
                        continue;
                    }
                    boRanking.setStandardTournamentId(thirdSportTournament.getReferenceId());
                    //获取三方球队信息
                    ThirdSportTeam thirdSportTeam = thirdTeamSourceId2Team.get(dataSourceCode + FIX + oldRanking.getSportId() + FIX + oldRanking.getThirdTeamSourceId());
                    if(null != thirdSportTeam){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(thirdSportTeam.getReferenceId());
                        if(null != standardSportTeamDetail){
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                boRanking.setTeamLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                boRanking.setTeamLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                            boRanking.setStandardTeamId(standardSportTeamDetail.getId());
                            boRanking.setTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrl())){
                                boRanking.setTeamLogo(thirdSportTeam.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrlThumb())){
                                boRanking.setTeamLogoUrlThumb(thirdSportTeam.getLogoUrlThumb());
                            };
                            List<LanguageInternation> languageList = nameCode2Language.get(thirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                boRanking.setTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }
                    if(CollectionUtils.isEmpty(boRanking.getTeamNameIl8nList())){
                        //组合多语言列表
                        List<I18nItemDTO> i18nItemDTOList = getI18nItemDTOList(oldRanking.getTeamCnName(), oldRanking.getTeamEnName());
                        boRanking.setTeamNameIl8nList(getI18nItemBOList(i18nItemDTOList));
                    }
                    resList.add(boRanking);
                    log.info("【queryThirdSportPlayerRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球员榜单数据 ：{},源联赛ID：{}" ,boRanking.getId() ,boRanking.getThirdTournamentSourceId());
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方联赛球员榜单数据异常,源联赛ID="+oldRanking.getThirdTournamentSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdSportPlayerRanking】【::"+request.getLinkId()+"::】分页同步三方联赛球员榜单数据结束,返回结果 ：{},总条数 ：{}" ,JSON.toJSONString(response),resList.size());
            pageModel.setData(resList);
        }else{
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("本次分页同步三方联赛球员榜单数据为空！");
        }
        return response;
    }


    @Autowired
    private ThirdMatchHistoryExpressionService thirdMatchHistoryExpressionService;

    @Override
    public Response<PageModel<List<ThirdMatchHistoryExpressionBO>>> queryThirdMatchHistoryExpression(Request<PageModel<QueryThirdRankingInfoDTO>> request){
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据开始,入参：{}",JSON.toJSONString(request.getData()));
        QueryThirdRankingInfoDTO data = request.getData().getData();
        if(null == data.getBeginTime() && StringUtils.isBlank(data.getSeasonId()) && StringUtils.isBlank(data.getThirdTournamentSourceId())){
            log.info("【queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            return response;
        }
        //获取库中球队榜单列表
        Page<ThirdMatchHistoryExpression> resPage = thirdMatchHistoryExpressionService.getHistoryExpressionPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据列表耗时={},条数:{}"),System.currentTimeMillis() - beginTime,resPage.size());
        if(!CollectionUtils.isEmpty(resPage)){
            //转换后的分页对象
            PageModel<List<ThirdMatchHistoryExpressionBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //数据来源编码
            Set<String> dataSourceCodes = resPage.stream().map(thi -> thi.getDataSourceCode()).collect(Collectors.toSet());
            //获取榜单三方数据源联赛ID
            Set<String> thirdTournamentSourceIds = resPage.stream().map(thi -> thi.getThirdTournamentSourceId()).collect(Collectors.toSet());
            //获取三方联赛列表
            List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdTournamentSourceIds));
            Map<String, ThirdSportTournament> thirdTournamentSourceId2Tournament = thirdSportTournaments.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTournamentSourceId(), thi -> thi));

            //获取对应标准联赛信息
            Set<Long> standardTournamentIds = thirdSportTournaments.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            List<StandardSportTournament> standardSportTournaments = standardSportTournamentService.getItems(Lists.newArrayList(standardTournamentIds));
            Map<Long, Long> standardTournamentId2NameCode = standardSportTournaments.stream().collect(Collectors.toMap(StandardSportTournament::getId, obj -> obj.getNameCode()));

            //获取库中三方球队信息
            Set<String> thirdTeamSourceIds = resPage.stream().map(obj -> obj.getThirdTeamSourceId()).collect(Collectors.toSet());
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCodes), null, Lists.newArrayList(thirdTeamSourceIds));
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言
            List<Long> nameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Language = languageInternationService.getItemsByNameCodes(nameCodes);

            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));

            //返回的列表数据
            List<ThirdMatchHistoryExpressionBO> resList = new LinkedList<>();
            List<String> removeIds = new LinkedList<>();
            for (ThirdMatchHistoryExpression oldItem: resPage) {
                try{
                    if (filterOutValueEqualZero(removeIds, oldItem)) {
                        continue;
                    }
                    ThirdMatchHistoryExpressionBO newItem = new ThirdMatchHistoryExpressionBO();
                    BeanUtils.copyProperties(oldItem,newItem);
                    //获取三方联赛信息
                    ThirdSportTournament thirdSportTournament = thirdTournamentSourceId2Tournament.get(oldItem.getDataSourceCode() + FIX + oldItem.getSportId() + FIX + oldItem.getThirdTournamentSourceId());
                    if(Objects.isNull(thirdSportTournament)){
                        log.info("【queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据,,当前三方联赛数据为空,联赛源ID：{}",oldItem.getThirdTournamentSourceId());
                        continue;
                    }
                    newItem.setStandardTournamentId(thirdSportTournament.getReferenceId());
                    newItem.setStandardTournamentNameCode(standardTournamentId2NameCode.get(thirdSportTournament.getReferenceId()));
                    //获取三方球队信息
                    ThirdSportTeam thirdSportTeam = thirdTeamSourceId2Team.get(oldItem.getDataSourceCode() + FIX + oldItem.getSportId() + FIX + oldItem.getThirdTeamSourceId());
                    if(!Objects.isNull(thirdSportTeam)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(thirdSportTeam.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            newItem.setStandardTeamId(standardSportTeamDetail.getId());
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                newItem.setTeamLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                newItem.setTeamLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                            newItem.setTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrl())){
                                newItem.setTeamLogo(thirdSportTeam.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrlThumb())){
                                newItem.setTeamLogoUrlThumb(thirdSportTeam.getLogoUrlThumb());
                            }
                            List<LanguageInternation> languageList = nameCode2Language.get(thirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                newItem.setTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }
                    if(CollectionUtils.isEmpty(newItem.getTeamNameIl8nList())){
                        //组合多语言列表
                        List<I18nItemDTO> i18nItemDTOList = getI18nItemDTOList(oldItem.getTeamCnName(), oldItem.getTeamEnName());
                        newItem.setTeamNameIl8nList(getI18nItemBOList(i18nItemDTOList));
                    }
                    resList.add(newItem);
                    log.info("【queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据 ：{},源联赛ID：{}" ,newItem.getId(),newItem.getThirdTournamentSourceId());
//                    log.info("【测试环境调试专用日志】【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】同步三方联赛球队历史表现数据={}" ,JSON.toJSONString(newItem));
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据异常,源联赛ID="+oldItem.getThirdTournamentSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            //85139 值全部为0的数据过滤掉
            if (resList.size()==0 && pageModel.getTotal()<=pageModel.getSize()) {
                response.setData(null);
            }
            if (removeIds.size()>0) {
                log.info("【queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据过滤移除数据,{}" ,JSON.toJSONString(removeIds));
            }
            log.info("【queryThirdMatchHistoryExpression】【::"+request.getLinkId()+"::】分页同步三方联赛球队历史表现数据结束,返回结果 ：{},总条数 ：{}" ,JSON.toJSONString(response),resList.size());
            pageModel.setData(resList);
        }else{
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("本次分页同步三方联赛球队历史表现数据为空！");
        }
        return response;
    }


    @Autowired
    private ThirdMatchSeasonStatisticsService thirdMatchSeasonStatisticsService;

    @Override
    public Response<PageModel<List<ThirdMatchSeasonStatisticsBO>>> queryThirdMatchSeasonStatistics(Request<PageModel<QueryThirdRankingInfoDTO>> request){
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据开始,入参：{}",JSON.toJSONString(request.getData()));
        QueryThirdRankingInfoDTO data = request.getData().getData();
        if(null == data.getBeginTime() && StringUtils.isBlank(data.getSeasonId()) && StringUtils.isBlank(data.getThirdTournamentSourceId())){
            log.info("【queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("传入参数beginTime,seasonId,thirdTournamentSourceId不能同时为空！");
            return response;
        }
        //获取库中球队榜单列表
        Page<ThirdMatchSeasonStatistics> resPage = thirdMatchSeasonStatisticsService.getSeasonStatisticsPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据列表耗时={},条数:{}"),System.currentTimeMillis() - beginTime,resPage.size());
        if(!CollectionUtils.isEmpty(resPage)){
            //转换后的分页对象
            PageModel<List<ThirdMatchSeasonStatisticsBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //数据来源编码
            Set<String> dataSourceCodes = resPage.stream().map(thi -> thi.getDataSourceCode()).collect(Collectors.toSet());
            //获取榜单三方数据源联赛ID
            Set<String> thirdTournamentSourceIds = resPage.stream().map(thi -> thi.getThirdTournamentSourceId()).collect(Collectors.toSet());
            //获取三方联赛列表
            List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdTournamentSourceIds));
            Map<String, ThirdSportTournament> thirdTournamentSourceId2Tournament = thirdSportTournaments.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTournamentSourceId(), thi -> thi));

            //获取对应标准联赛信息
            Set<Long> standardTournamentIds = thirdSportTournaments.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            List<StandardSportTournament> standardSportTournaments = standardSportTournamentService.getItems(Lists.newArrayList(standardTournamentIds));
            Map<Long, Long> standardTournamentId2NameCode = standardSportTournaments.stream().collect(Collectors.toMap(StandardSportTournament::getId, obj -> obj.getNameCode()));

            //返回的列表数据
            List<ThirdMatchSeasonStatisticsBO> resList = new LinkedList<>();
            List<String> removeIds = new LinkedList<>();
            for (ThirdMatchSeasonStatistics oldItem: resPage) {
                try{
                    if (filterOutValueEqualZero(removeIds, oldItem)) {
                        continue;
                    }
                    ThirdMatchSeasonStatisticsBO newItem = new ThirdMatchSeasonStatisticsBO();
                    BeanUtils.copyProperties(oldItem,newItem);
                    //获取三方联赛信息
                    ThirdSportTournament thirdSportTournament = thirdTournamentSourceId2Tournament.get(oldItem.getDataSourceCode() + FIX + oldItem.getSportId() + FIX + oldItem.getThirdTournamentSourceId());
                    if(Objects.isNull(thirdSportTournament)){
                        log.info("【queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据,联赛源ID：{}",oldItem.getThirdTournamentSourceId());
                        continue;
                    }
                    newItem.setStandardTournamentId(thirdSportTournament.getReferenceId());
                    newItem.setStandardTournamentNameCode(standardTournamentId2NameCode.get(thirdSportTournament.getReferenceId()));
                    resList.add(newItem);
                    log.info("【queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据 ：{},源联赛ID：{}" ,newItem.getId(),newItem.getThirdTournamentSourceId());
//                    log.info("【测试环境调试专用日志】【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】同步三方联赛赛季统计数据={}" ,JSON.toJSONString(newItem));
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据异常,源联赛ID="+oldItem.getThirdTournamentSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            //85139 值全部为0的数据过滤掉
            if (resList.size()==0 && pageModel.getTotal()<=pageModel.getSize()) {
                response.setData(null);
            }
            if (removeIds.size()>0) {
                log.info("【queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据结束过滤移除数据,{}" ,JSON.toJSONString(removeIds));
            }
            log.info("【queryThirdMatchSeasonStatistics】【::"+request.getLinkId()+"::】分页同步三方联赛赛季统计数据结束,返回结果 ：{},总条数 ：{}" ,JSON.toJSONString(response),resList.size());
            pageModel.setData(resList);
        }else{
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("本次分页同步三方联赛赛季统计数据为空！");
        }
        return response;
    }

    //85139 值全部为0的数据过滤掉
    private boolean filterOutValueEqualZero(List<String> removeIds, ThirdMatchHistoryExpression oldItem) {
        if (oldItem.getGoalsForTotal()!=null && oldItem.getGoalsForTotal()==0 && oldItem.getAverageGoal()!=null && BigDecimal.ZERO.compareTo(oldItem.getAverageGoal())==0
                && oldItem.getWinPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getWinPercent())==0 && oldItem.getBothGoalPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getBothGoalPercent())==0
                && oldItem.getNotLostPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getNotLostPercent())==0 && oldItem.getFirstGoalPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getFirstGoalPercent())==0
                && oldItem.getAverageGoalPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getAverageGoalPercent())==0 && oldItem.getGoalPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getGoalPercent())==0
                && oldItem.getLostGoalPercent()!=null && BigDecimal.ZERO.compareTo(oldItem.getLostGoalPercent())==0 && oldItem.getGoalXg()!=null && BigDecimal.ZERO.compareTo(oldItem.getGoalXg())==0
                && oldItem.getGoalXga()!=null && BigDecimal.ZERO.compareTo(oldItem.getGoalXga())==0) {
            removeIds.add(oldItem.getId());
            return true;
        }
        return false;
    }


    private boolean filterOutValueEqualZero(List<String> removeIds, ThirdMatchSeasonStatistics oldItem) {
        if (oldItem.getPercentThanOne()!=null && BigDecimal.ZERO.compareTo(oldItem.getPercentThanOne())==0 && oldItem.getPercentThanTwo()!=null && BigDecimal.ZERO.compareTo(oldItem.getPercentThanTwo())==0
            && oldItem.getPercentThanThree()!=null && BigDecimal.ZERO.compareTo(oldItem.getPercentThanThree())==0 && oldItem.getAverageGoal()!=null && BigDecimal.ZERO.compareTo(oldItem.getAverageGoal())==0
            && oldItem.getAverageCard()!=null && BigDecimal.ZERO.compareTo(oldItem.getAverageCard())==0 && oldItem.getAverageCorner()!=null && BigDecimal.ZERO.compareTo(oldItem.getAverageCorner())==0){
            removeIds.add(oldItem.getId());
            return true;
        }
        return false;
    }

}
