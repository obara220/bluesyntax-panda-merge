package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchHistoryOddsDTO;
import com.panda.merge.mapper.ThirdMatchHistoryOddsMapper;
import com.panda.merge.mapper.ThirdMatchHistoryStatisticsMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchHistoryOdds;
import com.panda.merge.model.ThirdMatchHistoryStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 三方赛事百家赔信息
 * @author  tell
 * @since   2021年4月16日12:03:10
 */
@Slf4j
@Validated
@Component
public class ThirdMatchHistoryOddsProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchHistoryOddsMapper thirdMatchHistoryOddsMapper;

    @Autowired
    private ThirdMatchHistoryStatisticsMapper thirdMatchHistoryStatisticsMapper;

    public Response processMatchHistoryOddsData(@Valid Request<ThirdMatchHistoryOddsDTO> request) {
        ThirdMatchHistoryOddsDTO dtoItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_ODDS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】{}:赛事百家赔数据接收开始",dtoItem.getThirdMatchSourceId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_HISTORY_STATISTICS_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
        //库中赛事统计信息ID
        String matchHistoryStatisticsId = dataSource.getId() + "" + dtoItem.getThirdMatchSourceId();
        //本次操作对象
        ThirdMatchHistoryOdds upItem = new ThirdMatchHistoryOdds();
        BeanUtil.copyProperties(dtoItem, upItem);
        //数据来源ID+赛事源ID+供应商ID+玩法ID+盘口类型
        upItem.setId(matchHistoryStatisticsId+FIX+dtoItem.getBookId()+FIX+dtoItem.getTypeId()+FIX+dtoItem.getMarketType());
        //视频赛事级别分布式锁
        String tryLockKey = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchHistoryOddsDTO:"+upItem.getId();
        boolean lockFlag = false;
        try {
            //获取分布式锁
            lockFlag = redisService.tryLock(tryLockKey, tryLockKey, 5, 2);
            upItem.setSportId(sportId);
            upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //获取库中信息
            ThirdMatchHistoryOdds oldItem = thirdMatchHistoryOddsMapper.selectByPrimaryKey(upItem.getId());
            if(null == oldItem){
                upItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdMatchHistoryOddsMapper.insertSelective(upItem);
            }else{
                //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
                if(!EntityEqualsUtils.equalsIsObjToString(upItem,oldItem)){
                    upItem.setCreateTime(oldItem.getCreateTime());
                    thirdMatchHistoryOddsMapper.updateByPrimaryKeySelective(upItem);
                }else{
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_ODDS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收赛事百家赔数据和库中数据一致，跳过修改，库中数据为：{}" , JSON.toJSONString(oldItem));
                }
            }
            //如果是bet365(BookId:31),赛前盘（MarketType:1）则进行特殊处理
            if(31 == upItem.getBookId() && ONE.equals(upItem.getMarketType())){
                //如果是1:让球胜负,3:大小球 则赋值对应的初始盘口到历史赛事表
                if(ONE.equals(upItem.getTypeId()) || THREE.equals(upItem.getTypeId())){
                    ThirdMatchHistoryStatistics item = new ThirdMatchHistoryStatistics();
                    item.setId(matchHistoryStatisticsId);
                    if(ONE.equals(upItem.getTypeId())){
                        item.setHandicapVal(upItem.getValue0());
                    }
                    if(THREE.equals(upItem.getTypeId())){
                        item.setOverUnderVal(upItem.getValue0());
                    }
                    item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    thirdMatchHistoryStatisticsMapper.updateByPrimaryKeySelective(item);
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_ODDS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】更新历史赛事信息item:{}",JSON.toJSONString(item));
                }
            }
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_ODDS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事百家赔数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        }finally {
            if (lockFlag) {
                //释放redis锁
                redisService.unLock(tryLockKey, tryLockKey);
            }
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_ODDS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事百家赔数据接收结束");
        }
       return response;
    }


}

