package com.panda.merge.service;

import com.panda.merge.model.SportMarketRelation;

import java.util.List;
import java.util.Set;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @description : TODO
 * @date: 2020-10-16 16:24
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface SportMarketRelationService {

    SportMarketRelation getItem(String key);

    void insertBatch(Long standardMatchId, List<SportMarketRelation> sportMarketRelation);

    void insert(String redisKey, Long relationMarketId);

    void delDbAndCache(Set<String> relationMarketKeyList);
}
