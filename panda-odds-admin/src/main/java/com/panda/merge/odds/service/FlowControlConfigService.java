package com.panda.merge.odds.service;

import com.panda.merge.cache.CacheConstant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.odds.cache.AbstractLocalCacheService;
import com.panda.merge.odds.model.FlowControlNotificationDto;
import com.panda.merge.odds.model.FlowControlState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.panda.merge.odds.constants.CacheConstant.EXPIRE_ONE_MONTH;

/**
 * FlowControlConfigService
 *
 * @description:
 * @date: 7/16/2025
 * 流控赛事列表
 * redis key odds:{fc}:matchIds
 * redis value matchId set
 * <p>
 * 流控等级
 * redis key odds:{fc}:stage
 * value int
 **/
@Service
@Slf4j
@CacheConfig(cacheNames = CacheConstant.CACHE_FLOW_CONTROL, cacheManager = "localCacheExpireManager")
public class FlowControlConfigService extends AbstractLocalCacheService {

    private static final String KEY_MATCH_IDS = "odds:{fc}:matchIds";

    private static final String KEY_STAGE = "odds:{fc}:stage";

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "localCacheExpireManager")
    private CacheManager cacheManager;

    @Autowired
    private FlowControlMatchCloseService matchCloseService;

    public void update(Request<FlowControlNotificationDto> request) {
        String linkId = request.getLinkId();
        FlowControlNotificationDto data = request.getData();
        if (data.getFlowControlNotificationStatus() == 1 || data.getFlowControlNotificationStage() == 0) {
            clear(linkId);
        } else {
            Integer oldStage = (Integer) redisService.get(KEY_STAGE);
            if (Objects.isNull(oldStage) || !oldStage.equals(data.getFlowControlNotificationStage())) {
                set(linkId, data);
            } else {
                merge(linkId, data);
            }
        }
        senRefreshMessage(null, linkId);
        log.info("linkId:{},update flow control config finished", linkId);
        matchCloseService.scheduledMatchClose(linkId, data.getFlowControlNotificationMatchNotInIds());

    }

    @Override
    public void refresh(String key) {
        Cache cache = cacheManager.getCache(CacheConstant.CACHE_FLOW_CONTROL);
        if (cache != null) {
            cache.clear();
        }
        log.info("flow control config cache refresh finished");
    }

    @Cacheable(key = "'fc'")
    public FlowControlState get() {
        List<Object> objects = redisTemplate.executePipelined(

                new SessionCallback<Object>() {

                    public Object execute(RedisOperations operations) throws DataAccessException {
                        operations.opsForValue().get(KEY_STAGE);
                        operations.opsForSet().members(KEY_MATCH_IDS);
                        return null;
                    }
                });
        if (CollectionUtils.isNotEmpty(objects))  {
            int stage = objects.get(0) == null ? 0 : (int) objects.get(0);
            Set<Long> matchIds = (Set<Long>) objects.get(1);
            return new FlowControlState(stage,matchIds);
        }
        return FlowControlState.DISABLED;
    }

    private void merge(String linkId, FlowControlNotificationDto data) {
        redisTemplate.executePipelined(

                new SessionCallback<Object>() {

                    public Object execute(RedisOperations operations) throws DataAccessException {
                        operations
                                .opsForValue()
                                .set(KEY_STAGE,
                                     data.getFlowControlNotificationStage(),
                                     EXPIRE_ONE_MONTH,
                                     TimeUnit.SECONDS);
                        operations
                                .opsForSet()
                                .add(KEY_MATCH_IDS,
                                     data.getFlowControlNotificationMatchNotInIds().toArray(new Long[0]));
                        return null;
                    }
                });
        log.info("linkId:{},merge flow control config finished", linkId);
    }

    private void clear(String linkId) {

        redisTemplate.executePipelined(

                new SessionCallback<Object>() {

                    public Object execute(RedisOperations operations) throws DataAccessException {
                        operations.delete(KEY_MATCH_IDS);
                        operations.delete(KEY_STAGE);
                        return null;
                    }
                });
        log.info("linkId:{},clear flow control config finished", linkId);
    }

    private void set(String linkId, FlowControlNotificationDto data) {
        redisTemplate.executePipelined(

                new SessionCallback<Object>() {

                    public Object execute(RedisOperations operations) throws DataAccessException {
                        operations
                                .opsForValue()
                                .set(KEY_STAGE,
                                     data.getFlowControlNotificationStage(),
                                     EXPIRE_ONE_MONTH,
                                     TimeUnit.SECONDS);
                        operations.delete(KEY_MATCH_IDS);
                        operations
                                .opsForSet()
                                .add(KEY_MATCH_IDS,
                                     data.getFlowControlNotificationMatchNotInIds().toArray(new Long[0]));
                        return null;
                    }
                });
        log.info("linkId:{},set flow control config finished", linkId);
    }

}
