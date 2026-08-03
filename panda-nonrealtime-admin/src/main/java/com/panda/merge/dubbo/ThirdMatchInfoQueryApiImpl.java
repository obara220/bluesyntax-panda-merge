package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.panda.merge.api.IThirdMatchInfoQueryApi;
import com.panda.merge.bo.*;
import com.panda.merge.bo.thirdmatch.ThirdMatchPromotionChartBO;
import com.panda.merge.bo.thirdmatch.ThirdMatchTeamSkillStatisticsBO;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.LanguageTypeEnum;
import com.panda.merge.common.enums.PlayerPositionTypeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.query.QueryThirdSportTournamentDTO;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛事分析，赛事级别数据同步
 * @author :  tell
 * @Date:    2020年9月9日11:33:27
 */
@Slf4j
@Component
@DubboService
public class ThirdMatchInfoQueryApiImpl extends BaseProcessor implements IThirdMatchInfoQueryApi {

    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private ThirdSportTeamService thirdSportTeamService;

    @Autowired
    private StandardSportTeamService standardSportTeamService;

    @Autowired
    public RedisService redisService;

    @Override
    public Response<ThirdMatchInfoBO> queryThirdMatchInfoByThirdSourceId(Request<ThirdMatchInfoDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        ThirdMatchInfoDTO thirdMatchInfoDTO = request.getData();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_MATCH_INFO_BY_THIRD_SOURCE_ID+"】【::"+request.getLinkId()+"】根据三方数据源赛事ID查询三方赛事信息开始,入参：{}",JSON.toJSONString(request.getData()));
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, thirdMatchInfoDTO.getDataSourceCode());
        /** 02 校验三方运动类型是否合法并返回标准运动类型*/
        validateSportId(dataSource.getCode(), String.valueOf(thirdMatchInfoDTO.getThirdSportId()));
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(dataSource.getCode(), thirdMatchInfoDTO.getThirdMatchSourceId());
        if (null != oldThirdMatchInfo) {
            ThirdMatchInfoBO thirdMatchInfoBO = new ThirdMatchInfoBO();
            BeanUtils.copyProperties(oldThirdMatchInfo,thirdMatchInfoBO);
            response.setData(thirdMatchInfoBO);
        }else{
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("未找到数据来源【"+dataSource.getCode()+"】,三方数据源运动类型【"+thirdMatchInfoDTO.getThirdSportId()+"】,三方数据源赛事ID【"+thirdMatchInfoDTO.getThirdMatchSourceId()+"】的赛事信息！");
        }
        stopWatch.stop();
        response.setDataSourceTime(stopWatch.getTotalTimeMillis());
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_MATCH_INFO_BY_THIRD_SOURCE_ID+"】【::"+request.getLinkId()+"::】根据三方数据源赛事ID查询三方赛事信息结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    @Override
    public Response<List<ThirdSportTournamentBO>> queryThirdSportTournamentList(Request<QueryThirdSportTournamentDTO> request){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        QueryThirdSportTournamentDTO thirdTournamentDTO = request.getData();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_SPORT_TOURNAMENT_LIST+"】【::"+request.getLinkId()+"::】获取三方联赛列表信息开始,入参：{}",JSON.toJSONString(request.getData()));
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, thirdTournamentDTO.getDataSourceCode());
        //获取三方联赛列表
        List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(dataSource.getCode(),thirdTournamentDTO.getSportId(), thirdTournamentDTO.getStandardTournamentIds());
        if(!CollectionUtils.isEmpty(thirdSportTournaments)){
            List<Long> nameCodes = thirdSportTournaments.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Language = languageInternationService.getItemsByNameCodes(nameCodes);
            List<ThirdSportTournamentBO> resList = new LinkedList<>();
            for (ThirdSportTournament item: thirdSportTournaments) {
                ThirdSportTournamentBO itemBO = new ThirdSportTournamentBO();
                BeanUtils.copyProperties(item,itemBO);
                List<LanguageInternation> internationList = nameCode2Language.get(item.getNameCode());
                if(!CollectionUtils.isEmpty(internationList)){
                    itemBO.setIl8nNameList(getI18nItemBOList(internationList));
                }
                resList.add(itemBO);
            }
            response.setData(resList);
        }else{
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("未找到数据来源=【"+dataSource.getCode()+"】,标准联赛ID列表=【"+JSON.toJSONString(thirdTournamentDTO.getStandardTournamentIds())+"】的三方联赛信息！");
        }
        stopWatch.stop();
        response.setDataSourceTime(stopWatch.getTotalTimeMillis());
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_SPORT_TOURNAMENT_LIST+"】【::"+request.getLinkId()+"::】获取三方联赛列表信息结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    @Autowired
    private ThirdVideoBoardCastRecordService thirdVideoBoardCastRecordService;

    /**
     * 目前没其他地方调用
     * */
    @Override
    public Response<List<VideoAnimationBO>> queryThirdMatchVideoPage(Request<ThirdMatchInfoDTO> request){
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchVideoPage】【::"+request.getLinkId()+"::】查询三方赛事视频信息开始,入参：{}", JSON.toJSONString(request.getData()));
        List<ThirdVideoBoardCastRecord> videoList = thirdVideoBoardCastRecordService.getItemByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchVideoPage】【::"+request.getLinkId()+"::】查询三方赛事视频信息条数：{}，耗时={}"),videoList.size(),System.currentTimeMillis() - beginTime);
        //视频信息列表
        List<VideoAnimationBO> videoBoList = new LinkedList<>();
        for (ThirdVideoBoardCastRecord thirdVideo: videoList) {
            VideoAnimationBO itemBo = new VideoAnimationBO();
            BeanUtils.copyProperties(thirdVideo, itemBo);
            //三方赛事信息处理
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(thirdVideo.getDataSourceCode(), thirdVideo.getMatchId());
            if(null != thirdMatchInfo){
                itemBo.setThirdMatchId(thirdMatchInfo.getId());
                if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                    itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                }else{
                    continue;
                }
            }
            itemBo.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
            itemBo.setSportId(thirdMatchInfo.getSportId());
            itemBo.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
            //主客队是否相反
            itemBo.setReverse(thirdMatchInfo.getHomeAwayOpposite());
            log.info("【queryThirdMatchVideoPage】【::"+request.getLinkId()+"::】查询三方赛事视频信息信息 ：{}" ,itemBo.getThirdMatchSourceId());
            videoBoList.add(itemBo);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【queryThirdMatchVideoPage】【::"+request.getLinkId()+"::】查询三方赛事视频信息结束,返回结果 ：{}" ,JSON.toJSONString(response));
        response.setData(videoBoList);
        return response;
    }


    @Autowired
    private ThirdMatchHistoryStatisticsService thirdMatchHistoryStatisticsService;

    @Override
    public Response<PageModel<List<ThirdMatchHistoryStatisticsBO>>> queryThirdMatchHistoryStatisticsPage(Request<PageModel<StandardMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事历史统计列表开始,入参：{}", JSON.toJSONString(request.getData()));
        StandardMatchInfoDTO data = request.getData().getData();
        if(null == data.getBeginTime() && StringUtils.isBlank(data.getThirdSeasonSourceId()) && StringUtils.isBlank(data.getThirdTournamentSourceId())){
            log.info("【getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】传入参数BeginTime,ThirdSeasonSourceId,TournamentSourceId不能全为空！");
            response.setCode(ResultCode.VALIDATE_FAILED.getCode());
            response.setMsg("传入参数beginTime,thirdSeasonSourceId,tournamentSourceId不能同时为空！");
            return response;
        }
        Page<ThirdMatchHistoryStatistics> resPage = thirdMatchHistoryStatisticsService.getItemPageByModifyTime(request.getData());
        log.info("【getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事历史统计列表条数：{}，耗时={}",resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源联赛ID列表
            Set<String> thirdTournamentSourceIds = new HashSet<>();
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = new HashSet<>();
            //数据来源列表
            Set<String> dataSourceCodes = new HashSet<>();
            //数据源球队ID列表
            Set<String> thirdTeamSourceIds = new HashSet<>();
            for (ThirdMatchHistoryStatistics item: resPage) {
                thirdTournamentSourceIds.add(item.getThirdTournamentSourceId());
                thirdMatchSourceIds.add(item.getThirdMatchSourceId());
                dataSourceCodes.add(item.getDataSourceCode());
                thirdTeamSourceIds.add(item.getHomeTeamId());
                thirdTeamSourceIds.add(item.getAwayTeamId());
            }
            //获取库中三方联赛列表
            List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdTournamentSourceIds));
            Map<String, ThirdSportTournament> thirdTournamentSourceId2Obj = new LinkedHashMap<>();
            Set<Long> thirdTournamentNameCodes = new HashSet<>();
            Set<Long> standardTournamentIds = new HashSet<>();
            for (ThirdSportTournament item: thirdSportTournaments) {
                thirdTournamentSourceId2Obj.put(item.getDataSourceCode() + FIX + item.getSportId() +FIX + item.getThirdTournamentSourceId(),item);
                //有对应标准联赛信息
                Long referenceId = item.getReferenceId();
                if(null != referenceId && referenceId != 0){
                    standardTournamentIds.add(referenceId);
                }else{
                    thirdTournamentNameCodes.add(item.getNameCode());
                }
            }
            //获取对应标准联赛信息
            List<StandardSportTournament> standardSportTournaments = standardSportTournamentService.getItems(Lists.newArrayList(standardTournamentIds));
            Set<Long> standardTournamentNameCodes = standardSportTournaments.stream().map(obj->obj.getNameCode()).collect(Collectors.toSet());
            Map<Long, Long> standardTournamentId2NameCode = standardSportTournaments.stream().collect(Collectors.toMap(StandardSportTournament::getId, obj -> obj.getNameCode()));
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getThirdMatchSourceId(), thi -> thi));
            //获取库中三方球队列表
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCodes),null, Lists.newArrayList(thirdTeamSourceIds));
            //数据源赛事ID和三方球队关系
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言code
            Set<Long> teamNnameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            //获取标准球队列表
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));

            //获取多语言列表
            List<Long> nameCodes = new LinkedList<>();
            nameCodes.addAll(thirdTournamentNameCodes);
            nameCodes.addAll(standardTournamentNameCodes);
            nameCodes.addAll(teamNnameCodes);
            Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(nameCodes);

            //转换后的分页对象
            PageModel<List<ThirdMatchHistoryStatisticsBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchHistoryStatisticsBO> resList = new LinkedList<>();
            for (ThirdMatchHistoryStatistics item: resPage) {
                try{
                    ThirdMatchHistoryStatisticsBO itemBo = new ThirdMatchHistoryStatisticsBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    //联赛信息处理
                    ThirdSportTournament thirdSportTournament = thirdTournamentSourceId2Obj.get(itemBo.getDataSourceCode() + FIX + item.getSportId() +FIX + itemBo.getThirdTournamentSourceId());
                    if(null != thirdSportTournament){
                        List<LanguageInternation> languageList;
                        Long referenceId = thirdSportTournament.getReferenceId();
                        if(null != referenceId && referenceId != 0){
                            itemBo.setStandardTournamentId(thirdSportTournament.getReferenceId());
                            languageList = nameCode2Languages.get(standardTournamentId2NameCode.get(referenceId));
                        }else{
                            languageList = nameCode2Languages.get(thirdSportTournament.getNameCode());
                        }
                        if(!CollectionUtils.isEmpty(languageList)){
                            itemBo.setTournamentIl8nList(getI18nItemBOList(languageList));
                        }
                    }
                    //比分信息特殊处理
                    String homeTeamScore = itemBo.getHomeTeamScore();
                    if(StringUtils.isBlank(homeTeamScore)){
                        if(StringUtils.isNotBlank(itemBo.getHomeTeamScoreD01())){
                            itemBo.setHomeTeamScore(itemBo.getHomeTeamScoreD01());
                        }
                    }else{
                        if(homeTeamScore.contains(FIX)){
                            String[] scores = homeTeamScore.split(FIX);
                            itemBo.setHomeTeamScore(scores[0]);
                            itemBo.setHomeTeamPtScore(scores[1]);
                        }
                    }
                    String awayTeamScore = itemBo.getAwayTeamScore();
                    if(StringUtils.isBlank(awayTeamScore)){
                        if(StringUtils.isNotBlank(itemBo.getAwayTeamScoreD01())){
                            itemBo.setAwayTeamScore(itemBo.getAwayTeamScoreD01());
                        }
                    }else{
                        if(awayTeamScore.contains(FIX)){
                            String[] scores = awayTeamScore.split(FIX);
                            itemBo.setAwayTeamScore(scores[0]);
                            itemBo.setAwayTeamPtScore(scores[1]);
                        }
                    }

                    //主队名称多语言处理
                    ThirdSportTeam homeThirdSportTeam = thirdTeamSourceId2Team.get(itemBo.getDataSourceCode() + FIX + itemBo.getSportId()+ FIX + itemBo.getHomeTeamId());
                    if(!Objects.isNull(homeThirdSportTeam)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(homeThirdSportTeam.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            itemBo.setStandardHomeTeamId(standardSportTeamDetail.getId());
                            itemBo.setHomeTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                itemBo.setTeamHomeLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                itemBo.setTeamHomeLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                        }else{
                            List<LanguageInternation> languageList = nameCode2Languages.get(homeThirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                itemBo.setHomeTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                            if(StringUtils.isNotBlank(homeThirdSportTeam.getLogoUrl())){
                                itemBo.setTeamHomeLogo(homeThirdSportTeam.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(homeThirdSportTeam.getLogoUrlThumb())){
                                itemBo.setTeamHomeLogoUrlThumb(homeThirdSportTeam.getLogoUrlThumb());
                            }
                        }
                    }

                    //客队名称多语言处理
                    ThirdSportTeam awayThirdSportTeam = thirdTeamSourceId2Team.get(itemBo.getDataSourceCode() + FIX + itemBo.getSportId()+ FIX + itemBo.getAwayTeamId());
                    if(!Objects.isNull(awayThirdSportTeam)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(awayThirdSportTeam.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            itemBo.setStandardAwayTeamId(standardSportTeamDetail.getId());
                            itemBo.setAwayTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                itemBo.setTeamAwayLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                itemBo.setTeamAwayLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                        }else{
                            List<LanguageInternation> languageList = nameCode2Languages.get(awayThirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                itemBo.setAwayTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                            if(StringUtils.isNotBlank(awayThirdSportTeam.getLogoUrl())){
                                itemBo.setTeamAwayLogo(awayThirdSportTeam.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(awayThirdSportTeam.getLogoUrlThumb())){
                                itemBo.setTeamAwayLogoUrlThumb(awayThirdSportTeam.getLogoUrlThumb());
                            }
                        }
                    }

                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode() + FIX + itemBo.getThirdMatchSourceId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }
                    }
                    log.info("【getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事历史统计信息,源赛事ID={},标准联赛={},标准赛事={},标准主队={},标准客队={}"
                            ,itemBo.getThirdMatchSourceId(),itemBo.getStandardTournamentId(),itemBo.getStandardMatchId(),itemBo.getStandardHomeTeamId(),itemBo.getStandardAwayTeamId());
//                    log.info("【测试环境调试专用日志】【"+ PROJECT_ID_NOREALTIME +" ：getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事历史统计信息={}",JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事历史统计信息异常,源赛事ID="+item.getThirdMatchSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事历史统计列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }

    @Autowired
    private ThirdMatchLineupService thirdMatchLineupService;

    public Response<PageModel<List<ThirdMatchLineupBO>>> queryThirdMatchLineupPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        PageModel<ThirdMatchInfoDTO> page = request.getData();
        //缓存参数相同的页码
        String pageSizeKey = "queryThirdMatchLineupPage:"+page.getData().getBeginTime();
        Object pageSizeObj = redisService.get(pageSizeKey);
        if(pageSizeObj != null){
            page.setCurrent(Integer.valueOf(pageSizeObj.toString()) + 1);
        }
        Response response = Response.success();
        log.info("【queryThirdMatchLineupPage】【::"+request.getLinkId()+"::】分页获取三方赛事阵容列表开始,入参：{}", JSON.toJSONString(page));
        Page<ThirdMatchLineup> resPage = thirdMatchLineupService.getItemPageByModifyTime(page);
        log.info("【queryThirdMatchLineupPage】【::"+request.getLinkId()+"::】分页获取三方赛事阵容列表条数：{}，耗时={}",resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().map(obj->obj.getThirdMatchSourceId()).collect(Collectors.toSet());
            //数据来源列表 + FIX +
            Set<String> dataSourceCodes = resPage.stream().map(obj->obj.getDataSourceCode()).collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));
            //转换后的分页对象
            PageModel<List<ThirdMatchLineupBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchLineupBO> resList = new LinkedList<>();
            //是否需要缓存页码,如果一页修改时间完全一致
            boolean flag = true;
            Long modifyTime = resPage.get(0).getModifyTime();
            for (ThirdMatchLineup item: resPage) {
                try{
                    if(!modifyTime.equals(item.getModifyTime())){
                        flag = false;
                    }
                    ThirdMatchLineupBO itemBo = new ThirdMatchLineupBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    itemBo.setPositionName(PlayerPositionTypeEnum.convertMsg(itemBo.getPositionName()));
                    PlayerPositionTypeEnum positionTypeEnum = PlayerPositionTypeEnum.getItemByMsg(itemBo.getPositionName());
                    if(null != positionTypeEnum){
//                        itemBo.setPositionEnName(positionTypeEnum.getCode());
                        itemBo.setPositionEnName(positionTypeEnum.getNames().getString(LanguageTypeEnum.en.name()));
                        itemBo.setPositionNameList(getI18nItemBOList(positionTypeEnum.getNames()));
                    }
                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getThirdMatchSourceId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }
                        //109917 主客队反转
                        if (ConstantSystem.ONE.equals(thirdMatchInfo.getHomeAwayOpposite())){
                            if (ONE.equals(itemBo.getHomeAway())){
                                itemBo.setHomeAway(TWO);
                            } else if (TWO.equals(itemBo.getHomeAway())){
                                itemBo.setHomeAway(ONE);
                            }
                            if(StringUtils.isBlank(item.getHomeFormation())){
                                itemBo.setAwayFormation(thirdMatchInfo.getHomeFormation());
                            }
                            if(StringUtils.isBlank(item.getAwayFormation())){
                                itemBo.setHomeFormation(thirdMatchInfo.getAwayFormation());
                            }
                        } else {
                            if(StringUtils.isBlank(item.getHomeFormation())){
                                itemBo.setHomeFormation(thirdMatchInfo.getHomeFormation());
                            }
                            if(StringUtils.isBlank(item.getAwayFormation())){
                                itemBo.setAwayFormation(thirdMatchInfo.getAwayFormation());
                            }
                        }
                    }
                    log.info("【queryThirdMatchLineupPage】【::"+request.getLinkId()+"::】分页获取三方赛事阵容信息={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchLineupPage】【::"+request.getLinkId()+"::】分页获取三方赛事阵容信息异常,源赛事ID="+item.getThirdMatchSourceId()+",Exception:",e);
                }
            }
            //是否需要缓存页码
            if(flag && resPage.getTotal() >= page.getSize()){
                redisService.set(pageSizeKey,page.getCurrent(), RedisConfig.REDIS_HOUR_TIME);
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdMatchLineupPage】【::"+request.getLinkId()+"::】分页获取三方赛事阵容列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }else{
            redisService.del(pageSizeKey);
        }
        return response;
    }

    @Autowired
    private ThirdMatchHistoryOddsService thirdMatchHistoryOddsService;

    @Override
    public Response<PageModel<List<ThirdMatchHistoryOddsBO>>> queryThirdMatchHistoryOddsPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchHistoryOddsPage】【::"+request.getLinkId()+"::】分页获取三方赛事百家赔列表开始,入参：{}", JSON.toJSONString(request.getData()));
        Page<ThirdMatchHistoryOdds> resPage = thirdMatchHistoryOddsService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchHistoryOddsPage】【::"+request.getLinkId()+"::】分页获取三方赛事百家赔列表条数：{}，耗时={}"),resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().map(obj->obj.getThirdMatchSourceId()).collect(Collectors.toSet());
            //数据来源列表
            Set<String> dataSourceCodes = resPage.stream().map(obj->obj.getDataSourceCode()).collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));
            //转换后的分页对象
            PageModel<List<ThirdMatchHistoryOddsBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchHistoryOddsBO> resList = new LinkedList<>();
            for (ThirdMatchHistoryOdds item: resPage) {
                try{
                    ThirdMatchHistoryOddsBO itemBo = new ThirdMatchHistoryOddsBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    //泰森玩法说明： 1:让球胜负,2:胜平负,3:大小球
                    if(ONE.equals(item.getTypeId())){
                        itemBo.setHandicapVal0(item.getValue0());
                        itemBo.setHandicapVal(item.getValue());
                        itemBo.setHandicapOdds(item.getOddsJson());
                    }
                    if(TWO.equals(item.getTypeId())){
                        itemBo.setWinnerOdds(item.getOddsJson());
                    }
                    if(THREE.equals(item.getTypeId())){
                        itemBo.setOverUnderVal0(item.getValue0());
                        itemBo.setOverUnderVal(item.getValue());
                        itemBo.setOverUnderOdds(item.getOddsJson());
                    }
                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getThirdMatchSourceId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }
                    }
                    log.info("【queryThirdMatchHistoryOddsPage】【::"+request.getLinkId()+"::】分页获取三方赛事百家赔信息={}" ,itemBo.getId());
