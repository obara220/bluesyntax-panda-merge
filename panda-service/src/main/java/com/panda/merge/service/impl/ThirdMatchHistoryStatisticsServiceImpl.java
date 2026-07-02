package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchHistoryStatisticsDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchHistoryStatisticsMapper;
import com.panda.merge.model.ThirdMatchHistoryStatistics;
import com.panda.merge.model.ThirdMatchHistoryStatisticsExample;
import com.panda.merge.service.ThirdMatchHistoryStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 三方赛事历史统计信息
 * @author tell
 * @since  2021年2月10日17:20:13
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchHistoryStatisticsServiceImpl implements ThirdMatchHistoryStatisticsService {

    @Autowired
    private ThirdMatchHistoryStatisticsMapper thirdMatchHistoryStatisticsMapper;

    @Autowired
    private ThirdMatchHistoryStatisticsDao thirdMatchHistoryStatisticsDao;


    @Override
    public Page<ThirdMatchHistoryStatistics> getItemPageByModifyTime(PageModel<StandardMatchInfoDTO> page) {
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchHistoryStatisticsDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchHistoryStatisticsExample example) {
        ThirdMatchHistoryStatistics item = new ThirdMatchHistoryStatistics();
        item.setModifyTime(modifyTime);
        return thirdMatchHistoryStatisticsMapper.updateByExampleSelective(item,example);
    }

}
