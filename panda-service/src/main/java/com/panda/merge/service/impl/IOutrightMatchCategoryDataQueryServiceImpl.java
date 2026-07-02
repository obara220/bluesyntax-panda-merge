package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.mapper.StandardOutrightMatchCategoryMapper;
import com.panda.merge.model.StandardOutrightMatchCategory;
import com.panda.merge.model.StandardOutrightMatchCategoryExample;
import com.panda.merge.service.IOutrightMatchCategoryDataQueryService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : TODO
 * @date: 2020-10-02 12:06
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class IOutrightMatchCategoryDataQueryServiceImpl extends BaseServiceImpl<StandardOutrightMatchCategory> implements IOutrightMatchCategoryDataQueryService {

    @Resource
    private StandardOutrightMatchCategoryMapper standardOutrightMatchCategoryMapper;

    @Override
    @Cacheable(key = "'StandardOutrightMatchCategory:'+#outrightMatchInfoDTO.beginTime+'-'+#outrightMatchInfoDTO.endTime", unless = "#result == null || #result.size() == 0")
    public List<StandardOutrightMatchCategory> queryOutrihtMatchCategory(OutrightMatchInfoDTO outrightMatchInfoDTO) {
        StandardOutrightMatchCategoryExample example = new StandardOutrightMatchCategoryExample();
        example.setOrderByClause("modfiy_time desc");
        example.createCriteria().andModfiyTimeGreaterThanOrEqualTo(outrightMatchInfoDTO.getBeginTime()).andModfiyTimeLessThanOrEqualTo(outrightMatchInfoDTO.getEndTime());
        List<StandardOutrightMatchCategory> standardOutrightMatchCategories = standardOutrightMatchCategoryMapper.selectByExample(example);
        return standardOutrightMatchCategories;
    }
}