//                    log.info("【测试环境调试专用日志】【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchHistoryOddsPage】【::"+request.getLinkId()+"::】分页获取三方赛事百家赔信息={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchHistoryOddsPage】【::"+request.getLinkId()+"::】分页获取三方赛事百家赔信息异常,源赛事ID="+item.getThirdMatchSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdMatchHistoryOddsPage】【::"+request.getLinkId()+"::】分页获取三方赛事百家赔列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }

    @Autowired
    private ThirdMatchSidelinedService thirdMatchSidelinedService;

    @Override
    public Response<PageModel<List<ThirdMatchSidelinedBO>>> queryThirdMatchSidelinedPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchSidelinedPage】【::"+request.getLinkId()+"::】分页获取三方赛事伤停球员列表开始,入参：{}", JSON.toJSONString(request.getData()));
        Page<ThirdMatchSidelined> resPage = thirdMatchSidelinedService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchSidelinedPage】【::"+request.getLinkId()+"::】分页获取三方赛事停球员列表条数：{}，耗时={}"),resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().map(obj->obj.getThirdMatchSourceId()).collect(Collectors.toSet());
            //数据来源列表
            Set<String> dataSourceCodes = resPage.stream().map(obj->obj.getDataSourceCode()).collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));
            //转换后的分页对象
            PageModel<List<ThirdMatchSidelinedBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchSidelinedBO> resList = new LinkedList<>();
            for (ThirdMatchSidelined item: resPage) {
                try{
                    ThirdMatchSidelinedBO itemBo = new ThirdMatchSidelinedBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    itemBo.setPositionEnName(PlayerPositionTypeEnum.getPositionEnNameByMsg(item.getPosition()));
                    itemBo.setPositionName(item.getPosition());
                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getThirdMatchSourceId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }
                        //109917 主客队反转
                        if (ConstantSystem.ONE.equals(thirdMatchInfo.getHomeAwayOpposite())){
                            if (ONE.equals(itemBo.getHomeAway())){
                                itemBo.setHomeAway(TWO);
                            } else if (TWO.equals(itemBo.getHomeAway())){
                                itemBo.setHomeAway(ONE);
                            }
                        }
                    }
                    log.info("【queryThirdMatchSidelinedPage】【::"+request.getLinkId()+"::】分页获取三方赛事停球员信息={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchSidelinedPage】【::"+request.getLinkId()+"::】分页获取三方赛事停球员信息异常,源赛事ID="+item.getThirdMatchSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdMatchSidelinedPage】【::"+request.getLinkId()+"::】分页获取三方赛事停球员列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }

    @Autowired
    private ThirdMatchExInfomationService thirdMatchExInfomationService;

    @Override
    public Response<PageModel<List<ThirdMatchExInfomationBO>>> queryThirdMatchExInfomationPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchExInfomationPage】【::"+request.getLinkId()+"::】分页获取三方赛事比赛情报综合资讯数据列表开始,入参：{}", JSON.toJSONString(request.getData()));
        Page<ThirdMatchExInfomation> resPage = thirdMatchExInfomationService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchExInfomationPage】【::"+request.getLinkId()+"::】分页获取三方赛事比赛情报综合资讯数据列表条数：{}，耗时={}"),resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().map(obj->obj.getThirdMatchSourceId()).collect(Collectors.toSet());
            //数据来源列表
            Set<String> dataSourceCodes = resPage.stream().map(obj->obj.getDataSourceCode()).collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));
            PageModel<List<ThirdMatchExInfomationBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchExInfomationBO> resList = new LinkedList<>();
            for (ThirdMatchExInfomation item: resPage) {
                try{
                    ThirdMatchExInfomationBO itemBo = new ThirdMatchExInfomationBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    if(StringUtils.isNotBlank(item.getHomeCoach())){
                        itemBo.setHomeCoach(JSON.parseObject(item.getHomeCoach(),ThirdMatchCoachDTO.class));
                    }
                    if(StringUtils.isNotBlank(item.getAwayCoach())){
                        itemBo.setAwayCoach(JSON.parseObject(item.getAwayCoach(),ThirdMatchCoachDTO.class));
                    }
                    if(StringUtils.isNotBlank(item.getWinningOdds())){
                        itemBo.setWinningOdds(JSON.parseObject(item.getWinningOdds(),ThirdMatchWinningOddsDTO.class));
                    }
                    if(StringUtils.isNotBlank(item.getInformations())){
                        itemBo.setInforMatinsList(JSON.parseObject(item.getInformations(),new ArrayList<ThirdMatchInforMatinsDTO>().getClass()));
                    }
                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getThirdMatchSourceId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }
                    }
                    log.info("【queryThirdMatchExInfomationPage】【::"+request.getLinkId()+"::】获取三方赛事比赛情报综合资讯信息={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchExInfomationPage】【::"+request.getLinkId()+"::】获取三方赛事比赛情报综合资讯信息异常,源赛事ID="+item.getThirdMatchSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdMatchExInfomationPage】【::"+request.getLinkId()+"::】分页获取三方赛事比赛情报综合资讯数据列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }


    @Autowired
    private ThirdMatchFrontStatisticsService thirdMatchFrontStatisticsService;

    @Override
    public Response<PageModel<List<ThirdMatchFrontStatisticsBO>>> queryThirdMatchFrontStatisticsPage(Request<PageModel<ThirdMatchInfoDTO>> request){
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方赛事正面交手数据列表开始,入参：{}", JSON.toJSONString(request.getData()));
        Page<ThirdMatchFrontStatistics> resPage = thirdMatchFrontStatisticsService.getFrontStatisticsPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方赛事正面交手数据列表条数：{}，耗时={}"),resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().map(obj->obj.getThirdMatchSourceId()).collect(Collectors.toSet());
            //数据源球队ID列表
            Set<String> thirdTeamSourceIds = resPage.stream().flatMap(obj -> Stream.of(obj.getHomeTeamId(), obj.getAwayTeamId()))
                    .collect(Collectors.toSet());

            //数据来源列表
            Set<String> dataSourceCodes = resPage.stream().map(obj->obj.getDataSourceCode()).collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));

            //获取库中三方球队列表
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCodes),null, Lists.newArrayList(thirdTeamSourceIds));
            //数据源赛事ID和三方球队关系
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言code
            Set<Long> teamNnameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            //获取标准球队列表
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));
            //获取多语言列表
            List<Long> nameCodes = new LinkedList<>();
            nameCodes.addAll(teamNnameCodes);
            Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(nameCodes);

            //转换后的分页对象
            PageModel<List<ThirdMatchFrontStatisticsBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchFrontStatisticsBO> resList = new LinkedList<>();
            List<String> removeIds = new LinkedList<>();
            for (ThirdMatchFrontStatistics item: resPage) {
                try{
                    if (filterOutValueEqualZero(removeIds, item)) {
                        continue;
                    }
                    ThirdMatchFrontStatisticsBO itemBo = new ThirdMatchFrontStatisticsBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getThirdMatchSourceId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }
                    }
                    //主队名称多语言处理
                    ThirdSportTeam homeThirdSportTeam = thirdTeamSourceId2Team.get(itemBo.getDataSourceCode() + FIX + item.getSportId() + FIX + itemBo.getHomeTeamId());
                    if(!Objects.isNull(homeThirdSportTeam)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(homeThirdSportTeam.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            itemBo.setStandardHomeTeamId(standardSportTeamDetail.getId());
                            itemBo.setHomeTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            List<LanguageInternation> languageList = nameCode2Languages.get(homeThirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                itemBo.setHomeTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }
                    //客队名称多语言处理
                    ThirdSportTeam awayThirdSportTeam = thirdTeamSourceId2Team.get(itemBo.getDataSourceCode() + FIX + item.getSportId() + FIX + itemBo.getAwayTeamId());
                    if(!Objects.isNull(awayThirdSportTeam)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(awayThirdSportTeam.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            itemBo.setStandardAwayTeamId(standardSportTeamDetail.getId());
                            itemBo.setAwayTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            List<LanguageInternation> languageList = nameCode2Languages.get(awayThirdSportTeam.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                itemBo.setAwayTeamNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }
                    log.info("【queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】获取三方赛事正面交手数据={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：getThirdMatchHistoryStatisticsPage】【::"+request.getLinkId()+"::】获取三方赛事正面交手数据异常,源赛事ID="+item.getThirdMatchSourceId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            //85139 值全部为0的数据过滤掉
            if (resList.size()==0 && pageModel.getTotal()<=pageModel.getSize()) {
                response.setData(null);
            }
            if (removeIds.size()>0) {
                log.info("【queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方赛事正面交手数据过滤移除数据,{}" ,JSON.toJSONString(removeIds));
            }
            log.info("【queryThirdMatchFrontStatisticsPage】【::"+request.getLinkId()+"::】分页同步三方赛事正面交手数据列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }

    //85139 值全部为0的数据过滤掉
    private boolean filterOutValueEqualZero(List<String> removeIds, ThirdMatchFrontStatistics item) {
        if (item.getCountTotal()!=null && item.getCountTotal()==0 && item.getHomeWin()!=null && item.getHomeWin()==0
                && item.getAwayWin()!=null && item.getAwayWin()==0 && item.getDogfallTotal()!=null && item.getDogfallTotal()==0
                && item.getMoreThanOne()!=null && item.getMoreThanOne()==0 && item.getMoreThanTwo()!=null && item.getMoreThanTwo()==0
                && item.getMoreThanThree()!=null && item.getMoreThanThree()==0 && item.getAllScores()!=null && item.getAllScores()==0
                && item.getHomeNotLost()!=null && item.getHomeNotLost()==0 && item.getAwayNotLost()!=null &&item.getAwayNotLost()==0) {
            removeIds.add(item.getId());
            return true;
        }
        return false;
    }

    @Autowired
    private ThirdMatchTeamSkillStatisticsService thirdMatchTeamSkillStatisticsService;

    @Override
    public Response<PageModel<List<ThirdMatchTeamSkillStatisticsBO>>> queryThirdMatchTeamSkillStatisticsPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchTeamSkillStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事球队技术统计数据列表开始,入参：{}", JSON.toJSONString(request.getData()));
        Page<ThirdMatchTeamSkillStatistics> resPage = thirdMatchTeamSkillStatisticsService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchTeamSkillStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事球队技术统计数据列表条数：{}，耗时={}"),resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().map(obj->obj.getMatchId()).collect(Collectors.toSet());
            //数据来源列表
            Set<String> dataSourceCodes = resPage.stream().map(obj->obj.getDataSourceCode()).collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));


            //获取库中三方球队信息
            Set<String> thirdTeamSourceIds = resPage.stream().map(obj -> obj.getTeamId()).collect(Collectors.toSet());
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCodes), null, Lists.newArrayList(thirdTeamSourceIds));
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言
            List<Long> nameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Language = languageInternationService.getItemsByNameCodes(nameCodes);

            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));


            PageModel<List<ThirdMatchTeamSkillStatisticsBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());
            //返回结果集
            List<ThirdMatchTeamSkillStatisticsBO> resList = new LinkedList<>();
            for (ThirdMatchTeamSkillStatistics item: resPage) {
                try{
                    ThirdMatchTeamSkillStatisticsBO itemBo = new ThirdMatchTeamSkillStatisticsBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);

                    //三方赛事信息处理
                    ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getMatchId());
                    if(null != thirdMatchInfo){
                        itemBo.setThirdMatchId(thirdMatchInfo.getId());
                        if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                            itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        }

                        //获取三方球队信息
                        ThirdSportTeam thirdSportTeam = thirdTeamSourceId2Team.get(item.getDataSourceCode() + FIX + item.getSportId() + FIX + item.getTeamId());
                        if(!Objects.isNull(thirdSportTeam)){
                            //获取标准球队信息
                            StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(thirdSportTeam.getReferenceId());
                            if(!Objects.isNull(standardSportTeamDetail)){
                                itemBo.setStandardTeamId(standardSportTeamDetail.getId());
                                if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                    itemBo.setTeamLogo(standardSportTeamDetail.getLogoUrl());
                                }
                                if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                    itemBo.setTeamLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                                }
                                itemBo.setTeamNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                            }else{
                                if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrl())){
                                    itemBo.setTeamLogo(thirdSportTeam.getLogoUrl());
                                }
                                if(StringUtils.isNotBlank(thirdSportTeam.getLogoUrlThumb())){
                                    itemBo.setTeamLogoUrlThumb(thirdSportTeam.getLogoUrlThumb());
                                }
                                List<LanguageInternation> languageList = nameCode2Language.get(thirdSportTeam.getNameCode());
                                if(!CollectionUtils.isEmpty(languageList)){
                                    itemBo.setTeamNameIl8nList(getI18nItemBOList(languageList));
                                }
                            }
                        }
                    }
                    log.info("【queryThirdMatchTeamSkillStatisticsPage】【::"+request.getLinkId()+"::】获取三方赛事球队技术统计信息={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchTeamSkillStatisticsPage】【::"+request.getLinkId()+"::】获取三方赛事球队技术统计信息异常,源赛事ID="+item.getMatchId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdMatchTeamSkillStatisticsPage】【::"+request.getLinkId()+"::】分页获取三方赛事球队技术统计数据列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }


    @Autowired
    private ThirdMatchPromotionChartService thirdMatchPromotionChartService;

    @Override
    public Response<PageModel<List<ThirdMatchPromotionChartBO>>> queryThirdMatchPromotionChartPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【queryThirdMatchPromotionChartPage】【::"+request.getLinkId()+"::】分页获取三方杯赛淘汰赛事数据列表开始,入参：{}", JSON.toJSONString(request.getData()));
        Page<ThirdMatchPromotionChart> resPage = thirdMatchPromotionChartService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchPromotionChartPage】【::"+request.getLinkId()+"::】分页获取三方杯赛淘汰赛事数据列表条数：{}，耗时={}"),resPage.size(),System.currentTimeMillis() - beginTime);
        if(!CollectionUtils.isEmpty(resPage)){
            //数据来源编码
            Set<String> dataSourceCodes = resPage.stream().map(thi -> thi.getDataSourceCode()).collect(Collectors.toSet());
            //获取榜单三方数据源联赛ID
            Set<String> thirdTournamentSourceIds = resPage.stream().map(thi -> thi.getTournamentId()).collect(Collectors.toSet());
            //获取三方联赛列表
            List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentService.getItems(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdTournamentSourceIds));
            Map<String, ThirdSportTournament> thirdTournamentSourceId2Tournament = thirdSportTournaments.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTournamentSourceId(), thi -> thi));

            //获取对应标准联赛信息
            Set<Long> standardTournamentIds = thirdSportTournaments.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            List<StandardSportTournament> standardSportTournaments = standardSportTournamentService.getItems(Lists.newArrayList(standardTournamentIds));
            Map<Long, Long> standardTournamentId2NameCode = standardSportTournaments.stream().collect(Collectors.toMap(StandardSportTournament::getId, obj -> obj.getNameCode()));

            PageModel<List<ThirdMatchPromotionChartBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
            pageModel.setTotal(resPage.getTotal());

            //数据源赛事ID列表
            Set<String> thirdMatchSourceIds = resPage.stream().filter(obj->StringUtils.isNotBlank(obj.getMatchId()))
                    .map(obj->obj.getMatchId())
                    .collect(Collectors.toSet());
            //获取库中三方赛事列表
            Map<String, ThirdMatchInfo> thirdMatchSourceId2Obj = thirdMatchInfoService.getItemsByThirdMatchSourceIds(Lists.newArrayList(dataSourceCodes), Lists.newArrayList(thirdMatchSourceIds))
                    .stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode()+ FIX +thi.getThirdMatchSourceId(), thi -> thi));

            //获取库中三方球队信息
            Set<String> thirdTeamSourceIds = resPage.stream().map(obj -> obj.getTeam1Id()).collect(Collectors.toSet());
            thirdTeamSourceIds.addAll(resPage.stream().map(obj -> obj.getTeam2Id()).collect(Collectors.toSet()));
            List<ThirdSportTeam> thirdSportTeams = thirdSportTeamService.getItemsByThirdTeamSourceIds(Lists.newArrayList(dataSourceCodes), null, Lists.newArrayList(thirdTeamSourceIds));
            Map<String, ThirdSportTeam> thirdTeamSourceId2Team = thirdSportTeams.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + FIX + thi.getSportId() + FIX + thi.getThirdTeamSourceId(), thi -> thi));

            //获取三方球队多语言
            List<Long> nameCodes = thirdSportTeams.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Language = languageInternationService.getItemsByNameCodes(nameCodes);

            //获取标准球队ID列表
            Set<Long> standardTeamIds = thirdSportTeams.stream().map(obj -> obj.getReferenceId()).collect(Collectors.toSet());
            Map<Long, StandardSportTeamDetail> standardTeamId2Team = standardSportTeamService.getItemByStandardTeamIds(Lists.newArrayList(standardTeamIds))
                    .stream().collect(Collectors.toMap(StandardSportTeamDetail::getId, thi -> thi));

            //返回结果集
            List<ThirdMatchPromotionChartBO> resList = new LinkedList<>();
            for (ThirdMatchPromotionChart item: resPage) {
                try{
                    ThirdMatchPromotionChartBO itemBo = new ThirdMatchPromotionChartBO();
                    //拷贝信息
                    BeanUtils.copyProperties(item, itemBo);
                    if (item.getBeginTime()!=null) {
                        itemBo.setBeginTime(item.getBeginTime().getTime());
                    }
                    //获取三方联赛信息
                    ThirdSportTournament thirdSportTournament = thirdTournamentSourceId2Tournament.get(item.getDataSourceCode() + FIX + item.getSportId() + FIX + item.getTournamentId());
                    if(Objects.isNull(thirdSportTournament)){
                        log.info("【queryThirdMatchPromotionChartPage】【::"+request.getLinkId()+"::】分页同步三方杯赛淘汰赛事数据,当前三方联赛数据为空,联赛源ID：{}",item.getTournamentId());
                        continue;
                    }
                    itemBo.setStandardTournamentId(thirdSportTournament.getReferenceId());
                    itemBo.setStandardTournamentNameCode(standardTournamentId2NameCode.get(thirdSportTournament.getReferenceId()));

                    //三方赛事信息处理
                    if(StringUtils.isNotBlank(item.getMatchId())){
                        ThirdMatchInfo thirdMatchInfo = thirdMatchSourceId2Obj.get(itemBo.getDataSourceCode()+ FIX +itemBo.getMatchId());
                        if(null != thirdMatchInfo){
                            itemBo.setThirdMatchId(thirdMatchInfo.getId());
                            if(null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0){
                                itemBo.setStandardMatchId(thirdMatchInfo.getReferenceId());
                            }
                        }
                    }

                    //获取三方球队主队信息
                    ThirdSportTeam thirdSportTeamHome = thirdTeamSourceId2Team.get(item.getDataSourceCode() + FIX + item.getSportId() + FIX + item.getTeam1Id());
                    if(!Objects.isNull(thirdSportTeamHome)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(thirdSportTeamHome.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            itemBo.setStandardHomeTeamId(standardSportTeamDetail.getId());
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                itemBo.setTeamHomeLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                itemBo.setTeamHomeLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                            itemBo.setTeamHomeNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            if(StringUtils.isNotBlank(thirdSportTeamHome.getLogoUrl())){
                                itemBo.setTeamHomeLogo(thirdSportTeamHome.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(thirdSportTeamHome.getLogoUrlThumb())){
                                itemBo.setTeamHomeLogoUrlThumb(thirdSportTeamHome.getLogoUrlThumb());
                            }
                            List<LanguageInternation> languageList = nameCode2Language.get(thirdSportTeamHome.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                itemBo.setTeamHomeNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }

                    //获取三方球队客队信息
                    ThirdSportTeam thirdSportTeamAway = thirdTeamSourceId2Team.get(item.getDataSourceCode() + FIX + item.getSportId() + FIX + item.getTeam2Id());
                    if(!Objects.isNull(thirdSportTeamAway)){
                        //获取标准球队信息
                        StandardSportTeamDetail standardSportTeamDetail = standardTeamId2Team.get(thirdSportTeamAway.getReferenceId());
                        if(!Objects.isNull(standardSportTeamDetail)){
                            itemBo.setStandardAwayTeamId(standardSportTeamDetail.getId());
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrl())){
                                itemBo.setTeamAwayLogo(standardSportTeamDetail.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(standardSportTeamDetail.getLogoUrlThumb())){
                                itemBo.setTeamAwayLogoUrlThumb(standardSportTeamDetail.getLogoUrlThumb());
                            }
                            itemBo.setTeamAwayNameIl8nList(getI18nItemBOList(standardSportTeamDetail.getIl8nNameList()));
                        }else{
                            if(StringUtils.isNotBlank(thirdSportTeamAway.getLogoUrl())){
                                itemBo.setTeamAwayLogo(thirdSportTeamAway.getLogoUrl());
                            }
                            if(StringUtils.isNotBlank(thirdSportTeamAway.getLogoUrlThumb())){
                                itemBo.setTeamAwayLogoUrlThumb(thirdSportTeamAway.getLogoUrlThumb());
                            }
                            List<LanguageInternation> languageList = nameCode2Language.get(thirdSportTeamAway.getNameCode());
                            if(!CollectionUtils.isEmpty(languageList)){
                                itemBo.setTeamAwayNameIl8nList(getI18nItemBOList(languageList));
                            }
                        }
                    }


                    //比分信息特殊处理
                    try{
                        int[] team1Scores = parseScore(item.getTeam1Score());
                        int[] team2Scores = parseScore(item.getTeam2Score());
                        //105054 【生产】【客户端】 【pc】 【产品】赛事分析积分榜未开赛产生0-0
                        if (StringUtils.isNotBlank(item.getTeam1Score())) {
                            itemBo.setTeam1Score(team1Scores[0]);
                        }
                        if (StringUtils.isNotBlank(item.getTeam2Score())) {
                            itemBo.setTeam2Score(team2Scores[0]);
                        }
                        itemBo.setTeam1PtScore(team1Scores[1] == 0 ? null:team1Scores[1]+"");
                        itemBo.setTeam2PtScore(team2Scores[1] == 0 ? null:team2Scores[1]+"");
                    }catch (Exception e){
                        log.error("queryThirdMatchPromotionChartPage,转换异常Exception:",e);
                    }

                    log.info("【queryThirdMatchPromotionChartPage】【::"+request.getLinkId()+"::】获取三方杯赛淘汰赛事信息={}" ,JSON.toJSONString(itemBo));
                    resList.add(itemBo);
                }catch (Exception e){
                    log.error("【"+ PROJECT_ID_NOREALTIME +" ：queryThirdMatchPromotionChartPage】【::"+request.getLinkId()+"::】获取三方杯赛淘汰赛事信息异常,ID="+item.getId()+",Exception:",e);
                }
            }
            response.setData(pageModel);
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【queryThirdMatchPromotionChartPage】【::"+request.getLinkId()+"::】分页获取三方杯赛淘汰赛事数据列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
            pageModel.setData(resList);
        }
        return response;
    }


    public static int[] parseScore(String text) {
        if (text == null || StringUtils.isBlank(text)) {
            return new int[]{0, 0};
        }
        // 去掉所有空格
        text = text.replace(" ", "");

        int num1 = 0;
        int num2 = 0;

        if (text.contains("(") && text.contains(")")) {
            try {
                num1 = Integer.parseInt(text.substring(0, text.indexOf("(")));
                num2 = Integer.parseInt(text.substring(text.indexOf("(") + 1, text.indexOf(")")));
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // 解析失败时保持默认值 0
            }
        } else {
            try {
                num1 = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // 忽略，保持默认值 0
            }
        }
        return new int[]{num1, num2};
    }



}
