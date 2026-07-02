package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.ConfigCashOutTradeItem;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ConfigCashOutTradeItemService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.SwiftSystemPreResultService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SwiftSystemPreResultServiceImpl implements SwiftSystemPreResultService {
    @Autowired
    public RedisService redisService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ConfigCashOutTradeItemService configCashOutTradeItemService;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Override
    public List<StandardMatchInfo> saveSystemPreResult(String params) {
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map mapCheck = redisService.hGetAll(SystemThirdMarketPreParams);
        log.info("收到(提前结算)系统级开关【SwiftSystemPreResultService:saveSystemPreResult】:获取原redis参数:",mapCheck);
        //获取AO,SR 系统层级开关指示参数并保存在redis
        Map<String, String> maps = (Map) JSON.parse(params);
        redisService.hSetAll(SystemThirdMarketPreParams,maps);
        List<StandardMatchInfo> standardMatchInfoList=new ArrayList<StandardMatchInfo>();

        int AoOnOff = Integer.valueOf(maps.get("AO"));
        int SrOnOff = Integer.valueOf(maps.get("SR"));

        //比赛是否结束:matchOver
        //查询出所有提前结算的赛事matchOver=1
        if (AoOnOff == 1) {
            StandardMatchInfoExample example = new StandardMatchInfoExample();
            example.createCriteria().andSportIdEqualTo(1L).andMatchOverEqualTo(1).andDataSourceCodeEqualTo("AO");
            standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
                return  standardMatchInfoList;
            }
        if (SrOnOff == 1) {
            StandardMatchInfoExample example = new StandardMatchInfoExample();
            example.createCriteria().andSportIdEqualTo(1L).andMatchOverEqualTo(1).andDataSourceCodeEqualTo("SR");
            standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
                return  standardMatchInfoList;
        }
        return standardMatchInfoList;
    }



    /**
     * 获取操盘配置
     *
     * @param standardMatchInfo
     * @param resultStatus       最终赛事状态 （提前结算开关 - 赛事操盘状态） 业务用
     * @param matchPreStatusRisk 提前结算开关 风控用
     * @return
     */
    private Map<Long, ConfigCashOutTradeItem> getCashOutTradeItemConfig( StandardMatchInfo standardMatchInfo, AtomicInteger resultStatus, AtomicInteger matchPreStatusRisk) {
        Integer operateMatchStatus = standardMatchInfo.getOperateMatchStatus() == -1 ? 0 : standardMatchInfo.getOperateMatchStatus();
        //转换状态为 0:关  1:开
        operateMatchStatus = operateMatchStatus == 0 ? 1 : 0;
        //玩法级别配置
        Map<Long, ConfigCashOutTradeItem> cashOutTradeItemCategoryMap = new HashMap<>();
        int marketType = isOddsLive(standardMatchInfo.getId());
        List<ConfigCashOutTradeItem> configCashOutTradeItemList = configCashOutTradeItemService.getItemList(standardMatchInfo.getId(), marketType);
        if (!CollectionUtils.isEmpty(configCashOutTradeItemList)) {
            //赛事级别配置
            List<ConfigCashOutTradeItem> cashOutTradeItemCategoryMatch = configCashOutTradeItemList.stream().filter(e -> e.getLeve() == 1).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(cashOutTradeItemCategoryMatch)) {
                //提前结算配置表状态
                Integer matchPreStatus = cashOutTradeItemCategoryMatch.get(0).getMatchPreStatus();
                matchPreStatusRisk.set(matchPreStatus);
                resultStatus.set(Math.min(matchPreStatus, operateMatchStatus));
                log.info("提前赛事配置,赛事ID:{},赛事操盘原始状态:{},赛事操盘转换后状态:{},赛事提前结算状态:{},最终下发状态:{}",
                         standardMatchInfo.getId(), standardMatchInfo.getOperateMatchStatus(), operateMatchStatus, matchPreStatus, resultStatus);
            }
            cashOutTradeItemCategoryMap = configCashOutTradeItemList.stream().filter(e -> e.getLeve() == 2).collect(Collectors.toMap(e -> e.getMarketCategoryId(), e -> e, (oldValue, newValue) -> newValue));
            log.info("提前结算配置,赛事ID:{},赛事信息:{},玩法信息:{}",
                     standardMatchInfo.getId(), JSONObject.toJSONString(cashOutTradeItemCategoryMatch), JSONObject.toJSONString(cashOutTradeItemCategoryMap));
        }
        return cashOutTradeItemCategoryMap;
    }

    /**
     * 查询缓存是否进入滚球
     *
     * @return
     */
    public int isOddsLive(Long standardMatchInfoId) {
        Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfoId);
        return Objects.isNull(marketTypeObj) ? 1 : 0;
    }
}
