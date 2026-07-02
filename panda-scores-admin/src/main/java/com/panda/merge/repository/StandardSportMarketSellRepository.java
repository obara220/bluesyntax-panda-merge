package com.panda.merge.repository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.service.StandardSportMarketSellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandardSportMarketSellRepository {

    @Autowired
    private RedisService redisService;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    //标准赛事id查询
    public StandardSportMarketSell selectThirdMatchInfoPrimaryKey(Long standardMatchId) {

        //先查缓存，没有则查数据，然后存缓存，缓存时间 3小时
//        StandardSportMarketSell standardSportMarketSell = new StandardSportMarketSell();
//        Object standardSportMarketSellCache = redisService.get(RepositoryConstant.STANDARD_SPORT_MARKET_SELL + standardMatchId);
//        if (standardSportMarketSellCache != null) {
//            standardSportMarketSell = JSONObject.toJavaObject(JSONObject.parseObject(standardSportMarketSellCache.toString()), StandardSportMarketSell.class);
//            if (standardSportMarketSell != null && standardSportMarketSell.getId() != null && standardSportMarketSell.getId() > 0) {
//                return standardSportMarketSell;
//            }
//        }
//        StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//        example.createCriteria().andMatchInfoIdEqualTo(standardMatchId);
//        List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(example);
//        if (!standardSportMarketSellList.isEmpty()) {
//            standardSportMarketSell = standardSportMarketSellList.get(0);
//            redisService.set(RepositoryConstant.STANDARD_SPORT_MARKET_SELL + standardMatchId, JSONObject.toJSON(standardSportMarketSell), RepositoryConstant.REDIS_ONE_MINUS);
//        }
//        return standardSportMarketSell;
//        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
//        if(standardSportMarketSell==null){
//            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//            example.createCriteria().andMatchInfoIdEqualTo(standardMatchId);
//            List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(example);
//            if(standardSportMarketSellList!=null && !standardSportMarketSellList.isEmpty()){
//                standardSportMarketSell=standardSportMarketSellList.get(0);
//            }
//        }
        return standardSportMarketSellService.getItem(standardMatchId);
    }

    public void cleanStandardMatchSell(Long aLong) {
       String key  =  RepositoryConstant.STANDARD_SPORT_MARKET_SELL + aLong;
        redisService.del(key);
    }
}
