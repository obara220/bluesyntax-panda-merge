package com.panda.merge.job;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.mapper.ThirdSportMarketMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.constant.ConstantSystem.HOUR_1;
import static com.panda.merge.constant.ConstantSystem.XIN;

/**
 * 根据传入key值清除redis缓存或刷新本地缓存
 * @author : tell
 * @since    2020年10月26日13:18:03
 */
@Slf4j
@Component
@JobHandler(value = "CleanRedisCachJob")
public class CleanRedisCachJob extends IJobHandler {

    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;

    @Autowired
    private LanguageTypeService languageTypeService;
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private ThirdSportTypeService thirdSportTypeService;

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private ThirdSportMarketCategoryService thirdSportMarketCategoryService;
    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;
    @Autowired
    private StandardMarketCategoryService standardMarketCategoryService;
    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;
    @Autowired
    private StandardMarketCategoryFieldService standardMarketCategoryFieldService;

    /**
     * 需要全量清理缓存的表
     * third_market_category 三方玩法表
     * third_sport_market_category 三方球种玩法表
     * third_market_category_field 三方投注项表
     * standard_market_category 标准玩法表
     * standard_sport_market_category 标准球种玩法表
     * standard_market_category_field 标准投注项表
     */
    private static List<String> tabs = Lists.newArrayList("third_market_category", "third_sport_market_category",
            "third_market_category_field", "standard_market_category", "standard_sport_market_category", "standard_market_category_field");
    @Override
    public ReturnT<String> execute(String parKey){
        //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 处理开始,入参: {}",parKey);
        XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 处理开始,入参: {}",parKey);
        try {
            if(StringUtils.isNotBlank(parKey)){
                if(parKey.contains(XIN)){
                    //清除redis缓存,根据*清理缓存已经废弃，运维那边禁止了的，只能根据明确的key清理
                    Set<String> keys = redisService.keys(parKey);
                    //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 需要清除的缓存条数：{}",keys.size());
                    XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 需要清除的缓存条数：{}",keys.size());

                    if(!CollectionUtils.isEmpty(keys)){
                        Long num = redisService.delete(keys);
                        //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 成功清除的缓存条数：{}",num);
                        XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 成功清除的缓存条数：{}",num);
                    }
                }else if (tabs.contains(parKey)) {
                    int num = 0;
                    switch (parKey) {
                        case "third_market_category":
                            num = thirdMarketCategoryService.delRedisByAll();
                            break;
                        case "third_sport_market_category":
                            num = thirdSportMarketCategoryService.delRedisByAll();
                            break;
                        case "third_market_category_field":
                            num = thirdMarketCategoryFieldService.delRedisByAll();
                            break;
                        case "standard_market_category":
                            num = standardMarketCategoryService.delRedisByAll();
                            break;
                        case "standard_sport_market_category":
                            num = standardSportMarketCategoryService.delRedisByAll();
                            break;
                        case "standard_market_category_field":
                            num = standardMarketCategoryFieldService.delRedisByAll();
                            break;
                        default:
                            break;
                    }
                    //log.info("【CleanRedisCachJob 根据传入表{}清除缓存】 成功清除的缓存条数：{}",parKey, num);
                    XxlJobLogger.log("【CleanRedisCachJob 根据传入表{}清除缓存】 成功清除的缓存条数：{}",parKey, num);
                } else{
//                    Object obj = redisService.get(parKey);
//                    //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 缓存信息为：{}",obj);
//                    XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 缓存信息为：{}",obj);

                    redisService.del(parKey);
                    //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 成功清除的缓存");
                    XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 成功清除的缓存");
                }
            }else{
                //刷新本地缓存
                languageTypeService.refreshCache();
                dataSourceService.refreshCache();
                thirdSportTypeService.refreshCache();
                //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 刷新本地缓存完成");
                XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 刷新本地缓存完成");
            }
        } catch (Exception e) {
            log.error("【CleanRedisCachJob 根据传入key值清除缓存执行异常】 Exception:", e);
            XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存执行异常】 Exception:"+e.getMessage());
        }
        //log.info("【CleanRedisCachJob 根据传入key值清除缓存】 处理结束");
        XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除缓存】 处理结束");
        return ReturnT.SUCCESS;
    }


