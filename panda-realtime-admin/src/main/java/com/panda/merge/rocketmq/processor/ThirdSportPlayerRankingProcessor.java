package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdSportPlayerRankingDTO;
import com.panda.merge.model.ThirdSportPlayerRanking;
import com.panda.merge.rocketmq.producer.ThirdSportRankingProducer;
import com.panda.merge.service.ThirdSportPlayerRankingService;
import lombok.extern.slf4j.Slf4j;
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
 * 联赛下球员排行榜单
 * @author tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Validated
@Component
public class ThirdSportPlayerRankingProcessor extends BaseProcessor {

    @Autowired
    private ThirdSportPlayerRankingService thirdSportPlayerRankingService;
    @Resource
    private ThirdSportRankingProducer thirdSportRankingProducer;

    /**
     * 球员榜单数据处理
     * */
    public Response processPlayerRankingData(@Valid Request<List<ThirdSportPlayerRankingDTO>> request) {
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_SPORT_PAYER_RANKING_API+"】【TS ::"+request.getLinkId()+"::】联赛下球员排行榜单数据接收开始");
        long beginTime = System.currentTimeMillis();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_SPORT_PAYER_RANKING_API,request);
        Response response = Response.success();
        List<ThirdSportPlayerRankingDTO> dtoItemList = request.getData();
        //组合当前榜单ID列表
        Set<String> ids = dtoItemList.stream().map(obj -> obj.getThirdSourceSeasonId() + FIX + obj.getRankingType() + FIX + obj.getThirdPlayerSourceId()).collect(Collectors.toSet());
        //获取库中存在的榜单列表
        Map<String, ThirdSportPlayerRanking> id2Ranking = thirdSportPlayerRankingService.getItems(Lists.newArrayList(ids)).stream().collect(Collectors.toMap(ThirdSportPlayerRanking::getId, i -> i));
        for (ThirdSportPlayerRankingDTO dtoItem: dtoItemList) {
            //获取标准赛种
            Long sportId = validateSportId(dtoItem.getDataSourceCode(), dtoItem.getSportId()+"");
            //组合当前榜单ID（三方数据源赛季ID+榜单类型+榜单序号）
            String id = dtoItem.getThirdSourceSeasonId() + FIX + dtoItem.getRankingType() + FIX + dtoItem.getThirdPlayerSourceId();
            //本次操作对象
            ThirdSportPlayerRanking item = new ThirdSportPlayerRanking();
            BeanUtil.copyProperties(dtoItem, item);
            item.setId(id);
            item.setSportId(sportId);
            //获取库中榜单信息
            ThirdSportPlayerRanking oldItem = id2Ranking.get(id);
            if(null == oldItem){
                item.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            }
            //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
            if(!EntityEqualsUtils.equalsIsObjToString(item,oldItem)){
                item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdSportPlayerRankingService.saveOrUpdate(item,request.getLinkId());
            }else{
                log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_SPORT_PAYER_RANKING_API+"】【TS ::"+request.getLinkId()+"::】本次接收到联赛下球员排行榜单数据和库中数据一致，跳过修改，id：{}" , id);
            }

            //3875 【比分网】比分网后台-榜單管理
//            thirdSportRankingProducer.pushThirdSportPlayerRankingPLS(request.getLinkId(),dtoItem.getDataSourceCode(),item);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_SPORT_PAYER_RANKING_API+"】【TS ::"+request.getLinkId()+"::】联赛下球员排行榜单数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }

   
}

