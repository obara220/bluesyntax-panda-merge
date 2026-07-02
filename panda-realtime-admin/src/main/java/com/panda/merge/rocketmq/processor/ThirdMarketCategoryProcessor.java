package com.panda.merge.rocketmq.processor;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketCategoryDTO;
import com.panda.merge.dto.ThirdMarketCategoryFieldDTO;
import com.panda.merge.model.*;
import com.panda.merge.service.I18nMarketCategoryService;
import com.panda.merge.service.ThirdMarketCategoryFieldService;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.processor
 * @description : 第三方玩法数据处理
 * @date: 2020-09-11 9:21
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Validated
@Component
public class ThirdMarketCategoryProcessor extends BaseProcessor {

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;

    @Autowired
    private ThirdSportMarketCategoryService thirdSportMarketCategoryService;

    @Autowired
    private I18nMarketCategoryService i18nMarketCategoryService;

    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    /**
     * 第三方玩法数据处理
     * @param request
     */
    public void putMarketCategory(@Valid Request<List<ThirdMarketCategoryDTO>> request) {
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_CATEGORY_API+"】【::"+request.getLinkId()+"::】第三方玩法数据接收开始...");
        long beginTime = System.currentTimeMillis();
        List<ThirdMarketCategoryDTO> thirdMarketCategoryDTOs = request.getData();
        //获取当前第三方数据源
        Set<String> dataSourceCodes = new HashSet<>();
        //本次传入的三方运动类型列表
        Set<String> thirdSportIds = new HashSet<>();
        //三方数据源玩法ID
        Set<String> thirdSourceIdSet = new HashSet<>();
        for (ThirdMarketCategoryDTO dto: thirdMarketCategoryDTOs) {
            dataSourceCodes.add(dto.getDataSourceCode());
            thirdSourceIdSet.add(dto.getThirdSourceId());
            thirdSportIds.addAll(dto.getSupportSports().stream().map(obj->String.valueOf(obj)).collect(Collectors.toSet()));
        }
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCodes(request, dataSourceCodes);
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_CATEGORY_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方玩法数据处理开始");
        /** 02 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联*/
        Map<String, Long> thirdSportId2referenceId = validateSportIds(dataSource.getCode(), thirdSportIds);
        //根据数据源、三方数据源玩法id 获取三方玩法信息
        List<ThirdMarketCategory> thirdMarketCategoryList = thirdMarketCategoryService.queryThirdMarketCategoryList(dataSourceCodes, thirdSourceIdSet);
        //三方玩法map对象
        Map<String, ThirdMarketCategory> categoryMap = new LinkedHashMap<>();
        //三方玩法id集合不为空，则查询赛种玩法表数据
        Map<String, ThirdSportMarketCategory> sportCategoryMap = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(thirdMarketCategoryList)) {
            //三方玩法map对象
            categoryMap = thirdMarketCategoryList.stream().collect(Collectors.toMap(thi -> thi.getDataSourceCode() + Constant.STR_SEPARATION + thi.getThirdSourceId(), thi -> thi));
            //三方玩法id集合对象
            Set<Long> categoryIdSet = thirdMarketCategoryList.stream().map(obj->obj.getId()).collect(Collectors.toSet());
            //查询三方赛种玩法关联关系表
            List<ThirdSportMarketCategory> thirdSportMarketCategoryList = thirdSportMarketCategoryService.queryThirdSportMarketCategoryList(categoryIdSet);
            if (!CollectionUtils.isEmpty(thirdSportMarketCategoryList)) {
                //赛种、三方玩法id作为key
                sportCategoryMap = thirdSportMarketCategoryList.stream().collect(Collectors.toMap(thi -> thi.getSportId() + Constant.STR_SEPARATION + thi.getMarketCategoryId(), thi -> thi));
            }
        }
        // 玩法数据
        List<ThirdMarketCategory> insertCategoryList = new ArrayList<>();
        List<ThirdMarketCategory> updateCategoryList = new ArrayList<>();
        //新增、修改的国际化信息
        List<I18nMarketCategory> insertI18nList = new ArrayList<>();
        List<I18nMarketCategory> updateI18nList = new ArrayList<>();
        // 赛种玩法数据
        List<ThirdSportMarketCategory> insertSportCategoryList = new ArrayList<>();
        //循环遍历入参集合对象
        for (ThirdMarketCategoryDTO dto : request.getData()) {
            //支持赛种
            List<Long> sportIdList = dto.getSupportSports();
            //需要操作的三方玩法对象
            ThirdMarketCategory thirdMarketCategory = new ThirdMarketCategory();
            //dto入参对象复制
            BeanUtils.copyProperties(dto, thirdMarketCategory);
            //获取数据库中三方玩法
            ThirdMarketCategory oldCategory = categoryMap.get(dto.getDataSourceCode() + Constant.STR_SEPARATION + dto.getThirdSourceId());
            //不为空则修改
            if (null != oldCategory) {
                thirdMarketCategory.setId(oldCategory.getId());
                thirdMarketCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdMarketCategory.setNameCode(this.getLanguageNameCode(dto.getNameI18n(), oldCategory.getNameCode(), oldCategory.getDataSourceCode(), insertI18nList, updateI18nList));
                updateCategoryList.add(thirdMarketCategory);
            } else {
                //新增三方玩法数据
                thirdMarketCategory.setId(UUIdUtils.getId());
                thirdMarketCategory.setNameCode(this.getLanguageNameCode(dto.getNameI18n(), thirdMarketCategory.getNameCode(), dto.getDataSourceCode(), insertI18nList, updateI18nList));
                thirdMarketCategory.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdMarketCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                insertCategoryList.add(thirdMarketCategory);
            }
            if (!CollectionUtils.isEmpty(sportIdList)) {
                for (Long sportId : sportIdList) {
                    //转换为标准运动种类id
                    Long standardSportId = thirdSportId2referenceId.get(String.valueOf(sportId));
                    if (null != oldCategory) {
                        String sportCategoryKey = standardSportId + Constant.STR_SEPARATION + oldCategory.getId();
                        if (sportCategoryMap.containsKey(sportCategoryKey)) {
                            log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_CATEGORY_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛种玩法表数据已经存在,三方玩法ID:{}",oldCategory.getId());
                            continue;
                        }
                    }
                    //新增赛种玩法数据
                    insertSportCategoryList.add(initThirdSportMarketCategory(thirdMarketCategory.getId(), standardSportId));
                }
            }
        }
        //入库
        insertCategory(request, insertCategoryList, updateCategoryList, insertI18nList, updateI18nList, insertSportCategoryList);
        long endTime = System.currentTimeMillis();
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_CATEGORY_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方玩法数据处理结束,总耗时 ：{}" , endTime - beginTime);
    }

    /**
     * 处理三方玩法数据入库
     * */
    private void insertCategory(Request request, List<ThirdMarketCategory> insertCategoryList, List<ThirdMarketCategory> updateCategoryList,
                                List<I18nMarketCategory> insertI18nAllList, List<I18nMarketCategory> upI18nAllList, List<ThirdSportMarketCategory> insertSportCategoryList) {
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_CATEGORY_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】三方玩法数据新增："
                + insertCategoryList.size() + "条。修改：" + updateCategoryList.size() + "条。");
        if (!insertCategoryList.isEmpty()) {
            thirdMarketCategoryService.saveBatch(insertCategoryList);
        }
        if (!updateCategoryList.isEmpty()) {
            thirdMarketCategoryService.updateBatchById(updateCategoryList);
        }
        if (!insertI18nAllList.isEmpty()) {
            i18nMarketCategoryService.saveBatch(insertI18nAllList);
        }
        if (!upI18nAllList.isEmpty()) {
            i18nMarketCategoryService.updateBatchById(upI18nAllList);
        }
        if (!insertSportCategoryList.isEmpty()) {
            thirdSportMarketCategoryService.saveBatch(insertSportCategoryList);
        }
    }

    /**
     * 第三方玩法投注项数据处理
     * @param request 数据处理入库
     */
    public void putMarketOddsFields(@Valid Request<List<ThirdMarketCategoryFieldDTO>> request) {
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API+"】【::"+request.getLinkId()+"::】第三方玩法投注项数据接收开始...");
        long beginTime = System.currentTimeMillis();
        //传入的玩法投注项列表
        List<ThirdMarketCategoryFieldDTO> marketCategoryFieldDtoList = request.getData();
        /** 01 校验dataSourceCode是否合法*/
        Set<String> dataSourceCodes = marketCategoryFieldDtoList.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
        simpleValidateDataSourceCodes(request, dataSourceCodes);
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方玩法数据处理开始");
        //本次传入的三方数据源玩法ID
        Set<String> thirdMarketCategorySourceIdSet = marketCategoryFieldDtoList.stream().map(ThirdMarketCategoryFieldDTO::getThirdCategorySourceId).collect(Collectors.toSet());
        //根据三方玩法原始id 查询三方玩法数据
        List<ThirdMarketCategory> oldThirdMarketCategorieList = thirdMarketCategoryService.queryByThirdMarketCategorySourceIdSet(thirdMarketCategorySourceIdSet);
        if (CollectionUtils.isEmpty(oldThirdMarketCategorieList)) {
            log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API+"】【::"+request.getLinkId()+"::】根据三方数据源玩法id查询,三方玩法数据为空！");
            return;
        }
        //库中三方玩法数据
        Map<String, ThirdMarketCategory> odlCategoryMap = oldThirdMarketCategorieList.stream().collect(Collectors.toMap(thi-> thi.getDataSourceCode()+ Constant.STR_SEPARATION + thi.getThirdSourceId(), thi -> thi));
        //本次传入的三方数据源玩法投注项ID
        Set<String> thirdTempletSourceIdSet = marketCategoryFieldDtoList.stream().map(ThirdMarketCategoryFieldDTO::getThirdSourceId).collect(Collectors.toSet());
        //根据三方玩法投注项原始id获取询库中三方玩法投注项数据
        Map<String, ThirdMarketCategoryField> oldThirdSourceId2CategoryField = thirdMarketCategoryFieldService.queryThirdSportOddsFieldsLists(thirdTempletSourceIdSet)
                .stream().collect(Collectors.toMap(ThirdMarketCategoryField::getThirdSourceId, thi -> thi));
        //新增、修改的玩法投注项信息
        List<ThirdMarketCategoryField> insertCategoryFieldList = new ArrayList<>();
        List<ThirdMarketCategoryField> updateCategoryFieldList = new ArrayList<>();
        //新增、修改的国际化信息
        List<I18nMarketCategory> insertI18nList = new ArrayList<>();
        List<I18nMarketCategory> updateI18nList = new ArrayList<>();
        for (ThirdMarketCategoryFieldDTO dto : request.getData()) {
            //如果获取第三方玩法主键为空，跳过不处理,key:数据源编码+玩法原始id
            String key = dto.getDataSourceCode() + Constant.STR_SEPARATION + dto.getThirdCategorySourceId();
            if (!odlCategoryMap.containsKey(key)) {
                log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API+"】【::"+request.getLinkId()+"::】入参玩法原始id与数据库不一致,跳过不处理！入参玩法原始id:{}，入参投注项原始id：{}"
                        ,dto.getThirdCategorySourceId(), dto.getThirdSourceId());
                continue;
            }
            ThirdMarketCategoryField thirdSportOddsFields = new ThirdMarketCategoryField();
            BeanUtils.copyProperties(dto, thirdSportOddsFields);
            //获取数据库中玩法投注项对象
            ThirdMarketCategoryField oldThirdMarketCategoryField = oldThirdSourceId2CategoryField.get(dto.getThirdSourceId());
            thirdSportOddsFields.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            if (null != oldThirdMarketCategoryField) {
                //玩法主键id
                thirdSportOddsFields.setMarketCategoryId(odlCategoryMap.get(key).getId());
                thirdSportOddsFields.setNameCode(this.getLanguageNameCode(dto.getNameI18n(), oldThirdMarketCategoryField.getNameCode(), dto.getDataSourceCode(), insertI18nList, updateI18nList));
                //设置三方投注项的主键id
                thirdSportOddsFields.setId(oldThirdMarketCategoryField.getId());
                updateCategoryFieldList.add(thirdSportOddsFields);
            } else {
                //主键id
                thirdSportOddsFields.setId(IdWorker.getId());
                //设置玩法ID主键
                thirdSportOddsFields.setMarketCategoryId(odlCategoryMap.get(key).getId());
                thirdSportOddsFields.setNameCode(this.getLanguageNameCode(dto.getNameI18n(), thirdSportOddsFields.getNameCode(), dto.getDataSourceCode(), insertI18nList, updateI18nList));
                thirdSportOddsFields.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                insertCategoryFieldList.add(thirdSportOddsFields);
            }
        }
        //入库
        insertOddsFieldsTemplet(request,insertCategoryFieldList, updateCategoryFieldList, insertI18nList, updateI18nList);
        long endTime = System.currentTimeMillis();
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方玩法数据处理结束,总耗时 ：{}" , endTime - beginTime);
    }

    /**
     * 三方玩法投注项数据入库
     * @param request
     * @param insertCategoryFieldList
     * @param updateCategoryFieldList
     * @param insertAllList
     * @param upAllList
     */
    private void insertOddsFieldsTemplet(Request request, List<ThirdMarketCategoryField> insertCategoryFieldList, List<ThirdMarketCategoryField> updateCategoryFieldList,
                                         List<I18nMarketCategory> insertAllList, List<I18nMarketCategory> upAllList) {
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】三方玩法投注项数据新增："
                + insertCategoryFieldList.size() + "条。修改：" + updateCategoryFieldList.size() + "条。");
        if (!CollectionUtils.isEmpty(insertCategoryFieldList)) {
            thirdMarketCategoryFieldService.saveBatch(insertCategoryFieldList);
        }
        if (!CollectionUtils.isEmpty(updateCategoryFieldList)) {
            thirdMarketCategoryFieldService.updateBatchById(updateCategoryFieldList);
        }
        if (!insertAllList.isEmpty()) {
            i18nMarketCategoryService.saveBatch(insertAllList);
        }
        if (!upAllList.isEmpty()) {
            i18nMarketCategoryService.updateBatchById(upAllList);
        }
    }



    /**
     * 生成多语言nameCode值
     * @param i18nItemDTOList
     * @param oldNameCode       库中nameCode
     * @param dataSourceCode
     * @return
     */
    public Long getLanguageNameCode(List<I18nItemDTO> i18nItemDTOList, Long oldNameCode, String dataSourceCode, List<I18nMarketCategory> insertI18nList, List<I18nMarketCategory> updateI18nList) {
        //根据数据源、国际化编码查询国际化信息
        Map<String, I18nMarketCategory> oldLanguageType2Id = new LinkedHashMap<>();
        //如果nameCode为空或为0，需要重新生成nameCode
        if(Objects.isNull(oldNameCode) || oldNameCode == 0) {
            oldNameCode = UUIdUtils.getId();
        }else{
            oldLanguageType2Id = i18nMarketCategoryService.queryLanguageInternation(dataSourceCode, oldNameCode);
        }
        if (CollectionUtils.isEmpty(i18nItemDTOList)) {
            return oldNameCode;
        }
        for (I18nItemDTO param : i18nItemDTOList) {
            I18nMarketCategory languageInternation = new I18nMarketCategory();
            BeanUtils.copyProperties(param, languageInternation);
            languageInternation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //库中当前多语言ID
            I18nMarketCategory oldLanguage = oldLanguageType2Id.get(param.getLanguageType());
            //新增
            if(null == oldLanguage){
                languageInternation.setId(IdWorker.getId());
                languageInternation.setDataSourceCode(dataSourceCode);
                languageInternation.setNameCode(oldNameCode);
                languageInternation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                insertI18nList.add(languageInternation);
            }else{
                languageInternation.setId(oldLanguage.getId());
                languageInternation.setDataSourceCode(dataSourceCode);
                updateI18nList.add(languageInternation);
            }
        }
        return oldNameCode;
    }


    /**
     * 新增赛种玩法数据
     *
     * @param thirdMarketCategoryId 三方玩法表 ID
     * @param standardSportId       运动种类id
     * @return
     */
    private ThirdSportMarketCategory initThirdSportMarketCategory(Long thirdMarketCategoryId, Long standardSportId) {
        ThirdSportMarketCategory thirdSportMarketCategory = new ThirdSportMarketCategory();
        thirdSportMarketCategory.setId(IdWorker.getId());
        thirdSportMarketCategory.setSportId(standardSportId);
        thirdSportMarketCategory.setMarketCategoryId(thirdMarketCategoryId);
        thirdSportMarketCategory.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdSportMarketCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        return thirdSportMarketCategory;
    }
}
