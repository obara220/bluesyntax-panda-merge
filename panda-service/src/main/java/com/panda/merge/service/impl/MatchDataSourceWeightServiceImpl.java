package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.mapper.MatchDataSourceWeightMapper;
import com.panda.merge.model.MatchDataSourceWeight;
import com.panda.merge.model.MatchDataSourceWeightExample;
import com.panda.merge.service.MatchDataSourceWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/26 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
@Slf4j
public class MatchDataSourceWeightServiceImpl implements MatchDataSourceWeightService {

    @Autowired
    private MatchDataSourceWeightMapper matchDataSourceWeightMapper;

    @Override
    @CachePut(key = "'MatchDataSourceWeight:' + #standardMatchId+ '-' + #marketType", unless = "#result == null ")
    public MatchDataSourceWeight getItem(Long standardMatchId, Integer marketType) {
        MatchDataSourceWeightExample matchDataSourceWeightExample = new MatchDataSourceWeightExample();
        matchDataSourceWeightExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
                .andMarketTypeEqualTo(marketType.toString());
        List<MatchDataSourceWeight> matchDataSourceWeights = matchDataSourceWeightMapper.selectByExample(matchDataSourceWeightExample);
        if (CollectionUtils.isEmpty(matchDataSourceWeights)) {
            return null;
        }
        return matchDataSourceWeights.get(0);
    }

    @Override
    @CacheEvict(key = "'MatchDataSourceWeight:' + #matchDataSourceWeight.standardMatchId + '-' + #matchDataSourceWeight.marketType")
    public MatchDataSourceWeight update(MatchDataSourceWeight matchDataSourceWeight) {
        matchDataSourceWeight.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchDataSourceWeightMapper.updateByPrimaryKey(matchDataSourceWeight);
        return matchDataSourceWeight;
    }

    @Override
    public MatchDataSourceWeight save(Integer srWeight, Integer bcWeight, Integer bgWeight, Integer txWeight, Integer rbWeight,
                                      Integer pdWeight, Integer piWeight, Integer aoWeight, Integer lsWeight, Integer beWeight ,
                                      Integer koWeight, Integer btWeight, Integer odWeight,Integer n01Weight, Integer n02Weight,
                                      Integer f01Weight, Integer n03Weight, Integer l02Weight, Integer tournamentLevel, Long now,
                                      MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo, Long operaterId) {
        MatchDataSourceWeight dataSourceWeight = new MatchDataSourceWeight();
        dataSourceWeight.setSrWeight(srWeight);
        dataSourceWeight.setBcWeight(bcWeight);
        dataSourceWeight.setBgWeight(bgWeight);
        dataSourceWeight.setTxWeight(txWeight);
        dataSourceWeight.setRbWeight(rbWeight);
        dataSourceWeight.setPdWeight(pdWeight);
        dataSourceWeight.setPiWeight(piWeight);
        dataSourceWeight.setAoWeight(aoWeight);
        dataSourceWeight.setLsWeight(lsWeight);
        dataSourceWeight.setBeWeight(beWeight);
        dataSourceWeight.setKoWeight(koWeight);
        dataSourceWeight.setBtWeight(btWeight);
        dataSourceWeight.setOdWeight(odWeight);
        dataSourceWeight.setN01Weight(n01Weight);
        dataSourceWeight.setN02Weight(n02Weight);
        dataSourceWeight.setF01Weight(f01Weight);
        dataSourceWeight.setN03Weight(n03Weight);
        dataSourceWeight.setL02Weight(l02Weight);
        dataSourceWeight.setMarketType(Integer.toString(categoryConfigutaionInfo.getMarketType()));
        dataSourceWeight.setStandardMatchId(categoryConfigutaionInfo.getStandardMatchId());
        dataSourceWeight.setTournamentLevel(tournamentLevel);
        dataSourceWeight.setCreateTime(now);
        dataSourceWeight.setModifyTime(now);
        dataSourceWeight.setOperaterId(operaterId);
        log.info("MatchDataSourceWeightServiceImpl，标准赛事：{}权重{}插入信息！ lsWeight is::{}", categoryConfigutaionInfo.getStandardMatchId(), JSON.toJSONString(dataSourceWeight), lsWeight);
        matchDataSourceWeightMapper.insert(dataSourceWeight);
        return dataSourceWeight;
    }
}
