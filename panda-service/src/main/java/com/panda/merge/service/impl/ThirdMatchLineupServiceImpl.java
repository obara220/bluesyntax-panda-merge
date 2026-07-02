package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchLineupDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchLineupMapper;
import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.model.ThirdMatchLineupExample;
import com.panda.merge.service.ThirdMatchLineupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * 赛事首发阵容信息
 * @author      tell
 * @since       2021年2月6日17:50:52
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchLineupServiceImpl extends BaseServiceImpl<ThirdMatchLineup> implements ThirdMatchLineupService {

    @Autowired
    private ThirdMatchLineupMapper thirdMatchLineupMapper;

    @Autowired
    private ThirdMatchLineupDao thirdMatchLineupDao;


    @Override
    public Page<ThirdMatchLineup> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchLineupDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public List<ThirdMatchLineup> getItemByModifyTime(ThirdMatchInfoDTO item){
        ThirdMatchLineupExample example = new ThirdMatchLineupExample();
        if(StringUtils.isNotBlank(item.getDataSourceCode())){
            example.createCriteria().andDataSourceCodeEqualTo(item.getDataSourceCode());
        }
        if(null != item.getBeginTime()){
            example.createCriteria().andModifyTimeGreaterThanOrEqualTo(item.getEndTime());
        }
        if(null != item.getEndTime()){
            example.createCriteria().andModifyTimeLessThanOrEqualTo(item.getEndTime());
        }
        return thirdMatchLineupMapper.selectByExample(example);
    }

    @Override
    public List<ThirdMatchLineup> getItemList(String thirdMatchSourceId,String dataSourceCode){
        ThirdMatchLineupExample example = new ThirdMatchLineupExample();
        example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andDataSourceCodeEqualTo(dataSourceCode);
        return thirdMatchLineupMapper.selectByExample(example);
    }

    @Override
    public List<ThirdMatchLineup> getItemList(List<String> thirdMatchSourceIds,String dataSourceCode){
        if(!CollectionUtils.isEmpty(thirdMatchSourceIds)){
            ThirdMatchLineupExample example = new ThirdMatchLineupExample();
            example.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
            return thirdMatchLineupMapper.selectByExample(example);
        }
        return Lists.newArrayList();
    }

    @Override
    public ThirdMatchLineup getItem(String id){
        return thirdMatchLineupMapper.selectByPrimaryKey(id);
    }

    @Override
    public ThirdMatchLineup saveItem(ThirdMatchLineup item,String linkId) {
        item.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //2S内不允许重复入库
        String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchLineup:DbLock:%s", item.getId());
        //如果是新增,避免同时新增出现唯一索引异常
        if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
            thirdMatchLineupMapper.insertSelective(item);
        }else{
            log.info("linkId={},ThirdMatchLineup,数据源编码={}，ID={},2S内不允许重复入库",linkId,item.getDataSourceCode(),item.getId());
        }
        return item;
    }

    @Override
    public ThirdMatchLineup updateItem(ThirdMatchLineup item) {
        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdMatchLineupMapper.updateByPrimaryKey(item);
        return item;
    }

    @Override
    public ThirdMatchLineup getItemByPrimaryKey(String id){
        return refreshCache(thirdMatchLineupMapper.selectByPrimaryKey(id));
    }


    @Override
    public ThirdMatchLineup updateByPrimaryKeySelective(ThirdMatchLineup item){
        return null;
    }

    /** 刷新缓存*/
    @Override
    public ThirdMatchLineup refreshCache(ThirdMatchLineup item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchLineup:" + item.getId(), item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }


    @Override
    public void delItemById(String id) {
        thirdMatchLineupMapper.deleteByPrimaryKey(id);
    }


    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchLineupExample example){
        ThirdMatchLineup item = new ThirdMatchLineup();
        item.setModifyTime(modifyTime);
        return thirdMatchLineupMapper.updateByExampleSelective(item,example);
    }
}
