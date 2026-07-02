package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.I18nMarketCategoryApi;
import com.panda.merge.dto.Response;
import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.StandardMarketCategoryField;
import com.panda.merge.model.ThirdMarketCategoryField;
import com.panda.merge.service.I18nMarketCategoryService;
import com.panda.merge.service.StandardMarketCategoryFieldService;
import com.panda.merge.service.ThirdMarketCategoryFieldService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author raulvii
 */
@Slf4j
@Component
@DubboService
public class I18MarketCategoryApiImpl implements I18nMarketCategoryApi {

    @Autowired
    public I18nMarketCategoryService i18nMarketCategoryService;
    @Autowired
    private StandardMarketCategoryFieldService standardMarketCategoryFieldService;
    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    /**
     * 标准投注项名称多语言,
     */
    public static List<String> languageTypeList = Arrays.asList("zs", "en", "zh", "es", "it", "de", "fr", "pb", "ru", "ja", "ko", "th", "vi");

    @Override
    public Response initI18nMarketCategory() {
        log.info("initI18nMarketCategory 开始");
        List<StandardMarketCategoryField> itemList = standardMarketCategoryFieldService.getItemList();
        List<Long> collect = itemList.stream().map(StandardMarketCategoryField::getNameCode).collect(Collectors.toList());
        Map<Long, List<I18nMarketCategory>> longListMap = i18nMarketCategoryService.getItemsByNameCodes(collect);
        if (MapUtils.isEmpty(longListMap)) {
            return Response.success("未获取到投注项多语言集合");
        }
        List<I18nMarketCategory> i18nMarketCategoryList = new ArrayList<>();
        longListMap.forEach((k, v) -> {
            if (null == k || 0 == k) {
                return;
            }
            List<String> vLanguageTypeList = v.stream().map(I18nMarketCategory::getLanguageType).collect(Collectors.toList());
            languageTypeList.forEach(x -> {
                if (!vLanguageTypeList.contains(x)) {
                    I18nMarketCategory i18nMarketCategory = new I18nMarketCategory();
                    i18nMarketCategory.setNameCode(k);
                    i18nMarketCategory.setFlag(2);
                    i18nMarketCategory.setLanguageType(x);
                    i18nMarketCategory.setDataSourceCode(v.get(0).getDataSourceCode());
                    i18nMarketCategory.setCreateTime(System.currentTimeMillis());
                    i18nMarketCategory.setModifyTime(System.currentTimeMillis());
                    i18nMarketCategoryList.add(i18nMarketCategory);
                }
            });
        });
        if (!CollectionUtils.isEmpty(i18nMarketCategoryList)) {
            log.info("initI18nMarketCategory saveBatch size:{}", i18nMarketCategoryList.size());
            try {
                i18nMarketCategoryService.saveBatch(i18nMarketCategoryList);
            } catch (Exception e) {
                log.info("initI18nMarketCategory saveBatch error:{}", e);
            }
        }
        initI18nMarketCategoryText();

        return Response.success();
    }

    @Override
    public void initI18nMarketCategoryText() {
        log.info("initI18nMarketCategoryText，start:{}");
        try {
            List<StandardMarketCategoryField> standardFieldList = standardMarketCategoryFieldService.getItemList();
            if (CollectionUtils.isEmpty(standardFieldList)) {
                log.info("initI18nMarketCategoryText，standardFieldList集合为空");
                return;
            }
            List<ThirdMarketCategoryField> thirdFieldList = thirdMarketCategoryFieldService.queryThirdMarketCategoryField("SR");
            if (CollectionUtils.isEmpty(thirdFieldList)) {
                log.info("thirdFieldList，standardFieldList集合为空");
                return;
            }
            Map<Long, ThirdMarketCategoryField> categoryFieldMap = thirdFieldList.stream().collect(Collectors.toMap(thi -> thi.getReferenceId(), thi -> thi,(oldValue,newValue)->newValue));
            if (MapUtils.isEmpty(categoryFieldMap)) {
                log.info("initI18nMarketCategoryText，categoryFieldMap集合为空");
                return;
            }
            standardFieldList.forEach(x -> {
                try {
                    log.info("更新标准投注项多语言nameCode:{}, start", x.getNameCode());
                    ThirdMarketCategoryField thirdMarketCategoryField = categoryFieldMap.get(x.getId());
                    if (null == thirdMarketCategoryField) {
                        log.info("initI18nMarketCategoryText，nameCode:{},标准投注项id:{},thirdMarketCategoryField为空", x.getNameCode(), x.getId());
                        return;
                    }
                    List<Long> nameCodes = new ArrayList<>();
                    nameCodes.add(x.getNameCode());
                    nameCodes.add(thirdMarketCategoryField.getNameCode());
                    Map<Long, List<I18nMarketCategory>> longListMap = i18nMarketCategoryService.getItemsByNameCodes(nameCodes);
                    if (MapUtils.isEmpty(longListMap) || longListMap.size() != 2) {
                        log.info("initI18nMarketCategoryText，nameCode:{},标准投注项id:{},longListMap为空", x.getNameCode(), x.getId());
                        return;
                    }//组装三方投注项多语言为map数据
                    List<I18nMarketCategory> thirdMarketCategoryFields = longListMap.get(thirdMarketCategoryField.getNameCode());
                    Map<String, I18nMarketCategory> map = thirdMarketCategoryFields.stream().collect(Collectors.toMap(thi -> thi.getLanguageType(), thi -> thi));
                    //赋值
                    List<I18nMarketCategory> i18nMarketCategoryList = longListMap.get(x.getNameCode());
                    i18nMarketCategoryList.forEach(i18nMarketCategory -> {
                        if (null != map.get(i18nMarketCategory.getLanguageType())) {
                            i18nMarketCategory.setText(map.get(i18nMarketCategory.getLanguageType()).getText());
                            i18nMarketCategory.setModifyTime(System.currentTimeMillis());
                        }
                    });
                    //更新
                    if (!CollectionUtils.isEmpty(i18nMarketCategoryList)) {
                        i18nMarketCategoryService.updateBatchById(i18nMarketCategoryList);
                    }
                    log.info("更新标准投注项多语言nameCode:{},受影响行数:{}", x.getNameCode(), i18nMarketCategoryList.size());
                } catch (Exception e) {
                    log.info("更新标准投注项多语言nameCode:{}, error:{}", x.getNameCode(), JSON.toJSON(e));
                }
            });
        } catch (Exception e) {
            log.info("initI18nMarketCategoryText,error:{}", JSON.toJSON(e));
        }
        log.info("initI18nMarketCategoryText，end");
    }
}
