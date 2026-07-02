package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardSportTournamentDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardSportTournamentDTO;
import com.panda.merge.dto.StandardSportTournamentDetail;
import com.panda.merge.mapper.StandardSportTournamentMapper;
import com.panda.merge.model.StandardSportTournament;
import com.panda.merge.model.StandardSportTournamentExample;
import com.panda.merge.service.StandardSportTournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 标准联赛信息 <br>
 * @author   tell
 * @since    2020年9月10日10:32:26
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE + ":StandardSportTournament")
public class StandardSportTournamentServiceImpl implements StandardSportTournamentService {

    @Autowired
    private StandardSportTournamentMapper standardSportTournamentMapper;

    @Autowired
    private StandardSportTournamentDao standardSportTournamentDao;

    @Autowired
    RedisService redisService;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Page<StandardSportTournamentDetail> getItemPageByModifyTime(PageModel<StandardSportTournamentDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return standardSportTournamentDao.getItemPageByModifyTime(page.getData());
    }


    @Override
    public List<StandardSportTournament> getItems(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        StandardSportTournamentExample example = new StandardSportTournamentExample();
        example.createCriteria().andIdIn(ids);
        return standardSportTournamentMapper.selectByExample(example);
    }

    @Override
    public List<StandardSportTournament> getItemsCache(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<StandardSportTournament> results = new ArrayList<>();
        List<String> cacheKeys = getCacheKeys(ids);
        List<Object> caches = redisService.mGet(cacheKeys);
        List<Long> cacheMissedIds = new ArrayList<>();
        for (int i = 0; i < caches.size(); i++) {
            if (caches.get(i) == null) {
                cacheMissedIds.add(ids.get(i));
            } else {
                results.add((StandardSportTournament) caches.get(i));
            }
        }
        if (!cacheMissedIds.isEmpty()) {
            List<StandardSportTournament> dbResults = getItems(cacheMissedIds);
            if (CollectionUtils.isEmpty(dbResults)) {
                return results;
            }
            results.addAll(dbResults);
            redisService.mSet(dbResults
                                      .stream()
                                      .collect(Collectors.toMap(t -> getCacheKey(t.getId()),
                                                                Function.identity(),
                                                                (t1, t2) -> t1)));
        }
        return results;
    }

    @Override
    @Cacheable(key = "#id", unless = "#result == null")
    public StandardSportTournament getItem(Long id) {
        return standardSportTournamentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void evitCache(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        redisService.del(getCacheKeys(ids));
    }

    private List<String> getCacheKeys(List<Long> ids) {
        return ids.stream().map(this::getCacheKey).collect(Collectors.toList());
    }

    private String getCacheKey(Long id) {
        return RedisConfig.REDIS_KEY_DATABASE + ":StandardSportTournament::" + id;
    }
}
