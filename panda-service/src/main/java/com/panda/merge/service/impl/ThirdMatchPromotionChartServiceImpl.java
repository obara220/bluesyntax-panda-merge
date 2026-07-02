package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchPromotionChartDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchPromotionChartMapper;
import com.panda.merge.model.ThirdMatchPromotionChart;
import com.panda.merge.model.ThirdMatchPromotionChartExample;
import com.panda.merge.service.ThirdMatchPromotionChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 杯赛淘汰赛
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchPromotionChartServiceImpl extends BaseServiceImpl<ThirdMatchPromotionChart> implements ThirdMatchPromotionChartService {

    @Autowired
    private ThirdMatchPromotionChartMapper thirdMapper;

    @Autowired
    private ThirdMatchPromotionChartDao thirdDao;


    @Override
    public Page<ThirdMatchPromotionChart> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public ThirdMatchPromotionChart getItem(String id){
        return thirdMapper.selectByPrimaryKey(id);
    }

    @Override
    public ThirdMatchPromotionChart saveItem(ThirdMatchPromotionChart item, String linkId){
        thirdMapper.insertSelective(item);
        return item;
    }

    @Override
    public ThirdMatchPromotionChart updateItem(ThirdMatchPromotionChart item){
        thirdMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchPromotionChartExample example){
        ThirdMatchPromotionChart item = new ThirdMatchPromotionChart();
        item.setModifyTime(modifyTime);
        return thirdMapper.updateByExampleSelective(item,example);
    }



}
