package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardSportRegionDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardSportRegionDTO;
import com.panda.merge.mapper.StandardSportRegionMapper;
import com.panda.merge.model.StandardSportRegion;
import com.panda.merge.service.StandardSportRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 标准区域信息 <br>
 * @author   tell
 * @since    2020年9月10日10:32:26
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportRegionServiceImpl implements StandardSportRegionService {

    @Autowired
    private StandardSportRegionMapper StandardSportRegionMapper;

    @Autowired
    private StandardSportRegionDao standardSportRegionDao;

    @Override
    public Page<StandardSportRegion> getItemPageByModifyTime(PageModel<StandardSportRegionDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return standardSportRegionDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public StandardSportRegion getStandardSportRegion(Long id){
        return StandardSportRegionMapper.selectByPrimaryKey(id);
    }


}
