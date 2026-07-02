package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.ThirdSportSeasonMapper;
import com.panda.merge.model.ThirdSportSeason;
import com.panda.merge.model.ThirdSportSeasonExample;
import com.panda.merge.model.ThirdSportTournament;
import com.panda.merge.service.ThirdSportSeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author Kepa
 * @Date 2021/2/10 15:37
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportSeasonServiceImpl extends BaseServiceImpl<ThirdSportSeason> implements ThirdSportSeasonService {

    @Autowired
    private ThirdSportSeasonMapper thirdSportSeasonMapper;

    @Override
    @Cacheable(key = "'ThirdSportSeason:'+#dataSourceCode+'-'+#sportId+'-'+#thirdSourceSeasonId",unless="#result == null")
    public ThirdSportSeason getOneItem(String dataSourceCode, Long sportId, String thirdSourceSeasonId) {
        ThirdSportSeasonExample example = new ThirdSportSeasonExample();
        example.createCriteria().andThirdSourceSeasonIdEqualTo(thirdSourceSeasonId).andSportIdEqualTo(sportId).andDataSourceCodeEqualTo(dataSourceCode);
        List<ThirdSportSeason> thirdSportSeasons = thirdSportSeasonMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(thirdSportSeasons)){
            return null;
        }
        return thirdSportSeasons.get(0);
    }

    @Override
    public ThirdSportSeason saveOrupdate(ThirdSportSeason upItem) {
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(null != upItem.getCreateTime()){
            thirdSportSeasonMapper.insertSelective(upItem);
        }else{
            if(null != upItem.getModifyTime()){
                ThirdSportSeason item = new ThirdSportSeason();
                BeanUtil.copyProperties(upItem,item);
                thirdSportSeasonMapper.updateByPrimaryKeySelective(item);
            }
        }
        return refreshCache(upItem);
    }

    /** 刷新缓存*/
    private ThirdSportSeason refreshCache(ThirdSportSeason item){
        if(null != item){
           redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportSeason:" + item.getDataSourceCode()+"-"+item.getSportId()+"-"+item.getThirdSourceSeasonId(),item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }
}
