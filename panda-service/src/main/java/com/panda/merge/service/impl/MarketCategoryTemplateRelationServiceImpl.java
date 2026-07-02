package com.panda.merge.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.panda.merge.bo.MarketCategoryTemplateRelationBO;
import com.panda.merge.common.utils.ListBeanUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.MarketCategoryTemplateRelationMapper;
import com.panda.merge.model.MarketCategoryTemplateRelation;
import com.panda.merge.model.MarketCategoryTemplateRelationExample;
import com.panda.merge.service.MarketCategoryTemplateRelationService;

/**
 * <Description> 玩法模板关系
 * @author
 * @since
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class MarketCategoryTemplateRelationServiceImpl implements MarketCategoryTemplateRelationService {

    @Autowired
    private MarketCategoryTemplateRelationMapper mctRelationMapper;

	@Override
	public List<MarketCategoryTemplateRelationBO> getMarketCategoryTemplateRelation() {
		MarketCategoryTemplateRelationExample example = new MarketCategoryTemplateRelationExample();
		example.setOrderByClause("sport_id , template_id");
		List<MarketCategoryTemplateRelation> mctRelations = mctRelationMapper.selectByExample(example);
		if (CollectionUtils.isEmpty(mctRelations)) {
			return new ArrayList<>();
		}
		List<MarketCategoryTemplateRelationBO> resList = ListBeanUtils.copyListProperties(mctRelations,
				MarketCategoryTemplateRelationBO::new,
				(MarketCategoryTemplateRelation, MarketCategoryTemplateRelationBO) -> {
				});

		return resList;
	}
}
