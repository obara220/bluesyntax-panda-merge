package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.thirdmatch.ThirdMatchTeamSkillStatisticsDTO;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchTeamSkillStatistics;
import com.panda.merge.service.ThirdMatchTeamSkillStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛事球队技术统计
 * @author  tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Validated
@Component
public class ThirdMatchTeamSkillStatisticsProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchTeamSkillStatisticsService thirdService;

    public Response processData(@Valid Request<ThirdMatchTeamSkillStatisticsDTO> request) {
        ThirdMatchTeamSkillStatisticsDTO dtoItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_TEAM_SKILL_STATISTICS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】:{}赛事球队技术统计数据接收开始",dtoItem.getMatchId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_TEAM_SKILL_STATISTICS_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_TEAM_SKILL_STATISTICS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事球队技术统计数据接收开始");
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
        //编辑的对象
        ThirdMatchTeamSkillStatistics upItem = new ThirdMatchTeamSkillStatistics();
        BeanUtil.copyProperties(dtoItem, upItem);
        upItem.setId(String.join("",dataSource.getId().toString(),dtoItem.getMatchId(),dtoItem.getTeamId()));
        upItem.setSportId(sportId);
//        //视频赛事级别分布式锁
//        String tryLockKey = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamSkillStatisticsDTO:"+upItem.getId();
//        boolean lockFlag = false;
        try {
            //获取分布式锁
//            lockFlag = redisService.tryLock(tryLockKey, tryLockKey, 10, 10);
            //获取库中信息
            ThirdMatchTeamSkillStatistics oldItem = thirdService.getItem(upItem.getId());
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
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_TEAM_SKILL_STATISTICS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事球队技术统计数据接收结束,返回结果：{}", JSONObject.toJSONString(response));
        }
        return response;
    }


}