    /**
     * 根据开始时间小于等于1天前，大于等于等于2天前的赛事，清理redis中相关缓存，失效时间大于一天的相关缓存（每天凌晨3点执行一次 ）
     * */
//    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanRedisCacheByDay() {
        //避免分布式定时任务启动重复
        String lockKey = RedisConfig.REDIS_KEY_DATABASE +"::job:cleanRedisCacheByDay";
        if(!redisService.tryLockOnce(lockKey,lockKey,REDIS_FIVE_MINS_TIME)){
            //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，定时任务启动重复，一次只能启动一个定时任务！");
            return;
        }
        StopWatch stopWatch = new StopWatch(UUID.randomUUID().toString());
        stopWatch.start();
        long beginTime = System.currentTimeMillis();
        //1天前的时间戳
        Long oneDayTime = beginTime - 1 * 24 * 60 * 60 * 1000L;
        //2天前的时间戳
        Long twoDayTime = (beginTime - 2 * 24 * 60 * 60 * 1000L) - HOUR_1;
        //需要清除的运动类型
//        List<Long> sportIds = Lists.newArrayList(StandardSportTypeEnum.FootBall.code, StandardSportTypeEnum.Basketball.code);
        //查询标准赛事表
        StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
        //开始时间小于等于1天前，大于等于等于2天前
        standardMatchInfoExample.createCriteria().andMatchOverEqualTo(YesNoEnum.Y.value).andBeginTimeLessThanOrEqualTo(oneDayTime).andBeginTimeGreaterThanOrEqualTo(twoDayTime);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(standardMatchInfoExample);
        //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，标准赛事条数：{}",standardMatchInfoList.size());
        //需要清理的总条数值
        Long totalNum = 0L;
        if(!CollectionUtils.isEmpty(standardMatchInfoList)){
            //方法废弃， 注释
            //需要根据标准赛事关联的KEY （标准盘口信息：Ronghe:StandardMarketData:1509650_SR）
            //Set<String> marketIdKeys = standardMatchInfoList.stream().map(obj -> Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + obj.getId()).collect(Collectors.toSet());
            //Set<String> marketKeys = redisService.keys(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + "*");
            ////log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存标准盘口条数：{}，标准盘口条数：{}",marketKeys.size(),marketIdKeys.size());
            //Set<String> delKeys1 = getDelKey(marketKeys, marketIdKeys);
            Long num = 0L;

            //totalNum += num;
            ////log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存标准盘口需要删除的条数：{}，删除成功的条数：{}",delKeys1.size(),num);
            //需要根据标准赛事关联的KEY （标准盘口关联信息：Ronghe:StandardMarket:RelationMarketId:1465105_28_4）
            Set<String> relationMatchIdKeys = standardMatchInfoList.stream().map(obj -> Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_RELATION_MARKET_ID + obj.getId()).collect(Collectors.toSet());
            Set<String> relationMarketKeys = redisService.keys(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_RELATION_MARKET_ID + "*");
            //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存标准盘口关联信息条数：{}，标准盘口关联条数：{}",relationMarketKeys.size(),relationMatchIdKeys.size());
            Set<String> delKeys2 = getDelKey(relationMarketKeys, relationMatchIdKeys);
            num = redisService.delete(delKeys2);

            totalNum += num;
            //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存标准盘口关联信息需要删除的条数：{}，删除成功的条数：{}",delKeys2.size(),num);
            //标准赛事ID列表
            List<Long> matchIds = standardMatchInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
            //查询标准赛事盘口信息
            StandardSportMarketExample standardSportMarketExample = new StandardSportMarketExample();
            standardSportMarketExample.createCriteria().andStandardMatchInfoIdIn(matchIds);
            List<StandardSportMarket> standardSportMarketList = standardSportMarketMapper.selectByExample(standardSportMarketExample);
            if(!CollectionUtils.isEmpty(standardSportMarketList)){
                //需要根据标准盘口投注项关联的KEY （标准盘口投注项信息：Ronghe:StandardMarketOdds:RelationMarketOddsId:1319916083809005570_1）
                Set<String> marketOddsIdKeys = standardSportMarketList.stream().map(obj -> Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID + obj.getRelationMarketId()).collect(Collectors.toSet());
                Set<String> marketOddsKeys = redisService.keys(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID + "*");
                //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存标准盘口投注项信息条数：{}，标准盘口条数：{}",marketOddsKeys.size(),marketOddsIdKeys.size());
                Set<String> delKeys3 = getDelKey(marketOddsKeys, marketOddsIdKeys);
                num = redisService.delete(delKeys3);
                totalNum += num;
                //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存标准盘口投注项需要删除的条数：{}，删除成功的条数：{}",delKeys3.size(),num);
                //清除TX坑位数据
                Set<String> thirdMarketKey = standardSportMarketList.stream().map(obj -> Constant.REDIS_KEY.RONGHE_TX_THIRD_MARKET + obj.getStandardMatchInfoId() + "_" + obj.getMarketCategoryId()).collect(Collectors.toSet());
                num = redisService.delete(thirdMarketKey);
                totalNum += num;
                //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存TX坑位数据需要删除的条数：{}，删除成功的条数：{}", delKeys3.size(), num);
            }
            //玩法自动关盘清理
            Set<String> delKeys6 = new HashSet<>();
            for (StandardMatchInfo item : standardMatchInfoList) {
                String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + item.getId());
                delKeys6.add(autoCloseRedisKey);
                redisService.hDel(Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_WARNING, String.valueOf(item.getId()));
            }
            if (!CollectionUtils.isEmpty(delKeys6)) {
                num = redisService.delete(delKeys6);
                totalNum += num;
                //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存自动关盘需要删除的条数：{}，删除成功的条数：{}", delKeys6.size(), num);
            }
        }
        //查询三方赛事表
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        //开始时间小于等于1天前，大于等于等于2天前
        thirdMatchInfoExample.createCriteria().andBeginTimeLessThanOrEqualTo(oneDayTime).andBeginTimeGreaterThanOrEqualTo(twoDayTime);
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，三方赛事条数：{}",thirdMatchInfoList.size());
        if(!CollectionUtils.isEmpty(thirdMatchInfoList)){
            //三方ID列表
            Map<Long,ThirdMatchInfo> thirdMatchId2Item = thirdMatchInfoList.stream().collect(Collectors.toMap(ThirdMatchInfo::getId, thi -> thi));
            //查询三方盘口信息
            ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
            thirdSportMarketExample.createCriteria().andDataSourceCodeEqualTo(DataSourceCodeEnum.BC.code).andMatchIdIn(Lists.newArrayList(thirdMatchId2Item.keySet()));
            List<ThirdSportMarket> thirdSportMarketList = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
            if(!CollectionUtils.isEmpty(thirdSportMarketList)){
                //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，三方盘口条数：{}",thirdSportMarketList.size());
                Set<String> delKeys4 = new HashSet<>();
                for (ThirdSportMarket item:thirdSportMarketList) {
                    ThirdMatchInfo thirdMatchInfo = thirdMatchId2Item.get(item.getMatchId());
                    if(null != thirdMatchInfo && oneDayTime > thirdMatchInfo.getBeginTime()){
                        //需要根据三方数据源盘口ID(SR,BG,BC)关联的KEY （三方盘口信息：Ronghe:ThridMarket:dataSourceTime:BG_1_BG:7127273:102667694:0）
                        String key = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + item.getThirdMarketSourceId();
                        delKeys4.add(key);
                    }
                }
                if(!CollectionUtils.isEmpty(delKeys4)){
                    Long num = redisService.delete(delKeys4);
                    totalNum += num;
                    //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，缓存三方盘口(BC)需要删除的条数：{}，删除成功的条数：{}",delKeys4.size(),num);
                }
            }
        }
        stopWatch.stop();
        //log.info("定时任务[每天凌晨3点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，清理redis中相关缓存条数：{}！执行用时{}毫秒",totalNum,stopWatch.getTotalTimeMillis());
    }

    /**
     * 每天检查缓存，panda-merge下缓存时间为-1的设置为一天
     * */
