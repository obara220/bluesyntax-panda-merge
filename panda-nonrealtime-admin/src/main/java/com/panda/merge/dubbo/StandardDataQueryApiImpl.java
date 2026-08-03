package com.panda.merge.dubbo;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.panda.merge.api.IStandardDataQueryApi;
import com.panda.merge.bo.*;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMatchEventResultMessage;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 查询接口
 * @author  tell
 * @since   2020年9月10日13:40:13
 * */
@Slf4j
@Component
@DubboService(timeout = 1000000)
public class StandardDataQueryApiImpl extends BaseProcessor implements IStandardDataQueryApi {

    @Autowired
    private StandardSportTypeService iStandardSportTypeSupport;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private ConfigMarketDisplayTradeService configMarketDisplayTradeService;
    @Autowired
    private StandardSportTeamService standardSportTeamService;
    @Autowired
    private StandardSportRegionService standardSportRegionService;
    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;
    @Autowired
    private StandardMarketCategoryFieldService standardMarketCategoryFieldService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdSportTeamService thirdSportTeamService;
    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;
    @Autowired
    private ThirdVideoBoardCastRecordService thirdVideoBoardCastRecordService;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdMatchLineupService thirdMatchLineupService;
    @Autowired
    private MarketCategoryTemplateRelationService mctRelationService;
    @Autowired
    private StandardMatchResultService smResultService;
    @Autowired
    private ThirdSportTeamRankingService thirdSportTeamRankingService;
    @Autowired
    private ThirdSportTournamentService thirdSportTournamentService;


