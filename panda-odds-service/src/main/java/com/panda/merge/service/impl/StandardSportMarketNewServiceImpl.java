package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.MarketDbProducer;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dao.StandardSportMarketDao;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.odds.StandardMarketModification;
import com.panda.merge.mapper.StandardSportMarketMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.I18nOutrightMarketService;
import com.panda.merge.service.StandardSportMarketNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
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


@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportMarketNewServiceImpl implements StandardSportMarketNewService {

    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;

    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StandardSportMarketDao standardSportMarketDao;

    @Autowired
    private MarketDbProducer marketDbProducer;
    @Autowired
    @Qualifier("pandaOddsJdbcTemplate")
    private JdbcTemplate jdbcTemplate1;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'StandardSportMarket:' + #standardMatchId + '-' + #thirdMarketSourceId", unless = "#result == null ")
    public StandardSportMarket getItem(String dataSourceCode, String thirdMarketSourceId, Long standardMatchId) {
        String sql = "select *  from standard_sport_market  where data_source_code=? and third_market_source_id=? and standard_match_info_id =? ";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{dataSourceCode, thirdMarketSourceId, standardMatchId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return null;
        }
        return standardSportMarkets.get(0);
    }

    @Override
    public List<StandardSportMarket> getItems(List<OddsWrapper<ThirdMarketDTO>> standardSportMarkets) {
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return Collections.emptyList();
        }
        List<StandardSportMarket> result = new ArrayList<>();
        List<OddsWrapper<ThirdMarketDTO>> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = standardSportMarkets.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarket:"
                + t.getStandardSourceId()+"-"+t.getData().getThirdMarketSourceId()+"-"+t.getDataSourceCode()).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(standardSportMarkets, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }

        String sql = "select *  from standard_sport_market  where (standard_match_info_id, third_market_source_id, data_source_code) in (";
        for (OddsWrapper<ThirdMarketDTO> item : requiredCallItems) {
            sql += "(" + item.getStandardSourceId() + ", \""+ item.getData().getThirdMarketSourceId() + "\", \"" + item.getDataSourceCode() + "\"), ";
        }
        sql = sql.substring(0, sql.length()-2) + ")";
        List<StandardSportMarket> sportMarkets = jdbcTemplate1.query(sql, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        result.addAll(sportMarkets);
        // Storing the remained data into redis
        Map<String, Object> redisVal = sportMarkets.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarket:" + t.getStandardMatchInfoId() +"-"+t.getThirdMarketSourceId()
                        +"-"+t.getDataSourceCode(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    public StandardSportMarket getItemNoCache(String dataSourceCode, String thirdMarketSourceId, Long standardMatchId) {
        String sql = "select *  from standard_sport_market  where data_source_code=? and third_market_source_id=? and standard_match_info_id =? ";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{dataSourceCode, thirdMarketSourceId, standardMatchId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return null;
        }
        return standardSportMarkets.get(0);
    }
    /**
     * 创建标准盘口
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdMarketDTO
     * @param standardSportMarketCategory
     * @return
     */
    @CachePut(key = "'StandardSportMarket:' + #standardMatchInfo.id + '-' + #thirdMarketDTO.thirdMarketSourceId", unless = "#result == null ")
    @Override
    public StandardSportMarket create(String linkId, StandardMatchInfo standardMatchInfo, ThirdMarketDTO thirdMarketDTO, StandardSportMarketCategory standardSportMarketCategory) {
        StandardSportMarket standardSportMarket = new StandardSportMarket();
        BeanUtils.copyProperties(thirdMarketDTO, standardSportMarket);
        standardSportMarket.setMarketCategoryId(standardSportMarketCategory.getMarketCategoryId());
        standardSportMarket.setStandardMatchInfoId(standardMatchInfo.getId());
        standardSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
        standardSportMarket.setStatus(thirdMarketDTO.getStatus());
        standardSportMarket.setThirdMarketSourceId(thirdMarketDTO.getThirdMarketSourceId());
        MergeFunctionUtils.setNumberOfWinners( standardSportMarket, thirdMarketDTO.getNumberOfWinners());
        standardSportMarket.setId(UUIdUtils.getId());
        try {
            //TX生成统一盘口ID特殊处理 盘口值规则生成赋值:SendData ,三方数据源盘口ID生成赋值:RelationMarketId
            if (standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) {
                standardSportMarket.setRelationMarketId(Long.valueOf(txCreateRelationMarketId(standardSportMarket.getThirdMarketSourceId())));
                standardSportMarket.setSendData(createRelationMarketId(linkId, standardSportMarket).toString());
            } else {
                standardSportMarket.setRelationMarketId(createRelationMarketId(linkId, standardSportMarket));
            }
        } catch (Exception e) {
            log.info("::{}::生成relationMarketId异常,三方盘口源id{}，error", linkId, thirdMarketDTO.getThirdMarketSourceId(), e);
            return null;
        }
        standardSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
        standardSportMarket.setScopeId(standardSportMarketCategory.getScopeId());
        standardSportMarket.setTradeType(0);
        standardSportMarket.setLinkId(linkId);
        standardSportMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //tx的修改时间必须严格使用上游的修改时间
        if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
            standardSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
        }
        standardSportMarket.setNameCode(UUIdUtils.getId());
        try {
            //standardSportMarketMapper.insertSelective(standardSportMarket);
            //发送mq新增
            marketDbProducer.sendStandardMarketInsertInfo(linkId, Arrays.asList(standardSportMarket));
            //标准冠军盘口名称多语言处理
            if (2 == standardSportMarket.getMarketType() && !CollectionUtils.isEmpty(thirdMarketDTO.getI18nNames())) {
                StandardSportMarket finalStandardSportMarket = standardSportMarket;
                List<I18nOutrightMarket> i18nOutrightMarketList = new ArrayList<>();
                thirdMarketDTO.getI18nNames().forEach(i18nItemDTO -> {
                    I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                    BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                    i18nOutrightMarket.setFlag(2);
                    i18nOutrightMarket.setNameCode(finalStandardSportMarket.getNameCode());
                    i18nOutrightMarket.setDataSourceCode(finalStandardSportMarket.getDataSourceCode());
                    i18nOutrightMarketList.add(i18nOutrightMarket);
                });
                i18nOutrightMarketService.saveBatch(i18nOutrightMarketList);
            }
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口依然需要投递给下游
            log.info("::{}::insert标准盘口唯一约束冲突，error", linkId, e);
        }
        return standardSportMarket;
    }

    /**
     * 创建标准盘口(此方法不使用 @CachePut)
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdSportMarket
     * @param scopeId
     * @return
     */
    @Override
    @CachePut(key = "'StandardSportMarket:' + #standardMatchInfo.id + '-' + #thirdSportMarket.thirdMarketSourceId", unless = "#result == null ")
    public StandardSportMarket create(String linkId, StandardMatchInfo standardMatchInfo, ThirdSportMarket thirdSportMarket, String scopeId) {
        StandardSportMarket standardSportMarket = new StandardSportMarket();
        BeanUtils.copyProperties(thirdSportMarket, standardSportMarket);
        standardSportMarket.setId(UUIdUtils.getId());
        standardSportMarket.setMarketCategoryId(thirdSportMarket.getMarketCategoryId());
        standardSportMarket.setStandardMatchInfoId(standardMatchInfo.getId());
        standardSportMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        try {
            //TX生成统一盘口ID特殊处理 盘口值规则生成赋值:SendData ,三方数据源盘口ID生成赋值:RelationMarketId
            if (standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) {
                standardSportMarket.setRelationMarketId(Long.valueOf(txCreateRelationMarketId(standardSportMarket.getThirdMarketSourceId())));
                standardSportMarket.setSendData(createRelationMarketId(linkId, standardSportMarket).toString());
            } else {
                standardSportMarket.setRelationMarketId(createRelationMarketId(linkId, standardSportMarket));
                standardSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            }
            standardSportMarket.setModifyTime(!Objects.isNull(thirdSportMarket.getModifyTime()) ? thirdSportMarket.getModifyTime() : thirdSportMarket.getCreateTime());
        } catch (Exception e) {
            log.info("::{}::生成relationMarketId异常,三方盘口源id{}，error", linkId, thirdSportMarket.getThirdMarketSourceId(), e);
            return null;
        }

        //如果是PA自建盘口，需要统一为手动操盘
        if (standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.PA.code) || DataSourceCodeEnum.PA.name().equals(standardSportMarket.getRemark())) {
            standardSportMarket.setTradeType(Constant.OUTRIGHT_ONE);
        } else {
            standardSportMarket.setTradeType(Constant.OUTRIGHT_ZERO);
        }
        standardSportMarket.setScopeId(scopeId);
        standardSportMarket.setThirdMarketSourceStatus(thirdSportMarket.getStatus());
        standardSportMarket.setNameCode(UUIdUtils.getId());
        standardSportMarket.setLinkId(linkId);
        try {
            //standardSportMarketMapper.insertSelective(standardSportMarket);
            //发送mq新增
            marketDbProducer.sendStandardMarketInsertInfo(linkId, Arrays.asList(standardSportMarket));
            //标准冠军盘口名称多语言处理
            if (2 == standardSportMarket.getMarketType()) {
                List<I18nOutrightMarket> i18nOutrightMarketList = i18nOutrightMarketService.selectI18nOutrightMarketList(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getNameCode());
                if (!CollectionUtils.isEmpty(i18nOutrightMarketList)) {
                    StandardSportMarket finalStandardSportMarket = standardSportMarket;
                    i18nOutrightMarketList.forEach(i18nOutrightMarket -> {
                        i18nOutrightMarket.setFlag(2);
                        i18nOutrightMarket.setNameCode(finalStandardSportMarket.getNameCode());
                        i18nOutrightMarket.setDataSourceCode(finalStandardSportMarket.getDataSourceCode());
                    });
                    i18nOutrightMarketService.saveBatch(i18nOutrightMarketList);
                }
            }
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口依然需要投递给下游,但是需要重新从数据获取，不然同一个盘口会有不同的主键id跟统一盘口id
            //存在上面getItem 为空，但是创建又唯一键重复问题，导致下游看到的单盘口的玩法，但是下发了多个盘口的数据问题
            standardSportMarket = getItem(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(), standardMatchInfo.getId());
            log.info("::{}::insert标准盘口唯一约束冲突，error", linkId, e);
        }
        return standardSportMarket;
    }

    @Override
    @CachePut(key = "'StandardSportMarket:' + #standardSportMarket.standardMatchInfoId+ '-' + #standardSportMarket.thirdMarketSourceId", unless = "#result == null ")
    public StandardSportMarket updateByPrimaryKeySelective(StandardSportMarket standardSportMarket) {
        StandardSportMarket upStandardSportMarket = new StandardSportMarket();
        //数据来源，三方数据源盘口ID无需修改
        BeanUtil.copyProperties(standardSportMarket, upStandardSportMarket, "dataSourceCode", "thirdMarketSourceId", "createTime");
        //发送MQ修改
        marketDbProducer.sendStandardMarketUpdateInfo("", Arrays.asList(standardSportMarket));
        return standardSportMarket;
    }

    @Override
    public Long getRelationMarketId(String linkId, StandardSportMarket standardSportMarket) {
        String redisKey = RelationKeyFactory.getMarketRelationKey(linkId, standardSportMarket);
        Long relationMarketId;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketId = Long.valueOf(obj.toString());
        }
        redisService.set(redisKey, relationMarketId.toString(), RedisConfig.REDIS_MONTH_TIME);
        return relationMarketId;
    }

    //TODO 这里需要改进，将redisKey存到标准盘口表里面，不能完全依赖缓存

    /**
     * 构建RelationMarketId，任何数据商的相同盘口共用一个RelationMarketId
     *
     * @param linkId
     * @param standardSportMarket
     * @return
     */
    @Override
    public Long createRelationMarketId(String linkId, StandardSportMarket standardSportMarket) {
        return createRelationMarketId(linkId, standardSportMarket.getStandardMatchInfoId(), standardSportMarket);
    }

    @Override
    public <T extends StandardMarketModification> Long createRelationMarketId(String linkId,
                                                                              Long matchId,
                                                                              T standardSportMarket) {
        String redisKey = RelationKeyFactory.getMarketRelationKey(linkId, matchId,standardSportMarket);
        Long relationMarketId;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketId = Long.valueOf(obj.toString());
        }
        redisService.set(redisKey, relationMarketId.toString(), RedisConfig.REDIS_MONTH_TIME);
        return relationMarketId;
    }

    @Override
    public List<Object> createRelationMarketIds(List<StandardSportMarket> markets) {
        List<String> redisKeys = markets.stream().map(t->RelationKeyFactory.getMarketRelationKey(t.getLinkId(), t)).collect(Collectors.toList());
        List<Object> objs = redisService.mGet(redisKeys);
        Map<String, Object> storeData = new HashMap<>();
        for(int i = 0; i < objs.size(); i++) {
            if (objs.get(i) == null || StringUtils.isEmpty(objs.get(i).toString())) {
                Long relationMarketId = MD5Utils.getLongByMD5(redisKeys.get(i));
                storeData.put(redisKeys.get(i), relationMarketId);
                objs.set(i, relationMarketId);
            }
        }
        if(!MapUtils.isEmpty(storeData)){
            redisService.mSet(storeData);
        }
        return objs;
    }

    /**
     * TX 统一盘口ID生成
     * 1.根据盘口值生成统一盘口ID
     * 2.三方数据源盘口ID生成统一盘口ID
     *
     * @param thirdMarketSourceId
     * @return
     */
    @Override
    public String txCreateRelationMarketId(String thirdMarketSourceId) {
        //TX 标准盘口统一ID
        String redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_RELATION_MARKET_ID).append(thirdMarketSourceId).toString();
        Long relationMarketId;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketId = Long.valueOf(obj.toString());
        }
        redisService.set(redisKey, relationMarketId.toString(), RedisConfig.REDIS_MONTH_TIME);
        return relationMarketId.toString();
    }

    @Override
    public List<Object> txCreateRelationMarketIds(List<String> thirdMarketSourceId) {
        //TX 标准盘口统一ID
        List<String> redisKeys = thirdMarketSourceId.stream().map(t->Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_RELATION_MARKET_ID+t).collect(Collectors.toList());
        List<Object> objs = redisService.mGet(redisKeys);
        Map<String, Object> storeData = new HashMap<>();
        for(int i = 0; i < objs.size(); i++) {
            if (objs.get(i) == null || StringUtils.isEmpty(objs.get(i).toString())) {
                Long relationMarketId = MD5Utils.getLongByMD5(redisKeys.get(i));
                storeData.put(redisKeys.get(i), relationMarketId);
                objs.set(i, relationMarketId);
            }
        }
        if(!MapUtils.isEmpty(storeData)){
            redisService.mSet(storeData);
        }
        return objs;
    }

    @Override
    public List<StandardSportMarket> getItemList(Long standardMatchInfoId) {
        String sql = "select *  from standard_sport_market  where standard_match_info_id =? ";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{standardMatchInfoId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        return standardSportMarkets;
    }

    @Override
    public List<StandardSportMarket> getItemByThirdMarketSourceIdsAndDataSourceCode(List<String> strList, String dataSourceCode, Long standardMatchId) {
        /*String str = String.join("\',\'", strList);
        String finalStr = "'" + str + "'";
        String sql = "SELECT *  FROM standard_sport_market WHERE third_market_source_id in(" + finalStr + ") AND data_source_code =? AND standard_match_info_id =?";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{dataSourceCode, standardMatchId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
*/
        String sql = "select *  from standard_sport_market  where (standard_match_info_id, third_market_source_id, data_source_code) in (";
        for (String item : strList) {
            sql += "(" + standardMatchId + ", \""+ item + "\", \"" + dataSourceCode + "\"), ";
        }
        sql = sql.substring(0, sql.length()-2) + ")";
        List<StandardSportMarket> sportMarkets = jdbcTemplate1.query(sql, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        return sportMarkets;
    }

    @Override
    public List<StandardSportMarket> getItemByMatchIdAndDataSourceCode(Long standardMatchId, String dataSourceCode, List<Integer> marketTypeList) {
        String sql = "SELECT *  FROM standard_sport_market WHERE standard_match_info_id =? AND data_source_code =? AND market_type in(" + marketTypeList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{standardMatchId, dataSourceCode}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return null;
        }
        return standardSportMarkets;
    }

    @Override
    public int updateByExampleSelective(Integer status, String dataSource, Long standardMatchInfoId, List<Integer> statusList, List<Integer> marketTypeList) {
        //刷新数据库所有相关开盘盘口的状态为封盘
        StandardSportMarketExample standardSportMarketExample = new StandardSportMarketExample();
        //清理单个标准盘口缓存
        String sql = "SELECT *  FROM standard_sport_market WHERE data_source_code =? and standard_match_info_id =? and status " + "in (" + statusList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ") and market_type " + "in(" + marketTypeList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{dataSource, standardMatchInfoId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return 0;
        }
        return updateByExampleSelective(standardSportMarkets, standardSportMarketExample, status);
    }


    @Override
    public int updateBySelective(Long standardMatchId, Set<Long> marketCategoryIds, Integer status) {
        //查询标准玩法
        StandardSportMarketExample standardSportMarketExample = new StandardSportMarketExample();
//        standardSportMarketExample.createCriteria()
//                .andStandardMatchInfoIdEqualTo(standardMatchId)
//                .andMarketCategoryIdIn(new ArrayList<>(marketCategoryIds))
//                .andStatusNotEqualTo(status);
//        List<StandardSportMarket> standardSportMarkets = standardSportMarketMapper.selectByExample(standardSportMarketExample);
        String sql = "SELECT *  FROM standard_sport_market WHERE standard_match_info_id =? and market_category_id " + "in(" + marketCategoryIds.stream().map(Object::toString).collect(Collectors.joining(", ")) + ") and status !=? ";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{standardMatchId, status}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return 0;
        }
        return updateByExampleSelective(standardSportMarkets, standardSportMarketExample, status);
    }

    /**
     * 根据条件修改
     */
    private int updateByExampleSelective(List<StandardSportMarket> standardSportMarkets, StandardSportMarketExample standardSportMarketExample, Integer status) {
        standardSportMarkets.forEach(market -> {
            market.setStatus(status);
            market.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            refreshCache(market);
            market.setThirdMarketSourceStatus(status);
            market.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        });
        //发送mq修改
        marketDbProducer.sendStandardMarketUpdateInfo("", standardSportMarkets);
        return 1;
    }

    /**
     * 刷新缓存
     */
    public void refreshCache(StandardSportMarket item) {
        if (null != item) {
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarket:" + item.getStandardMatchInfoId() + "-" + item.getThirdMarketSourceId(), item, RedisConfig.REDIS_MY_TIME);
        }
    }

    @Override
    public StandardSportMarket getMarketByRelationId(Long relationMarketId) {
        String sql = "SELECT *  FROM standard_sport_market WHERE relation_market_id = ? ";
        List<StandardSportMarket> standardSportMarkets = jdbcTemplate1.query(sql, new Object[]{relationMarketId}, new BeanPropertyRowMapper<>(StandardSportMarket.class));
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return null;
        }
        return standardSportMarkets.get(0);
    }

    @Override
    public List<StandardSportMarket> getItemsByRelationMarketIds(List<Long> relationMarketIds) {
        if (CollectionUtils.isEmpty(relationMarketIds)) {
            return Collections.emptyList();
        }
        String inClause = relationMarketIds.stream().map(Object::toString).collect(Collectors.joining(", "));
        String sql = "select * from standard_sport_market where relation_market_id in (" + inClause + ")";
        return jdbcTemplate1.query(sql, new BeanPropertyRowMapper<>(StandardSportMarket.class));
    }


}
