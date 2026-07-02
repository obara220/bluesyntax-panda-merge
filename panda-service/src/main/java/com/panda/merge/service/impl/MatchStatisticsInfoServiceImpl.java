package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.MatchStatisticsInfoMapper;
import com.panda.merge.model.MatchStatisticsInfo;
import com.panda.merge.model.MatchStatisticsInfoExample;
import com.panda.merge.service.MatchStatisticsInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
@Slf4j
public class MatchStatisticsInfoServiceImpl implements MatchStatisticsInfoService {

    @Autowired
    public RedisService redisService;

    @Autowired
    private MatchStatisticsInfoMapper matchStatisticsInfoMapper;

    @Override
    public MatchStatisticsInfo saveOrUpdate(MatchStatisticsInfo item, String linkId) {
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "::lock:MatchStatisticsInfo:" + item.getDataSourceCode()+ item.getSportId()+ item.getThirdSourceMatchId();
        boolean flag = false;
        try {
            flag = redisService.tryLock(redisKey, redisKey, 10, 10);
            item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //创建时间为空表示新增
            if (item.getCreateTime() == null) {
                item.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                //2S内不允许重复入库
                String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::MatchStatisticsInfo:DbLock:%s_%s_%s", item.getDataSourceCode(), item.getSportId(), item.getThirdSourceMatchId());
                //如果是新增,避免同时新增出现唯一索引异常
                if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
                    matchStatisticsInfoMapper.insertSelective(item);
                }else{
                    log.info("linkId={},MatchStatisticsInfo,数据源编码={}，源赛事ID={},2S内不允许重复入库",linkId,item.getDataSourceCode(),item.getThirdSourceMatchId());
                }
            } else {
                matchStatisticsInfoMapper.updateByPrimaryKeySelective(item);
            }
        } finally {
            if(flag){
                redisService.unLock(redisKey,redisKey);
            }
        }
        return item;
    }

    @Override
    public MatchStatisticsInfo getItem(String thirdSourceMatchId, String dataSourceCode, String linkId) {
        try {
            MatchStatisticsInfoExample matchStatisticsInfoExample = new MatchStatisticsInfoExample();
            matchStatisticsInfoExample.createCriteria().andThirdSourceMatchIdEqualTo(thirdSourceMatchId).andDataSourceCodeEqualTo(dataSourceCode);
            List<MatchStatisticsInfo> matchStatisticsInfos = matchStatisticsInfoMapper.selectByExample(matchStatisticsInfoExample);
            // 使用Optional处理可能的空列表
            return matchStatisticsInfos.stream().findFirst().orElse(null);
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】统计信息处理，获取统计数据异常,Exception:", e);
            return null;
        }
    }
}
