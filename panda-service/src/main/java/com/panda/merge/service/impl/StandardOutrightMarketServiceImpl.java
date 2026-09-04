package com.panda.merge.service.impl;

import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardOutrightMarketDao;
import com.panda.merge.mapper.StandardOutrightMarketMapper;
import com.panda.merge.model.StandardOutrightMarket;
import com.panda.merge.model.StandardOutrightMarketExample;
import com.panda.merge.service.StandardOutrightMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author : raulvii
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : 盘口名称多语言
 * @date: 2020-09-11 9:52
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardOutrightMarketServiceImpl implements StandardOutrightMarketService {

    @Autowired
    private StandardOutrightMarketDao standardOutrightMarketDao;

    @Autowired
    private StandardOutrightMarketMapper standardOutrightMarketMapper;

    @Override
    public void saveBatch(List<StandardOutrightMarket> standardOutrightMarketList) {
        standardOutrightMarketDao.saveBatch(standardOutrightMarketList);
    }

    @Override
    public void updateBatchById(List<StandardOutrightMarket> standardOutrightMarketList) {
        standardOutrightMarketDao.updateBatchById(standardOutrightMarketList);
    }

    @Override
    public int updateByExampleSelective(StandardOutrightMarket record, StandardOutrightMarketExample example) {
        return standardOutrightMarketMapper.updateByExampleSelective(record, example);
    }

    @Override
    public List<StandardOutrightMarket> selectOutrightMarketSellList(Long standardMatchId) {
        StandardOutrightMarketExample standardOutrightMarketExample = new StandardOutrightMarketExample();
        standardOutrightMarketExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andMarketSellStatusEqualTo(SellStatusEnum.SOLD.value);
        return standardOutrightMarketMapper.selectByExample(standardOutrightMarketExample);
    }

    @Override
    public List<StandardOutrightMarket> selectOutrightMarketSellListByIds(List<Long> standardMatchIds) {
        StandardOutrightMarketExample standardOutrightMarketExample = new StandardOutrightMarketExample();
        standardOutrightMarketExample.createCriteria().andStandardMatchIdIn(standardMatchIds).andMarketSellStatusEqualTo(SellStatusEnum.SOLD.value);
        return standardOutrightMarketMapper.selectByExample(standardOutrightMarketExample);
    }

    @Override
    public void updateByPrimaryKeySelective(StandardOutrightMarket record){
        standardOutrightMarketMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public StandardOutrightMarket selectByExample(Long standardMarketId) {
        StandardOutrightMarketExample example = new StandardOutrightMarketExample();
        example.createCriteria().andIdEqualTo(standardMarketId);
        List<StandardOutrightMarket> somList = standardOutrightMarketMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(somList)) {
            return null;
        }
        return somList.get(0);
    }

    @Override
    public StandardOutrightMarket getOutrightMarketData (Long relationMarketId) {
        return standardOutrightMarketMapper.selectByPrimaryKey(relationMarketId);
    }


    @Override
    public List<StandardOutrightMarket> queryChampionMarket(Long matchId) {
        StandardOutrightMarketExample standardOutrightMarketExample = new StandardOutrightMarketExample();
        standardOutrightMarketExample.createCriteria().andStandardMatchIdEqualTo(matchId);
        return standardOutrightMarketMapper.selectByExample(standardOutrightMarketExample);
    }

}
