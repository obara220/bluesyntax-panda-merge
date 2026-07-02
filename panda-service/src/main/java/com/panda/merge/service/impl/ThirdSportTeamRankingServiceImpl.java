package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ThirdSportTeamRankingDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.mapper.ThirdSportTeamRankingMapper;
import com.panda.merge.model.ThirdSportTeamRanking;
import com.panda.merge.model.ThirdSportTeamRankingExample;
import com.panda.merge.service.ThirdSportTeamRankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * 联赛下球队排行榜单(泰森独有)
 * @author tell
 * @since  2020年10月18日09:01:35
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportTeamRankingServiceImpl implements ThirdSportTeamRankingService {

    @Autowired
    public RedisService redisService;

    @Autowired
    private ThirdSportTeamRankingMapper thirdSportTeamRankingMapper;

    @Autowired
    private ThirdSportTeamRankingDao thirdSportTeamRankingDao;

    @Override
    public Page<ThirdSportTeamRanking> getItemPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdSportTeamRankingDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    @Deprecated
    public List<ThirdSportTeamRanking> getTeamRankingBySeasonIdAndMatchId(QueryThirdRankingInfoDTO dto){
        return thirdSportTeamRankingDao.getTeamRankingBySeasonIdAndMatchId(dto);
    }

    @Override
    public List<ThirdSportTeamRanking> getItems(List<String> ids){
        ThirdSportTeamRankingExample example = new ThirdSportTeamRankingExample();
        example.createCriteria().andIdIn(ids);
        return thirdSportTeamRankingMapper.selectByExample(example);
    }

    @Override
    public List<ThirdSportTeamRanking> getItemsInSeasonIds(List<String> seasonIds){
        ThirdSportTeamRankingExample example = new ThirdSportTeamRankingExample();
        example.createCriteria().andThirdSourceSeasonIdIn(seasonIds);
        return thirdSportTeamRankingMapper.selectByExample(example);
    }

    @Override
    public ThirdSportTeamRanking saveTeamRanking(ThirdSportTeamRanking upItem,String linkId) {
        //2S内不允许重复入库
        String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportTeamRanking:DbLock:%s", upItem.getId());
        //如果是新增,避免同时新增出现唯一索引异常
        if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
            thirdSportTeamRankingMapper.insertSelective(upItem);
        }else{
            log.info("linkId={},ThirdSportTeamRanking，ID={},2S内不允许重复入库",linkId,upItem.getId());
        }
        return upItem;
    }

    @Override
    public ThirdSportTeamRanking updateTeamRanking(ThirdSportTeamRanking upItem,String linkId) {
        ThirdSportTeamRanking item = new ThirdSportTeamRanking();
        //三方数据源赛季ID，运动类型，三方数据源球队ID,创建时间无需修改
        BeanUtil.copyProperties(upItem,item,"thirdSourceSeasonId","sportId","thirdTeamSourceId");
        thirdSportTeamRankingMapper.updateByPrimaryKeySelective(item);
        return upItem;
    }

    @Override
    public int updateModifyTimeByExampleSelective(Long modifyTime, ThirdSportTeamRankingExample example){
        ThirdSportTeamRanking item = new ThirdSportTeamRanking();
        item.setModifyTime(modifyTime);
        return thirdSportTeamRankingMapper.updateByExampleSelective(item,example);
    }

}
