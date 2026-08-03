package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardMarketCategoryDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardMarketCategoryDetail;
import com.panda.merge.dto.StandardSportMarketCategoryDTO;
import com.panda.merge.mapper.StandardMarketCategoryMapper;
import com.panda.merge.model.StandardMarketCategory;
import com.panda.merge.service.StandardMarketCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 标准玩法信息
 * @author  tell
 * @since   2020年10月7日09:47:03
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardMarketCategoryServiceImpl implements StandardMarketCategoryService {

    @Autowired
    private StandardMarketCategoryDao standardMarketCategoryDao;

    @Autowired
    private StandardMarketCategoryMapper standardMarketCategoryMapper;

    @Override
    public Page<StandardMarketCategoryDetail> getItemPageByModifyTime(PageModel<StandardSportMarketCategoryDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return standardMarketCategoryDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public StandardMarketCategory getItemById(Long id) {
        return standardMarketCategoryMapper.selectByPrimaryKey(id);
    }

    public int delRedisByAll(){
        return 0;
    }
}
