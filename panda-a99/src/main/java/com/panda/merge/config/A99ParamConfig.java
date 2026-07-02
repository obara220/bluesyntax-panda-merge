package com.panda.merge.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.service.ThirdSportMarketCategoryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Component
@Data
public class A99ParamConfig {

    @Resource
    private ThirdSportMarketCategoryService thirdSportMarketCategoryService;

    //@Value("${calculate.standard.category.classify}")
    private String standardCategory="{\"10001\":[4,2,19,18],\"10002\":[113,114,121,122],\"10005\":[306,307,308,309],\"10003\":[128,127,130,332],\"10006\":[1100414,331,1100416,1100417],\"10007\":[1100406,1100407,1100409,1100410]}";

    public  List<String> thirdMarketCategorySourceIds = new ArrayList<>();
    public  Map<String, List<Integer>> categoryMap = new HashMap<>();
    public  List<String> standardMarketIds = new ArrayList<>(50);
    @PostConstruct
    public void init()
    {
        log.info("获取配置:standardCategory:{}",standardCategory);
        if (ObjectUtil.isEmpty(standardCategory)) {
            log.error("请检查calculate.standard.category.classify配置是否正确");
            return;
        }

        log.info("开始解析配置: calculate.standard.category.classify");
        categoryMap = JSONObject.parseObject(standardCategory, Map.class);
        log.info("配置解析完成，categoryMap: {}", categoryMap);

        log.info("开始转换 categoryMap 的值为字符串列表:categoryMa: {}",categoryMap);
        for (Map.Entry<String, List<Integer>> entry : categoryMap.entrySet()) {
            List<String> strList = entry.getValue().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            standardMarketIds.addAll(strList);
        }
        log.info("转换完成，standardMarketIds: {}", standardMarketIds);

        log.info("开始转换 referenceIds");
        List<Long> referenceIds = Convert.convert(List.class, standardMarketIds);
        log.info("referenceIds 转换完成: {}", referenceIds);

        if (ObjectUtil.isNotEmpty(referenceIds)) {
            log.info("查询三方玩法源id参数, referenceIds:{}, sportIds:{}", referenceIds, Arrays.asList(StandardSportTypeEnum.FootBall.code));
            List<ThirdMarketCategory> thirdMarketCategories = thirdSportMarketCategoryService.queryThirdMarketCategoryList(referenceIds, Arrays.asList(StandardSportTypeEnum.FootBall.code));
            log.info("查询三方玩法源id数量:{}", thirdMarketCategories.size());

            if (ObjectUtil.isNotEmpty(thirdMarketCategories)) {
                log.info("开始提取 thirdMarketCategorySourceIds");
                thirdMarketCategorySourceIds = thirdMarketCategories.stream()
                        .filter(e -> e != null)
                        .map(ThirdMarketCategory::getThirdSourceId)
                        .collect(Collectors.toList());
                log.info("三方玩法源id提取完成，结果:{}", thirdMarketCategorySourceIds);
            }
        }
    }

}
