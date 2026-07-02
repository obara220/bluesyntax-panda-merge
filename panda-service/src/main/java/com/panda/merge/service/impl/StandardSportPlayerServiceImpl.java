package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardSportPlayerDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardSportPlayerDTO;
import com.panda.merge.dto.StandardSportPlayerDetail;
import com.panda.merge.mapper.StandardSportPlayerMapper;
import com.panda.merge.model.StandardSportPlayer;
import com.panda.merge.model.StandardSportPlayerExample;
import com.panda.merge.service.StandardSportPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 标准球员信息 <br>
 * @author   tell
 * @since    2020年9月9日12:01:57
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportPlayerServiceImpl implements StandardSportPlayerService {

    @Autowired
    private StandardSportPlayerDao standardSportPlayerDao;

    @Autowired
    private StandardSportPlayerMapper standardSportPlayerMapper;

    @Override
    public Page<StandardSportPlayerDetail> getPageItemGreaterThanOrModifyTime(PageModel<StandardSportPlayerDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return standardSportPlayerDao.getPageItemGreaterThanOrModifyTime(page.getData());
    }

    @Override
    @Cacheable(key = "'StandardSportPlayer:' + #sportId+ '-' + #thirdSourcePlayIdId", unless = "#result == null ")
    public StandardSportPlayer getItem(Long sportId, String thirdSourcePlayIdId) {
        StandardSportPlayerExample example = new StandardSportPlayerExample();
        example.createCriteria().andSportIdEqualTo(sportId).andThirdSourcePlayerIdEqualTo(thirdSourcePlayIdId);
        List<StandardSportPlayer> standardSportPlayers = standardSportPlayerMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(standardSportPlayers)) {
            return null;
        }
        return standardSportPlayers.get(0);
    }

    @Override
    public StandardSportPlayer getItemById(Long id) {
        return standardSportPlayerMapper.selectByPrimaryKey(id);
    }

}
