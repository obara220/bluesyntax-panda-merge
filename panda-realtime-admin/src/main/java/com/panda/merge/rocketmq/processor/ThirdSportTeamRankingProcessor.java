package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdSportTeamRankingDTO;
import com.panda.merge.model.ThirdSportTeamRanking;
import com.panda.merge.rocketmq.producer.ThirdSportRankingProducer;
import com.panda.merge.service.ThirdSportTeamRankingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 联赛下球队排行榜单
 * @author tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Validated
@Component
public class ThirdSportTeamRankingProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportTeamRankingService thirdSportTeamRankingService;
    @Resource
    private ThirdSportRankingProducer thirdSportTeamRankingProducer;

    /**
     * 球队榜单数据处理
     * */
    public Response processTeamRankingData(@Valid Request<List<ThirdSportTeamRankingDTO>> request) {
        //默认数据源编码为V02
        String dataSourceCode = StringUtils.isBlank(request.getDataSourceCode()) ? DataSourceCodeEnum.TS.getCode() : request.getDataSourceCode();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_SPORT_TEAM_RANKING_API+"】【"+dataSourceCode+" ::"+request.getLinkId()+"::】联赛下球队排行榜单数据接收开始");
        long beginTime = System.currentTimeMillis();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_SPORT_TEAM_RANKING_API,request);
        Response response = Response.success();
        List<ThirdSportTeamRankingDTO> dtoItemList = request.getData();
        //组合当前榜单ID列表
        Set<String> ids = dtoItemList.stream().map(obj -> obj.getThirdSourceSeasonId() + FIX + obj.getRankingId() + FIX + obj.getThirdTeamSourceId()).collect(Collectors.toSet());
        //获取库中存在的榜单列表
        Map<String, ThirdSportTeamRanking> id2Ranking = thirdSportTeamRankingService.getItems(Lists.newArrayList(ids)).stream().collect(Collectors.toMap(ThirdSportTeamRanking::getId, i -> i));
        for (ThirdSportTeamRankingDTO dtoItem: dtoItemList) {
            if(StringUtils.isBlank(dtoItem.getDataSourceCode())){
                dtoItem.setDataSourceCode(dataSourceCode);
            }
            //获取标准赛种
            Long sportId = validateSportId(dtoItem.getDataSourceCode(), dtoItem.getSportId()+"");
            //组合当前榜单ID（三方数据源赛季ID+运动类型+球队ID）
            String id = dtoItem.getThirdSourceSeasonId() + FIX + dtoItem.getRankingId() + FIX + dtoItem.getThirdTeamSourceId();
            //本次操作对象
            ThirdSportTeamRanking item = new ThirdSportTeamRanking();
            BeanUtil.copyProperties(dtoItem, item);
            item.setId(id);
            item.setSportId(sportId);
            //获取库中榜单信息
            ThirdSportTeamRanking oldItem = id2Ranking.get(id);
            if(null == oldItem){
                item.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdSportTeamRankingService.saveTeamRanking(item,request.getLinkId());
            }else{
                if(oldItem.getEditStatus() != null && oldItem.getEditStatus()){
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_SPORT_TEAM_RANKING_API+"】【"+dataSourceCode+" ::"+request.getLinkId()+"::】本次接收到联赛下球队排行榜单数据被手动修改，跳过程序修改，ID：{}", id);
                    continue;
                }

                //103882【联赛积分】排名后四场赛事标注降级区，根据赛制排名最后3名的球队为降级球队
                if (StringUtils.isBlank(dtoItem.getPromotionCnName()) &&
                        StringUtils.isBlank(dtoItem.getPromotionEnName()) &&
                        StringUtils.isBlank(dtoItem.getPromotionId())) {
                    item.setPromotionCnName("");
                    item.setPromotionEnName("");
                    item.setPromotionId("");
                }

                item.setCreateTime(oldItem.getCreateTime());
                item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdSportTeamRankingService.updateTeamRanking(item,request.getLinkId());
            }

            //3875 【比分网】比分网后台-榜單管理
            thirdSportTeamRankingProducer.pushThirdSportTeamRankingPLS(request.getLinkId(),dtoItem.getDataSourceCode(),item);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_SPORT_TEAM_RANKING_API+"】【"+dataSourceCode+" ::"+request.getLinkId()+"::】联赛下球队排行榜单数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }


}

