package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchSidelinedDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchSidelinedMapper;
import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.model.ThirdMatchSidelined;
import com.panda.merge.model.ThirdMatchSidelinedExample;
import com.panda.merge.service.ThirdMatchSidelinedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 三方赛事伤停球员信息
 * @author      tell
 * @since       2021年4月22日16:19:42
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchSidelinedServiceImpl extends BaseServiceImpl<ThirdMatchLineup> implements ThirdMatchSidelinedService {

    @Autowired
    private ThirdMatchSidelinedMapper thirdMatchSidelinedMapper;

    @Autowired
    private ThirdMatchSidelinedDao thirdMatchSidelinedDao;


    @Override
    public Page<ThirdMatchSidelined> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchSidelinedDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchSidelinedExample example){
        ThirdMatchSidelined item = new ThirdMatchSidelined();
        item.setModifyTime(modifyTime);
        return thirdMatchSidelinedMapper.updateByExampleSelective(item,example);
    }

    @Override
    public void delItemById(String id) {
        thirdMatchSidelinedMapper.deleteByPrimaryKey(id);
    }

}
