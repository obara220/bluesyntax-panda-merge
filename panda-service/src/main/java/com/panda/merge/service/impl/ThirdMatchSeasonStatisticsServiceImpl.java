package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchSeasonStatisticsDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.mapper.ThirdMatchSeasonStatisticsMapper;
import com.panda.merge.model.ThirdMatchSeasonStatistics;
import com.panda.merge.model.ThirdMatchSeasonStatisticsExample;
import com.panda.merge.service.ThirdMatchSeasonStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 三方联赛赛季统计数据
 * @author tell
 * @since  2020年10月18日09:01:35
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchSeasonStatisticsServiceImpl implements ThirdMatchSeasonStatisticsService {

    @Autowired
    private ThirdMatchSeasonStatisticsMapper thirdMatchSeasonStatisticsMapper;

    @Autowired
    private ThirdMatchSeasonStatisticsDao thirdMatchSeasonStatisticsDao;

    @Override
    public Page<ThirdMatchSeasonStatistics> getSeasonStatisticsPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchSeasonStatisticsDao.getSeasonStatisticsPageByModifyTime(page.getData());
    }

    @Override
    public List<ThirdMatchSeasonStatistics> getItems(List<String> ids){
        ThirdMatchSeasonStatisticsExample example = new ThirdMatchSeasonStatisticsExample();
        example.createCriteria().andIdIn(ids);
        return thirdMatchSeasonStatisticsMapper.selectByExample(example);
    }

    @Override
    public List<ThirdMatchSeasonStatistics> getItemsInSeasonIds(List<String> seasonIds){
        ThirdMatchSeasonStatisticsExample example = new ThirdMatchSeasonStatisticsExample();
        example.createCriteria().andThirdSourceSeasonIdIn(seasonIds);
        return thirdMatchSeasonStatisticsMapper.selectByExample(example);
    }

    @Override
    public ThirdMatchSeasonStatistics saveOrUpdate(ThirdMatchSeasonStatistics upItem) {
        if (null != upItem.getCreateTime()) {
            thirdMatchSeasonStatisticsMapper.insertSelective(upItem);
        } else {
            ThirdMatchSeasonStatistics item = new ThirdMatchSeasonStatistics();
            //三方数据源赛季ID，运动类型，三方数据源球队ID,创建时间无需修改
            BeanUtil.copyProperties(upItem,item,"thirdSourceSeasonId","sportId","dataSourceCode");
            thirdMatchSeasonStatisticsMapper.updateByPrimaryKeySelective(item);
        }
        return upItem;
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchSeasonStatisticsExample example){
        ThirdMatchSeasonStatistics item = new ThirdMatchSeasonStatistics();
        item.setModifyTime(modifyTime);
        return thirdMatchSeasonStatisticsMapper.updateByExampleSelective(item,example);
    }

}
