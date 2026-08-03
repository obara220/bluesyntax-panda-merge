package com.panda.merge.repository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.DataSourceConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StandardMatchInfoRepository {

    @Autowired
    private RedisService redisService;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    //标准赛事id查询
    public StandardMatchInfo selectStandardMatchPrimaryKey(Long standardMatchId) {

//        //先查缓存，没有则查数据，然后存缓存，缓存时间 3小时
//        StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
//        Object standardMatchCache = redisService.get(RepositoryConstant.STANDARD_MATCH_INFO + standardMatchId);
//        if (standardMatchCache != null) {
//            standardMatchInfo = JSONObject.toJavaObject(JSONObject.parseObject(standardMatchCache.toString()), StandardMatchInfo.class);
//            if (standardMatchInfo != null && standardMatchInfo.getId() != null && standardMatchInfo.getId() > 0) {
//                return standardMatchInfo;
//            }
//        }
//        standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(standardMatchId);
//        redisService.set(RepositoryConstant.STANDARD_MATCH_INFO + standardMatchId, JSONObject.toJSON(standardMatchInfo), RepositoryConstant.REDIS_ONE_MINUS);
//        return standardMatchInfo;
//        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(standardMatchId);
//        if(  standardMatchInfo ==null){
//            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(standardMatchId);
//        }
        return standardMatchInfoService.getItem(standardMatchId);
    }

    public Long selectAoMatchId(Long standardMatchId) {
        String key = RepositoryConstant.AO_MATCH_ID + standardMatchId;
//        Object o = redisService.get(key);
//        if(o!=null){
//            return Long.parseLong(o.toString());
//        }
//        ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
//        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(standardMatchId).andDataSourceCodeEqualTo("AO");
//        List<ThirdMatchInfo> list =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//        if(list.size()!=0){
//            redisService.set(key,list.get(0).getId(), RepositoryConstant.REDIS_THREE_TIME);
//            return list.get(0).getId();
//        }
//        return null;
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchId,DataSourceCodeEnum.AO.code);
        if(thirdMatchInfo!=null){
            redisService.set(key,thirdMatchInfo.getId(), RepositoryConstant.REDIS_THREE_TIME);
            log.info("获取标准赛事ID对应的A01赛事ID：{}，{}",standardMatchId,thirdMatchInfo.getId());
            return thirdMatchInfo.getId();
        }
        log.info("获取标准赛事ID对应的A01赛事ID：{}，未查询到三方赛事信息",standardMatchId);
        return null;
    }
}
