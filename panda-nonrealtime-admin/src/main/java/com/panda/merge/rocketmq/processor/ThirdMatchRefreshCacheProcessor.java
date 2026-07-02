package com.panda.merge.rocketmq.processor;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.message.MatchAssociationMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.mapper.ThirdMatchHistoryOddsMapper;
import com.panda.merge.mapper.ThirdVideoBoardCastRecordMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 赛程项目修改三方赛事或者标准赛事信息后，通知本项目刷新缓存
 * @author :  tell
 * @since 2020年12月1日16:05:11
 */
@Slf4j
@Validated
@Component
public class ThirdMatchRefreshCacheProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    /**
     * 多条三方赛事缓存刷新
     * @param  request       请求参数
     * */
    @ExceptionHelper
    public Response matchListRefreshCache(@Valid Request<List<MatchAssociationMessage>> request) {
        //循环清除三方赛事缓存信息
        for(MatchAssociationMessage obj : request.getData()){
            matchRefreshCache(request.getLinkId(),obj.getThirdMatchId(),obj.getStandardMatchId());
        }
        return Response.success();
    }



    /**
     * 单条三方赛事缓存刷新
     * @param  linkId             线路ID
     * @param  thirdMatchId       三方赛事ID
     * @param  standardMatchId    标准赛事ID
     * */
    public void matchRefreshCache(String linkId,Long thirdMatchId,Long standardMatchId) {
        //通过三方赛事id查询三方赛事信息 并 刷新缓存
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(thirdMatchId);
        if(null != thirdMatchInfo){
            log.info("::{}::matchRefreshCache，刷新后三方赛事缓存：{}",linkId,thirdMatchInfo);
            //传入标准赛事信息是否需要单独刷新标准赛事缓存
            Boolean flag = false;
            //刷新当前三方赛事对应的标准赛事缓存
            if(!Objects.isNull(thirdMatchInfo.getReferenceId())){
                //当前三方赛事对应的标准赛事信息
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(thirdMatchInfo.getReferenceId());
                log.info("::{}::matchRefreshCache，刷新后标准赛事缓存：{}",linkId,standardMatchId);
                //刷新开售缓存并返回最新开售信息
                StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchId);
                log.info("::{}::matchRefreshCache，刷新后标准赛事开售缓存：{}",linkId,standardSportMarketSell);
                //传入标准赛事ID和当前标准赛事不一样，则需要单独刷新标准赛事缓存
                if(null != standardMatchInfo && null != standardMatchId && !standardMatchInfo.getId().equals(standardMatchId)){
                    flag = true;
                }
            }
            if(flag){
                StandardMatchInfo oldStandardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchId);
                log.info("::{}::matchRefreshCache，刷新后old标准赛事缓存{}",linkId,oldStandardMatchInfo);
            }
            //赛事分析相关的数据源赛事，需要额外操作部分数据
            if(DataSourceCodeEnum.getAnalysisCodeList().contains(thirdMatchInfo.getDataSourceCode())){
                log.info("::{}::matchRefreshCache，修改赛事分析相关的数据源数据：{}",linkId,thirdMatchInfo);
                updateTsMatchModifyTime(thirdMatchInfo,linkId);
            }
        }else{
            log.info("::{}::matchRefreshCache，未找到三方赛事{}信息！",linkId,thirdMatchId);
        }
    }


    /**
     * 如果是解绑和绑定泰森赛事，需要更新泰森的其他事数据的修改时间，方便下游同步到泰森赛事关联的最新标准赛事ID
     * */
    @Autowired
    private ThirdVideoBoardCastRecordMapper thirdVideoBoardCastRecordMapper;
    @Autowired
    private ThirdMatchHistoryOddsMapper thirdMatchHistoryOddsMapper;
    @Autowired
    private ThirdMatchHistoryStatisticsService thirdMatchHistoryStatisticsService;
    @Autowired
    private ThirdMatchLineupService thirdMatchLineupService;
    @Autowired
    private ThirdMatchExInfomationService thirdMatchExInfomationService;
    @Autowired
    private ThirdMatchSidelinedService thirdMatchSidelinedService;
    @Autowired
    private ThirdMatchFrontStatisticsService thirdMatchFrontStatisticsService;
    @Autowired
    private ThirdMatchTeamSkillStatisticsService thirdMatchTeamSkillStatisticsService;
    @Autowired
    private ThirdMatchPromotionChartService thirdMatchPromotionChartService;

    /**
     * 修改泰森赛事额外数据的修改时间，方便下游同步到最新绑定标准赛事关系
     * */
    private void updateTsMatchModifyTime(ThirdMatchInfo thirdMatchInfo,String linkId){
        try{
            //历史赛事数据
            ThirdMatchHistoryStatisticsExample matchExample = new ThirdMatchHistoryStatisticsExample();
            matchExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num1 = thirdMatchHistoryStatisticsService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(), matchExample);
            log.info("::{}::updateTsMatchModifyTime，刷新历史赛事条数：{}",linkId,num1);

            //视频数据
            ThirdVideoBoardCastRecord upVideo = new ThirdVideoBoardCastRecord();
            upVideo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            ThirdVideoBoardCastRecordExample videoExample = new ThirdVideoBoardCastRecordExample();
            videoExample.createCriteria().andMatchIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num2 = thirdVideoBoardCastRecordMapper.updateByExampleSelective(upVideo,videoExample);
            log.info("::{}::updateTsMatchModifyTime，刷新视频数据条数：{}",linkId,num2);

            //赛事阵容
            ThirdMatchLineupExample lineupExample = new ThirdMatchLineupExample();
            lineupExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num3 = thirdMatchLineupService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(),lineupExample);
            log.info("::{}::updateTsMatchModifyTime，刷新赛事阵容数据条数：{}",linkId,num3);

            //赛事情报综合资讯
            ThirdMatchExInfomation upExInfomation = new ThirdMatchExInfomation();
            upExInfomation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            ThirdMatchExInfomationExample exInfomationExample = new ThirdMatchExInfomationExample();
            exInfomationExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num4 = thirdMatchExInfomationService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(),exInfomationExample);
            log.info("::{}::updateTsMatchModifyTime，刷新赛事情报综合资讯条数：{}",linkId,num4);

            //赛事伤停球员
            ThirdMatchSidelinedExample sidelinedExample = new ThirdMatchSidelinedExample();
            sidelinedExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num5 = thirdMatchSidelinedService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(),sidelinedExample);
            log.info("::{}::updateTsMatchModifyTime，刷新赛事伤停球员数据条数：{}",linkId,num5);

            //赛事百家赔
            ThirdMatchHistoryOdds upOdds = new ThirdMatchHistoryOdds();
            upOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            ThirdMatchHistoryOddsExample oddsExample = new ThirdMatchHistoryOddsExample();
            oddsExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num6 = thirdMatchHistoryOddsMapper.updateByExampleSelective(upOdds,oddsExample);
            log.info("::{}::updateTsMatchModifyTime，刷新赛事百家赔数据条数：{}",linkId,num6);

            //正面交手数据
            ThirdMatchFrontStatisticsExample frontExample = new ThirdMatchFrontStatisticsExample();
            frontExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num7 = thirdMatchFrontStatisticsService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(),frontExample);
            log.info("::{}::updateTsMatchModifyTime，刷新正面交手数据条数：{}",linkId,num7);

            //赛事球队技术统计
            ThirdMatchTeamSkillStatisticsExample skillExample = new ThirdMatchTeamSkillStatisticsExample();
            skillExample.createCriteria().andMatchIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
            int num8 = thirdMatchTeamSkillStatisticsService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(),skillExample);
            log.info("::{}::updateTsMatchModifyTime，刷新赛事球队技术统计数据条数：{}",linkId,num8);

            //杯赛淘汰赛事
//            ThirdMatchPromotionChartExample promotionChartExample = new ThirdMatchPromotionChartExample();
//            promotionChartExample.createCriteria().andMatchIdEqualTo(thirdMatchInfo.getThirdMatchSourceId()).andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode());
//            int num9 = thirdMatchPromotionChartService.updateModifyTimeByExampleSelective(TimeUtils.millsSecondsEast8ZoneGmt(),promotionChartExample);
//            log.info("::{}::updateTsMatchModifyTime，刷新杯赛淘汰赛事数据条数：{}",linkId,num9);

        }catch (Exception e){
            log.error("::"+linkId+"::updateTsMatchModifyTime，刷新赛事相关信息异常！Exception:",e);
        }
    }


}
