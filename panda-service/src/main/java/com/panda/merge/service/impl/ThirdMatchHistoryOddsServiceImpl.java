package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchHistoryOddsDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchHistoryOddsMapper;
import com.panda.merge.model.ThirdMatchHistoryOdds;
import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.service.ThirdMatchHistoryOddsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 赛事百家赔信息
 * @author      tell
 * @since       2021年4月22日16:19:42
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchHistoryOddsServiceImpl extends BaseServiceImpl<ThirdMatchLineup> implements ThirdMatchHistoryOddsService {

    @Autowired
    private ThirdMatchHistoryOddsMapper thirdMatchHistoryOddsMapper;

    @Autowired
    private ThirdMatchHistoryOddsDao thirdMatchHistoryOddsDao;


    @Override
    public Page<ThirdMatchHistoryOdds> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchHistoryOddsDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public void delItemById(String id) {
        thirdMatchHistoryOddsMapper.deleteByPrimaryKey(id);
    }

}
