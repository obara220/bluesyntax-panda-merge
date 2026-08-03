package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdSportTournamentDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardTournamentRuleDTO;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.mapper.ThirdSportTournamentMapper;
import com.panda.merge.model.ThirdSportTournament;
import com.panda.merge.model.ThirdSportTournamentExample;
import com.panda.merge.service.ThirdSportTournamentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <Description> 三方联赛信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportTournamentServiceImpl extends BaseServiceImpl<ThirdSportTournament> implements ThirdSportTournamentService {

    @Autowired
    private ThirdSportTournamentMapper thirdSportTournamentMapper;

    @Autowired
    private ThirdSportTournamentDao thirdSportTournamentDao;

    @Override
    public Page<ThirdSportTournament> getItemPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdSportTournamentDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    @Cacheable(key = "'ThirdSportTournament:'+#dataSourceCode+'-'+#sportId+'-'+#thirdSourceId",unless="#result == null")
    public ThirdSportTournament getOneItem(String dataSourceCode,Long sportId,String thirdSourceId) {
        ThirdSportTournamentExample example = new ThirdSportTournamentExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSportIdEqualTo(sportId).andThirdTournamentSourceIdEqualTo(thirdSourceId);
        List<ThirdSportTournament> thirdSportTournaments = thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
        if(CollectionUtils.isEmpty(thirdSportTournaments)){
            return null;
        }
        return thirdSportTournaments.get(0);
    }

    @Override
    public List<ThirdSportTournament> getItems(String dataSourceCode, Long sportId, List<Long> referenceIds){
        if(StringUtils.isBlank(dataSourceCode)){
            return new LinkedList<>();
        }
        ThirdSportTournamentExample example = new ThirdSportTournamentExample();
        if(!CollectionUtils.isEmpty(referenceIds) && null != sportId){
            example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSportIdEqualTo(sportId).andReferenceIdIn(referenceIds);
            return thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
        }
        if(!CollectionUtils.isEmpty(referenceIds)){
            example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andReferenceIdIn(referenceIds);
            return thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
        }
        if(null != sportId){
            example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSportIdEqualTo(sportId);
            return thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
        }
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode);
        return thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<ThirdSportTournament> getItems(List<String> dataSourceCodes,List<String> thirdTournamentSourceIds){
        if(CollectionUtils.isEmpty(dataSourceCodes) || CollectionUtils.isEmpty(thirdTournamentSourceIds)){
            return new LinkedList<>();
        }
        ThirdSportTournamentExample example = new ThirdSportTournamentExample();
        example.createCriteria().andDataSourceCodeIn(dataSourceCodes).andThirdTournamentSourceIdIn(thirdTournamentSourceIds);
        return thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
    }


    @Override
    public ThirdSportTournament saveOrupdate(ThirdSportTournament upItem){
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(null != upItem.getCreateTime()){
            upItem.setModifyTime(upItem.getCreateTime());
            thirdSportTournamentMapper.insertSelective(upItem);
        }else{
            if(null != upItem.getModifyTime()){
                ThirdSportTournament item = new ThirdSportTournament();
                //数据来源，运动类型，三方数据源联赛ID 无需修改
                BeanUtil.copyProperties(upItem,item,"dataSourceCode","sportId","thirdTournamentSourceId","createTime","referenceId");
                thirdSportTournamentMapper.updateByPrimaryKeySelective(item);
            }
        }
        return refreshCache(upItem);
    }

    /** 刷新缓存*/
    private ThirdSportTournament refreshCache(ThirdSportTournament item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportTournament:" + item.getDataSourceCode()+"-"+item.getSportId()+"-"+item.getThirdTournamentSourceId(),item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

    @Override
    public ThirdSportTournament getThirdSportTournament(Long id){
        return thirdSportTournamentMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<ThirdSportTournament> getTournamentRulePage(PageModel<StandardTournamentRuleDTO> page) {
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdSportTournamentDao.getTournamentRulePage(page.getData());
    }
}