//    @Scheduled(cron = "0 0 1 * * ?")
    public void updatePandaMergeCachExpire(){
        //避免分布式定时任务启动重复
        String lockKey = RedisConfig.REDIS_KEY_DATABASE +"::job:updatePandaMergeCachExpire";
        if(!redisService.tryLockOnce(lockKey,lockKey,REDIS_FIVE_MINS_TIME)){
            //log.info("定时任务[每天凌晨1点]：根据开始时间小于等于1天前，大于等于等于2天前的赛事，定时任务启动重复，一次只能启动一个定时任务！");
            return;
        }
        StopWatch stopWatch = new StopWatch(UUID.randomUUID().toString());
        stopWatch.start();
        Set<String> keys = new HashSet<>();
        //panda-merge下缓存
        keys.addAll(redisService.keys(RedisConfig.REDIS_KEY_DATABASE+":*"));
        //赔率服务缓存
        keys.addAll(redisService.keys("Ronghe:*"));
        //将值为空的缓存删掉，将缓存失效时间为-1的设置为一天
        for (String str: keys) {
            try{
                Long expireTime = redisService.getExpire(str);
                if(expireTime < 0){
                    Object obj = redisService.get(str);
                    if(null == obj){
                        redisService.del(str);
                    }else{
                        redisService.expire(str,RedisConfig.REDIS_DEFAULT_TIME);
                    }
                }
            }catch (Exception e){
                log.error("定时任务[每天凌晨1点检查panda-merge下缓存],Exception:",e);
                redisService.del(str);
            }
        }
        stopWatch.stop();
        //log.info("定时任务[每天凌晨1点检查panda-merge下缓存]：缓存时间为-1的设置为一天！执行用时{}毫秒",stopWatch.getTotalTimeMillis());
    }



    /**
     * 获取需要清除的key值
     * @param  keys       数据库存在的key
     * @param  relations  关联信息
     * @return   delKeys    需要清除的key
     * */
    private Set<String> getDelKey(Set<String> keys,Set<String> relations){
        Set<String> delKeys = new HashSet<>();
        for (String marketKey: keys) {
            try{
                //获取当前key值过期时间
                Long expireTime = redisService.getExpire(marketKey);
                //如果为-2表示已过期，删除key
                if(expireTime == -2){
                    delKeys.add(marketKey);
                    continue;
                }
                //如果当前key值包含其中某个关联数据，则删除当前key值
                List<String> objs = relations.stream().filter(id -> marketKey.contains(id)).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(objs)){
                    delKeys.add(marketKey);
                }
            }catch (Exception e){
                log.error("【getDelKey 获取需要清除的key值】 Exception:", e);
                delKeys.add(marketKey);
            }
        }
        return delKeys;
    }

}
