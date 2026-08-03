package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.thirdmatch.ThirdMatchPromotionChartDTO;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchPromotionChart;
import com.panda.merge.service.ThirdMatchPromotionChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 杯赛淘汰赛
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Slf4j
@Validated
@Component
public class ThirdMatchPromotionChartProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchPromotionChartService thirdService;

//    @Autowired
//    private ThirdMatchInfoService thirdMatchInfoService;

    public Response processData(@Valid Request<ThirdMatchPromotionChartDTO> request) {
        ThirdMatchPromotionChartDTO dtoItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_PROMOTION_CHART_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】系列赛事:{}杯赛淘汰赛事数据接收开始",dtoItem.getSeriesId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_PROMOTION_CHART_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_PROMOTION_CHART_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事杯赛淘汰赛事数据接收开始");
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
        //编辑的对象
        ThirdMatchPromotionChart upItem = new ThirdMatchPromotionChart();
        BeanUtil.copyProperties(dtoItem, upItem);
        upItem.setId(String.join("",dataSource.getId().toString(),dtoItem.getSeasonId(),dtoItem.getSeriesId()));
        upItem.setSportId(sportId);
        if(StringUtils.isNotBlank(upItem.getMatchIds())){
            upItem.setMatchId(upItem.getMatchIds().split(",")[0]);
        }

//        //视频赛事级别分布式锁
//        String tryLockKey = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchPromotionChartDTO:"+upItem.getId();
//        boolean lockFlag = false;
        try {
            //获取分布式锁
//            lockFlag = redisService.tryLock(tryLockKey, tryLockKey, 10, 10);
            //获取库中信息
            ThirdMatchPromotionChart oldItem = thirdService.getItem(upItem.getId());
            if(null == oldItem){
                upItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdService.saveItem(upItem,request.getLinkId());
            }else{
                upItem.setCreateTime(oldItem.getCreateTime());
                upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdService.updateItem(upItem);
            }
        }finally {
//            if (lockFlag) {
//                //释放redis锁
//                redisService.unLock(tryLockKey, tryLockKey);
//            }
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_PROMOTION_CHART_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事杯赛淘汰赛事数据接收结束,返回结果：{}", JSONObject.toJSONString(response));
        }
        return response;
    }


}

