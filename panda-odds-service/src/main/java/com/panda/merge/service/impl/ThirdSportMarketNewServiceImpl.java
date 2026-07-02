package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.MarketDbProducer;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dao.ThirdSportMarketDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdSportMarketDTO;
import com.panda.merge.dto.odds.MatchCategoryThirdMarketStatistics;
import com.panda.merge.mapper.StandardSportMarketMapper;
import com.panda.merge.mapper.ThirdSportMarketMapper;
import com.panda.merge.model.I18nOutrightMarket;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.service.I18nOutrightMarketService;
import com.panda.merge.service.SportMarketRelationService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketNewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/15 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportMarketNewServiceImpl implements ThirdSportMarketNewService {

    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;
    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;
    @Autowired
    private ThirdSportMarketDao thirdSportMarketDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SportMarketRelationService sportMarketRelationService;
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private MarketDbProducer marketDbProducer;
    @Autowired
    @Qualifier("pandaOddsJdbcTemplate")
    private JdbcTemplate jdbcTemplate1;

    @Resource
    private RedisHelper redisHelper;

    @Override
    public ThirdSportMarket getItem(String thirdMarketSourceId) {
        String sql = "select *  from third_sport_market  where third_market_source_id=? ";
        List<ThirdSportMarket> thirdSportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMarketSourceId}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        if (CollectionUtils.isEmpty(thirdSportMarkets)) {
            return null;
        }
        return thirdSportMarkets.get(0);
    }

    @Override
    @Cacheable(key = "'ThirdSportMarket:' + #thirdMatchInfoId+  '-' + #thirdMarketSourceId", unless = "#result == null ")
    public ThirdSportMarket getItem(String dataSourceCode, String thirdMarketSourceId, Long thirdMatchInfoId) {
        String sql = "select *  from third_sport_market  where data_source_code=? and third_market_source_id=? and match_id=? ";
        List<ThirdSportMarket> thirdSportMarkets = jdbcTemplate1.query(sql, new Object[]{dataSourceCode, thirdMarketSourceId, thirdMatchInfoId}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        if (CollectionUtils.isEmpty(thirdSportMarkets)) {
            return null;
        }
        return thirdSportMarkets.get(0);
    }

    @Override
    public List<ThirdSportMarket> getItemByMarketDTO(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs) {
        if(CollectionUtils.isEmpty(thirdMarketDTOs)) {
            return Collections.EMPTY_LIST;
        }
        List<ThirdSportMarket> result = new ArrayList<>();
        List<OddsWrapper<ThirdMarketDTO>> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = thirdMarketDTOs.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + t.getThirdMatchId()
                +"-"+t.getData().getThirdMarketSourceId()+"-"+t.getDataSourceCode()).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(thirdMarketDTOs, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        String sql = "select *  from third_sport_market  where (third_market_source_id, data_source_code, match_id) in (";
        for (OddsWrapper<ThirdMarketDTO> item : requiredCallItems) {
            sql += "(\"" + item.getData().getThirdMarketSourceId() + "\", \""+ item.getDataSourceCode() + "\", " + item.getThirdMatchId() + "), ";
        }
        sql = sql.substring(0, sql.length()-2) + ")";
        List<ThirdSportMarket> thirdSportMarkets = jdbcTemplate1.query(sql, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        result.addAll(thirdSportMarkets);
        // Storing the remained data into redis
        Map<String, Object> redisVal = thirdSportMarkets.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + t.getMatchId() +"-"+t.getThirdMarketSourceId()
                        +"-"+t.getDataSourceCode(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    /**
     * @param thirdMatchSourceId
     * @param marketType
     * @param marketCategoryIds
     * @return
     */
    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchSourceId, String dataSourceWeight, Integer marketType, List<Long> marketCategoryIds) {
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            return new LinkedList<>();
        }
        String sql = "select *  from third_sport_market  where match_id=? and data_source_code=? and market_type=? and market_category_id" + " in (" + marketCategoryIds.stream().map(Object::toString).collect(Collectors.joining(", ")) + ") ";
        List<ThirdSportMarket> thirdSportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMatchSourceId, dataSourceWeight, marketType}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        return thirdSportMarkets;
    }

    /**
     * 开售时只开售开跟封的盘口
     *
     * @param thirdMatchSourceId
     * @param dataSourceWeight
     * @param marketType
     * @param marketCategoryIds
     * @param status
     * @return
     */
    @Override
    public List<ThirdSportMarket> getItemListByStatus(Long thirdMatchSourceId, String dataSourceWeight, Integer marketType, List<Long> marketCategoryIds, Integer status) {
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            return new LinkedList<>();
        }
        String sql = "select *  from third_sport_market  where match_id=? and data_source_code=? and status < ? and market_type = ? market_category_id " + "in (" + marketCategoryIds.stream().map(Object::toString).collect(Collectors.joining(", ")) + ") ";
        List<ThirdSportMarket> thirdSportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMatchSourceId, dataSourceWeight, status, marketType}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        return thirdSportMarkets;
    }

    @Override
    public List<ThirdSportMarket> getItem(Long thirdMatchId, String dataSourceCode, Long categoryId, String addtion1) {
        String sql = "select *  from third_sport_market  where match_id=? and data_source_code=? and  market_category_id =? and addition1 =? and status != ?  ";
        List<ThirdSportMarket> sportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMatchId, dataSourceCode, categoryId, addtion1, 2}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        if (CollectionUtils.isEmpty(sportMarkets)) {
            return null;
        }
        return sportMarkets;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchId, String dataSourceCode, Long marketCategoryId) {
        String sql = "select *  from third_sport_market  where match_id=? and data_source_code=? and  market_category_id =? ";
        List<ThirdSportMarket> sportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMatchId, dataSourceCode, marketCategoryId}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        if (CollectionUtils.isEmpty(sportMarkets)) {
            return null;
        }
        return sportMarkets;
    }

    @Override
    public Long getRelationMarketId(String linkId, Long standardMatchId, Long categoryId, String addition1, String addition2, String addition3, String addition4, String addition5, Integer marketType, String thirdMarketSourceId) {
        String redisKey = RelationKeyFactory.getMarketRelationKeyByThirdInfo(linkId, standardMatchId, categoryId, addition1, addition2, addition3, addition4, addition5, marketType, thirdMarketSourceId);
        Long relationMarketId;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketId = Long.valueOf(obj.toString());
        }
        return relationMarketId;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchId, String dataSourceCode, Long marketCategoryId, Integer marketType) {
        String sql = "select *  from third_sport_market  where match_id=? and data_source_code=? and  market_category_id =? and market_type=? ";
        List<ThirdSportMarket> sportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMatchId, dataSourceCode, marketCategoryId, marketType}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        if (CollectionUtils.isEmpty(sportMarkets)) {
            return null;
        }
        return sportMarkets;
    }

    @Override
    public Long countByThirdMatchIds(List<Long> thirdMatchIds) {
        if (CollectionUtils.isEmpty(thirdMatchIds)) {
            return 0L;
        }
        String sql = "select count(*) from third_sport_market where match_id in (" +
                thirdMatchIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";
        Object[] params = thirdMatchIds.toArray();
        return jdbcTemplate1.queryForObject(sql, params, Long.class);
    }

    @Override
    public List<MatchCategoryThirdMarketStatistics> getThirdMarketStatistics(List<Long> thirdMatchIds, List<Long> standardCategoryIds,
                                                                             List<Integer> excludeStatus,
                                                                             Integer validSeconds) {
        if (CollectionUtils.isEmpty(thirdMatchIds) || CollectionUtils.isEmpty(standardCategoryIds) || Objects.isNull(validSeconds) ||
                validSeconds <= 0L) {
            throw new IllegalArgumentException(String.format(
                    "thirdMatchId: %s, standardCategoryId: %s, validSeconds: %s",
                    thirdMatchIds,
                    standardCategoryIds,
                    validSeconds));
        }
        long timestamp = System.currentTimeMillis() - (validSeconds * 1000L);

        String sql =
                "select match_id, market_category_id, count(*) as count from third_sport_market where match_id in (" +
                        thirdMatchIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ") " +
                        "and market_category_id in (" +
                        standardCategoryIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ") " +
                        "and status not in (" +
                        excludeStatus.stream().map(id -> "?").collect(Collectors.joining(", ")) + ") " +
                        "and modify_time >= ?  group by match_id, market_category_id";
        List<Object> params = new ArrayList<>();
        params.addAll(thirdMatchIds);
        params.addAll(standardCategoryIds);
        params.addAll(excludeStatus);
        params.add(timestamp);
        return jdbcTemplate1.query(sql, params.toArray(),new BeanPropertyRowMapper<>(MatchCategoryThirdMarketStatistics.class));
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchSourceId) {
        if (thirdMatchSourceId == null) {
            return null;
        }
        String sql = "select *  from third_sport_market  where match_id=? ";
        List<ThirdSportMarket> sportMarkets = jdbcTemplate1.query(sql, new Object[]{thirdMatchSourceId}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        return sportMarkets;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long matchId, int marketType) {
        String sql = "select *  from third_sport_market  where match_id=? and market_type=? ";
        List<ThirdSportMarket> sportMarkets = jdbcTemplate1.query(sql, new Object[]{matchId, marketType}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        return sportMarkets;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchId, Long standardMatchId) {
        Map<String, Long> map = new HashMap();
        map.put("thirdMatchId", thirdMatchId);
        List<ThirdSportMarket> activeMarketList = Lists.newArrayList();
        String sql = "select *  from third_sport_market  where match_id=? ";
        List<ThirdSportMarket> queryMarketList = jdbcTemplate1.query(sql, new Object[]{thirdMatchId}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));

        if (!CollectionUtils.isEmpty(queryMarketList)) {
            List<String> thirdSourceIdList = queryMarketList.stream().filter(tsm -> !StringUtils.isEmpty(tsm.getThirdMarketSourceId())).map(ThirdSportMarket::getThirdMarketSourceId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(thirdSourceIdList)) {
                String str = String.join("\',\'", thirdSourceIdList);
                String finalStr = "'" + str + "'";
                String sql1 = "select *  from standard_sport_market  where standard_match_info_id=? and third_market_source_id " + "in (" + finalStr + ") ";
                List<StandardSportMarket> filterMarketList = jdbcTemplate1.query(sql1, new Object[]{standardMatchId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
                if (CollectionUtils.isEmpty(filterMarketList)) {
                    activeMarketList.addAll(queryMarketList);
                } else {
                    Set<String> filterSourceIdSet = filterMarketList.stream().map(StandardSportMarket::getThirdMarketSourceId).collect(Collectors.toSet());
                    for (ThirdSportMarket thirdSportMarket : queryMarketList) {
                        String thirdMarketSourceId = thirdSportMarket.getThirdMarketSourceId();
                        if (!filterSourceIdSet.contains(thirdMarketSourceId)) {
                            activeMarketList.add(thirdSportMarket);
                        }
                    }
                }
            }
        }
        return activeMarketList;
    }

    /**
     * 创建三方盘口
     *
     * @param linkId
     * @param thirdMarketDTO
     * @param thirdMatchInfoId
     * @param standardSportMarketCategory
     * @return
     */
    @CachePut(key = "'ThirdSportMarket:' + #thirdMatchInfoId+  '-' + #thirdMarketDTO.thirdMarketSourceId", unless = "#result == null ")
    @Override
    public ThirdSportMarket create(String linkId, ThirdMarketDTO thirdMarketDTO, Long thirdMatchInfoId, StandardSportMarketCategory standardSportMarketCategory) {
        ThirdSportMarket thirdSportMarket = new ThirdSportMarket();
        BeanUtils.copyProperties(thirdMarketDTO, thirdSportMarket);
        thirdSportMarket.setMatchId(thirdMatchInfoId);
        thirdSportMarket.setMarketCategoryId(standardSportMarketCategory.getMarketCategoryId());
        thirdSportMarket.setId(UUIdUtils.getId());
        thirdSportMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdSportMarket.setScopeId(standardSportMarketCategory.getScopeId());
        thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
        //tx的创建时间必须严格使用上游的时间
        if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
            thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
        }
        thirdSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
        thirdSportMarket.setNameCode(UUIdUtils.getId());
        thirdSportMarket.setOfferLineId(thirdMarketDTO.getOfferLineId());
        MergeFunctionUtils.setNumberOfWinners( thirdSportMarket, thirdMarketDTO.getNumberOfWinners());
        thirdSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
        //thirdSportMarket.setEventType(thirdMarketDTO.getEventType());
        try {
            //发送mq
            marketDbProducer.sendThirdMarketInsertInfo(linkId, Arrays.asList(thirdSportMarket));
            //三方冠军盘口名称多语言处理
            if (thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketDTO.getI18nNames())) {
                List<I18nOutrightMarket> i18nOutrightMarketList = new ArrayList<>();
                thirdMarketDTO.getI18nNames().forEach(i18nItemDTO -> {
                    I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                    BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                    i18nOutrightMarket.setNameCode(thirdSportMarket.getNameCode());
                    i18nOutrightMarket.setDataSourceCode(thirdSportMarket.getDataSourceCode());
                    i18nOutrightMarketList.add(i18nOutrightMarket);
                });
                i18nOutrightMarketService.saveBatch(i18nOutrightMarketList);
            }
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口依然需要投递给下游
            log.info("::{}::insert三方盘口唯一约束冲突，尝试重新入库,matchId:{},三方盘口ID:{}", linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getThirdMarketSourceId());
        }
        return thirdSportMarket;
    }

    @Override
    @CachePut(key = "'ThirdSportMarket:' + #thirdSportMarket.matchId+ '-' + #thirdSportMarket.thirdMarketSourceId")
    public ThirdSportMarket updateByPrimaryKeySelective(ThirdSportMarket thirdSportMarket) {
        //发送mq
        marketDbProducer.sendThirdMarketUpdateInfo("", Arrays.asList(thirdSportMarket));
        //thirdSportMarketMapper.updateByPrimaryKeySelective(thirdSportMarket);
        return thirdSportMarket;
    }

    @Override
    public int updateByExampleSelective(Integer status, String dataSource, Long thirdMatchInfoId, List<Integer> statusList, List<Integer> marketTypeList) {
        String sql = "select *  from third_sport_market  where data_source_code=? and match_id=? and status " + "in (" + statusList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ") " + "and market_type in (" + marketTypeList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ") ";
        List<ThirdSportMarket> thirdSportMarkets = jdbcTemplate1.query(sql, new Object[]{dataSource, thirdMatchInfoId}, new BeanPropertyRowMapper<>(ThirdSportMarket.class));
        if (CollectionUtils.isEmpty(thirdSportMarkets)) {
            return 0;
        }
        List keyList = new ArrayList();
        thirdSportMarkets.forEach(thirdSportMarket -> {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId();
            keyList.add(key);
            thirdSportMarket.setStatus(status);
            thirdSportMarket.setThirdMarketSourceStatus(status);
            thirdSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        });
        redisService.del(keyList);
        //发送mq
        marketDbProducer.sendThirdMarketUpdateInfo("", thirdSportMarkets);
        return 1;
    }


    @Override
    public Page<ThirdSportMarket> getItemPageByModifyTime(PageModel<ThirdSportMarketDTO> page) {
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdSportMarketDao.getItemPageByModifyTime(page.getData());
    }


}
