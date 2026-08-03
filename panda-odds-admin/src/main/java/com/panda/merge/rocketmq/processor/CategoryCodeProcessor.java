package com.panda.merge.rocketmq.processor;

import cn.hutool.json.JSONUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.CategoryCodeProducer;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import springfox.documentation.spring.web.json.Json;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class CategoryCodeProcessor  extends BaseProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private CategoryCodeProducer categoryCodeProducer;

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private ThirdSportMarketCategoryService thirdSportMarketCategoryService;
    /**
     * 处理三方赛事内部编码
     * @param request
     */
    public void processThirdMatchInternalCode(@Valid Request<ThirdMatchInternalCode> request) {
        ThirdMatchInternalCode thirdMatchInternalCode = request.getData();
        String thirdMatchId = thirdMatchInternalCode.getThirdMatchSourceId();
        String linkId = request.getLinkId();
        log.info("::{}::processThirdMatchInternalCode 入参 thirdMatchId:{},详细信息：{}", linkId, thirdMatchId, JSONUtil.toJsonStr(request.getData()));
        if (thirdMatchInternalCode.getInternalCodeList() == null || thirdMatchInternalCode.getInternalCodeList().isEmpty()){
            log.info("::{}::processThirdMatchInternalCode internalCodeList 入参为空", linkId);
            return;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(thirdMatchInternalCode.getDataSourceCode(),thirdMatchId);
        if (thirdMatchInfo == null){
            log.info("::{}::processThirdMatchInternalCode 未找到赛事信息，请检查赛事ID是否正确,thirdMatchId:{}", linkId,thirdMatchId);
            return;
        }
        String key = Constant.REDIS_KEY.RONGHE_INTERNAL_CODE + thirdMatchId + "_" + thirdMatchInternalCode.getDataSourceCode()+"_"+thirdMatchInternalCode.getMarketType();
        String key1 = Constant.REDIS_KEY.RONGHE_CURRENT_INTERNAL_CODE + thirdMatchId + "_" + thirdMatchInternalCode.getDataSourceCode()+"_"+thirdMatchInternalCode.getMarketType();
        Map<String,String> map = null;
        if (CollectionUtils.isNotEmpty(thirdMatchInternalCode.getInternalCodeList())){
            map = thirdMatchInternalCode.getInternalCodeList().stream().collect(Collectors.toMap(thi->thi,thi->thi,(oldValue,newValue)->newValue));
        }
        if (map!= null){
            redisService.hSetAll(key,map,marketCacheTime(thirdMatchInfo.getBeginTime()));
            redisService.set(key1,thirdMatchInternalCode.getCurrentInternalCode(),marketCacheTime(thirdMatchInfo.getBeginTime()));
        }
        /* 暂时注释掉mq下发
        if (thirdMatchInfo.getReferenceId() != null && thirdMatchInfo.getReferenceId()!= 0 ){
            categoryCodeProducer.sendStandardInternalCodeToMQ(convertStandardCategoryDataSourceCodeDTO(linkId,thirdMatchInfo, thirdMatchInternalCode));
        }*/
        log.info("::{}::processThirdMatchInternalCode 处理完成 map:{}", linkId,JSONUtil.toJsonStr(map));
    }

    /**
     * 组装提供给风控的dubbo接口数据
     * @param linkId
     * @param thirdMatchInfos
     * @param standardMatchId
     * @return
     */
    public StandardCategoryDataSourceCodeDTO processStandardMatchInternalCode(String linkId, List<ThirdMatchInfo> thirdMatchInfos,Long standardMatchId) {
        if (CollectionUtils.isEmpty(thirdMatchInfos)){
            log.info("::{}::processStandardMatchInternalCode 入参为空", linkId);
            return null;
        }
        int marketType = isOddsLive(standardMatchId);
        List<ThirdMatchInternalCode> thirdMatchInternalCodeList = new ArrayList<>();
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfos){
            String key = Constant.REDIS_KEY.RONGHE_INTERNAL_CODE + thirdMatchInfo.getThirdMatchSourceId() + "_" + thirdMatchInfo.getDataSourceCode()+"_"+marketType;
            Object obj = redisService.hGetAll(key);
            if (obj == null){
                log.info("::{}::processStandardMatchInternalCode 未找到赛事内部编码，请检查赛事ID是否正确,thirdMatchId:{}", linkId,thirdMatchInfo.getThirdMatchSourceId());
                return null;
            }
            Map<String,String> map = (Map<String, String>) obj;
            if (map != null && !map.isEmpty()){
                String key1 = Constant.REDIS_KEY.RONGHE_CURRENT_INTERNAL_CODE + thirdMatchInfo.getThirdMatchSourceId() + "_" + thirdMatchInfo.getDataSourceCode()+"_"+marketType;
                Object obj1 = redisService.get(key1);
                ThirdMatchInternalCode thirdMatchInternalCode = new ThirdMatchInternalCode();
                thirdMatchInternalCode.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                thirdMatchInternalCode.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
                thirdMatchInternalCode.setCurrentInternalCode(obj1 !=null ? obj1.toString() : null);
                thirdMatchInternalCode.setInternalCodeList(map.values().stream().collect(Collectors.toList()));
                thirdMatchInternalCode.setMarketType(marketType);
                thirdMatchInternalCodeList.add(thirdMatchInternalCode);
            }
        }
        return buildDTO(standardMatchId,thirdMatchInternalCodeList);
    }

    /**
     * 开售时，如果选择了内部站点，通知接入下发赔率
     * @param linkId
     * @param thirdMatchInfo
     * @param map  k-标准玩法，v-内部编码
     */
    public void processToApi(String linkId, ThirdMatchInfo thirdMatchInfo,Map<Long,String> map,Integer marketType){
        if (map == null || thirdMatchInfo == null || map.isEmpty()){
            log.info("::{}::processToApi 入参为空", linkId);
            return;
        }
        log.info("::{}::processToApi 入参 thirdMatchId:{},sportId:{},详细信息：{}", linkId, thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getSportId(), JSONUtil.toJsonStr(map));
        String dataSourceCode = thirdMatchInfo.getDataSourceCode();
        if (!DataSourceCodeEnum.LS.getCode().equalsIgnoreCase(dataSourceCode)
                && !DataSourceCodeEnum.TX.getCode().equalsIgnoreCase(dataSourceCode)
                && !DataSourceCodeEnum.L02.getCode().equalsIgnoreCase(dataSourceCode)) {
            log.info("::{}::processToApi 不支持的数据源,thirdMatchId:{},dataSourceCode:{}", linkId, thirdMatchInfo.getThirdMatchSourceId(), dataSourceCode);
            return;
        }
        Long sportId = thirdMatchInfo.getSportId();
        if (sportId == null) {
            log.info("::{}::processToApi 赛种为空,thirdMatchId:{}", linkId, thirdMatchInfo.getThirdMatchSourceId());
            return;
        }
        // 标准玩法 -> 三方玩法：必须带赛种；Redis 批量缓存，miss 才回源 DB
        List<ThirdMarketCategory> categoryList = thirdSportMarketCategoryService.getItemsBySportReferenceIds(
                dataSourceCode, sportId, new ArrayList<>(map.keySet()));
        if (CollectionUtils.isEmpty(categoryList)) {
            log.info("::{}::processToApi 未找到赛种玩法映射,thirdMatchId:{},sportId:{},standardCategoryIds:{}",
                    linkId, thirdMatchInfo.getThirdMatchSourceId(), sportId, map.keySet());
            return;
        }
        Map<Long, List<ThirdMarketCategory>> categoryByReferenceId = categoryList.stream()
                .collect(Collectors.groupingBy(ThirdMarketCategory::getReferenceId));
        Map<String, String> thirdCategoryMap = new LinkedHashMap<>();
        map.forEach((standardCategoryId, internalCode) -> {
            List<ThirdMarketCategory> categories = categoryByReferenceId.get(standardCategoryId);
            if (CollectionUtils.isEmpty(categories)) {
                log.info("::{}::processToApi 未找到当前数据源下赛种玩法映射,thirdMatchId:{},sportId:{},dataSourceCode:{},standardCategoryId:{}",
                        linkId, thirdMatchInfo.getThirdMatchSourceId(), sportId, dataSourceCode, standardCategoryId);
                return;
            }
            if (categories.size() > 1) {
                log.warn("::{}::processToApi 赛种下标准玩法对应多个三方玩法,跳过不下发,thirdMatchId:{},sportId:{},dataSourceCode:{},standardCategoryId:{},thirdSourceIds:{}",
                        linkId, thirdMatchInfo.getThirdMatchSourceId(), sportId, dataSourceCode, standardCategoryId,
                        categories.stream().map(ThirdMarketCategory::getThirdSourceId).collect(Collectors.toList()));
                return;
            }
            thirdCategoryMap.put(categories.get(0).getThirdSourceId(), internalCode);
        });
        if (thirdCategoryMap.isEmpty()) {
            log.info("::{}::processToApi thirdCategoryMap为空,thirdMatchId:{},sportId:{}", linkId, thirdMatchInfo.getThirdMatchSourceId(), sportId);
            return;
        }
        Request<List<CategoryDataSourceCodeDTO>> categoryCodeRequest = convertCategoryCode(linkId,thirdMatchInfo,thirdCategoryMap,marketType);
        if (thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.LS.getCode())){
            categoryCodeProducer.sendLCodeApiTOMQ(categoryCodeRequest);
        }
        else if (thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.TX.getCode())){
            categoryCodeProducer.sendTCodeApiTOMQ(categoryCodeRequest);
        }else if (thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.L02.getCode())){
            categoryCodeProducer.sendL02CodeApiTOMQ(categoryCodeRequest);
        }
    }

    private Request<StandardCategoryDataSourceCodeDTO> convertStandardCategoryDataSourceCodeDTO(String linkId, ThirdMatchInfo thirdMatchInfo, ThirdMatchInternalCode thirdMatchInternalCode) {
        Request<StandardCategoryDataSourceCodeDTO> standardCategoryDataSourceCodeDTORequest = new Request<>();
        standardCategoryDataSourceCodeDTORequest.setLinkId(linkId);
        List<ThirdMatchInternalCode> ThirdMatchInternalCodeList = new ArrayList<>();
        ThirdMatchInternalCodeList.add(thirdMatchInternalCode);
        standardCategoryDataSourceCodeDTORequest.setData(buildDTO(thirdMatchInfo.getReferenceId(),ThirdMatchInternalCodeList));
        return standardCategoryDataSourceCodeDTORequest;
    }
    private StandardCategoryDataSourceCodeDTO buildDTO(Long standardMatchId,List<ThirdMatchInternalCode> thirdMatchInternalCodes){
        StandardCategoryDataSourceCodeDTO standardCategoryDataSourceCodeDTO = new StandardCategoryDataSourceCodeDTO();
        standardCategoryDataSourceCodeDTO.setStandardMatchSourceId(standardMatchId);
        standardCategoryDataSourceCodeDTO.setThirdMatchInternalCodeList(thirdMatchInternalCodes);
        return standardCategoryDataSourceCodeDTO;
    }

    private Request<List<CategoryDataSourceCodeDTO>> convertCategoryCode(String linkId , ThirdMatchInfo thirdMatchInfo,Map<String,String> map ,Integer marketType) {
        List<CategoryDataSourceCodeDTO> categoryDataSourceCodeDTOList = new ArrayList<>();
        if (map != null && !map.isEmpty()){
            map.forEach((key,value)->{
                CategoryDataSourceCodeDTO categoryDataSourceCodeDTO = new CategoryDataSourceCodeDTO();
                categoryDataSourceCodeDTO.setInternalDataSourceCode(value);
                categoryDataSourceCodeDTO.setThirdCategoryId(key);
                categoryDataSourceCodeDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
                categoryDataSourceCodeDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                categoryDataSourceCodeDTO.setMarketType(marketType);
                categoryDataSourceCodeDTOList.add(categoryDataSourceCodeDTO);
            });
        }
        Request<List<CategoryDataSourceCodeDTO>> categoryCodeRequest = new Request<>();
        categoryCodeRequest.setLinkId(linkId);
        categoryCodeRequest.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        categoryCodeRequest.setData(categoryDataSourceCodeDTOList);
        return categoryCodeRequest;
    }
}
