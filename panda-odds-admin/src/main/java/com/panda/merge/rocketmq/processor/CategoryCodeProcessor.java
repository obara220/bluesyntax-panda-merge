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
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import springfox.documentation.spring.web.json.Json;

import javax.validation.Valid;
import java.util.ArrayList;
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
        log.info("::{}::processToApi 入参 thirdMatchId:{},详细信息：{}", linkId, thirdMatchInfo.getThirdMatchSourceId(), JSONUtil.toJsonStr(map));
        //标准玩法转三方玩法
        List<String> dataSourceReferences = null;
        if (thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.LS.getCode())){
            dataSourceReferences = map.keySet().stream().map(thi->DataSourceCodeEnum.LS.getCode()+"-"+thi).collect(Collectors.toList());
        }else if (thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.TX.getCode())){
            dataSourceReferences = map.keySet().stream().map(thi->DataSourceCodeEnum.TX.getCode()+"-"+thi).collect(Collectors.toList());
        }else if (thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.L02.getCode())){
            dataSourceReferences = map.keySet().stream().map(thi->DataSourceCodeEnum.L02.getCode()+"-"+thi).collect(Collectors.toList());
        }
        if (dataSourceReferences == null || dataSourceReferences.isEmpty()){
            log.info("::{}::processToApi 未找到赛事内部编码，请检查赛事ID是否正确 dataSourceReferences==null,thirdMatchId:{}", linkId,thirdMatchInfo.getThirdMatchSourceId());
            return;
        }
        List<ThirdMarketCategory> categoryList = thirdMarketCategoryService.getItemsByDataSourceAndReferenceIds(dataSourceReferences);
        if (CollectionUtils.isEmpty(categoryList)){
            log.info("::{}::processToApi 未找到赛事内部编码，请检查赛事ID是否正确 categoryList==null,thirdMatchId:{}", linkId,thirdMatchInfo.getThirdMatchSourceId());
            return;
        }
        //转三方玩法
        Map<String,String> thirdCategoryMap = categoryList.stream().collect(Collectors.toMap(thi->thi.getThirdSourceId(),thi->map.get(thi.getReferenceId()),(oldValue,newValue)->newValue));
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
