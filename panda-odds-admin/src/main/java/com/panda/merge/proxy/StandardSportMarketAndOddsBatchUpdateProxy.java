package com.panda.merge.proxy;

import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardSportMarketDao;
import com.panda.merge.dao.StandardSportMarketOddsDao;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MarketDBMessage;
import com.panda.merge.mapper.StandardSportMarketMapper;
import com.panda.merge.mapper.StandardSportMarketOddsMapper;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 标准盘口 ，投注项赔率 批量操作
 */
@Component
@Slf4j
public class StandardSportMarketAndOddsBatchUpdateProxy {
    @Autowired
    StandardSportMarketDao standardSportMarketDao;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    StandardSportMarketOddsDao standardSportMarketOddsDao;
    @Autowired
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    @Qualifier("pandaOddsJdbcTemplate")
    private JdbcTemplate oddsJdbcTemplate;

    /**
     * 标准盘口 批量新增
     *
     * @param standardSportMarketList
     */
    @Async("standardMarketInsertAndUpdate")
    public void batchStandardMarketInsert(String linkId, List<StandardSportMarket> standardSportMarketList) {
        standardSportMarketList.forEach(standardSportMarket -> {
            if(StringUtils.isEmpty(standardSportMarket.getSendData())){
                standardSportMarket.setSendData("Y");
            }
        });
        log.info("::{}::batchStandardMarketInsert，标准盘口新增,批量接收数据:{},开始处理 ", linkId, standardSportMarketList.size());
        String sql = " insert into standard_sport_market (id, relation_market_id, standard_tournament_id,\n" +
                "        standard_match_info_id, market_category_id, market_type,\n" +
                "        trade_type, name_code, odds_value,\n" +
                "        odds_name, order_type, odds_metric,\n" +
                "        addition1, addition2, addition3,\n" +
                "        addition4, addition5, data_source_code,\n" +
                "        status, third_market_source_status, scope_id,\n" +
                "        third_odds_type, third_market_source_id, send_data,\n" +
                "        link_id, extra_info, place_num,\n" +
                "        remark, create_time, modify_time,\n" +
                "        number_of_winners) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            oddsJdbcTemplate.batchUpdate( sql,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, Objects.isNull(standardSportMarketList.get(i).getId()) ? 0 : standardSportMarketList.get(i).getId());
                            ps.setLong(2, Objects.isNull(standardSportMarketList.get(i).getRelationMarketId()) ? 0 : standardSportMarketList.get(i).getRelationMarketId());
                            ps.setLong(3, Objects.isNull(standardSportMarketList.get(i).getStandardTournamentId()) ? 0 : standardSportMarketList.get(i).getStandardTournamentId());
                            ps.setLong(4, Objects.isNull(standardSportMarketList.get(i).getStandardMatchInfoId()) ? 0 : standardSportMarketList.get(i).getStandardMatchInfoId());
                            ps.setLong(5, Objects.isNull(standardSportMarketList.get(i).getMarketCategoryId()) ? 0 : standardSportMarketList.get(i).getMarketCategoryId());
                            ps.setInt(6, Objects.isNull(standardSportMarketList.get(i).getMarketType()) ? 0 : standardSportMarketList.get(i).getMarketType());
                            ps.setInt(7, Objects.isNull(standardSportMarketList.get(i).getTradeType()) ? 0 : standardSportMarketList.get(i).getTradeType());
                            ps.setLong(8, Objects.isNull(standardSportMarketList.get(i).getNameCode()) ? 0 : standardSportMarketList.get(i).getNameCode());
                            ps.setString(9, Objects.isNull(standardSportMarketList.get(i).getOddsValue()) ? null : standardSportMarketList.get(i).getOddsValue());
                            ps.setString(10, Objects.isNull(standardSportMarketList.get(i).getOddsName()) ? null : standardSportMarketList.get(i).getOddsName());
                            ps.setString(11, Objects.isNull(standardSportMarketList.get(i).getOrderType()) ? null : standardSportMarketList.get(i).getOrderType());
                            ps.setLong(12, Objects.isNull(standardSportMarketList.get(i).getOddsMetric()) ? 0 : standardSportMarketList.get(i).getOddsMetric());
                            ps.setString(13, Objects.isNull(standardSportMarketList.get(i).getAddition1()) ? null : standardSportMarketList.get(i).getAddition1());
                            ps.setString(14, Objects.isNull(standardSportMarketList.get(i).getAddition2()) ? null : standardSportMarketList.get(i).getAddition2());
                            ps.setString(15, Objects.isNull(standardSportMarketList.get(i).getAddition3()) ? null : standardSportMarketList.get(i).getAddition3());
                            ps.setString(16, Objects.isNull(standardSportMarketList.get(i).getAddition4()) ? null : standardSportMarketList.get(i).getAddition4());
                            ps.setString(17, Objects.isNull(standardSportMarketList.get(i).getAddition5()) ? null : standardSportMarketList.get(i).getAddition5());
                            ps.setString(18, Objects.isNull(standardSportMarketList.get(i).getDataSourceCode()) ? null : standardSportMarketList.get(i).getDataSourceCode());
                            ps.setInt(19, Objects.isNull(standardSportMarketList.get(i).getStatus()) ? 0 : standardSportMarketList.get(i).getStatus());
                            ps.setInt(20, Objects.isNull(standardSportMarketList.get(i).getThirdMarketSourceStatus()) ? 0 : standardSportMarketList.get(i).getThirdMarketSourceStatus());
                            ps.setString(21, Objects.isNull(standardSportMarketList.get(i).getScopeId()) ? null : standardSportMarketList.get(i).getScopeId());
                            ps.setString(22, Objects.isNull(standardSportMarketList.get(i).getThirdOddsType()) ? null : standardSportMarketList.get(i).getThirdOddsType());
                            ps.setString(23, Objects.isNull(standardSportMarketList.get(i).getThirdMarketSourceId()) ? null : standardSportMarketList.get(i).getThirdMarketSourceId());
                            ps.setString(24, Objects.isNull(standardSportMarketList.get(i).getSendData()) ? null : standardSportMarketList.get(i).getSendData());
                            ps.setString(25, Objects.isNull(standardSportMarketList.get(i).getLinkId()) ? null : standardSportMarketList.get(i).getLinkId());
                            ps.setString(26, Objects.isNull(standardSportMarketList.get(i).getExtraInfo()) ? null : standardSportMarketList.get(i).getExtraInfo());
                            ps.setInt(27, Objects.isNull(standardSportMarketList.get(i).getPlaceNum()) ? 0 : standardSportMarketList.get(i).getPlaceNum());
                            ps.setString(28, Objects.isNull(standardSportMarketList.get(i).getRemark()) ? null : standardSportMarketList.get(i).getRemark());
                            ps.setLong(29, Objects.isNull(standardSportMarketList.get(i).getCreateTime()) ? 0 : standardSportMarketList.get(i).getCreateTime());
                            ps.setLong(30, Objects.isNull(standardSportMarketList.get(i).getModifyTime()) ? 0 : standardSportMarketList.get(i).getModifyTime());
                            ps.setInt(31, Objects.isNull(standardSportMarketList.get(i).getNumberOfWinners()) ? 0 : standardSportMarketList.get(i).getNumberOfWinners());
                        }
                        public int getBatchSize() {
                            return standardSportMarketList.size();
                        }
                    });
            log.info("::{}::batchStandardMarketInsert，标准盘口新增,批量接收数据:{},处理完成 ", linkId, standardSportMarketList.size());
        } catch (Exception e) {
            log.info("::{}::batchStandardOddsInsert，,标准盘口新增,批量接收数据处理出现异常:{}", linkId, e.getMessage());
            e.printStackTrace();
        }




      /*  try {
            standardSportMarketDao.insertList(standardSportMarketList);
        } catch (DuplicateKeyException e) {
            log.info("::{}::batchStandardMarketInsert，标准盘口批量新增：{}，出现主键冲突", linkId, JSONObject.toJSONString(standardSportMarketList), e);
            //出现主键冲突，重新设置主键后，改为单条新增 ,要重新设置下缓存数据
            for (StandardSportMarket standardSportMarket : standardSportMarketList) {
                standardSportMarket.setId(UUIdUtils.getId());
                standardSportMarketMapper.insert(standardSportMarket);
                String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarket:" + standardSportMarket.getStandardMatchInfoId() + "-" + standardSportMarket.getThirdMarketSourceId();
                redisService.set(key, standardSportMarket);
                log.info("::{}::batchStandardMarketInsert，标准盘口批量新增：{}，出现主键冲突后再次单条入库", linkId, JSONObject.toJSONString(standardSportMarket));
            }
        }*/
    }

    /**
     * 标准盘口 批量修改
     *
     * @param requests
     */
    @Async("standardMarketInsertAndUpdate")
    public void batchStandardMarketUpdate(List<Request<MarketDBMessage>> requests) {
        String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
        Long uuid = UUIdUtils.getId();
        log.info("::{}:: batchStandardMarketUpdate，标准盘口修改,批量接收数据 start UUID: {} 请求size: {}", linkIds, uuid, requests.size());
        List<StandardSportMarket> standardSportMarketList = requests.stream()
                .filter(r -> !CollectionUtils.isEmpty(r.getData().getStandardSportMarkets()))
                .map(r -> r.getData().getStandardSportMarkets())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(standardSportMarketList)){
            return;
        }
        try {
            standardSportMarketList.forEach(standardSportMarket -> {
                if(StringUtils.isEmpty(standardSportMarket.getSendData())){
                    standardSportMarket.setSendData("Y");
                }
            });
            oddsJdbcTemplate.batchUpdate(" update standard_sport_market set " +
                            "  `relation_market_id` = ?," +
                            "  `standard_tournament_id` = ?," +
                            "  `standard_match_info_id` = ?," +
                            "  `market_category_id` = ?," +
                            "  `market_type` = ?," +
                            "  `trade_type` = ?," +
                            "  `name_code` = ?," +
                            "  `odds_value` = ?," +
                            "  `odds_name` = ?," +
                            "  `order_type` = ?," +
                            "  `odds_metric` = ?," +
                            "  `addition1` = ?," +
                            "  `addition2` = ?," +
                            "  `addition3` = ?," +
                            "  `addition4` = ?," +
                            "  `addition5` = ?," +
                            "  `data_source_code` = ?," +
                            "  `status` = ?," +
                            "  `third_market_source_status` = ?," +
                            "  `scope_id` = ?," +
                            "  `third_odds_type` = ?," +
                            "  `third_market_source_id` = ?," +
                            "  `send_data` = ?," +
                            "  `link_id` = ?," +
                            "  `extra_info` = ?," +
                            "  `place_num` = ?," +
                            "  `remark` = ?," +
                            "  `create_time` = ?," +
                            "  `modify_time` = ?," +
                            "  `number_of_winners` = ? " +
                            "WHERE " +
                            "  `id` = ?",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, Objects.isNull(standardSportMarketList.get(i).getRelationMarketId()) ? 0 : standardSportMarketList.get(i).getRelationMarketId());
                            ps.setLong(2, Objects.isNull(standardSportMarketList.get(i).getStandardTournamentId()) ? 0 : standardSportMarketList.get(i).getStandardTournamentId());
                            ps.setLong(3, Objects.isNull(standardSportMarketList.get(i).getStandardMatchInfoId()) ? 0 : standardSportMarketList.get(i).getStandardMatchInfoId());
                            ps.setLong(4, Objects.isNull(standardSportMarketList.get(i).getMarketCategoryId()) ? 0 : standardSportMarketList.get(i).getMarketCategoryId());
                            ps.setInt(5, Objects.isNull(standardSportMarketList.get(i).getMarketType()) ? 0 : standardSportMarketList.get(i).getMarketType());
                            ps.setInt(6, Objects.isNull(standardSportMarketList.get(i).getTradeType()) ? 0 : standardSportMarketList.get(i).getTradeType());
                            ps.setLong(7, Objects.isNull(standardSportMarketList.get(i).getNameCode()) ? 0 : standardSportMarketList.get(i).getNameCode());
                            ps.setString(8, Objects.isNull(standardSportMarketList.get(i).getOddsValue()) ? null : standardSportMarketList.get(i).getOddsValue());
                            ps.setString(9, Objects.isNull(standardSportMarketList.get(i).getOddsName()) ? null : standardSportMarketList.get(i).getOddsName());
                            ps.setString(10, Objects.isNull(standardSportMarketList.get(i).getOrderType()) ? null : standardSportMarketList.get(i).getOrderType());
                            ps.setLong(11, Objects.isNull(standardSportMarketList.get(i).getOddsMetric()) ? 0 : standardSportMarketList.get(i).getOddsMetric());
                            ps.setString(12, Objects.isNull(standardSportMarketList.get(i).getAddition1()) ? null : standardSportMarketList.get(i).getAddition1());
                            ps.setString(13, Objects.isNull(standardSportMarketList.get(i).getAddition2()) ? null : standardSportMarketList.get(i).getAddition2());
                            ps.setString(14, Objects.isNull(standardSportMarketList.get(i).getAddition3()) ? null : standardSportMarketList.get(i).getAddition3());
                            ps.setString(15, Objects.isNull(standardSportMarketList.get(i).getAddition4()) ? null : standardSportMarketList.get(i).getAddition4());
                            ps.setString(16, Objects.isNull(standardSportMarketList.get(i).getAddition5()) ? null : standardSportMarketList.get(i).getAddition5());
                            ps.setString(17, Objects.isNull(standardSportMarketList.get(i).getDataSourceCode()) ? null : standardSportMarketList.get(i).getDataSourceCode());
                            ps.setInt(18, Objects.isNull(standardSportMarketList.get(i).getStatus()) ? 0 : standardSportMarketList.get(i).getStatus());
                            ps.setInt(19, Objects.isNull(standardSportMarketList.get(i).getThirdMarketSourceStatus()) ? 0 : standardSportMarketList.get(i).getThirdMarketSourceStatus());
                            ps.setString(20, Objects.isNull(standardSportMarketList.get(i).getScopeId()) ? null : standardSportMarketList.get(i).getScopeId());
                            ps.setString(21, Objects.isNull(standardSportMarketList.get(i).getThirdOddsType()) ? null : standardSportMarketList.get(i).getThirdOddsType());
                            ps.setString(22, Objects.isNull(standardSportMarketList.get(i).getThirdMarketSourceId()) ? null : standardSportMarketList.get(i).getThirdMarketSourceId());
                            ps.setString(23, Objects.isNull(standardSportMarketList.get(i).getSendData()) ? null : standardSportMarketList.get(i).getSendData());
                            ps.setString(24, Objects.isNull(standardSportMarketList.get(i).getLinkId()) ? null : standardSportMarketList.get(i).getLinkId());
                            ps.setString(25, Objects.isNull(standardSportMarketList.get(i).getExtraInfo()) ? null : standardSportMarketList.get(i).getExtraInfo());
                            ps.setInt(26, Objects.isNull(standardSportMarketList.get(i).getPlaceNum()) ? 0 : standardSportMarketList.get(i).getPlaceNum());
                            ps.setString(27, Objects.isNull(standardSportMarketList.get(i).getRemark()) ? null : standardSportMarketList.get(i).getRemark());
                            ps.setLong(28, Objects.isNull(standardSportMarketList.get(i).getCreateTime()) ? 0 : standardSportMarketList.get(i).getCreateTime());
                            ps.setLong(29, Objects.isNull(standardSportMarketList.get(i).getModifyTime()) ? 0 : standardSportMarketList.get(i).getModifyTime());
                            ps.setInt(30, Objects.isNull(standardSportMarketList.get(i).getNumberOfWinners()) ? 0 : standardSportMarketList.get(i).getNumberOfWinners());
                            ps.setLong(31, standardSportMarketList.get(i).getId());
                        }
                        public int getBatchSize() {
                            return standardSportMarketList.size();
                        }
                    });
            log.info("::{}::batchStandardMarketUpdate，标准盘口修改,批量接收数据:{},处理完成 ", uuid, standardSportMarketList.size());
        } catch (Exception e) {
            log.info("::{}::batchStandardMarketUpdate，标准盘口修改,批量修改处理盘口出现异常:{}", uuid, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 标准投注项 批量新增
     *
     * @param standardSportMarketOddsList
     */
    @Async("standardMarketOddsInsertAndUpdate")
    public void batchStandardOddsInsert(String linkId, List<StandardSportMarketOdds> standardSportMarketOddsList) {
        //投注项赔率分组
        Map<Long, List<StandardSportMarketOdds>> standardSportMarketOddsMap = standardSportMarketOddsList.stream().collect(Collectors.groupingBy(odds -> odds.getMarketId() % 10));
        for (Map.Entry<Long, List<StandardSportMarketOdds>> entry : standardSportMarketOddsMap.entrySet()) {
            Long tableShardingValue = entry.getKey();
            List<StandardSportMarketOdds> standardSportMarketOdds = entry.getValue();
            log.info("::{}::标准盘口赔率新增,批量接收数据: {}，开始处理tableShardingValue：{}", linkId, standardSportMarketOdds.size(), tableShardingValue);
            String sql = " insert into standard_sport_market_odds_"+tableShardingValue+
                    "(id, relation_market_odds_id, market_id,\n" +
                    "        relation_market_id, odds_fields_template_id, third_template_source_id,\n" +
                    "        active, settlement_result_text, settlement_result,\n" +
                    "        bet_settlement_certainty, odds_type, addition1,\n" +
                    "        addition2, addition3, addition4,\n" +
                    "        addition5, name_code, name,\n" +
                    "        name_expression_value, malay_odds_value, odds_value,\n" +
                    "        pa_odds_value, original_odds_value, target_side,\n" +
                    "        order_odds, data_source_code, third_odds_field_source_id,\n" +
                    "        extra_info, remark, create_time,\n" +
                    "        modify_time, standard_match_id)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                oddsJdbcTemplate.batchUpdate( sql,
                        new BatchPreparedStatementSetter() {
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, Objects.isNull(standardSportMarketOdds.get(i).getId()) ? 0 : standardSportMarketOdds.get(i).getId());
                                ps.setLong(2, Objects.isNull(standardSportMarketOdds.get(i).getRelationMarketOddsId()) ? 0 : standardSportMarketOdds.get(i).getRelationMarketOddsId());
                                ps.setLong(3, Objects.isNull(standardSportMarketOdds.get(i).getMarketId()) ? 0 : standardSportMarketOdds.get(i).getMarketId());
                                ps.setLong(4, Objects.isNull(standardSportMarketOdds.get(i).getRelationMarketId()) ? 0 : standardSportMarketOdds.get(i).getRelationMarketId());
                                ps.setLong(5, Objects.isNull(standardSportMarketOdds.get(i).getOddsFieldsTemplateId()) ? 0 : standardSportMarketOdds.get(i).getOddsFieldsTemplateId());
                                ps.setString(6, Objects.isNull(standardSportMarketOdds.get(i).getThirdTemplateSourceId()) ? null : standardSportMarketOdds.get(i).getThirdTemplateSourceId());
                                ps.setInt(7, Objects.isNull(standardSportMarketOdds.get(i).getActive()) ? 0 : standardSportMarketOdds.get(i).getActive());
                                ps.setString(8, Objects.isNull(standardSportMarketOdds.get(i).getSettlementResultText()) ? null : standardSportMarketOdds.get(i).getSettlementResultText());
                                ps.setString(9, Objects.isNull(standardSportMarketOdds.get(i).getSettlementResult()) ? null : standardSportMarketOdds.get(i).getSettlementResult());
                                ps.setString(10, Objects.isNull(standardSportMarketOdds.get(i).getBetSettlementCertainty()) ? null : standardSportMarketOdds.get(i).getBetSettlementCertainty());
                                ps.setString(11, Objects.isNull(standardSportMarketOdds.get(i).getOddsType()) ? null : standardSportMarketOdds.get(i).getOddsType());
                                ps.setString(12, Objects.isNull(standardSportMarketOdds.get(i).getAddition1()) ? null : standardSportMarketOdds.get(i).getAddition1());
                                ps.setString(13, Objects.isNull(standardSportMarketOdds.get(i).getAddition2()) ? null : standardSportMarketOdds.get(i).getAddition2());
                                ps.setString(14, Objects.isNull(standardSportMarketOdds.get(i).getAddition3()) ? null : standardSportMarketOdds.get(i).getAddition3());
                                ps.setString(15, Objects.isNull(standardSportMarketOdds.get(i).getAddition4()) ? null : standardSportMarketOdds.get(i).getAddition4());
                                ps.setString(16, Objects.isNull(standardSportMarketOdds.get(i).getAddition5()) ? null : standardSportMarketOdds.get(i).getAddition5());
                                ps.setLong(17, Objects.isNull(standardSportMarketOdds.get(i).getNameCode()) ? 0 : standardSportMarketOdds.get(i).getNameCode());
                                ps.setString(18, Objects.isNull(standardSportMarketOdds.get(i).getName()) ? null : standardSportMarketOdds.get(i).getName());
                                ps.setString(19, Objects.isNull(standardSportMarketOdds.get(i).getNameExpressionValue()) ? null : standardSportMarketOdds.get(i).getNameExpressionValue());
                                ps.setDouble(20, Objects.isNull(standardSportMarketOdds.get(i).getMalayOddsValue()) ? 0 : standardSportMarketOdds.get(i).getMalayOddsValue());
                                ps.setInt(21, Objects.isNull(standardSportMarketOdds.get(i).getOddsValue()) ? 0 : standardSportMarketOdds.get(i).getOddsValue());
                                ps.setInt(22, Objects.isNull(standardSportMarketOdds.get(i).getPaOddsValue()) ? 0 : standardSportMarketOdds.get(i).getPaOddsValue());
                                ps.setInt(23, Objects.isNull(standardSportMarketOdds.get(i).getOriginalOddsValue()) ? 0 : standardSportMarketOdds.get(i).getOriginalOddsValue());
                                ps.setString(24, Objects.isNull(standardSportMarketOdds.get(i).getTargetSide()) ? null : standardSportMarketOdds.get(i).getTargetSide());
                                ps.setInt(25, Objects.isNull(standardSportMarketOdds.get(i).getOrderOdds()) ? 0 : standardSportMarketOdds.get(i).getOrderOdds());
                                ps.setString(26, Objects.isNull(standardSportMarketOdds.get(i).getDataSourceCode()) ? null : standardSportMarketOdds.get(i).getDataSourceCode());
                                ps.setString(27, Objects.isNull(standardSportMarketOdds.get(i).getThirdOddsFieldSourceId()) ? null : standardSportMarketOdds.get(i).getThirdOddsFieldSourceId());
                                ps.setString(28, Objects.isNull(standardSportMarketOdds.get(i).getExtraInfo()) ? null : standardSportMarketOdds.get(i).getExtraInfo());
                                ps.setString(29, Objects.isNull(standardSportMarketOdds.get(i).getRemark()) ? null : standardSportMarketOdds.get(i).getRemark());
                                ps.setLong(30, Objects.isNull(standardSportMarketOdds.get(i).getCreateTime()) ? 0 : standardSportMarketOdds.get(i).getCreateTime());
                                ps.setLong(31, Objects.isNull(standardSportMarketOdds.get(i).getModifyTime()) ? 0 : standardSportMarketOdds.get(i).getModifyTime());
                                ps.setLong(32, Objects.isNull(standardSportMarketOdds.get(i).getStandardMatchId()) ? 0 : standardSportMarketOdds.get(i).getStandardMatchId());
                            }
                            public int getBatchSize() {
                                return standardSportMarketOdds.size();
                            }
                        });
                log.info("::{}::batchStandardOddsInsert,标准盘口赔率新增,批量接收数据: {}，开始处理tableShardingValue完成：{}", linkId, standardSportMarketOdds.size(), tableShardingValue);
            } catch (Exception e) {
                log.info("::{}::batchStandardOddsInsert，标准投注项tableShardingValue:{},批量新增出现异常:{}", linkId, tableShardingValue, e.getMessage());
                e.printStackTrace();
            }



           /* HintManager instance = HintManager.getInstance();
            try {
                instance.addTableShardingValue("standard_sport_market_odds", tableShardingValue);
                instance.addDatabaseShardingValue("standard_sport_market_odds", "ds1");
                log.info("::{}::batchStandardOddsInsert，标准投注项tableShardingValue:{},批量新增处理盘口:{}", linkId, tableShardingValue, standardSportMarketOdds.size());
                standardSportMarketOddsDao.insertList(standardSportMarketOdds, tableShardingValue);
            } catch (DuplicateKeyException e) {
                instance.close();
                log.info("::{}::batchStandardOddsInsert，标准投注项tableShardingValue:{},批量新增：{}，出现主键冲突", linkId, tableShardingValue, JSONObject.toJSONString(standardSportMarketOddsList), e);
                //出现主键冲突，重新设置主键后，改为单条新增 ,要重新设置下缓存数据
                for (StandardSportMarketOdds sportMarketOdds : standardSportMarketOdds) {
                    sportMarketOdds.setId(UUIdUtils.getId());
                    standardSportMarketOddsMapper.insert(sportMarketOdds);
                    String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketOdds:" + sportMarketOdds.getId() + '-' + sportMarketOdds.getThirdOddsFieldSourceId();
                    redisService.set(key, sportMarketOdds);
                    log.info("::{}::batchStandardOddsInsert，标准投注项tableShardingValue:{},批量新增：{}，出现主键冲突后再次单条入库", linkId, tableShardingValue, JSONObject.toJSONString(sportMarketOdds));
                }
            } finally {
                instance.close();
                log.info("::{}::batchStandardOddsInsert，标准投注项tableShardingValue批量新增处理完成", linkId);
            }*/
        }
    }

    /**
     * 标准投注项 批量修改
     *
     * @param requests
     */
    @Async("standardMarketOddsInsertAndUpdate")
    public void batchStandardOddsUpdate(List<Request<MarketDBMessage>> requests) {
        String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
        Long uuid = UUIdUtils.getId();
        log.info("::{}:: 标准盘口赔率修改,批量接收数据 start UUID: {} 请求size: {}", linkIds, uuid, requests.size());
        List<StandardSportMarketOdds> standardSportMarketOddsList = requests.stream()
                .filter(r -> !CollectionUtils.isEmpty(r.getData().getStandardSportMarketOdds()))
                .map(r -> r.getData().getStandardSportMarketOdds())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(standardSportMarketOddsList)){
            return;
        }
        //投注项赔率分组
        Map<Long, List<StandardSportMarketOdds>> standardSportMarketOddsMap = standardSportMarketOddsList.stream().collect(Collectors.groupingBy(odds -> odds.getMarketId() % 10));
        for (Map.Entry<Long, List<StandardSportMarketOdds>> entry : standardSportMarketOddsMap.entrySet()) {
            Long tableShardingValue = entry.getKey();
            List<StandardSportMarketOdds> standardSportMarketOdds = entry.getValue();
            log.info("::{}::标准盘口赔率修改,批量接收数据: {},tableShardingValue开始处理：{}", uuid, standardSportMarketOdds.size(),tableShardingValue);
            try {
                oddsJdbcTemplate.batchUpdate("update standard_sport_market_odds_"+tableShardingValue + " set " +
                                "  `relation_market_odds_id` = ?," +
                                "  `market_id` = ?," +
                                "  `relation_market_id` = ?," +
                                "  `odds_fields_template_id` = ?," +
                                "  `third_template_source_id` = ?," +
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
                                "  `name_code` = ?," +
                                "  `name` = ?," +
                                "  `name_expression_value` = ?," +
                                "  `malay_odds_value` = ?," +
                                "  `odds_value` = ?," +
                                "  `pa_odds_value` = ?," +
                                "  `original_odds_value` = ?," +
                                "  `target_side` = ?," +
                                "  `order_odds` = ?," +
                                "  `data_source_code` = ?," +
                                "  `third_odds_field_source_id` = ?," +
                                "  `extra_info` = ?," +
                                "  `remark` = ?," +
                                "  `create_time` = ?," +
                                "  `modify_time` = ?," +
                                "  `standard_match_id` = ? " +
                                "WHERE " +
                                "  `id` = ?",
                        new BatchPreparedStatementSetter() {
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, Objects.isNull(standardSportMarketOdds.get(i).getRelationMarketOddsId()) ? 0 : standardSportMarketOdds.get(i).getRelationMarketOddsId());
                                ps.setLong(2, Objects.isNull(standardSportMarketOdds.get(i).getMarketId()) ? 0 : standardSportMarketOdds.get(i).getMarketId());
                                ps.setLong(3, Objects.isNull(standardSportMarketOdds.get(i).getRelationMarketId()) ? 0 : standardSportMarketOdds.get(i).getRelationMarketId());
                                ps.setLong(4, Objects.isNull(standardSportMarketOdds.get(i).getOddsFieldsTemplateId()) ? 0 : standardSportMarketOdds.get(i).getOddsFieldsTemplateId());
                                ps.setString(5, Objects.isNull(standardSportMarketOdds.get(i).getThirdTemplateSourceId()) ? null : standardSportMarketOdds.get(i).getThirdTemplateSourceId());
                                ps.setInt(6, Objects.isNull(standardSportMarketOdds.get(i).getActive()) ? 0 : standardSportMarketOdds.get(i).getActive());
                                ps.setString(7, Objects.isNull(standardSportMarketOdds.get(i).getSettlementResultText()) ? null : standardSportMarketOdds.get(i).getSettlementResultText());
                                ps.setString(8, Objects.isNull(standardSportMarketOdds.get(i).getSettlementResult()) ? null : standardSportMarketOdds.get(i).getSettlementResult());
                                ps.setString(9, Objects.isNull(standardSportMarketOdds.get(i).getBetSettlementCertainty()) ? null : standardSportMarketOdds.get(i).getBetSettlementCertainty());
                                ps.setString(10, Objects.isNull(standardSportMarketOdds.get(i).getOddsType()) ? null : standardSportMarketOdds.get(i).getOddsType());
                                ps.setString(11, Objects.isNull(standardSportMarketOdds.get(i).getAddition1()) ? null : standardSportMarketOdds.get(i).getAddition1());
                                ps.setString(12, Objects.isNull(standardSportMarketOdds.get(i).getAddition2()) ? null : standardSportMarketOdds.get(i).getAddition2());
                                ps.setString(13, Objects.isNull(standardSportMarketOdds.get(i).getAddition3()) ? null : standardSportMarketOdds.get(i).getAddition3());
                                ps.setString(14, Objects.isNull(standardSportMarketOdds.get(i).getAddition4()) ? null : standardSportMarketOdds.get(i).getAddition4());
                                ps.setString(15, Objects.isNull(standardSportMarketOdds.get(i).getAddition5()) ? null : standardSportMarketOdds.get(i).getAddition5());
                                ps.setLong(16, Objects.isNull(standardSportMarketOdds.get(i).getNameCode()) ? 0 : standardSportMarketOdds.get(i).getNameCode());
                                ps.setString(17, Objects.isNull(standardSportMarketOdds.get(i).getName()) ? null : standardSportMarketOdds.get(i).getName());
                                ps.setString(18, Objects.isNull(standardSportMarketOdds.get(i).getNameExpressionValue()) ? null : standardSportMarketOdds.get(i).getNameExpressionValue());
                                ps.setDouble(19, Objects.isNull(standardSportMarketOdds.get(i).getMalayOddsValue()) ? 0 : standardSportMarketOdds.get(i).getMalayOddsValue());
                                ps.setInt(20, Objects.isNull(standardSportMarketOdds.get(i).getOddsValue()) ? 0 : standardSportMarketOdds.get(i).getOddsValue());
                                ps.setInt(21, Objects.isNull(standardSportMarketOdds.get(i).getPaOddsValue()) ? 0 : standardSportMarketOdds.get(i).getPaOddsValue());
                                ps.setInt(22, Objects.isNull(standardSportMarketOdds.get(i).getOriginalOddsValue()) ? 0 : standardSportMarketOdds.get(i).getOriginalOddsValue());
                                ps.setString(23, Objects.isNull(standardSportMarketOdds.get(i).getTargetSide()) ? null : standardSportMarketOdds.get(i).getTargetSide());
                                ps.setInt(24, Objects.isNull(standardSportMarketOdds.get(i).getOrderOdds()) ? 0 : standardSportMarketOdds.get(i).getOrderOdds());
                                ps.setString(25, Objects.isNull(standardSportMarketOdds.get(i).getDataSourceCode()) ? null : standardSportMarketOdds.get(i).getDataSourceCode());
                                ps.setString(26, Objects.isNull(standardSportMarketOdds.get(i).getThirdOddsFieldSourceId()) ? null : standardSportMarketOdds.get(i).getThirdOddsFieldSourceId());
                                ps.setString(27, Objects.isNull(standardSportMarketOdds.get(i).getExtraInfo()) ? null : standardSportMarketOdds.get(i).getExtraInfo());
                                ps.setString(28, Objects.isNull(standardSportMarketOdds.get(i).getRemark()) ? null : standardSportMarketOdds.get(i).getRemark());
                                ps.setLong(29, Objects.isNull(standardSportMarketOdds.get(i).getCreateTime()) ? 0 : standardSportMarketOdds.get(i).getCreateTime());
                                ps.setLong(30, Objects.isNull(standardSportMarketOdds.get(i).getModifyTime()) ? 0 : standardSportMarketOdds.get(i).getModifyTime());
                                ps.setLong(31, Objects.isNull(standardSportMarketOdds.get(i).getStandardMatchId()) ? 0 : standardSportMarketOdds.get(i).getStandardMatchId());
                                ps.setLong(32, standardSportMarketOdds.get(i).getId());
                            }
                            public int getBatchSize() {
                                return standardSportMarketOdds.size();
                            }
                        });
                log.info("::{}::标准盘口赔率修改,批量接收数据: {},tableShardingValue处理完成：{}", uuid, standardSportMarketOdds.size(),tableShardingValue);
            } catch (Exception e) {
                log.info("::{}::batchStandardOddsUpdate，标准投注项tableShardingValue:{},批量修改处理盘口出现异常:{}", uuid, tableShardingValue, e.getMessage());
                e.printStackTrace();
            }
          /*  HintManager instance = HintManager.getInstance();
            try {
                instance.addTableShardingValue("standard_sport_market_odds", tableShardingValue);
                instance.addDatabaseShardingValue("standard_sport_market_odds", "ds1");
                log.info("::{}::batchStandardOddsUpdate，标准投注项tableShardingValue:{},批量修改处理盘口:{}", linkId, tableShardingValue, standardSportMarketOdds.size());
                standardSportMarketOddsDao.upDataList(standardSportMarketOdds, tableShardingValue);
            } catch (Exception e) {
                log.info("::{}::batchStandardOddsUpdate，标准投注项tableShardingValue:{},批量修改出现异常", linkId, tableShardingValue, e);
            } finally {
                instance.close();
                log.info("::{}::batchStandardOddsUpdate，标准投注项tableShardingValue批量修改处理完成", linkId);
            }*/
        }
    }
}
