package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.api.SystemMarketPreApi;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardMatchInfoDao;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.StandardMatchPreResultProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@DubboService
@Async
public class SystemMarketPreApiImpl implements SystemMarketPreApi {
    @Autowired
    private StandardMatchPreResultProducer standardMatchPreResultProducer;
    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchInfoDao standardMatchInfoDao;
    /**
     * 提前结算开关，false关，true开
     */
    @NacosValue(value = "${market.pre.switch}", autoRefreshed = true)
    private boolean marketPreSwitch;
    @Override
    public void saveSystemPreResultAndPush(String params) {
        if (!marketPreSwitch) {
            log.info("提前结算NACOS关,系统级别开关切换不处理");
            return;
        }
        /***
         * 流程:(1)判断params开关是否与缓存开关相同
         *     (2)不同,查询数据源赛事信息,查处redis赛事对应提前结算信息
         *     (3)如果是关闭操作,cashout=-1 对其进行下发到业务系统
         *     (4)如果是打开操作,不进行下发,通过赛事级开关打开
         */
        //将前端系统级开关,保存在缓存,并返回需要下发的标准赛事
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map<String,Integer> mapRdis = redisService.hGetAll(SystemThirdMarketPreParams);
        log.info("收到(提前结算)系统级开关【SystemMarketPreApiImpl:saveSystemPreResultAndPush】:获取原redis参数:", mapRdis);
        int isEquals = 0;
        //获取AO,SR 系统层级开关指示参数并保存在redis
        Map<String, Integer> maps = (Map) JSON.parse(params);
        Iterator<String> iter1 = maps.keySet().iterator();
        while (iter1.hasNext()) {
            String m1Key = (String) iter1.next();
            //若两个map中相同key对应的value不相等,isEquals+1
            if (maps.get(m1Key)!=mapRdis.get(m1Key)) {
                isEquals = isEquals+1;
            }
        }
        if (isEquals>0) {
            List<StandardMatchInfo> standardMatchInfoList = new ArrayList<StandardMatchInfo>();
            int AoOnOff = maps.get("AO");
            int SrOnOff = maps.get("SR");

            //比赛是否结束:matchOver
            //查询出所有提前结算的赛事matchOver=0(赛事未结束)
            if (AoOnOff == 0&&AoOnOff!=mapRdis.get("AO")) {
                List<StandardMatchInfo> standardMatchInfoAOList = standardMatchInfoDao.selectByConfigCashOutItem("AO");
                log.info("查询需要关闭的AO赛事列表:"+standardMatchInfoAOList);
                standardMatchInfoList.addAll(standardMatchInfoAOList);
            }
            if (SrOnOff == 0&&SrOnOff!=mapRdis.get("SR")) {
                List<StandardMatchInfo> standardMatchInfoSRList = standardMatchInfoDao.selectByConfigCashOutItem("SR");
                log.info("查询需要关闭的SR赛事列表:"+standardMatchInfoSRList);
                standardMatchInfoList.addAll(standardMatchInfoSRList);
            }
            //刷新缓存
            redisService.hSetAll(SystemThirdMarketPreParams, maps);
            log.info("系统级提前结算开关切换,重新下发条数:"+standardMatchInfoList.size());
            //循环并设置 提前结算标准盘口
            for (StandardMatchInfo standardMatchInfo : standardMatchInfoList) {
                List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = new ArrayList<>();
                String linkId = IdWorker.getId() + "PRERESULT_SWIFT";
                Long standardMatchId = standardMatchInfo.getId();
                //提前结算标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
                String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + standardMatchId;
                Map<String, StandardMatchMarketPreResultMessage>
                        standardMatchMarketPreResultMessageMap = redisService.hGetAll(standardPreMarketKey);
                Set<String> standardMatchMarketPreResultMessage = standardMatchMarketPreResultMessageMap.keySet();
                //循环并更改 盘口提前结算
                for (String key : standardMatchMarketPreResultMessage) {
                    StandardMatchMarketPreResultMessage marketMessage = standardMatchMarketPreResultMessageMap.get(key);
                    //更改cashOut为-2,并下发
                    marketMessage.setCashOutStatus(-2);
                    marketPreResultMessageList.add(marketMessage);
                    //刷新缓存[缓存时间:（比赛时间 - 系统时间） + 一周时间]
                    log.info("刷新提前结算缓存:"+marketMessage);
                    redisService.hSet(standardPreMarketKey, key, marketMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                }
                //
                if(marketPreResultMessageList.size()==0){
                    log.info("【SystemMarketPreApiImpl:saveSystemPreResultAndPush】:赛事对应提前结算盘口为空,不下发！");
                    continue;
                }
                //下发盘口数据
                log.info("关闭提前结算,cashout=-2,下发提前结算数据到业务系统:"+marketPreResultMessageList);
                standardMatchPreResultProducer.sendStandardMatchPreResult(linkId, standardMatchInfo, 1L, marketPreResultMessageList, marketPreResultMessageList.get(0).getMatchPreStatus(), System.currentTimeMillis());
            }
        }
    }

    @Override
    public Map<String,Object> searchSystemPreResultAndPush(String params) {
        //将前端系统级开关,保存在缓存,并返回需要下发的标准赛事
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map  maps = redisService.hGetAll(SystemThirdMarketPreParams);
        return maps;
    }

    /**
     * 盘口缓存时间
     * （比赛时间 - 系统时间） + 一周时间
     *
     * @param beginTime 比赛时间
     */
    public Long marketCacheTime(Long beginTime) {
        if (beginTime == null || beginTime == 0) {
            return RedisConfig.REDIS_WEEK_TIME.longValue();
        }
        Long cacheTime = (beginTime - Calendar.getInstance().getTimeInMillis());
        if (cacheTime <= 0) {
            return RedisConfig.REDIS_WEEK_TIME.longValue();
        }
        return (cacheTime / 1000) + RedisConfig.REDIS_WEEK_TIME;
    }
}
