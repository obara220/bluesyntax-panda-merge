package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ThirdSportPlayerRankingDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.mapper.ThirdSportPlayerRankingMapper;
import com.panda.merge.model.ThirdMatchSidelined;
import com.panda.merge.model.ThirdSportPlayerRanking;
import com.panda.merge.model.ThirdSportPlayerRankingExample;
import com.panda.merge.service.ThirdSportPlayerRankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * 联赛下球员排行榜单(泰森独有)
 * @author tell
 * @since  2020年10月18日09:01:35
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportPlayerRankingServiceImpl implements ThirdSportPlayerRankingService {

    @Autowired
    public RedisService redisService;

    @Autowired
    private ThirdSportPlayerRankingMapper thirdSportPlayerRankingMapper;

    @Autowired
    private ThirdSportPlayerRankingDao thirdSportPlayerRankingDao;

    @Override
    public Page<ThirdSportPlayerRanking> getItemPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdSportPlayerRankingDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public List<ThirdSportPlayerRanking> getItems(List<String> ids) {
        ThirdSportPlayerRankingExample example = new ThirdSportPlayerRankingExample();
        example.createCriteria().andIdIn(ids);
        return thirdSportPlayerRankingMapper.selectByExample(example);
    }

    @Override
    public List<ThirdSportPlayerRanking> getItemsInSeasonIds(List<String> seasonIds){
        ThirdSportPlayerRankingExample example = new ThirdSportPlayerRankingExample();
        example.createCriteria().andThirdSourceSeasonIdIn(seasonIds);
        return thirdSportPlayerRankingMapper.selectByExample(example);
    }

    @Override
    public ThirdSportPlayerRanking saveOrUpdate(ThirdSportPlayerRanking upItem,String linkId) {
        if (null != upItem.getCreateTime()) {
            //2S内不允许重复入库
            String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportPlayerRanking:DbLock:%s", upItem.getId());
            //如果是新增,避免同时新增出现唯一索引异常
            if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
                thirdSportPlayerRankingMapper.insertSelective(upItem);
            }else{
                log.info("linkId={},ThirdSportPlayerRanking，ID={},2S内不允许重复入库",linkId,upItem.getId());
            }
        } else {
            ThirdSportPlayerRanking item = new ThirdSportPlayerRanking();
            //三方数据源赛季ID，榜单类型，榜单序号,创建时间无需修改
            BeanUtil.copyProperties(upItem,item,"thirdSourceSeasonId","rankingType","rankingSort");
            thirdSportPlayerRankingMapper.updateByPrimaryKeySelective(item);
        }
        return upItem;
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdSportPlayerRankingExample example){
        ThirdSportPlayerRanking item = new ThirdSportPlayerRanking();
        item.setModifyTime(modifyTime);
        return thirdSportPlayerRankingMapper.updateByExampleSelective(item,example);
    }


}
