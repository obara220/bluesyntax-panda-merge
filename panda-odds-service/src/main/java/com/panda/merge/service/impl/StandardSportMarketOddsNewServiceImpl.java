package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.MarketDbProducer;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dao.StandardSportMarketOddsDao;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.mapper.StandardSportMarketOddsMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.I18nOutrightMarketOddsService;
import com.panda.merge.service.StandardSportMarketOddsNewService;
import com.panda.merge.service.ThirdSportTeamService;
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
 * @createDate 2020/8/14 <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportMarketOddsNewServiceImpl implements StandardSportMarketOddsNewService {

    @Autowired
    private StandardSportMarketOddsMapper standardSportMarketOddsMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MarketDbProducer marketDbProducer;

    @Autowired
    private I18nOutrightMarketOddsService i18nOutrightMarketOddsService;

    @Autowired
    private ThirdSportTeamService thirdSportTeamService;


    @Autowired
    private StandardSportMarketOddsDao standardSportMarketOddsDao;

    @Autowired
    @Qualifier("pandaOddsJdbcTemplate")
    private JdbcTemplate jdbcTemplate1;

    @Resource
    private RedisHelper redisHelper;

    @Override
    @Cacheable(key = "'StandardSportMarketOdds:' + #standardMarketId + '-' + #thirdOddsFieldSourceId", unless = "#result == null ")
    public StandardSportMarketOdds getItem(String dataSourceCode, String thirdOddsFieldSourceId, Long standardMarketId) {
        String sql = "select *  from standard_sport_market_odds_" + standardMarketId % 10 + " where  data_source_code=? and third_odds_field_source_id=? and market_id=?  ";
        List<StandardSportMarketOdds> standardSportMarketOdds = jdbcTemplate1.query(sql, new Object[]{dataSourceCode, thirdOddsFieldSourceId, standardMarketId}, new BeanPropertyRowMapper<>(StandardSportMarketOdds.class));
        if (CollectionUtils.isEmpty(standardSportMarketOdds)) {
            return null;
        }
        return standardSportMarketOdds.get(0);
    }

    @Override
    public List<StandardSportMarketOdds> getItems(List<String> dataSourceIdAndMarketId) {
        if(CollectionUtils.isEmpty(dataSourceIdAndMarketId)) {
            return Collections.emptyList();
        }
        List<StandardSportMarketOdds> result = new ArrayList<>();
        List<String> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keysRedis = new ArrayList<>();
        for (String item : dataSourceIdAndMarketId) {
            String[] array = item.split("->");         // dataSourceCode: array   thirdMarketCategorySourceId: arr[1]
            if (array.length != 3) {
                throw new RuntimeException("[StandardSportMarketOddsNewServiceImpl] getItems parameter marketSellkeys's split array length is not equal to 3!");
            }
            keysRedis.add(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketOdds:" + array[0] +"-"+array[1]+"-"+array[2]);
        }
        List<Object> objectList= redisService.mGet(keysRedis);
        redisHelper.postProcMget(dataSourceIdAndMarketId, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }

        Map<Long, List<String[]>> keys = new HashMap<>();
        for(String item : requiredCallItems) {
            String[] array = item.split("->");
            Long key = Long.parseLong(array[2]) %10;
            List<String[]> value = keys.getOrDefault(key, new ArrayList<>());
            value.add(array);
            keys.put(key, value);
        }
        Map<String, Object> redisVal = new HashMap<>();
        for(Map.Entry<Long, List<String[]>> entry : keys.entrySet()) {
            String sql = "SELECT * FROM standard_sport_market_odds_" + entry.getKey() + " WHERE (data_source_code, third_odds_field_source_id, market_id) IN (";
            for (int i = 0; i < entry.getValue().size(); i++) {
                String[] values = entry.getValue().get(i);
                sql += "(\"" + values[0] + "\", \""+ values[1] + "\", " + values[2] + "), ";  // 0:  dataSourceCode 1: thirdOddsFieldSourceId 2: standardMarketId
            }
            sql = sql.substring(0, sql.length()-2) + ")";
            List<StandardSportMarketOdds> standardSportMarketOdds = jdbcTemplate1.query(sql, new BeanPropertyRowMapper<>(StandardSportMarketOdds.class));
            result.addAll(standardSportMarketOdds);
            for(StandardSportMarketOdds item : standardSportMarketOdds){
                String redisKey = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketOdds:" + item.getDataSourceCode() +"-"+item.getThirdOddsFieldSourceId() + "-" + item.getMarketId();
                redisVal.put(redisKey, item);
            }
        }
        redisService.mSet(redisVal);
        return result;
    }

    /**
     * 创建标准盘口投注项(此方法不使用 @CachePut)
     *
     * @param linkId
     * @param thirdMarketOddsDTO
     * @param thirdSportOddsFieldsTemplet
     * @return
     */
    @CachePut(key = "'StandardSportMarketOdds:' + #standardSportMarket.id + '-' + #thirdMarketOddsDTO.thirdOddsFieldSourceId", unless = "#result == null ")
    @Override
    public StandardSportMarketOdds create(String linkId, boolean isOutRight, StandardSportMarket standardSportMarket, ThirdMarketOddsDTO thirdMarketOddsDTO, ThirdMarketCategoryField thirdSportOddsFieldsTemplet) {
        StandardSportMarketOdds standardSportMarketOdds = new StandardSportMarketOdds();
        BeanUtils.copyProperties(thirdMarketOddsDTO, standardSportMarketOdds);
        standardSportMarketOdds.setMarketId(standardSportMarket.getId());
        standardSportMarketOdds.setRelationMarketId(standardSportMarket.getRelationMarketId());
        standardSportMarketOdds.setOddsFieldsTemplateId(thirdSportOddsFieldsTemplet.getReferenceId());
        standardSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
        standardSportMarketOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardSportMarketOdds.setId(UUIdUtils.getId());
        standardSportMarketOdds.setRelationMarketOddsId(createRelationMarketOddsId(standardSportMarketOdds, standardSportMarket));
        standardSportMarketOdds.setStandardMatchId(standardSportMarket.getStandardMatchInfoId());
        standardSportMarketOdds.setName(getOddsName(thirdMarketOddsDTO.getI18nNames()));
        standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
        //标准球队转换
        convertStandardTeam(linkId, standardSportMarketOdds, standardSportMarket);
        try {
            //standardSportMarketOddsMapper.insertSelective(standardSportMarketOdds);
            //发送MQ
            marketDbProducer.sendStandardMarketOddsInsertInfo(linkId, Arrays.asList(standardSportMarketOdds));
            log.info("::{}::insert标准盘口投注项成功,盘口ID:{},标准盘口ID:{},标准投注项ID:{}", linkId, standardSportMarketOdds.getMarketId(), standardSportMarketOdds.getRelationMarketId(), standardSportMarketOdds.getRelationMarketOddsId());
            //冠军投注项国际化入库
            if (isOutRight && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
                List<I18nOutrightMarketOdds> i18nMarketOddsList = new ArrayList<>();
                for (I18nItemDTO dto : thirdMarketOddsDTO.getI18nNames()) {
                    I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                    BeanUtils.copyProperties(dto, i18nOutrightMarketOdds);
                    i18nOutrightMarketOdds.setNameCode(standardSportMarketOdds.getNameCode());
                    i18nOutrightMarketOdds.setDataSourceCode(standardSportMarketOdds.getDataSourceCode());
                    i18nMarketOddsList.add(i18nOutrightMarketOdds);
                }
                i18nOutrightMarketOddsService.saveBatch(i18nMarketOddsList);
            }
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::insert标准盘口投注项唯一约束冲突，error", linkId, e);
        }
        standardSportMarketOdds.setI18nNames(thirdMarketOddsDTO.getI18nNames());
        return standardSportMarketOdds;
    }

    /**
     * 投注项的三方球队id转为标准的球队id
     *
     * @param linkId
     * @param standardSportMarketOdds
     * @param standardSportMarket
     */

    @Override
    public void convertStandardTeam(String linkId, StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket) {
        Long marketCategoryId = standardSportMarket.getMarketCategoryId();
        try {
            if (DataSourceCodeEnum.AO.code.equals(standardSportMarket.getDataSourceCode())) {
                return;
            }
            if (Arrays.asList(Constant.CATEGORY_ADDITION1).contains(marketCategoryId)) {
                ThirdSportTeam thirdSportTeam = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition1());
                standardSportMarketOdds.setAddition1(null == thirdSportTeam ? "0" : String.valueOf(thirdSportTeam.getReferenceId()));
            } else if (Arrays.asList(Constant.CATEGORY_ADDITION2).contains(marketCategoryId)) {
                ThirdSportTeam thirdSportTeam = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition2());
                standardSportMarketOdds.setAddition2(null == thirdSportTeam ? "0" : String.valueOf(thirdSportTeam.getReferenceId()));
            } else if (Arrays.asList(Constant.CATEGORY_ADDITION3).contains(marketCategoryId)) {
                ThirdSportTeam thirdSportTeam = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition3());
                standardSportMarketOdds.setAddition3(null == thirdSportTeam ? "0" : String.valueOf(thirdSportTeam.getReferenceId()));
            } else if (Arrays.asList(Constant.CATEGORY_ADDITION2_ADDITION4).contains(marketCategoryId)) {
                ThirdSportTeam thirdSportTeam = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition2());
                standardSportMarketOdds.setAddition2(null == thirdSportTeam ? "0" : String.valueOf(thirdSportTeam.getReferenceId()));
                ThirdSportTeam thirdSportTeam1 = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition4());
                standardSportMarketOdds.setAddition4(null == thirdSportTeam1 ? "0" : String.valueOf(thirdSportTeam1.getReferenceId()));
            } else if (Arrays.asList(Constant.CATEGORY_ADDITION1_ADDITION2).contains(marketCategoryId)) {
                ThirdSportTeam thirdSportTeam = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition2());
                standardSportMarketOdds.setAddition2(null == thirdSportTeam ? "0" : String.valueOf(thirdSportTeam.getReferenceId()));
                ThirdSportTeam thirdSportTeam1 = thirdSportTeamService.getItemByExampleNoSportId(standardSportMarket.getDataSourceCode(), standardSportMarketOdds.getAddition1());
                standardSportMarketOdds.setAddition1(null == thirdSportTeam1 ? "0" : String.valueOf(thirdSportTeam1.getReferenceId()));
            }
        } catch (Exception e) {
            log.info("::{}::convertStandardTeam,玩法id:{},addition1-4:{},{},{},{},error:{}", linkId, marketCategoryId, standardSportMarketOdds.getAddition1(), standardSportMarketOdds.getAddition2(), standardSportMarketOdds.getAddition3(), standardSportMarketOdds.getAddition4(), e);
        }
    }

    /**
     * 创建标准盘口投注项(此方法不使用 @CachePut)
     *
     * @param linkId
     * @param standardSportMarketOdds
     * @return
     */
    @Override
    public StandardSportMarketOdds create(String linkId, StandardSportMarketOdds standardSportMarketOdds) {
        try {
            //发送mq
            marketDbProducer.sendStandardMarketOddsInsertInfo(linkId, Arrays.asList(standardSportMarketOdds));
            //standardSportMarketOddsMapper.insertSelective(standardSportMarketOdds);
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("::{}::insert标准盘口投注项唯一约束冲突，error", linkId, e);
        }
        return standardSportMarketOdds;
    }

    @Override
    @CachePut(key = "'StandardSportMarketOdds:' + #standardSportMarketOdds.marketId + '-' + #standardSportMarketOdds.thirdOddsFieldSourceId")
    //@Async("StandardSportMarketThreadPool")
    public StandardSportMarketOdds updateByPrimaryKeySelective(StandardSportMarketOdds standardSportMarketOdds) {
        //发送mq
        marketDbProducer.sendStandardMarketOddsUpdateInfo("",Arrays.asList(standardSportMarketOdds));
        return standardSportMarketOdds;
    }

    @Override
    public Long getRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, Long marketGategoryId) {
        Long relationMarketOddsId;
        //兼容冠军投注项id历史数据
        if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(marketGategoryId)) {
            StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID);
            String key = redisKey.append(standardSportMarketOdds.getRelationMarketId()).append("_").append(standardSportMarketOdds.getOddsType()).toString();
            if (redisService.get(key) != null && !StringUtils.isEmpty(redisService.get(key).toString())) {
                return Long.valueOf(redisService.get(key).toString());
            }
        }
        String redisKey = RelationKeyFactory.getMarketOddsRelationKey(standardSportMarketOdds.getRelationMarketId(), standardSportMarketOdds, marketGategoryId);
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketOddsId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketOddsId = Long.valueOf(obj.toString());
        }
        redisService.set(redisKey, relationMarketOddsId, RedisConfig.REDIS_MONTH_TIME);
        return relationMarketOddsId;
    }


    //TODO 这里需要改进，将redisKey存到标准盘口表里面，不能完全依赖缓存

    /**
     * 构建RelationMarketOddsId，任何数据商的相同盘口投注项共用一个RelationMarketOddsId
     *
     * @param standardSportMarketOdds
     * @return
     */
    @Override
    public Long createRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket) {
        Long relationMarketOddsIdStr = null;
        try {
            //兼容冠军投注项id历史数据
            if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(standardSportMarket.getMarketCategoryId())) {
                StringBuffer redisKey = new StringBuffer(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID);
                String key = redisKey.append(standardSportMarketOdds.getRelationMarketId()).append("_").append(standardSportMarketOdds.getOddsType()).toString();
                if (redisService.get(key) != null && !StringUtils.isEmpty(redisService.get(key).toString())) {
                    return Long.valueOf(redisService.get(key).toString());
                }
            }
            String redisKey = RelationKeyFactory.getMarketOddsRelationKey(standardSportMarketOdds.getRelationMarketId(), standardSportMarketOdds, standardSportMarket.getMarketCategoryId());
            Object obj = redisService.get(redisKey);
            if (obj == null || StringUtils.isEmpty(obj.toString())) {
                relationMarketOddsIdStr = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();;
            } else {
                relationMarketOddsIdStr = Long.valueOf(obj.toString());
            }
            redisService.set(redisKey, relationMarketOddsIdStr.toString(), RedisConfig.REDIS_MONTH_TIME);
            //TX投注项特殊处理
            if (standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) {
                standardSportMarketOdds.setRemark(txCreateRelationMarketOddsId(standardSportMarketOdds, standardSportMarket));
            }
        } catch (Exception e) {
            log.info("::{}::createRelationMarketOddsId,error:{}", standardSportMarket.getRelationMarketId(), e);
        }
        return relationMarketOddsIdStr;
    }

    /**
     * TX根据三方盘口ID去除后缀位置生成 统一盘口ID 调盘
     *
     * @param
     * @return
     */
    public String adjustmentTxCreateRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardMarketDataMessage standardSportMarket) {
        String redisKey = RelationKeyFactory.getMarketOddsRelationKey(Long.valueOf(standardSportMarket.getSendData()), standardSportMarketOdds, standardSportMarket.getMarketCategoryId());
        Long relationMarketOddsIdStr;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketOddsIdStr = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();;
        } else {
            relationMarketOddsIdStr = Long.valueOf(obj.toString());
        }
        redisService.set(redisKey, relationMarketOddsIdStr.toString(), RedisConfig.REDIS_MONTH_TIME);
        return relationMarketOddsIdStr.toString();
    }

    /**
     * TX根据三方盘口ID去除后缀位置生成 统一盘口ID
     *
     * @param
     * @return
     */
    public String txCreateRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket) {
        String redisKey = RelationKeyFactory.getMarketOddsRelationKey(Long.valueOf(standardSportMarket.getSendData()), standardSportMarketOdds, standardSportMarket.getMarketCategoryId());
        Long relationMarketOddsIdStr;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketOddsIdStr = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();;
        } else {
            relationMarketOddsIdStr = Long.valueOf(obj.toString());
        }
        redisService.set(redisKey, relationMarketOddsIdStr.toString(), RedisConfig.REDIS_MONTH_TIME);
        return relationMarketOddsIdStr.toString();
    }

    @Override
    public List<StandardSportMarketOdds> getItemList(Long marketId) {
        String sql = "select *  from standard_sport_market_odds_" + marketId % 10 + " where  market_id=?  ";
        List<StandardSportMarketOdds> standardSportMarketOdds = jdbcTemplate1.query(sql, new Object[]{marketId}, new BeanPropertyRowMapper<>(StandardSportMarketOdds.class));
        return standardSportMarketOdds;
    }

    /**
     * 根据上游下发的国际化信息，按中文、英文、其他优先级获取投注项名称
     **/
    public static String getOddsName(List<I18nItemDTO> i18nNames) {
        String name = "";
        if (!CollectionUtils.isEmpty(i18nNames)) {
            name = i18nNames.get(0).getText();
            Map<String, String> collect = i18nNames.stream().collect(Collectors.toMap(I18nItemDTO::getLanguageType, I18nItemDTO::getText));
            if (!org.apache.commons.lang3.StringUtils.isEmpty(collect.get("zs"))) {
                name = collect.get("zs");
            } else if (!org.apache.commons.lang3.StringUtils.isEmpty(collect.get("en"))) {
                name = collect.get("en");
            }
            return name;
        }
        return name;
    }

    @Override
    public List<StandardSportMarketOdds> getMarketOddsByMatchIdList(List<Long> standardSportMarketIdList) {
        return standardSportMarketOddsDao.getMarketOddsByMatchIdList(standardSportMarketIdList);
    }

    @Override
    public void upStandardOddsList(String linkId, Long standardMatchId, List<StandardSportMarketOdds> upOddsList) {
        upOddsList = upOddsList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        for (StandardSportMarketOdds sportMarketOdds : upOddsList) {
            try {
                String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketOdds:" + sportMarketOdds.getId() + '-' + sportMarketOdds.getThirdOddsFieldSourceId();
                map.put(key, sportMarketOdds);
            }catch (Exception e){
                log.error("::" + linkId + "::upStandardOddsList,upOddsList:"+ JSONObject.toJSONString(upOddsList) +",出现异常", e);
            }
        }
        redisService.mSet(map);
        //发送mq
        marketDbProducer.sendStandardMarketOddsUpdateInfo(linkId,upOddsList);
    }
}
