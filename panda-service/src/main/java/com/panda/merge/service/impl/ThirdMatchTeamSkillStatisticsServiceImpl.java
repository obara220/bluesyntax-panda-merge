package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchTeamSkillStatisticsDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchTeamSkillStatisticsMapper;
import com.panda.merge.model.ThirdMatchTeamSkillStatistics;
import com.panda.merge.model.ThirdMatchTeamSkillStatisticsExample;
import com.panda.merge.service.ThirdMatchTeamSkillStatisticsService;
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
public class ThirdMatchTeamSkillStatisticsServiceImpl extends BaseServiceImpl<ThirdMatchTeamSkillStatistics> implements ThirdMatchTeamSkillStatisticsService {

    @Autowired
    private ThirdMatchTeamSkillStatisticsMapper thirdMapper;

    @Autowired
    private ThirdMatchTeamSkillStatisticsDao thirdDao;


    @Override
    public Page<ThirdMatchTeamSkillStatistics> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public ThirdMatchTeamSkillStatistics getItem(String id){
        return thirdMapper.selectByPrimaryKey(id);
    }

    @Override
    public ThirdMatchTeamSkillStatistics saveItem(ThirdMatchTeamSkillStatistics item, String linkId){
        thirdMapper.insertSelective(item);
        return item;
    }

    @Override
    public ThirdMatchTeamSkillStatistics updateItem(ThirdMatchTeamSkillStatistics item){
        thirdMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchTeamSkillStatisticsExample example){
        ThirdMatchTeamSkillStatistics item = new ThirdMatchTeamSkillStatistics();
        item.setModifyTime(modifyTime);
        return thirdMapper.updateByExampleSelective(item,example);
    }



}
