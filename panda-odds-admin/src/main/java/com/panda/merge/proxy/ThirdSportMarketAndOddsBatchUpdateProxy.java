package com.panda.merge.proxy;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ThirdSportMarketDao;
import com.panda.merge.dao.ThirdSportMarketOddsDao;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.mapper.ThirdSportMarketMapper;
import com.panda.merge.mapper.ThirdSportMarketOddsMapper;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.service.ThirdSportMarketOddsNewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 三方盘口 ，投注项赔率 批量操作
 */
@Component
@Slf4j
public class ThirdSportMarketAndOddsBatchUpdateProxy {
    @Autowired
    private ThirdSportMarketDao thirdSportMarketDao;
    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;
    @Autowired
    private ThirdSportMarketOddsMapper thirdSportMarketOddsMapper;
    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;

    @Autowired
    private ThirdSportMarketOddsDao thirdSportMarketOddsDao;
    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("pandaOddsJdbcTemplate")
    private JdbcTemplate oddsJdbcTemplate;


    /**
     * 三方盘口 批量新增
     *
     * @param thirdSportMarkets
     */
    @Async("thirdMarketInsertAndUpdate")
    public void batchMarketInsert(String linkId, List<ThirdSportMarket> thirdSportMarkets) {
        log.info("::{}::batchMarketInsert，三方盘口新增,批量接收数据,开始处理:{}", linkId, thirdSportMarkets.size());
        try{
        String sql = "   insert into third_sport_market (id, tournament_id, match_id,\n" +
                "        market_category_id, third_market_source_id, reference_id,\n" +
                "        market_type, data_source_code, status,\n" +
                "        scope_id, name_code, odds_type_name,\n" +
                "        third_odds_type, odds_value, order_type,\n" +
                "        odds_name, odds_metric, addition1,\n" +
                "        addition2, addition3, addition4,\n" +
                "        addition5, remark, create_time,\n" +
                "        modify_time, extra_info, third_market_source_status,\n" +
                "        offer_line_id, number_of_winners, internal_data_source_code,\n" +
                "        event_type) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        oddsJdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, Objects.isNull(thirdSportMarkets.get(i).getId()) ? 0 : thirdSportMarkets.get(i).getId());
                        ps.setLong(2, Objects.isNull(thirdSportMarkets.get(i).getTournamentId()) ? 0 : thirdSportMarkets.get(i).getTournamentId());
                        ps.setLong(3, Objects.isNull(thirdSportMarkets.get(i).getMatchId()) ? 0 : thirdSportMarkets.get(i).getMatchId());
                        ps.setLong(4, Objects.isNull(thirdSportMarkets.get(i).getMarketCategoryId()) ? 0 : thirdSportMarkets.get(i).getMarketCategoryId());
                        ps.setString(5, Objects.isNull(thirdSportMarkets.get(i).getThirdMarketSourceId()) ? null : thirdSportMarkets.get(i).getThirdMarketSourceId());
                        ps.setLong(6, Objects.isNull(thirdSportMarkets.get(i).getReferenceId()) ? 0 : thirdSportMarkets.get(i).getReferenceId());
                        ps.setInt(7, Objects.isNull(thirdSportMarkets.get(i).getMarketType()) ? 0 : thirdSportMarkets.get(i).getMarketType());
                        ps.setString(8, Objects.isNull(thirdSportMarkets.get(i).getDataSourceCode()) ? null : thirdSportMarkets.get(i).getDataSourceCode());
                        ps.setInt(9, Objects.isNull(thirdSportMarkets.get(i).getStatus()) ? 0 : thirdSportMarkets.get(i).getStatus());
                        ps.setString(10, Objects.isNull(thirdSportMarkets.get(i).getScopeId()) ? null : thirdSportMarkets.get(i).getScopeId());
                        ps.setLong(11, Objects.isNull(thirdSportMarkets.get(i).getNameCode()) ? 0 : thirdSportMarkets.get(i).getNameCode());
                        ps.setString(12, Objects.isNull(thirdSportMarkets.get(i).getOddsTypeName()) ? null : thirdSportMarkets.get(i).getOddsTypeName());
                        ps.setString(13, Objects.isNull(thirdSportMarkets.get(i).getThirdOddsType()) ? null : thirdSportMarkets.get(i).getThirdOddsType());
                        ps.setString(14, Objects.isNull(thirdSportMarkets.get(i).getOddsValue()) ? null : thirdSportMarkets.get(i).getOddsValue());
                        ps.setString(15, Objects.isNull(thirdSportMarkets.get(i).getOrderType()) ? null : thirdSportMarkets.get(i).getOrderType());
                        ps.setString(16, Objects.isNull(thirdSportMarkets.get(i).getOddsName()) ? null : thirdSportMarkets.get(i).getOddsName());
                        ps.setLong(17, Objects.isNull(thirdSportMarkets.get(i).getOddsMetric()) ? 0 : thirdSportMarkets.get(i).getOddsMetric());
                        ps.setString(18, Objects.isNull(thirdSportMarkets.get(i).getAddition1()) ? null : thirdSportMarkets.get(i).getAddition1());
                        ps.setString(19, Objects.isNull(thirdSportMarkets.get(i).getAddition2()) ? null : thirdSportMarkets.get(i).getAddition2());
                        ps.setString(20, Objects.isNull(thirdSportMarkets.get(i).getAddition3()) ? null : thirdSportMarkets.get(i).getAddition3());
                        ps.setString(21, Objects.isNull(thirdSportMarkets.get(i).getAddition4()) ? null : thirdSportMarkets.get(i).getAddition4());
                        ps.setString(22, Objects.isNull(thirdSportMarkets.get(i).getAddition5()) ? null : thirdSportMarkets.get(i).getAddition5());
                        ps.setString(23, Objects.isNull(thirdSportMarkets.get(i).getRemark()) ? null: thirdSportMarkets.get(i).getRemark());
                        ps.setLong(24, Objects.isNull(thirdSportMarkets.get(i).getCreateTime()) ? 0 : thirdSportMarkets.get(i).getCreateTime());
                        ps.setLong(25, Objects.isNull(thirdSportMarkets.get(i).getModifyTime()) ? 0 : thirdSportMarkets.get(i).getModifyTime());
                        ps.setString(26, Objects.isNull(thirdSportMarkets.get(i).getExtraInfo()) ? null : thirdSportMarkets.get(i).getExtraInfo());
                        ps.setInt(27, Objects.isNull(thirdSportMarkets.get(i).getThirdMarketSourceStatus()) ? 0 : thirdSportMarkets.get(i).getThirdMarketSourceStatus());
                        ps.setInt(28, Objects.isNull(thirdSportMarkets.get(i).getOfferLineId()) ? 0 : thirdSportMarkets.get(i).getOfferLineId());
                        ps.setInt(29, Objects.isNull(thirdSportMarkets.get(i).getNumberOfWinners()) ? 0 : thirdSportMarkets.get(i).getNumberOfWinners());
                        ps.setString(30, Objects.isNull(thirdSportMarkets.get(i).getInternalDataSourceCode()) ? null : thirdSportMarkets.get(i).getInternalDataSourceCode());
                        ps.setInt(31, Objects.isNull(thirdSportMarkets.get(i).getEventType()) ? 0 : thirdSportMarkets.get(i).getEventType());
                    }
                    public int getBatchSize() {
                        return thirdSportMarkets.size();
                    }
                });
            log.info("::{}::batchMarketInsert，三方盘口新增,批量接收数据,处理完成:{}", linkId, thirdSportMarkets.size());
        }catch (Exception e){
            //三方盘口新增失败后清理缓存,操盘玩法切换查数据库没数据
            List<String> keyList = new ArrayList();
            thirdSportMarkets.forEach(thirdSportMarket -> {
                String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId();
                keyList.add(key);
            });
            redisService.del(keyList);
            e.printStackTrace();
            log.info("::{}::batchMarketInsert，三方盘口新增,批量接收数据：{}，清理缓存：{}，错误信息：：{}", linkId, JSONObject.toJSONString(thirdSportMarkets), keyList, e.getMessage());
        }


     /*   try {
            int i = thirdSportMarketDao.insertList(thirdSportMarkets);
            System.out.println("三方盘口执行完成" + i);
        } catch (DuplicateKeyException e) {
            log.info("::{}::batchMarketInsert，三方盘口批量新增：{}，出现主键冲突", linkId, JSONObject.toJSONString(thirdSportMarkets), e);
            //出现主键冲突，重新设置主键后，改为单条新增 ,要重新设置下缓存数据
            for (ThirdSportMarket thirdSportMarket : thirdSportMarkets) {
                thirdSportMarket.setId(UUIdUtils.getId());
                thirdSportMarketMapper.insert(thirdSportMarket);
                String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId();
                redisService.set(key, thirdSportMarket);
                log.info("::{}::batchMarketInsert，三方盘口批量新增：{}，出现主键冲突后再次单条入库", linkId, JSONObject.toJSONString(thirdSportMarket));
            }
        }*/
    }

    /**
     * 三方盘口 批量修改
     *
     * @param requests
     */
    @Async("thirdMarketInsertAndUpdate")
    public void batchMarketUpdate(List<Request<MarketDBMessage>> requests) {
        String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
        Long uuid = UUIdUtils.getId();
        log.info("::{}:: batchMarketUpdate，三方盘口修改,批量接收数据 start UUID: {} 请求size: {}", linkIds, uuid, requests.size());
        List<ThirdSportMarket> thirdSportMarkets = requests.stream()
                .filter(r -> !CollectionUtils.isEmpty(r.getData().getThirdSportMarkets()))
                .map(r -> r.getData().getThirdSportMarkets())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(thirdSportMarkets)){
            log.info("::{}:: batchMarketUpdate，三方盘口修改,批量接收数据不存在: {} requests: {}", linkIds, uuid, JSONObject.toJSONString(requests));
            return;
        }
        try {
            int[] updateCounts = oddsJdbcTemplate.batchUpdate("update third_sport_market set " +
                            "  `tournament_id` = ?," +
                            "  `match_id` = ?," +
                            "  `market_category_id` = ?," +
                            "  `third_market_source_id` = ?," +
                            "  `reference_id` = ?," +
                            "  `market_type` = ?," +
                            "  `data_source_code` = ?," +
                            "  `status` = ?," +
                            "  `scope_id` = ?," +
                            "  `name_code` = ?," +
                            "  `odds_type_name` = ?," +
                            "  `third_odds_type` = ?," +
                            "  `odds_value` = ?," +
                            "  `order_type` = ?," +
                            "  `odds_name` = ?," +
                            "  `odds_metric` = ?," +
                            "  `addition1` = ?," +
                            "  `addition2` = ?," +
                            "  `addition3` = ?," +
                            "  `addition4` = ?," +
                            "  `addition5` = ?," +
                            "  `remark` = ?," +
                            "  `create_time` = ?," +
                            "  `modify_time` = ?," +
                            "  `extra_info` = ?," +
                            "  `third_market_source_status` = ?," +
                            "  `offer_line_id` = ?," +
                            "  `number_of_winners` = ?," +
                            "  `internal_data_source_code` = ?," +
                            "  `event_type` = ? " +
                            "WHERE " +
                            "  `id` = ?" +
                            " and "+
                            " `modify_time` <= ?",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, Objects.isNull(thirdSportMarkets.get(i).getTournamentId()) ?  0 : thirdSportMarkets.get(i).getTournamentId());
                            ps.setLong(2,Objects.isNull(thirdSportMarkets.get(i).getMatchId())? 0 :thirdSportMarkets.get(i).getMatchId());
                            ps.setLong(3, Objects.isNull(thirdSportMarkets.get(i).getMarketCategoryId())? 0 :thirdSportMarkets.get(i).getMarketCategoryId());
                            ps.setString(4, Objects.isNull(thirdSportMarkets.get(i).getThirdMarketSourceId())? null : thirdSportMarkets.get(i).getThirdMarketSourceId());
                            ps.setLong(5, Objects.isNull(thirdSportMarkets.get(i).getReferenceId())? 0 : thirdSportMarkets.get(i).getReferenceId());
                            ps.setInt(6, Objects.isNull(thirdSportMarkets.get(i).getMarketType())? 0 : thirdSportMarkets.get(i).getMarketType());
                            ps.setString(7, Objects.isNull(thirdSportMarkets.get(i).getDataSourceCode())? null : thirdSportMarkets.get(i).getDataSourceCode());
                            ps.setInt(8, Objects.isNull(thirdSportMarkets.get(i).getStatus())? 0 : thirdSportMarkets.get(i).getStatus());
                            ps.setString(9, Objects.isNull(thirdSportMarkets.get(i).getScopeId())? null : thirdSportMarkets.get(i).getScopeId());
                            ps.setLong(10, Objects.isNull(thirdSportMarkets.get(i).getNameCode())? 0 : thirdSportMarkets.get(i).getNameCode());
                            ps.setString(11, Objects.isNull(thirdSportMarkets.get(i).getOddsTypeName())? null :thirdSportMarkets.get(i).getOddsTypeName());
                            ps.setString(12, Objects.isNull(thirdSportMarkets.get(i).getThirdOddsType())? null :thirdSportMarkets.get(i).getThirdOddsType());
                            ps.setString(13, Objects.isNull(thirdSportMarkets.get(i).getOddsValue())? null :thirdSportMarkets.get(i).getOddsValue());
                            ps.setString(14,Objects.isNull( thirdSportMarkets.get(i).getOrderType())? null :thirdSportMarkets.get(i).getOrderType());
                            ps.setString(15,Objects.isNull( thirdSportMarkets.get(i).getOddsName())? null :thirdSportMarkets.get(i).getOddsName());
                            ps.setLong(16, Objects.isNull(thirdSportMarkets.get(i).getOddsMetric())? 0 :thirdSportMarkets.get(i).getOddsMetric());
                            ps.setString(17, Objects.isNull(thirdSportMarkets.get(i).getAddition1())? null : thirdSportMarkets.get(i).getAddition1());
                            ps.setString(18, Objects.isNull(thirdSportMarkets.get(i).getAddition2())? null : thirdSportMarkets.get(i).getAddition2());
                            ps.setString(19, Objects.isNull(thirdSportMarkets.get(i).getAddition3())? null :thirdSportMarkets.get(i).getAddition3());
                            ps.setString(20, Objects.isNull(thirdSportMarkets.get(i).getAddition4())? null :thirdSportMarkets.get(i).getAddition4());
                            ps.setString(21, Objects.isNull(thirdSportMarkets.get(i).getAddition5())? null :thirdSportMarkets.get(i).getAddition5());
                            ps.setString(22, Objects.isNull(thirdSportMarkets.get(i).getRemark())? null :thirdSportMarkets.get(i).getRemark());
                            ps.setLong(23, Objects.isNull(thirdSportMarkets.get(i).getCreateTime())? 0 :thirdSportMarkets.get(i).getCreateTime());
                            ps.setLong(24, Objects.isNull(thirdSportMarkets.get(i).getModifyTime())? 0 :thirdSportMarkets.get(i).getModifyTime());
                            ps.setString(25, Objects.isNull(thirdSportMarkets.get(i).getExtraInfo())? null :thirdSportMarkets.get(i).getExtraInfo());
                            ps.setInt(26, Objects.isNull(thirdSportMarkets.get(i).getThirdMarketSourceStatus())? 0 :thirdSportMarkets.get(i).getThirdMarketSourceStatus());
                            ps.setInt(27, Objects.isNull(thirdSportMarkets.get(i).getOfferLineId())? 0 :thirdSportMarkets.get(i).getOfferLineId());
                            ps.setInt(28, Objects.isNull(thirdSportMarkets.get(i).getNumberOfWinners())? 0 :thirdSportMarkets.get(i).getNumberOfWinners());
                            ps.setString(29, Objects.isNull(thirdSportMarkets.get(i).getInternalDataSourceCode())? null :thirdSportMarkets.get(i).getInternalDataSourceCode());
                            ps.setInt(30, Objects.isNull(thirdSportMarkets.get(i).getEventType())? 0 : thirdSportMarkets.get(i).getEventType());
                            ps.setLong(31, thirdSportMarkets.get(i).getId());
                            ps.setLong(32, thirdSportMarkets.get(i).getModifyTime());
                        }
                        public int getBatchSize() {
                            return thirdSportMarkets.size();
                        }
                    });

            int actualRows = Arrays.stream(updateCounts).filter(count -> count > 0).sum();
            if (actualRows != thirdSportMarkets.size()) {
                //删除这一批三方盘口缓存
                List<String> keyList = new ArrayList();
                thirdSportMarkets.forEach(thirdSportMarket -> {
                    String redisKeyCode = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId() + "-" + thirdSportMarket.getDataSourceCode();
                    keyList.add(redisKeyCode);
                });
                redisService.del(keyList);
                log.info("::{}::batchMarketUpdate，三方盘口修改,批量接收数据:{},updateCounts：{}，actualRows：{}，keyList：{}，修改不成功删除三方盘口缓存", uuid, updateCounts, actualRows, keyList);
            }
            log.info("::{}::batchMarketUpdate，三方盘口修改,批量接收数据:{},updateCounts：{}，处理完成", uuid, thirdSportMarkets.size(), updateCounts);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("::{}::batchMarketUpdate，三方盘口修改,批量修改处理盘口出现异常:{}", uuid, e.getMessage());
        }
    }

    /**
     * 三方投注项 批量新增
     *
     * @param thirdSportMarketOddsList
     */
    @Async("thirdMarketOddsInsertAndUpdate")
    public void batchOddsInsert(String linkId, List<ThirdSportMarketOdds> thirdSportMarketOddsList) {
        //投注项赔率分组
        Map<String, List<ThirdSportMarketOdds>> thirdSportMarketOddsMap = thirdSportMarketOddsList.stream().collect(Collectors.groupingBy(ThirdSportMarketOdds::getDataSourceCode));
        for (Map.Entry<String, List<ThirdSportMarketOdds>> entry : thirdSportMarketOddsMap.entrySet()) {
            String dataSourceCode = entry.getKey();
            List<ThirdSportMarketOdds> thirdSportMarketOdds = entry.getValue();
            log.info("::{}::三方盘口赔率新增,批量接收数据: {}，开始入库数据源：{}", linkId, thirdSportMarketOdds.size(),dataSourceCode);
            try {
                String sql = " insert into third_sport_market_odds_"+dataSourceCode.toLowerCase()+
                        "(id, market_id, reference_id,\n" +
                        "        active, settlement_result_text, settlement_result,\n" +
                        "        bet_settlement_certainty, odds_type, addition1,\n" +
                        "        addition2, addition3, addition4,\n" +
                        "        addition5, third_odds_field_source_id, order_odds,\n" +
                        "        name_code, name_expression_value, odds_value,\n" +
                        "        pa_odds_value, original_odds_value, odds_fields_template_id,\n" +
                        "        third_template_source_id, target_side, data_source_code,\n" +
                        "        remark, create_time, modify_time,\n" +
                        "        extra_info, name, third_match_id\n" +
                        "        )" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                oddsJdbcTemplate.batchUpdate(sql,
                        new BatchPreparedStatementSetter() {
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, Objects.isNull(thirdSportMarketOdds.get(i).getId()) ? 0 : thirdSportMarketOdds.get(i).getId());
                                ps.setLong(2, Objects.isNull(thirdSportMarketOdds.get(i).getMarketId()) ? 0 : thirdSportMarketOdds.get(i).getMarketId());
                                ps.setLong(3, Objects.isNull(thirdSportMarketOdds.get(i).getReferenceId()) ? 0 : thirdSportMarketOdds.get(i).getReferenceId());
                                ps.setInt(4, Objects.isNull(thirdSportMarketOdds.get(i).getActive()) ? 0 : thirdSportMarketOdds.get(i).getActive());
                                ps.setString(5, Objects.isNull(thirdSportMarketOdds.get(i).getSettlementResultText()) ? null : thirdSportMarketOdds.get(i).getSettlementResultText());
                                ps.setString(6, Objects.isNull(thirdSportMarketOdds.get(i).getSettlementResult()) ? null : thirdSportMarketOdds.get(i).getSettlementResult());
                                ps.setString(7, Objects.isNull(thirdSportMarketOdds.get(i).getBetSettlementCertainty()) ? null : thirdSportMarketOdds.get(i).getBetSettlementCertainty());
                                ps.setString(8, Objects.isNull(thirdSportMarketOdds.get(i).getOddsType()) ? null : thirdSportMarketOdds.get(i).getOddsType());
                                ps.setString(9, Objects.isNull(thirdSportMarketOdds.get(i).getAddition1()) ? null : thirdSportMarketOdds.get(i).getAddition1());
                                ps.setString(10, Objects.isNull(thirdSportMarketOdds.get(i).getAddition2()) ? null : thirdSportMarketOdds.get(i).getAddition2());
                                ps.setString(11, Objects.isNull(thirdSportMarketOdds.get(i).getAddition3()) ? null : thirdSportMarketOdds.get(i).getAddition3());
                                ps.setString(12, Objects.isNull(thirdSportMarketOdds.get(i).getAddition4()) ? null : thirdSportMarketOdds.get(i).getAddition4());
                                ps.setString(13, Objects.isNull(thirdSportMarketOdds.get(i).getAddition5()) ? null : thirdSportMarketOdds.get(i).getAddition5());
                                ps.setString(14, Objects.isNull(thirdSportMarketOdds.get(i).getThirdOddsFieldSourceId()) ? null : thirdSportMarketOdds.get(i).getThirdOddsFieldSourceId());
                                ps.setInt(15, Objects.isNull(thirdSportMarketOdds.get(i).getOrderOdds()) ? 0 : thirdSportMarketOdds.get(i).getOrderOdds());
                                ps.setLong(16, Objects.isNull(thirdSportMarketOdds.get(i).getNameCode()) ? 0 : thirdSportMarketOdds.get(i).getNameCode());
                                ps.setString(17, Objects.isNull(thirdSportMarketOdds.get(i).getNameExpressionValue()) ? null : thirdSportMarketOdds.get(i).getNameExpressionValue());
                                ps.setInt(18, Objects.isNull(thirdSportMarketOdds.get(i).getOddsValue()) ? 0 : thirdSportMarketOdds.get(i).getOddsValue());
                                ps.setInt(19, Objects.isNull(thirdSportMarketOdds.get(i).getPaOddsValue()) ? 0 : thirdSportMarketOdds.get(i).getPaOddsValue());
                                ps.setInt(20, Objects.isNull(thirdSportMarketOdds.get(i).getOriginalOddsValue()) ? 0 : thirdSportMarketOdds.get(i).getOriginalOddsValue());
                                ps.setLong(21, Objects.isNull(thirdSportMarketOdds.get(i).getOddsFieldsTemplateId()) ? 0 : thirdSportMarketOdds.get(i).getOddsFieldsTemplateId());
                                ps.setString(22, Objects.isNull(thirdSportMarketOdds.get(i).getThirdTemplateSourceId()) ? null : thirdSportMarketOdds.get(i).getThirdTemplateSourceId());
                                ps.setString(23, Objects.isNull(thirdSportMarketOdds.get(i).getTargetSide()) ? null: thirdSportMarketOdds.get(i).getTargetSide());
                                ps.setString(24, Objects.isNull(thirdSportMarketOdds.get(i).getDataSourceCode()) ? null : thirdSportMarketOdds.get(i).getDataSourceCode());
                                ps.setString(25, Objects.isNull(thirdSportMarketOdds.get(i).getRemark()) ? null : thirdSportMarketOdds.get(i).getRemark());
                                ps.setLong(26, Objects.isNull(thirdSportMarketOdds.get(i).getCreateTime()) ? 0 : thirdSportMarketOdds.get(i).getCreateTime());
                                ps.setLong(27, Objects.isNull(thirdSportMarketOdds.get(i).getModifyTime()) ? 0 : thirdSportMarketOdds.get(i).getModifyTime());
                                ps.setString(28, Objects.isNull(thirdSportMarketOdds.get(i).getExtraInfo()) ? null : thirdSportMarketOdds.get(i).getExtraInfo());
                                ps.setString(29, Objects.isNull(thirdSportMarketOdds.get(i).getName()) ? null : thirdSportMarketOdds.get(i).getName());
                                ps.setLong(30, Objects.isNull(thirdSportMarketOdds.get(i).getThirdMatchId()) ? 0 : thirdSportMarketOdds.get(i).getThirdMatchId());
                            }
                            public int getBatchSize() {
                                return thirdSportMarketOdds.size();
                            }
                        });
                log.info("::{}::三方盘口赔率新增,批量接收数据: {}，入库数据源完成：{}", linkId, thirdSportMarketOdds.size(), dataSourceCode);

            } catch (Exception e) {
                //清理投注项缓存
                List<String> keyList = new ArrayList();
                thirdSportMarketOdds.forEach(odds->{
                    String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + odds.getMarketId()+"-"+odds.getThirdOddsFieldSourceId();
                    keyList.add(key);
                });
                //清理盘口id下投注项缓存
                String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOdds.get(0).getMarketId();
                keyList.add(key);
                redisService.del(keyList);
                e.printStackTrace();
                log.info("::{}::batchInsert，三方投注项数据源:{},批量新增：{}，清理缓存：{}，出现主键冲突:{}", linkId, dataSourceCode,keyList, JSONObject.toJSONString(thirdSportMarketOdds), e.getMessage());
            }

   /*         HintManager instance = HintManager.getInstance();
            try {
                instance.addTableShardingValue("third_sport_market_odds", dataSourceCode.toLowerCase());
                instance.addDatabaseShardingValue("third_sport_market_odds", "ds1");
                log.info("::{}::batchInsert，三方投注项数据源:{},批量新增处理盘口:{}", linkId, dataSourceCode, thirdSportMarketOdds.size());
                thirdSportMarketOddsDao.insertList(thirdSportMarketOdds, dataSourceCode.toLowerCase());
                System.out.println("------------------------执行结果" + thirdSportMarketOdds.size());
            } catch (DuplicateKeyException e) {
                log.info("::{}::batchInsert，三方投注项数据源:{},批量新增：{}，出现主键冲突", linkId, dataSourceCode, JSONObject.toJSONString(thirdSportMarketOdds), e);
                //出现主键冲突，重新设置主键后，改为单条新增 ,要重新设置下缓存数据
                for (ThirdSportMarketOdds thirdSportMarketOdd : thirdSportMarketOdds) {
                    thirdSportMarketOdd.setId(UUIdUtils.getId());
                    thirdSportMarketOddsMapper.insert(thirdSportMarketOdd);
                    String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarketOdds:" + thirdSportMarketOdd.getMarketId() + '-' + thirdSportMarketOdd.getThirdOddsFieldSourceId();
                    redisService.set(key, thirdSportMarketOdd);
                    log.info("::{}::batchInsert，三方投注项数据源:{},批量新增：{}，出现主键冲突后再次单条入库", linkId, dataSourceCode, JSONObject.toJSONString(thirdSportMarketOdd));
                }
            } finally {
                instance.close();
                log.info("::{}::batchInsert，三方投注项数据源:{},批量新增处理完成", linkId, dataSourceCode);
            }*/
        }
    }

    /**
     * 三方投注项 批量修改
     *
     * @param requests
     */
    @Async("thirdMarketOddsInsertAndUpdate")
    public void batchOddsUpdate(List<Request<MarketDBMessage>> requests) {
        String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
        Long uuid = UUIdUtils.getId();
        log.info("::{}:: 三方盘口赔率修改,批量接收数据 start UUID: {} 请求size: {}", linkIds, uuid, requests.size());
        List<ThirdSportMarketOdds> thirdSportMarketOddsList = requests.stream()
                .filter(r -> !CollectionUtils.isEmpty(r.getData().getThirdSportMarketOdds()))
                .map(r -> r.getData().getThirdSportMarketOdds())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        //投注项赔率分组
        Map<String, List<ThirdSportMarketOdds>> thirdSportMarketOddsMap = thirdSportMarketOddsList.stream().collect(Collectors.groupingBy(ThirdSportMarketOdds::getDataSourceCode));
        for (Map.Entry<String, List<ThirdSportMarketOdds>> entry : thirdSportMarketOddsMap.entrySet()) {
            String dataSourceCode = entry.getKey();
            List<ThirdSportMarketOdds> thirdSportMarketOdds = entry.getValue();
            log.info("::{}::三方盘口赔率修改,批量接收数据: {},开始入库数据源：{}", uuid, thirdSportMarketOdds.size(), dataSourceCode);
            try {
                oddsJdbcTemplate.batchUpdate("update third_sport_market_odds_"+dataSourceCode.toLowerCase() + " set " +
                                "  `reference_id` = ?," +
                                "  `active` = ?," +
                                "  `settlement_result_text` = ?," +
                                "  `settlement_result` = ?," +
                                "  `bet_settlement_certainty` = ?," +
                                "  `odds_type` = ?," +
                                "  `addition1` = ?," +
                                "  `addition2` = ?," +
                                "  `addition3` = ?," +
                                "  `addition4` = ?," +
                                "  `addition5` = ?," +
                                "  `order_odds` = ?," +
                                "  `name_code` = ?," +
                                "  `name_expression_value` = ?," +
                                "  `odds_value` = ?," +
                                "  `pa_odds_value` = ?," +
                                "  `original_odds_value` = ?," +
                                "  `odds_fields_template_id` = ?," +
                                "  `third_template_source_id` = ?," +
                                "  `target_side` = ?," +
                                "  `remark` = ?," +
                                "  `create_time` = ?," +
                                "  `modify_time` = ?," +
                                "  `extra_info` = ?," +
                                "  `name` = ?," +
                                "  `third_match_id` = ? " +
                                "WHERE " +
                                "  `id` = ?",
                        new BatchPreparedStatementSetter() {
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, Objects.isNull(thirdSportMarketOdds.get(i).getReferenceId()) ? 0 : thirdSportMarketOdds.get(i).getReferenceId());
                                ps.setInt(2, Objects.isNull(thirdSportMarketOdds.get(i).getActive()) ? 0 : thirdSportMarketOdds.get(i).getActive());
                                ps.setString(3, Objects.isNull(thirdSportMarketOdds.get(i).getSettlementResultText()) ? null : thirdSportMarketOdds.get(i).getSettlementResultText());
                                ps.setString(4, Objects.isNull(thirdSportMarketOdds.get(i).getSettlementResult()) ? null : thirdSportMarketOdds.get(i).getSettlementResult());
                                ps.setString(5, Objects.isNull(thirdSportMarketOdds.get(i).getBetSettlementCertainty()) ? null : thirdSportMarketOdds.get(i).getBetSettlementCertainty());
                                ps.setString(6, Objects.isNull(thirdSportMarketOdds.get(i).getOddsType()) ? null : thirdSportMarketOdds.get(i).getOddsType());
                                ps.setString(7, Objects.isNull(thirdSportMarketOdds.get(i).getAddition1()) ? null : thirdSportMarketOdds.get(i).getAddition1());
                                ps.setString(8, Objects.isNull(thirdSportMarketOdds.get(i).getAddition2()) ? null : thirdSportMarketOdds.get(i).getAddition2());
                                ps.setString(9, Objects.isNull(thirdSportMarketOdds.get(i).getAddition3()) ? null : thirdSportMarketOdds.get(i).getAddition3());
                                ps.setString(10, Objects.isNull(thirdSportMarketOdds.get(i).getAddition4()) ? null : thirdSportMarketOdds.get(i).getAddition4());
                                ps.setString(11, Objects.isNull(thirdSportMarketOdds.get(i).getAddition5()) ? null : thirdSportMarketOdds.get(i).getAddition5());
                                ps.setInt(12, Objects.isNull(thirdSportMarketOdds.get(i).getOrderOdds()) ? 0 : thirdSportMarketOdds.get(i).getOrderOdds());
                                ps.setLong(13, Objects.isNull(thirdSportMarketOdds.get(i).getNameCode()) ? 0 : thirdSportMarketOdds.get(i).getNameCode());
                                ps.setString(14, Objects.isNull(thirdSportMarketOdds.get(i).getNameExpressionValue()) ? null : thirdSportMarketOdds.get(i).getNameExpressionValue());
                                ps.setInt(15, Objects.isNull(thirdSportMarketOdds.get(i).getOddsValue()) ? 0 : thirdSportMarketOdds.get(i).getOddsValue());
                                ps.setInt(16, Objects.isNull(thirdSportMarketOdds.get(i).getPaOddsValue()) ? 0 : thirdSportMarketOdds.get(i).getPaOddsValue());
                                ps.setInt(17, Objects.isNull(thirdSportMarketOdds.get(i).getOriginalOddsValue()) ? 0 : thirdSportMarketOdds.get(i).getOriginalOddsValue());
                                ps.setLong(18, Objects.isNull(thirdSportMarketOdds.get(i).getOddsFieldsTemplateId()) ? 0 : thirdSportMarketOdds.get(i).getOddsFieldsTemplateId());
                                ps.setString(19, Objects.isNull(thirdSportMarketOdds.get(i).getThirdTemplateSourceId()) ? null : thirdSportMarketOdds.get(i).getThirdTemplateSourceId());
                                ps.setString(20, Objects.isNull(thirdSportMarketOdds.get(i).getTargetSide()) ? null : thirdSportMarketOdds.get(i).getTargetSide());
                                ps.setString(21, Objects.isNull(thirdSportMarketOdds.get(i).getRemark()) ? null : thirdSportMarketOdds.get(i).getRemark());
                                ps.setLong(22, Objects.isNull(thirdSportMarketOdds.get(i).getCreateTime()) ? 0 : thirdSportMarketOdds.get(i).getCreateTime());
                                ps.setLong(23, Objects.isNull(thirdSportMarketOdds.get(i).getModifyTime()) ? 0 : thirdSportMarketOdds.get(i).getModifyTime());
                                ps.setString(24, Objects.isNull(thirdSportMarketOdds.get(i).getExtraInfo()) ? null : thirdSportMarketOdds.get(i).getExtraInfo());
                                ps.setString(25, Objects.isNull(thirdSportMarketOdds.get(i).getName()) ? null : thirdSportMarketOdds.get(i).getName());
                                ps.setLong(26, Objects.isNull(thirdSportMarketOdds.get(i).getThirdMatchId()) ? 0 : thirdSportMarketOdds.get(i).getThirdMatchId());
                                ps.setLong(27, thirdSportMarketOdds.get(i).getId());
                            }

                            public int getBatchSize() {
                                return thirdSportMarketOdds.size();
                            }
                        });
                log.info("::{}::三方盘口赔率修改,批量接收数据: {},入库数据源完成：{}", uuid, thirdSportMarketOdds.size(), dataSourceCode);
            } catch (Exception e) {
                log.info("::{}::batchUpdate，三方投注项数据源:{},批量修改处理盘口出现异常:{}", uuid, dataSourceCode, e.getMessage());
                e.printStackTrace();
            }

          /*  HintManager instance = HintManager.getInstance();
            try {
                instance.addTableShardingValue("third_sport_market_odds", dataSourceCode.toLowerCase());
                instance.addDatabaseShardingValue("third_sport_market_odds", "ds1");
                log.info("::{}::batchUpdate，三方投注项数据源:{},批量修改处理盘口:{}", linkId, dataSourceCode, thirdSportMarketOdds.size());
                thirdSportMarketOddsDao.upDataList(thirdSportMarketOdds, dataSourceCode.toLowerCase());
            } catch (Exception e) {
                log.info("::{}::batchUpdate，三方投注项数据源:{},批量修改处理盘口出现异常", linkId, dataSourceCode, e);
                e.printStackTrace();
            } finally {
                instance.close();
            }*/
        }
    }


}
