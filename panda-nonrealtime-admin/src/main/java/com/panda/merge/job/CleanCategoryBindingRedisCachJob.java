package com.panda.merge.job;

import com.google.common.collect.Lists;
import com.panda.merge.config.RedisService;
import com.panda.merge.service.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 清理玩法绑定关系redis
 */
@Slf4j
@Component
@JobHandler(value = "CleanCategoryBindingRedisCachJob")
public class CleanCategoryBindingRedisCachJob extends IJobHandler {

    @Autowired
    private RedisService redisService;
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
    private static List<String> tabs = Lists.newArrayList(
            "third_market_category", "third_sport_market_category",
            "third_market_category_field", "standard_market_category",
            "standard_sport_market_category", "standard_market_category_field");

    @Override
    public ReturnT<String> execute(String parKey) {
        log.info("【CleanCategoryBindingRedisCachJob 根据传入key值清除缓存】 处理开始,入参: {}", parKey);
        XxlJobLogger.log("【CleanCategoryBindingRedisCachJob 根据传入key值清除缓存】 处理开始,入参: {}", parKey);
        try {
            if (StringUtils.isBlank(parKey)) {
                tabs.stream().forEach(tab -> {
                    clear(tab);
                });
            } else if (tabs.contains(parKey)) {
                clear(parKey);
            }
        } catch (Exception e) {
            log.error("【CleanCategoryBindingRedisCachJob 根据传入key值清除缓存执行异常】 Exception:", e);
            XxlJobLogger.log("【CleanCategoryBindingRedisCachJob 根据传入key值清除缓存执行异常】 Exception:" + e.getMessage());
        }
        log.info("【CleanCategoryBindingRedisCachJob 根据传入key值清除缓存】 处理结束");
        XxlJobLogger.log("【CleanCategoryBindingRedisCachJob 根据传入key值清除缓存】 处理结束");
        return ReturnT.SUCCESS;
    }


    private void clear(String parKey) {
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
        log.info("【CleanCategoryBindingRedisCachJob 根据传入表{}清除缓存】 成功清除的缓存条数：{}", parKey, num);
        XxlJobLogger.log("【CleanCategoryBindingRedisCachJob 根据传入表{}清除缓存】 成功清除的缓存条数：{}", parKey, num);
    }

}