    @Override
    public Response<List<StandardSportTypeBO>> queryStandardSportTypePage(Request<StandardSportTypeDTO> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_TYPE_PAGE+"】【::"+request.getLinkId()+"::】查询标准体育类型列表(含多语言信息)开始,入参：{}",JSON.toJSONString(request.getData()));
        List<StandardSportType> standardSportTypeList = iStandardSportTypeSupport.getItemListByModifyTime(request.getData());
        //运动类型国际化
        List<Long> nameCodes = standardSportTypeList.stream().map(obj -> obj.getNameCode()).filter(obj -> obj!=null).collect(Collectors.toList());
        Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(nameCodes);
        //将查询出的标准联赛数据，转换成返回对象DTO
        List<StandardSportTypeBO> resList = new LinkedList<>();
        for (StandardSportType standardSportType: standardSportTypeList) {
            StandardSportTypeBO standardSportTypeBO = new StandardSportTypeBO();
            BeanUtils.copyProperties(standardSportType, standardSportTypeBO);
            List<LanguageInternation> languageList = nameCode2Languages.get(standardSportType.getNameCode());
            if(!CollectionUtils.isEmpty(languageList)){
                standardSportTypeBO.setIl8nNameList(getI18nItemBOList(languageList));
            }
            resList.add(standardSportTypeBO);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_TYPE_PAGE+"】【::"+request.getLinkId()+"::】查询标准体育类型列表(含多语言信息)结束,返回结果 ：{}" ,JSON.toJSONString(response));
        response.setData(resList);
        return response;
    }

    @Override
    public Response<PageModel<List<StandardSportTournamentBO>>> querySportTournamentPage(Request<PageModel<StandardSportTournamentDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_TOURNAMENT_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准联赛列表开始,入参：{}",JSON.toJSONString(request.getData()));
        Page<StandardSportTournamentDetail> resPage = standardSportTournamentService.getItemPageByModifyTime(request.getData());
        //联赛名称国际化
        Set<Long> nameCodes = resPage.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
        Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(Lists.newArrayList(nameCodes));
        //转换后的分页对象
        PageModel<List<StandardSportTournamentBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<StandardSportTournamentBO> resList = new LinkedList<>();
        for (StandardSportTournamentDetail standardSportTournament: resPage) {
            StandardSportTournamentBO standardSportTournamentBO = new StandardSportTournamentBO();
            BeanUtils.copyProperties(standardSportTournament, standardSportTournamentBO);
            standardSportTournamentBO.setPicUrl(standardSportTournament.getLogoUrl());
            standardSportTournamentBO.setRelatedDataSourceCoderNum(standardSportTournament.getRelatedDataSourceCoderNum());
            standardSportTournamentBO.setPicUrlThumb(standardSportTournament.getLogoUrlThumb());
            standardSportTournamentBO.setStandardSportRegionId(standardSportTournament.getRegionId());
            List<LanguageInternation> languageList = nameCode2Languages.get(standardSportTournament.getNameCode());
            if(!CollectionUtils.isEmpty(languageList)){
                standardSportTournamentBO.setIl8nNameList(getI18nItemBOList(languageList));
            }
            resList.add(standardSportTournamentBO);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_TOURNAMENT_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准联赛信息 ：{}" ,standardSportTournamentBO.getId());
        }
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_TOURNAMENT_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准联赛列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
        pageModel.setData(resList);
        return response;
    }


    @Override
    public Response<PageModel<List<StandardMatchInfoBO>>> querySportMathTeamPage(Request<PageModel<StandardMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事（球队）列表开始,入参：{}",JSON.toJSONString(request.getData()));
        Page<StandardMatchInfoDetail> resPage = standardMatchInfoService.getItemPageByModifyTime(request.getData());
        log.info(("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事列表耗时={},总条数:{}"),System.currentTimeMillis() - beginTime,resPage.getTotal());
        List<Long> standardMatchIds = resPage.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        //标准联赛列表
        Set<Long> standardTournamentIds = resPage.stream().map(obj -> obj.getStandardTournamentId()).collect(Collectors.toSet());
        List<StandardSportTournament> standardSportTournamentList = standardSportTournamentService.getItems(Lists.newArrayList(standardTournamentIds));
        //联赛区域信息
        Map<Long, Long> tournamentId2RegionId = standardSportTournamentList.stream().filter(obj->null != obj.getRegionId())
                .collect(Collectors.toMap(StandardSportTournament::getId,thi->thi.getRegionId()));
        //标准联赛nameCode
        Map<Long, Long> tournamentId2NameCode = standardSportTournamentList.stream().filter(obj->null != obj.getRegionId())
                .collect(Collectors.toMap(StandardSportTournament::getId,thi->thi.getNameCode()));
        Set<Long> leagueNameCodes = tournamentId2NameCode.values().stream().collect(Collectors.toSet());
        //获取标准赛事ID下所有三方赛事信息
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchIds, null);
        //根据标准赛事ID分组三方赛事信息
        Map<Long, List<ThirdMatchInfo>> referenceId2ThirdMatchInfoList = thirdMatchInfoList.stream().collect(Collectors.groupingBy(obj -> obj.getReferenceId()));
        //获取盘口数量信息
        List<ConfigMarketDisplayTrade> configMarketDisplayTradeList = configMarketDisplayTradeService.getItems(standardMatchIds);
        Map<Long, ConfigMarketDisplayTrade> standardMatchId2MarketDisplayTrade = new HashMap<>();
        if(!CollectionUtils.isEmpty(configMarketDisplayTradeList)){
            try{
                standardMatchId2MarketDisplayTrade = configMarketDisplayTradeList.stream().collect(Collectors.toMap(ConfigMarketDisplayTrade::getStandardMatchId, thi -> thi));
            }catch (Exception e){
                log.error("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事,获取盘口数量信息处理异常,Exception:",e);
            }
        }
        //获取场地国际化
        Set<Long> positionNameCodes = resPage.stream().map(obj -> obj.getMatchPositionNameCode()).collect(Collectors.toSet());
        positionNameCodes.addAll(leagueNameCodes);
        Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(Lists.newArrayList(positionNameCodes));
        //转换后的分页对象
        PageModel<List<StandardMatchInfoBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<StandardMatchInfoBO> resList = new LinkedList<>();
        //1：网羽乒斯  一定是 不显示中立场
        List<Long> sportIds_1 = Lists.newArrayList(StandardSportTypeEnum.Tennis.code, StandardSportTypeEnum.Badminton.code, StandardSportTypeEnum.TableTennis.code, StandardSportTypeEnum.Snooker.code);
        for (StandardMatchInfoDetail standardMatchInfo: resPage) {
            try{
                if(sportIds_1.contains(standardMatchInfo.getSportId())){
                    standardMatchInfo.setNeutralGround(ZERO);
                }else{
                    //2：补充区域下足蓝等电竞赛事不显示 中立场
                    Long regionId = tournamentId2RegionId.get(standardMatchInfo.getStandardTournamentId());
                    if(regionIds.contains(regionId)){
                        standardMatchInfo.setNeutralGround(ZERO);
                    }
                }
                StandardMatchInfoBO standardMatchInfoBO = new StandardMatchInfoBO();
                //获取开售信息
                StandardSportMarketSell standardSportMarketSell = standardMatchInfo.getMarketSell();
                if(null != standardSportMarketSell){
                    BeanUtils.copyProperties(standardSportMarketSell, standardMatchInfoBO);
                }
                //拷贝库中标准赛事信息
                BeanUtils.copyProperties(standardMatchInfo, standardMatchInfoBO);
                //设置标准联赛namecode
                Long standardTournamentNameCode = tournamentId2NameCode.get(standardMatchInfo.getStandardTournamentId());
                standardMatchInfoBO.setLeagueAsNameCode(standardTournamentNameCode);
                List<LanguageInternation> il8nLeagueAsNameList = nameCode2Languages.get(standardTournamentNameCode);
                if(!CollectionUtils.isEmpty(il8nLeagueAsNameList)){
                    standardMatchInfoBO.setIl8nLeagueAsNameList(getI18nItemBOList(il8nLeagueAsNameList));
                }else{
                    log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事（球队）列表处理中,标准联赛未找到对应多语言" +
                                    ",标准赛事ID={},,标准联赛ID={},nameCode={}",standardMatchInfo.getId(),standardMatchInfo.getStandardTournamentId(),standardTournamentNameCode);
                }
                //设置盘口数量信息
                ConfigMarketDisplayTrade marketDisplayTrade = standardMatchId2MarketDisplayTrade.get(standardMatchInfo.getId());
                if(null != marketDisplayTrade){
                    standardMatchInfoBO.setDisplayMarketCount(marketDisplayTrade.getDisplayMarketCount());
                    standardMatchInfoBO.setLiveMarketCount(marketDisplayTrade.getLiveMarketCount());
                }
                //获取场地国际化
                List<LanguageInternation> positionLanguageList = nameCode2Languages.get(standardMatchInfo.getMatchPositionNameCode());
                if(!CollectionUtils.isEmpty(positionLanguageList)){
                    standardMatchInfoBO.setIl8nMatchPositionList(getI18nItemBOList(positionLanguageList));
                }
                //获取球队，球队多语言，赛事球队关系
                List<StandardSportTeamDetail> standardSportTeamList = standardSportTeamService.getItemByStandardMatchId(standardMatchInfo.getId());
                List<StandardSportTeamBO> standardSportTeamBOList = new ArrayList<>();
                for (StandardSportTeamDetail standardSportTeam: standardSportTeamList) {
                    StandardSportTeamBO standardSportTeamBO = new StandardSportTeamBO();
                    BeanUtils.copyProperties(standardSportTeam, standardSportTeamBO);
                    StandardMatchTeamRelationBO standardMatchTeamRelationBO = new StandardMatchTeamRelationBO();
                    BeanUtils.copyProperties(standardSportTeam.getMatchTeamRelation(), standardMatchTeamRelationBO);
                    standardSportTeamBO.setMatchTeamRelation(standardMatchTeamRelationBO);
                    List<LanguageInternation> il8nNameList = standardSportTeam.getIl8nNameList();
                    standardSportTeamBO.setIl8nNameList(getI18nItemBOList(il8nNameList));
                    standardSportTeamBOList.add(standardSportTeamBO);
                }
                standardMatchInfoBO.setSportTeamList(standardSportTeamBOList);
                //标准赛事下的三方赛事信息
                List<ThirdMatchInfo> thirdMatchInfos = referenceId2ThirdMatchInfoList.get(standardMatchInfo.getId());
                //可能存在标准赛事下无三方赛事的情况（三方赛事被改绑到另一个标准赛事下面去了）
                if(!CollectionUtils.isEmpty(thirdMatchInfos)){
                    List<String> thirdMatchSourceIds = thirdMatchInfos.stream().map(obj -> obj.getThirdMatchSourceId()).collect(Collectors.toList());
                    log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事（球队）信息,标准赛事ID={},数据源编码={},数据源ID列表={}"
                            ,standardMatchInfoBO.getId(),standardMatchInfoBO.getDataSourceCode(),JSON.toJSONString(thirdMatchSourceIds));
                    //三方赛事信息列表
                    List<ThirdMatchInfoBO> thirdMatchInfoBos = new LinkedList<>();
                    //视频信息列表
                    List<VideoAnimationBO> videoBos = new LinkedList<>();
                    //动画信息列表
                    List<VideoAnimationBO> animationBos = new LinkedList<>();
                    for (ThirdMatchInfo thirdMatchInfo: thirdMatchInfos) {
                        //三方赛事信息
                        ThirdMatchInfoBO thirdMatchInfoBO = new ThirdMatchInfoBO();
                        thirdMatchInfoBO.setId(thirdMatchInfo.getId());
                        thirdMatchInfoBO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                        thirdMatchInfoBO.setTournamentId(thirdMatchInfo.getTournamentId());
                        thirdMatchInfoBO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
                        thirdMatchInfoBO.setHomeAwayOpposite(thirdMatchInfo.getHomeAwayOpposite());
                        //获取数据来源信息
                        DataSource source = dataSourceService.getItemByCode(thirdMatchInfo.getDataSourceCode());
                        //是否商业数据源（目前是支持赔率的则为商业数据源）
                        thirdMatchInfoBO.setCommerce(source.getCommerce());
                        //数据源不支持事件
                        if(source.getEventSupport().equals(ZERO)){
                            thirdMatchInfoBO.setEventSupport(ZERO);
                        }else{
                            //如果数据源是N01或N02，将eventSupport设置为0
                            if(DataSourceCodeEnum.N01.getCode().equals(thirdMatchInfo.getDataSourceCode())
                                    || DataSourceCodeEnum.N02.getCode().equals(thirdMatchInfo.getDataSourceCode())){
                                thirdMatchInfoBO.setEventSupport(ZERO);
                            }else{
                                //当前赛事是否支持事件
                                thirdMatchInfoBO.setEventSupport(thirdMatchInfo.getLiveOddSupport());
                            }
                            //优化单43014 ,事件源编码 == 三方赛事数据源编码
                            if(standardMatchInfoBO.getBusinessEvent().equals(thirdMatchInfo.getDataSourceCode())){
                                standardMatchInfoBO.setCompetitorType(thirdMatchInfo.getCompetitorType());
                                standardMatchInfoBO.setAccelerationFactor(thirdMatchInfo.getAccelerationFactor());
                            }
                        }
                        thirdMatchInfoBos.add(thirdMatchInfoBO);
//                        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】标准赛事：{}下三方赛事 ：{}" ,standardMatchInfo.getId(),thirdMatchInfoBO.getThirdMatchSourceId());
                        //如果当前三方赛事信息有视频
                        if(ONE.equals(thirdMatchInfo.getLmtMode())){
                            //动画&视频(同一个三方数据源赛事ID只会有一条记录)
                            ThirdVideoBoardCastRecord videoRecord = thirdVideoBoardCastRecordService.getItem(thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getDataSourceCode());
                            if (null != videoRecord) {
                                //SR赛事特殊处理
                                if(DataSourceCodeEnum.SR.getCode().equals(videoRecord.getDataSourceCode())){
                                    if(StringUtils.isBlank(videoRecord.getLiveVideoPathFlv()) && StringUtils.isBlank(videoRecord.getLiveVideoPathM3u8())){
                                        videoRecord.setLiveVideoPathFlv(videoRecord.getMatchId());
                                        videoRecord.setLiveVideoPathM3u8(videoRecord.getMatchId());
                                        VideoAnimationBO videoBO = new VideoAnimationBO();
                                        BeanUtils.copyProperties(videoRecord, videoBO);
                                        videoBos.add(videoBO);
                                    }
                                }

                                //只有视频的数据源,并且无需设置视频源编码的(电子赛事)
//                                if(DataSourceCodeEnum.getOnlyVideoCodeList().contains(videoRecord.getDataSourceCode())){
//                                    VideoAnimationBO videoBO = new VideoAnimationBO();
//                                    BeanUtils.copyProperties(videoRecord, videoBO);
//                                    videoBos.add(videoBO);
//                                }

                                //视频源编码对应的数据源需要同步给下游
                                if(thirdMatchInfo.getDataSourceCode().equals(standardSportMarketSell.getVideoCode())){
                                    if(StringUtils.isNotBlank(videoRecord.getLiveVideoPathFlv()) || StringUtils.isNotBlank(videoRecord.getLiveVideoPathM3u8())){
                                        VideoAnimationBO videoBO = new VideoAnimationBO();
                                        BeanUtils.copyProperties(videoRecord, videoBO);
                                        videoBos.add(ZERO,videoBO);
                                    }
                                }

                                //动画源编码对应的数据源需要同步给下游
                                if(thirdMatchInfo.getDataSourceCode().equals(standardSportMarketSell.getAnimationCode())){
                                    if(StringUtils.isNotBlank(videoRecord.getAniId())){
                                        VideoAnimationBO animationBO = new VideoAnimationBO();
                                        BeanUtils.copyProperties(videoRecord, animationBO);
                                        tsAnimationProcess(animationBO,thirdMatchInfo);
                                        animationBos.add(animationBO);
                                    }
                                }

                            }
                        }

                        //如果是V02数据源，查询联赛球队绑定
//                        if(DataSourceCodeEnum.TS.getCode().equals(thirdMatchInfo.getDataSourceCode())){
//                            QueryThirdRankingInfoDTO queryThirdRankingInfoDTO = new QueryThirdRankingInfoDTO();
//                            queryThirdRankingInfoDTO.setMatchId(thirdMatchInfo.getId());
//                            queryThirdRankingInfoDTO.setSeasonId(thirdMatchInfo.getSeasonId());
//                            List<ThirdSportTeamRanking> teamRankingList = thirdSportTeamRankingService.getTeamRankingBySeasonIdAndMatchId(queryThirdRankingInfoDTO);
//                            if(!CollectionUtils.isEmpty(teamRankingList)){
//                                List<Object> teamRankingBOList = new ArrayList<>(teamRankingList.size());
//                                for (ThirdSportTeamRanking oldRanking: teamRankingList) {
//                                    ThirdSportTeamRankingBO boRanking = new ThirdSportTeamRankingBO();
//                                    BeanUtils.copyProperties(oldRanking,boRanking);
//                                    teamRankingBOList.add(boRanking);
//                                }
//
//                            }
//                        }

                    }
                    //视频列表
                    standardMatchInfoBO.setVideoList(videoBos);
                    //动画列表
                    standardMatchInfoBO.setAnimationList(animationBos);
                    standardMatchInfoBO.setThirdMatchInfoList(thirdMatchInfoBos);
                }else{
                    log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事（球队）信息,标准赛事ID={},数据源编码={},三方赛事列表为空!"
                            ,standardMatchInfoBO.getId(),standardMatchInfoBO.getDataSourceCode());
                }
                standardMatchInfoBO.setMatchPeriodId(null);
                standardMatchInfoBO.setSecondsMatchStart(null);
                resList.add(standardMatchInfoBO);
            }catch (Exception e){
                log.error("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事（球队）列表处理异常 ："+JSON.toJSONString(standardMatchInfo)+",Exception:",e);
            }
        }
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SPORT_MATH_TEAM_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事（球队）列表结束,返回结果 ：{},入参:{}" ,JSON.toJSONString(response),JSON.toJSONString(request.getData()));
        pageModel.setData(resList);
        return response;
    }

    /**
     * 泰森动画特殊处理
     * */
    private void tsAnimationProcess(VideoAnimationBO animationBO,ThirdMatchInfo thirdMatchInfo){
        //TS动画主客队相反，特殊处理
        if(DataSourceCodeEnum.TS.getCode().equals(thirdMatchInfo.getDataSourceCode())){
            //如果主客队是相反
            if(ONE.equals(thirdMatchInfo.getHomeAwayOpposite())){
                String animationPath = animationBO.getAnimationPath();
                if(StringUtils.isNotBlank(animationPath) && animationPath.contains("reverse=0")){
                    animationBO.setAnimationPath(animationPath.replaceAll("reverse=0","reverse=1"));
                }
                String animation3Paths = animationBO.getAnimation3Paths();
                if(StringUtils.isNotBlank(animation3Paths) && animation3Paths.contains("reverse=0")){
                    animationBO.setAnimation3Paths(animation3Paths.replaceAll("reverse=0","reverse=1"));
                }
            }
            //主客队是否相反
            animationBO.setReverse(thirdMatchInfo.getHomeAwayOpposite());
        }
    }



    @Override
    public Response<PageModel<List<StandardSportRegionBO>>> queryStandardSportRegionPage(Request<PageModel<StandardSportRegionDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_REGION_PAGE+"】【::"+request.getLinkId()+"::】分页查询体育区域列表开始,入参：{}",JSON.toJSONString(request.getData()));
        Page<StandardSportRegion> resPage = standardSportRegionService.getItemPageByModifyTime(request.getData());
        //国际化信息处理
        List<Long> nameCodeList = resPage.stream().map(v -> v.getNameCode()).collect(Collectors.toList());
        Map<Long, List<LanguageInternation>> itemsByNameCodes = languageInternationService.getItemsByNameCodes(nameCodeList);
        //转换后的分页对象
        PageModel<List<StandardSportRegionBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<StandardSportRegionBO> resList = new ArrayList<>();
        for (StandardSportRegion oldItem: resPage) {
            StandardSportRegionBO itemBO = new StandardSportRegionBO();
            BeanUtils.copyProperties(oldItem, itemBO);
            itemBO.setIl8nNameList(getI18nItemBOList(itemsByNameCodes.get(oldItem.getNameCode())));
            resList.add(itemBO);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_REGION_PAGE+"】【::"+request.getLinkId()+"::】分页查询体育区域信息 ：{}" ,itemBO.getId());
        }
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_REGION_PAGE+"】【::"+request.getLinkId()+"::】分页查询体育区域列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
        pageModel.setData(resList);
        return response;
    }


    @Autowired
    private StandardMarketCategoryService standardMarketCategoryService;
    @Autowired
    private I18nMarketCategoryService i18nMarketCategoryService;

    @Override
     public Response<PageModel<List<StandardMarketCategoryBO>>> queryStandardSportMarketCategoryPage(Request<PageModel<StandardSportMarketCategoryDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_MARKET_CATEGORY_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准玩法玩，法投注项列表开始,入参：{}",JSON.toJSONString(request.getData()));
        Page<StandardMarketCategoryDetail> resPage = standardMarketCategoryService.getItemPageByModifyTime(request.getData());
        //全部标准玩法nameCode
        List<Long> categoryNameCodes = resPage.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
        Map<Long, List<I18nMarketCategory>> nameCode2I18ns = i18nMarketCategoryService.getItemsByNameCodes(categoryNameCodes);
        //全部标准玩法ID
        List<Long> standardCategoryIds = resPage.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        //获取全部标准玩法投注项信息
        List<StandardMarketCategoryFieldDetail> marketCategoryFieldAllList = standardMarketCategoryFieldService.getItems(standardCategoryIds);
        //获取全部运动类型玩法信息
        List<StandardSportMarketCategoryDetail> sportMarketCategoryAllList = standardSportMarketCategoryService.getItems(standardCategoryIds);
        //转换后的分页对象
        PageModel<List<StandardMarketCategoryBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<StandardMarketCategoryBO> resList = new ArrayList<>();
        for (StandardMarketCategoryDetail oldItem: resPage) {
            StandardMarketCategoryBO itemBO = new StandardMarketCategoryBO();
            BeanUtils.copyProperties(oldItem, itemBO);
            List<I18nMarketCategory> il8nNameList = nameCode2I18ns.get(oldItem.getNameCode());
            if(!CollectionUtils.isEmpty(il8nNameList)){
                itemBO.setNameI18n(getI18nItemBOList(il8nNameList));
            }
            //根据玩法ID获取玩法投注项信息
            List<StandardMarketCategoryFieldDetail> marketCategoryFieldList = marketCategoryFieldAllList.stream().filter(obj -> obj.getMarketCategoryId().equals(oldItem.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(marketCategoryFieldList)){
                List<StandardMarketCategoryFieldBO> marketCategoryFieldBOList = new ArrayList<>();
                for (StandardMarketCategoryFieldDetail oldFieldItem: marketCategoryFieldList) {
                    StandardMarketCategoryFieldBO fieldItemBO = new StandardMarketCategoryFieldBO();
                    BeanUtils.copyProperties(oldFieldItem, fieldItemBO);
                    fieldItemBO.setNameI18n(getI18nItemBOList(oldFieldItem.getIl8nNameList()));
                    marketCategoryFieldBOList.add(fieldItemBO);
                }
                itemBO.setMarketCategoryFields(marketCategoryFieldBOList);
            }
            //运动类型对应标准玩法
            List<StandardSportMarketCategoryDetail> sportMarketCategoryList = sportMarketCategoryAllList.stream().filter(obj -> obj.getMarketCategoryId().equals(oldItem.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(sportMarketCategoryList)){
                List<StandardSportMarketCategoryBO> standardSportMarketCategoryBos = new LinkedList<>();
                for (StandardSportMarketCategoryDetail oldSportItem: sportMarketCategoryList) {
                    StandardSportMarketCategoryBO sportItemBO = new StandardSportMarketCategoryBO();
                    BeanUtils.copyProperties(oldSportItem, sportItemBO);
                    sportItemBO.setNameI18n(getI18nItemBOList(oldSportItem.getIl8nNameList()));
                    if( null != oldSportItem.getDescNameCode() && !Long.valueOf(ZERO).equals(oldSportItem.getDescNameCode()) ){
                        List<I18nMarketCategory> descI18ns = Lists.newArrayList(i18nMarketCategoryService.queryLanguageInternation(null,oldSportItem.getDescNameCode()).values());
                        sportItemBO.setDescI18n(getI18nItemBOList(descI18ns));
                    }

                    if ( null != oldSportItem.getDetailNameCode() && !Long.valueOf(ZERO).equals(oldSportItem.getDetailNameCode())  ) {
                        List<I18nMarketCategory> detailNameI18ns = Lists.newArrayList(i18nMarketCategoryService.queryLanguageInternation(null,oldSportItem.getDetailNameCode()).values());
                        if ( !CollectionUtils.isEmpty(detailNameI18ns) ) {
                            sportItemBO.setDetailNameI18n(getI18nItemBOList(detailNameI18ns));
                        } else {
                            sportItemBO.setDetailNameI18n(sportItemBO.getNameI18n());
                        }
                    }

                    if ( null != oldSportItem.getMainNameCode() && !Long.valueOf(ZERO).equals(oldSportItem.getMainNameCode())  ) {
                        List<I18nMarketCategory> mainNameI18ns = Lists.newArrayList(i18nMarketCategoryService.queryLanguageInternation(null,oldSportItem.getMainNameCode()).values());
                        if ( !CollectionUtils.isEmpty(mainNameI18ns) ) {
                            sportItemBO.setMainNameI18n(getI18nItemBOList(mainNameI18ns));
                        } else {
                            sportItemBO.setMainNameI18n(sportItemBO.getNameI18n());
                        }
                    }

                    standardSportMarketCategoryBos.add(sportItemBO);
                }
                itemBO.setSportMarketCategories(standardSportMarketCategoryBos);
            }
            resList.add(itemBO);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_MARKET_CATEGORY_PAGE+"】【::"+request.getLinkId()+"::】{} ：标准玩法玩法投注项信息 ：{}" ,itemBO.getId());
        }
        pageModel.setData(resList);
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_MARKET_CATEGORY_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准玩法玩，法投注项列表结束,返回结果 ：{}"  ,JSON.toJSONString(response));
        return response;
    }


    @Override
    @Deprecated
    public Response<PageModel<List<ThirdSportMarketBO>>> queryThirdSportMarketPage(Request<PageModel<ThirdSportMarketDTO>> request) {
        long beginTime = System.currentTimeMillis();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_SPORT_MARKET_PAGE+"】【::"+request.getLinkId()+"::】分页查询三方盘口列表开始,入参：{}",JSON.toJSONString(request.getData()));
        Response response = this.queryThirdSportMarket(request);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_SPORT_MARKET_PAGE+"】【::"+request.getLinkId()+"::】分页查询三方盘口列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }


    @Override
    @Deprecated
    public Response<PageModel<List<ThirdSportMarketBO>>> queryThirdSportMarketPageForReport(Request<PageModel<ThirdSportMarketDTO>> request) {
        long beginTime = System.currentTimeMillis();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_SPORT_MARKET_PAGE_FOR_REPORT+"】【::"+request.getLinkId()+"::】分页查询第三方盘口列表(统计使用)开始,入参：{}",JSON.toJSONString(request.getData()));
        Response response = this.queryThirdSportMarket(request);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_SPORT_MARKET_PAGE_FOR_REPORT+"】【::"+request.getLinkId()+"::】分页查询第三方盘口列表(统计使用)结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    /**
     * 第三方盘口查询
     */
    private Response<PageModel<List<ThirdSportMarketBO>>> queryThirdSportMarket(Request<PageModel<ThirdSportMarketDTO>> request) {
        Response response = Response.success();
        Page<ThirdSportMarket> resPage = thirdSportMarketService.getItemPageByModifyTime(request.getData());
        //转换后的分页对象
        PageModel<List<ThirdSportMarketBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<ThirdSportMarketBO> resList = new ArrayList<>();
        for (ThirdSportMarket oldItem: resPage) {
            ThirdSportMarketBO itemBO = new ThirdSportMarketBO();
            BeanUtils.copyProperties(oldItem, itemBO);
            //获取三方赛事信息
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(oldItem.getMatchId());
            if(null != thirdMatchInfo){
                itemBO.setSportId(thirdMatchInfo.getSportId());
                itemBO.setBeginTime(thirdMatchInfo.getBeginTime());
                if(null != thirdMatchInfo.getReferenceId() && !Long.valueOf(ZERO).equals(thirdMatchInfo.getReferenceId())){
                    //获取标准赛事信息
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getDetailItem(thirdMatchInfo.getReferenceId());
                    if(null != standardMatchInfo){
                        itemBO.setStandardMatchId(standardMatchInfo.getId());
                        itemBO.setOperateMatchStatus(standardMatchInfo.getOperateMatchStatus());
                    }
                }
            }
            resList.add(itemBO);
        }
        pageModel.setData(resList);
        response.setData(pageModel);
        return response;
    }


    @Override
    public Response<PageModel<List<ThirdMatchInfoBO>>> queryThirdMatchInfoPage(Request<PageModel<ThirdMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_MATCH_INFO_PAGE+"】【::"+request.getLinkId()+"::】分页查询三方赛事列表开始,入参：{}",JSON.toJSONString(request.getData()));
        ThirdMatchInfoDTO thirdMatchInfoDTO = request.getData().getData();
        if(null == thirdMatchInfoDTO.getBeginTime()){
            thirdMatchInfoDTO.setBeginTime(TimeUtils.millsSecondsEast8ZoneGmt() - MINS_1);
        }
        if(null == thirdMatchInfoDTO.getEndTime()){
            thirdMatchInfoDTO.setEndTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }
        Page<ThirdMatchInfoDetail> resPage = thirdMatchInfoService.getItemPageByModifyTime(request.getData());
        List<Long> matchIds = resPage.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        //获取全部赛事球队关系数据
        List<ThirdMatchTeamRelation> relationAllList = thirdMatchTeamRelationService.getItemsByMatchIds(matchIds);
        //获取全部三方球队信息,和球队多语言
        Set<Long> teamIds = relationAllList.stream().map(obj -> obj.getTeamId()).collect(Collectors.toSet());
        Map<Long, ThirdSportTeamDetail> teamId2Team = thirdSportTeamService.getItems(Lists.newArrayList(teamIds)).stream().collect(Collectors.toMap(ThirdSportTeamDetail::getId, thi -> thi));
        //转换后的分页对象
        PageModel<List<ThirdMatchInfoBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<ThirdMatchInfoBO> resList = new ArrayList<>();
        for (ThirdMatchInfoDetail oldItem: resPage) {
            ThirdMatchInfoBO itemBO = new ThirdMatchInfoBO();
            BeanUtils.copyProperties(oldItem, itemBO);
            //获取当前三方赛事的赛事球队关系
            List<ThirdMatchTeamRelation> relationList = relationAllList.stream().filter(obj -> obj.getMatchId().equals(oldItem.getId())).collect(Collectors.toList());
            for(ThirdMatchTeamRelation relation : relationList){
                //获取球队信息
                ThirdSportTeamDetail teamItem = teamId2Team.get(relation.getTeamId());
                if(null != teamItem){
                    ThirdSportTeamBO thirdSportTeamBO = new ThirdSportTeamBO();
                    BeanUtils.copyProperties(teamItem, thirdSportTeamBO);
                    //获取球队多语言
                    List<LanguageInternation> languageList = teamItem.getIl8nNameList();
                    if(!CollectionUtils.isEmpty(languageList)){
                        thirdSportTeamBO.setIl8nNameList(getI18nItemBOList(languageList));
                    }
                    //转换赛事球队关系
                    ThirdMatchTeamRelationBO relationBO = new ThirdMatchTeamRelationBO();
                    BeanUtils.copyProperties(relation, relationBO);
                    thirdSportTeamBO.setMatchTeamRelation(relationBO);
                    //主客队标识, 主队：home 、客队：away
                    if(AWAY.equalsIgnoreCase(relationBO.getMatchPosition())){
                        itemBO.setAwaySportTeam(thirdSportTeamBO);
                    }
                    if(HOME.equalsIgnoreCase(relationBO.getMatchPosition())){
                        itemBO.setHomeSportTeam(thirdSportTeamBO);
                    }
                }
            }
            resList.add(itemBO);
        }
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_THIRD_MATCH_INFO_PAGE+"】【::"+request.getLinkId()+"::】分页查询三方赛事列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
        pageModel.setData(resList);
        return response;
    }

    @Override
    public Response<List<DataSourceBO>> queryDataSourcePage(Request<DataSourceDTO> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_DATA_SOURCE_PAGE+"】【::"+request.getLinkId()+"::】分页查询数据来源列表开始,入参：{}",JSON.toJSONString(request.getData()));
        List<DataSource> dataSourceList = dataSourceService.getItemList(request.getData());
        //将查询出的数据源数据，转换成返回对象BO
        List<DataSourceBO> resList = new ArrayList<>();
        for(DataSource oldItem:dataSourceList){
            DataSourceBO itemBO = new DataSourceBO();
            BeanUtils.copyProperties(oldItem,itemBO);
            resList.add(itemBO);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_DATA_SOURCE_PAGE+"】【::"+request.getLinkId()+"::】分页查询数据来源列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
        response.setData(resList);
        return response;
    }
	@Override
	public Response<List<MarketCategoryTemplateRelationBO>> queryMarketCategoryTemplateRelation(Request<Integer> request) {
		log.info("【queryMarketCategoryTemplateRelation】【::"+request.getLinkId()+"::】查询模板玩法关系基础数据开始,入参：{}",JSON.toJSONString(request.getData()));
		Response<List<MarketCategoryTemplateRelationBO>> response = Response.success();
		List<MarketCategoryTemplateRelationBO> mctRelations = mctRelationService.getMarketCategoryTemplateRelation();
		response.setDataSourceTime(System.currentTimeMillis());
		log.info("【queryMarketCategoryTemplateRelation】【::"+request.getLinkId()+"::】查询模板玩法关系基础数据结束,返回结果 ：{}" ,JSON.toJSONString(response));
        response.setData(mctRelations);
		return response;
	}

	@Override
	public Response<StandardMatchEventResultMessage> queryStandardMatchResult(Request<StandardMatchResultDTO> reqDto) {
		log.info("【queryStandardMatchResult】【::"+reqDto.getLinkId()+"::】查询标准赛果信息开始,入参：{}",JSON.toJSONString(reqDto.getData()));
		Response<StandardMatchEventResultMessage> response = Response.success();
		StandardMatchResult smResult = smResultService.getStandardMatchResult(reqDto.getData());
		if(smResult == null) {
			return response;
		}
		StandardMatchEventResultMessage smrMessage = new StandardMatchEventResultMessage();
		BeanUtils.copyProperties(smResult, smrMessage);
		response.setDataSourceTime(System.currentTimeMillis());
		log.info("【queryStandardMatchResult】【::"+reqDto.getLinkId()+"::】查询标准赛果信息结束,返回结果 ：{}" ,JSON.toJSONString(response));
        response.setData(smrMessage);
		return response;
	}

    @Override
    public Response<PageModel<List<StandardTournamentRuleBO>>> queryTournamentRulePage(Request<PageModel<StandardTournamentRuleDTO>> request) {

        long beginTime = System.currentTimeMillis();
        Response response = Response.success();

        log.info("【"+QUERY_TOURNAMENT_RULE_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准联赛规则列表开始,入参：{}",JSON.toJSONString(request.getData()));

        request.getData().getData().setDataSourceCode(DataSourceCodeEnum.TS.code);
        Page<ThirdSportTournament> resPage = thirdSportTournamentService.getTournamentRulePage(request.getData());

        //转换后的分页对象
        PageModel<List<StandardTournamentRuleBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        List<StandardTournamentRuleBO> resList = new LinkedList<>();

        for (ThirdSportTournament thirdSportTournament: resPage) {

            StandardTournamentRuleBO standardTournamentRuleBO = new StandardTournamentRuleBO();
            standardTournamentRuleBO.setTournamentId(thirdSportTournament.getReferenceId());
            standardTournamentRuleBO.setZsTournamentRule(thirdSportTournament.getZsTournamentRule());

            resList.add(standardTournamentRuleBO);
            log.info("【"+QUERY_TOURNAMENT_RULE_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准联赛规则 ：{}" , JSONUtil.toJsonStr(standardTournamentRuleBO));
        }
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+QUERY_TOURNAMENT_RULE_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准联赛规则列表结束,返回结果 ：{}" ,JSON.toJSONString(response));
        pageModel.setData(resList);
        return response;
    }

}
