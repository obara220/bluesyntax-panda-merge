package com.panda.merge.service.impl;

import cn.hutool.core.date.DateUtil;
import com.panda.merge.common.enums.PreSaleTraderLogConfigMessageEnum;
import com.panda.merge.common.enums.SaleOperateTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.UpdateMarketCategoryDataSourceCodeDTO;
import com.panda.merge.dto.odds.CategoryDataSource;
import com.panda.merge.mapper.StandardSportMarketSellLogMapper;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellLog;
import com.panda.merge.service.StandardSportMarketSellLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportMarketSellLogServiceImpl implements StandardSportMarketSellLogService {

    @Autowired
    private StandardSportMarketSellLogMapper standardSportMarketSellLogMapper;

    /**
     * 组装并新增日志记录
     *
     * @param matchId                   标准赛事id
     * @param standardSportMarketSellId 赛事开售表主键id
     * @param logInfo                   日志信息
     * @param operateType               操作类型
     * @param userId                    用户id
     * @param userName                  用户名称
     * @return num
     * @Author: Top
     * @Date: 2021/2/6 10:37
     */
    @Override
    public Integer AssemblyAndInsertStandardSportMarketSellLog(Long matchId, Long standardSportMarketSellId,
                                                               String logInfo,String logInfoEn, String operateType, String userId,
                                                               String userName) {
        StandardSportMarketSellLog standardSportMarketSellLog = new StandardSportMarketSellLog();
        standardSportMarketSellLog.setOperateId(userId);
        standardSportMarketSellLog.setOperateName(userName);
        standardSportMarketSellLog.setOperateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardSportMarketSellLog.setStandardMatchId(matchId);
        standardSportMarketSellLog.setStandardSportMarketSellId(standardSportMarketSellId);
        standardSportMarketSellLog.setLog(logInfo);
        standardSportMarketSellLog.setLogEn(logInfoEn);
        standardSportMarketSellLog.setOperateType(operateType);
        return standardSportMarketSellLogMapper.insert(standardSportMarketSellLog);
    }

    @Override
    public void log(List<UpdateMarketCategoryDataSourceCodeDTO> marketCategoryDataSourceCodeList,
                    StandardSportMarketSell standardSportMarketSell,
                    String linkId,
                    Integer marketType,
                    Long operaterId,
                    String operaterName) {

        String logInfoZh = "";
        String logInfoEn = "";
        if (marketType == 1) {
            if (marketCategoryDataSourceCodeList.size() == 1) {
                logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_PRE_SINGLE
                        .getMessageZh()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("MarketCategoryId",
                                 String.valueOf(marketCategoryDataSourceCodeList.get(0).getMarketCategoryId()))
                        .replace("newDataSourceCode", marketCategoryDataSourceCodeList.get(0).getDataSourceCode())
                        .replace("linkId", linkId);
                logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_PRE_SINGLE
                        .getMessageEn()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("MarketCategoryId",
                                 String.valueOf(marketCategoryDataSourceCodeList.get(0).getMarketCategoryId()))
                        .replace("newDataSourceCode", marketCategoryDataSourceCodeList.get(0).getDataSourceCode())
                        .replace("linkId", linkId);
            } else {
                logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_PRE
                        .getMessageZh()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("linkId", linkId);
                logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_PRE
                        .getMessageEn()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("linkId", linkId);
            }
        } else {
            //玩法数据源权重切换 玩法赔率告警解除
            //thirdMatchMarketProcessor.matchOddsWarning(linkId, marketType, standardMatchInfo, new HashSet<>(categoryList));
            if (marketCategoryDataSourceCodeList.size() == 1) {
                logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_LIVE_SINGLE
                        .getMessageZh()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("MarketCategoryId",
                                 String.valueOf(marketCategoryDataSourceCodeList.get(0).getMarketCategoryId()))
                        .replace("newDataSourceCode", marketCategoryDataSourceCodeList.get(0).getDataSourceCode())
                        .replace("linkId", linkId);
                logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_LIVE_SINGLE
                        .getMessageEn()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("MarketCategoryId",
                                 String.valueOf(marketCategoryDataSourceCodeList.get(0).getMarketCategoryId()))
                        .replace("newDataSourceCode", marketCategoryDataSourceCodeList.get(0).getDataSourceCode())
                        .replace("linkId", linkId);
            } else {
                logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_LIVE
                        .getMessageZh()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("linkId", linkId);
                logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_LIVE
                        .getMessageEn()
                        .replace("userId", String.valueOf(operaterId))
                        .replace("userName", operaterName)
                        .replace("nowTime", DateUtil.now())
                        .replace("linkId", linkId);
            }
        }
        //通过用户id查询用户信息
        // 记录操作日志
        AssemblyAndInsertStandardSportMarketSellLog(standardSportMarketSell.getMatchInfoId(),
                                                    standardSportMarketSell.getId(),
                                                    logInfoZh,
                                                    logInfoEn,
                                                    marketType == 1 ? SaleOperateTypeEnum.pre_match.name() :
                                                            SaleOperateTypeEnum.live_odd.name(),
                                                    String.valueOf(operaterId),
                                                    String.valueOf(operaterId));
    }

    @Override
    public void log(CategoryDataSource cds) {
        String logInfoZh = "";
        String logInfoEn = "";
        int marketType = cds.marketType;
        if (marketType == 1) {
            logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_PRE_SINGLE
                    .getMessageZh()
                    .replace("userId","0")
                    .replace("userName", "0")
                    .replace("nowTime", DateUtil.now())
                    .replace("MarketCategoryId",
                             String.valueOf(cds.categoryId))
                    .replace("newDataSourceCode", cds.internalTds)
                    .replace("linkId", cds.linkId);
            logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_PRE_SINGLE
                    .getMessageEn()
                    .replace("userId", "0")
                    .replace("userName", "0")
                    .replace("nowTime", DateUtil.now())
                    .replace("MarketCategoryId",
                             String.valueOf(cds.categoryId))
                    .replace("newDataSourceCode", cds.internalTds)
                    .replace("linkId", cds.linkId);
        } else {
            logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_LIVE_SINGLE
                    .getMessageZh()
                    .replace("userId", "0")
                    .replace("userName", "0")
                    .replace("nowTime", DateUtil.now())
                    .replace("MarketCategoryId",
                             String.valueOf(cds.categoryId))
                    .replace("newDataSourceCode", cds.internalTds)
                    .replace("linkId", cds.linkId);
            logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_PLAY_DATA_SOURCE_CODE_LIVE_SINGLE
                    .getMessageEn()
                    .replace("userId", "0")
                    .replace("userName", "0")
                    .replace("nowTime", DateUtil.now())
                    .replace("MarketCategoryId",
                             String.valueOf(cds.categoryId))
                    .replace("newDataSourceCode", cds.internalTds)
                    .replace("linkId", cds.linkId);
        }

        AssemblyAndInsertStandardSportMarketSellLog(cds.standardMatchInfo.getId(),
                                                    cds.standardSportMarketSell.getId(),
                                                    logInfoZh,
                                                    logInfoEn,
                                                    marketType == 1 ? SaleOperateTypeEnum.pre_match.name() :
                                                            SaleOperateTypeEnum.live_odd.name(),
                                                    String.valueOf(0),
                                                    String.valueOf(0));




    }

}
