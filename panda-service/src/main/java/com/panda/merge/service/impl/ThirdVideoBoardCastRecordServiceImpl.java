package com.panda.merge.service.impl;

import com.google.common.collect.Lists;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdVideoBoardCastRecordMapper;
import com.panda.merge.model.ThirdVideoBoardCastRecord;
import com.panda.merge.model.ThirdVideoBoardCastRecordExample;
import com.panda.merge.service.ThirdVideoBoardCastRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 赛事播控平台视频记录(泰森独有)
 * @author tell
 * @since 2020-09-13
 */
@Slf4j
@Service
//@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdVideoBoardCastRecordServiceImpl  extends BaseServiceImpl<ThirdVideoBoardCastRecord>  implements ThirdVideoBoardCastRecordService {

    @Autowired
    private ThirdVideoBoardCastRecordMapper thirdVideoBoardCastRecordMapper;


    @Override
    public List<ThirdVideoBoardCastRecord> getItemByModifyTime(ThirdMatchInfoDTO item){
        ThirdVideoBoardCastRecordExample example = new ThirdVideoBoardCastRecordExample();
        if(null == item.getBeginTime()){
            return Lists.newArrayList();
        }
        example.createCriteria().andModifyTimeGreaterThanOrEqualTo(item.getEndTime());
        if(null != item.getEndTime()){
            example.createCriteria().andModifyTimeLessThanOrEqualTo(item.getEndTime());
        }
        return thirdVideoBoardCastRecordMapper.selectByExample(example);
    }

    @Override
//    @Cacheable(key = "'ThirdVideoBoardCastRecord:'+ #thirdMatchSourceId +  '-' + #dataSourceCode", unless = "#result == null")
    public ThirdVideoBoardCastRecord getItem(String thirdMatchSourceId,String dataSourceCode) {
        ThirdVideoBoardCastRecordExample example = new ThirdVideoBoardCastRecordExample();
        example.createCriteria().andMatchIdEqualTo(thirdMatchSourceId).andDataSourceCodeEqualTo(dataSourceCode);
        List<ThirdVideoBoardCastRecord> thirdVideoBoardCastRecords = thirdVideoBoardCastRecordMapper.selectByExampleWithBLOBs(example);
        if (CollectionUtils.isEmpty(thirdVideoBoardCastRecords)) {
            return null;
        }
        return thirdVideoBoardCastRecords.get(0);
    }

    @Override
//    @CachePut(key = "'ThirdVideoBoardCastRecord:'+#item.matchId +  '-' + #item.dataSourceCode", unless = "#result == null")
    public ThirdVideoBoardCastRecord saveItem(ThirdVideoBoardCastRecord item) {
        item.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdVideoBoardCastRecordMapper.insertSelective(item);
        return item;
    }

    @Override
//    @CachePut(key = "'ThirdVideoBoardCastRecord:'+#item.matchId +  '-' + #item.dataSourceCode", unless = "#result == null")
    public ThirdVideoBoardCastRecord updateItem(ThirdVideoBoardCastRecord item) {
        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdVideoBoardCastRecordMapper.updateByPrimaryKeyWithBLOBs(item);
        return item;
    }

    /** 刷新缓存*/
    public ThirdVideoBoardCastRecord refreshCache(ThirdVideoBoardCastRecord item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdVideoBoardCastRecord:" + item.getMatchId()+'-'+ item.getDataSourceCode(), item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

}
