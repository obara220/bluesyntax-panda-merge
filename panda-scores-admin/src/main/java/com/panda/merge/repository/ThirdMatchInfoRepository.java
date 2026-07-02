package com.panda.merge.repository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.service.ThirdMatchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThirdMatchInfoRepository {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    //根据赛事获取三方赛事
    public ThirdMatchInfo selectThirdMatchInfoByPrimaryKey(Long thirdMatchId) {
//        ThirdMatchInfo thirdMatchInfo =  thirdMatchInfoService.getItem(thirdMatchId);
//        if(thirdMatchInfo == null){
//            thirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(thirdMatchId);
//        }
        return thirdMatchInfoService.getItem(thirdMatchId);
    }



    public ThirdMatchInfo selectByStandardIdAndDataSourceCode(Long referenceId, String businessEventCode) {
//        String key =RepositoryConstant.BUSINESS_EVENT_MATCH_ID + referenceId+"_"+businessEventCode;
//        ThirdMatchInfo thirdMatchInfo = null;
//        Object thirdMatchCache = redisService.get(key);
//        if (thirdMatchCache != null) {
//            thirdMatchInfo = JSONObject.toJavaObject(JSONObject.parseObject(thirdMatchCache.toString()), ThirdMatchInfo.class);
//            if (thirdMatchInfo != null && thirdMatchInfo.getId() != null && thirdMatchInfo.getId() > 0) {
//                if(!thirdMatchInfo.getDataSourceCode().equals("BC")){
//                    return thirdMatchInfo;
//                }
//            }
//        }
//        ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
//        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(referenceId).andDataSourceCodeEqualTo(businessEventCode);
//        List<ThirdMatchInfo> list = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//        if(list.size()!=0){
//            thirdMatchInfo =list.get(0);
//            redisService.set(key, JSONObject.toJSON(thirdMatchInfo),RepositoryConstant.REDIS_ONE_MINUS);
//            return thirdMatchInfo;
//        }
        return thirdMatchInfoService.getItem(referenceId, businessEventCode);
    }
}
