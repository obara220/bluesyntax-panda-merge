package com.panda.merge.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.panda.merge.bo.I18nItemBO;
import com.panda.merge.bo.MarketCategorySellBO;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolMonitor;
import com.panda.merge.constant.CategoryOppositeConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.exception.ApiException;
import com.panda.merge.exception.Asserts;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.constant.ConstantSystem.*;
/**
 * 数据处理公共方法
 * @author  tell
 * @since   2020年9月3日14:17:58
 * */

@Slf4j
@Component
public class BaseProcessor {

    @Autowired
    public RedisService redisService;
    @Autowired
    public LanguageInternationService languageInternationService;
    @Autowired
    public ThirdSportRegionService thirdSportRegionService;
    @Autowired
    public SystemItemDictService systemItemDictService;
    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
    @Autowired
    public DataSourceService dataSourceService;
    @Autowired
    public LanguageTypeService languageTypeService;
    @Autowired
    private MarketCategorySellService marketCategorySellService;
    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;
    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;
    @Autowired
    private ConfigMarketHeadGapService headGapService;
    @Autowired
    public ConfigMarketMarginGapService configMarketMarginGapService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService sportMarketSellService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private ConfigMarketOddsStatusService configMarketOddsStatusService;
    @Autowired
    private ConfigCashOutTradeItemService configCashOutTradeItemService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    /**
     * 简单判断请求参数格式是否正确，主要判断linkId和Data不能为空和条数限制
     * @param  request      请求参数
     * @param  key          缓存标识
     * @param  dataMaxSize  请求参数中data最大条数，可为空
     * */
    public <T> void simpleValidateParam(Request<List<T>> request, String key, Integer dataMaxSize) {
        if (Strings.isNullOrEmpty(request.getLinkId())) {
            throw new ApiException(PandaErrorCodeEnum.REQUEST_NO_LINKID.getErrorMsg());
        }
        //校验LinkId和缓存中是否重复
        validateLinkId(key,request);
        if (CollectionUtils.isEmpty(request.getData())) {
            throw new ApiException(PandaErrorCodeEnum.REQUEST_NO_DATA.getErrorMsg());
        }
        //超过n条数据，拒绝处理
        if (!Objects.isNull(dataMaxSize) && request.getData().size() > dataMaxSize) {
            throw new ApiException(PandaErrorCodeEnum.REQUEST_DATA_LIMIT.getErrorMsg().replace("maxSize",dataMaxSize+""));
        }
    }
    public boolean supportA99(String linkId,Long matchId,Integer marketType,Long categoryId){
        String key = marketType==1?Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS:Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS;
        Map<String, Object> map = redisService.hGetAll(key);
        Set<String> matchSet = map.keySet();
        Set<Long> set = matchSet.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        if (!set.contains(matchId)){
            return false;
        }
        Object categoryStrs = map.get(matchId.toString());

        if (categoryStrs==null){
            return false;
        }
        String[] categoryArrs = categoryStrs.toString().split(",");

        if (categoryArrs==null||categoryArrs.length==0){
            return false;
        }
        for (String cat : categoryArrs){
            if (MarginCategoryConfig.A99_category.containsKey(cat) && MarginCategoryConfig.A99_category.get(cat).contains(categoryId)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断请求数据源种类是否为空和超长
     * @param  dataSourceCodes      请求参数中数据源种类列表
     * */
    public <T> DataSource simpleValidateDataSourceCodes(Request<List<T>> request,Set<String> dataSourceCodes) {
        if(CollectionUtils.isEmpty(dataSourceCodes)){
            throw new ApiException(PandaErrorCodeEnum.DATASOURCE_IS_NOTNULL.getErrorMsg());
        }
        if(dataSourceCodes.size() > ONE){
            throw new ApiException(PandaErrorCodeEnum.DATASOURCE_LIMIT.getErrorMsg().replace("maxSize",String.valueOf(ONE)));
        }
        //当前数据源类型
        String dataSourceCode = dataSourceCodes.iterator().next();
        request.setDataSourceCode(dataSourceCode);
        DataSource dataSource = dataSourceService.getItemByCode(request.getDataSourceCode());
        if(null == dataSource){
            throw new ApiException(PandaErrorCodeEnum.DATASOURCE_NO_CHECK.getErrorMsg().replace("dataSourceCode",dataSourceCode));
        }
        return dataSource;
    }

    /**
     * 判断请求数据源种类是否为空和超长
     * @param  dataSourceCode      请求参数中数据源种类
     * */
    public DataSource simpleValidateDataSourceCode(Request request,String dataSourceCode) {
        if(StringUtils.isBlank(dataSourceCode)){
            throw new ApiException(PandaErrorCodeEnum.DATASOURCE_IS_NOTNULL.getErrorMsg());
        }
        request.setDataSourceCode(dataSourceCode);
        DataSource dataSource = dataSourceService.getItemByCode(request.getDataSourceCode());
        if(null == dataSource){
            throw new ApiException(PandaErrorCodeEnum.DATASOURCE_NO_CHECK.getErrorMsg().replace("dataSourceCode",dataSourceCode));
        }
        return dataSource;
    }

    /**
     * 检查是否还是在处理历史数据
     * false：历史开售的数据，true：新开售的数据
     * @param standardMatchId
     * @param marketType
     * @return
     */
    public boolean checkHistoryData(Long standardMatchId,Integer marketType)
    {
        String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchId+"_"+marketType;
        Map<String,String> stringHashMap = redisService.hGetAll(categoryRedisKey);
        return MapUtil.isEmpty(stringHashMap);
    }

    /**
     * 校验三方数据源运动类型是否合法并返回三方运动类型和标准运动类型关联
     * @param  dataSourceCode       数据来源
     * @param  thirdSportIds       运动类型列表
     * @return Map<String, Long>   三方运动类型和标准运动类型关系
     * */
    public Map<String, Long> validateSportIds(String dataSourceCode, Set<String> thirdSportIds){
        if(CollectionUtils.isEmpty(thirdSportIds)){
            throw new ApiException(PandaErrorCodeEnum.SPORT_ID_IS_NOTNULL.getErrorMsg());
        }
        //本次传入的三方运动类型转换为标准运动类型列表
        Map<String, Long> resMap = new LinkedHashMap<>();
        //校验SportId合法性
        for(String thirdSportId : thirdSportIds) {
            resMap.put(thirdSportId,validateSportId(dataSourceCode,thirdSportId));
        }
        return resMap;
    }

    /**
     * 校验三方运动类型是否合法 并返回标准运动类型
     * @param  dataSourceCode       数据来源
     * @param  thirdSportId       运动类型
     * @return Map<String, Long>   三方运动类型和标准运动类型关系
     * */
    public Long validateSportId(String dataSourceCode, String thirdSportId){
        if(StringUtils.isBlank(thirdSportId)){
            throw new ApiException(PandaErrorCodeEnum.SPORT_ID_IS_NOTNULL.getErrorMsg());
        }
        //无需校验运动类型的数据源
        if(DataSourceCodeEnum.getCodeList().contains(dataSourceCode)){
            return Long.valueOf(thirdSportId);
        }
        //根据数据源获取数据源下三方数据源运动类型和运动类型对应关系
        Map<String, ThirdSportType> thirdSportId2Item = thirdSportTypeService.getThirdSportId2Item(dataSourceCode);
        if(CollectionUtils.isEmpty(thirdSportId2Item)){
            DataSource dataSource = dataSourceService.getItemByCode(dataSourceCode);
            //如果是商业数据源，必须配置赛种对应关系
            if(ONE.equals(dataSource.getCommerce())){
                throw new ApiException(dataSourceCode+"运动种类为空，请配置运动种类信息！");
            }
            //如果是非商业数据源，数据源赛种和标准赛种默认相同
            return Long.valueOf(thirdSportId);
        }else{
            ThirdSportType thirdSportType = thirdSportId2Item.get(thirdSportId);
            if(Objects.isNull(thirdSportType)){
                throw new ApiException(PandaErrorCodeEnum.SPORT_ID_ILLEGAL.getErrorMsg().replace("sportId",thirdSportId));
            }
            return thirdSportType.getReferenceId();
        }
    }

    /**
     * 入参多语言国际化入参校验并返回需要的国际化语言
     * @param   i18nItems  多语言列表
     * @param   resLogo    返回标识（需要返回什么类型语言）
     * */
    public I18nItemDTO validateI18nItemDTOs(List<I18nItemDTO> i18nItems, String resLogo) {
        if(CollectionUtils.isEmpty(i18nItems)){
            Asserts.validateListForEmpty(i18nItems,PandaErrorCodeEnum.I18NS_IS_NOTNULL.getErrorMsg());
        }
        for (I18nItemDTO i18nItemDTO: i18nItems) {
            //校验国际化语言类型、文字内容
            Asserts.validateStringForEmpty(i18nItemDTO.getLanguageType(), PandaErrorCodeEnum.I18N_LANGUAGE_TYPE.getErrorMsg());
            Asserts.validateStringForEmpty(i18nItemDTO.getText(), PandaErrorCodeEnum.I18N_TEXT.getErrorMsg());
        }
        //语言类型和对象关系
        Map<String, I18nItemDTO> languageType2Obj = i18nItems.stream().collect(Collectors.toMap(thi -> thi.getLanguageType().toLowerCase(), thi -> thi, (oldValue, newValue) -> newValue));
        //英文国际化（不能为空）
        I18nItemDTO enI18nItemDTO = languageType2Obj.get(LanguageTypeEnum.en.name());
        Asserts.validateObjectForEmpty(enI18nItemDTO, PandaErrorCodeEnum.I18N_EN.getErrorMsg());
        //英文不能为空，中文国际化(可为空)
        I18nItemDTO resI18nItemDTO = languageType2Obj.get(resLogo);
        return Objects.isNull(resI18nItemDTO) ? enI18nItemDTO : resI18nItemDTO;
    }


    /**
     * 将i18nItemDTOList转换为语言类型和内容的关系
     * @param i18nItemDTOList
     * @return
     */
    public Map<String,String> getLanguageType2Text(List<I18nItemDTO> i18nItemDTOList) {
        Map<String, I18nItemDTO> languageType2Obj = i18nItemDTOList.stream().collect(Collectors.toMap(I18nItemDTO::getLanguageType, i -> i, (oldValue, newValue) -> newValue));
        //获取全部多语言类型
        List<LanguageType> languageTypeList = languageTypeService.getLanguageTypeList();
        Map<String,String> languageType2Text  = new LinkedHashMap<>(16);
        for (LanguageType language : languageTypeList) {
            String text = "";
            I18nItemDTO i18nItemDTO1 = languageType2Obj.get(language.getLanguageType());
            if (null != i18nItemDTO1) {
                text = i18nItemDTO1.getText();
            }
            languageType2Text.put(language.getLanguageType(),text);
        }
        return languageType2Text;
    }

    /**
     * 将i18nItemDTOList转换为语言类型和内容的关系，并转换为JSON字符串
     * @param i18nItemDTOList
     * @return
     */
    public String getLanguageType2Text2Json(List<I18nItemDTO> i18nItemDTOList) {
        return JSON.toJSONString(getLanguageType2Text(i18nItemDTOList));
    }

    /**
     * 获取商业数据源或非商业数据源code列表
     * @param commerce 是否是商业来源的数据. 1: 商业来源;0:非商业
     * */
    public List<String> getDataSourceCodes(Integer commerce){
        return dataSourceService.getItemList().stream().filter(obj->obj.getCommerce().equals(commerce)).map(obj -> obj.getCode()).collect(Collectors.toList());
    }

    /**
     * 根据唯一键(数据源+运动类型+三方数据源区域ID)获取三方区域信息
     * @param dataSourceCode      数据来源
     * @param standardSportId     标准运动类型
     * @param thirdSportRegionId  三方数据源区域ID
     * */
    public ThirdSportRegion getThirdSportRegionByUniqueStr(String dataSourceCode,Long standardSportId,String thirdSportRegionId){
        Map<String, ThirdSportRegion> unique2Item = thirdSportRegionService.getUnique2ItemByDataSourceCode(dataSourceCode);
        if(CollectionUtils.isEmpty(unique2Item)){
            return null;
        }
        //拼接唯一标识
        String id = dataSourceCode+FIX+DataSourceCodeEnum.getRegionSportIdByCode(dataSourceCode,standardSportId)+FIX+thirdSportRegionId;
        return unique2Item.get(id);
    }

    /**
     * 获取并设值需要编辑的球员区域
     * @param upThirdRegionId2Obj  需要编辑的三方区域
     * @param dataSourceCode       数据来源
     * @param standardSportId      标准运动类型
     * @param thirdSportRegionId   三方数据源区域ID
     * @param sportRegionName      三方数据源区域名称
     * */
    public ThirdSportRegion getThirdSportRegion(Map<String, ThirdSportRegion> upThirdRegionId2Obj,String dataSourceCode,Long standardSportId,String thirdSportRegionId,String sportRegionName){
        //获取库中存在的区域
        ThirdSportRegion oldThirdSportRegion = getThirdSportRegionByUniqueStr(dataSourceCode, standardSportId, thirdSportRegionId);
        ThirdSportRegion thirdSportRegion = new ThirdSportRegion();
        //根据数据源和三方区域ID判断是否电子联盟
        boolean dzFlag = DataSourceCodeEnum.getContainDzMatchCodeList().contains(dataSourceCode) && THIRD_DZ_REGION_ID.equals(thirdSportRegionId);
        if(Objects.isNull(oldThirdSportRegion)){
            Long regionSportId = DataSourceCodeEnum.getRegionSportIdByCode(dataSourceCode, standardSportId);
            thirdSportRegion.setId(dataSourceCode+FIX+regionSportId+FIX+thirdSportRegionId);
            thirdSportRegion.setSportId(regionSportId);
            if(DataSourceCodeEnum.getOnlyDzMatchCodeList().contains(dataSourceCode) || dzFlag) {
                //默认电子联盟
                thirdSportRegion.setReferenceId(15L);
            }else{
                //默认世界
                thirdSportRegion.setReferenceId(1L);
            }
            thirdSportRegion.setThirdRegionId(thirdSportRegionId);
            thirdSportRegion.setDataSourceCode(dataSourceCode);
            thirdSportRegion.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }else{
            BeanUtils.copyProperties(oldThirdSportRegion, thirdSportRegion);
            if(DataSourceCodeEnum.getOnlyDzMatchCodeList().contains(dataSourceCode) || dzFlag) {
                //默认电子联盟
                thirdSportRegion.setReferenceId(15L);
            }
            thirdSportRegion.setCreateTime(null);
        }
        thirdSportRegion.setIntroduction(sportRegionName);
        thirdSportRegion.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        if(StringUtils.isNotBlank(thirdSportRegionId) && StringUtils.isNotBlank(sportRegionName)){
            if(!EntityEqualsUtils.equalsIsObjToString(thirdSportRegion,oldThirdSportRegion)){
                upThirdRegionId2Obj.put(thirdSportRegion.getSportId()+FIX+thirdSportRegionId,thirdSportRegion);
            }
        }
        return thirdSportRegion;
    }

    /**
     * 筛选出需要新增或入库的多语言列表并返回NameCode
     * @param languageInternationList  需要新增或修改的多语言列表
     * @param i18nList                 本次传入的单个nameCode多语言列表
     * @param dataSource               数据来源对象
     * @param oldNameCode              当前多语言nameCode
     */
    public Long processLanguageNameCode(List<LanguageInternation> languageInternationList, List<I18nItemDTO> i18nList,
                                        DataSource dataSource, Long oldNameCode,String linkId, boolean pushThirdTeamTournmaentUpdate) {
        //本次传入多语言信息
        Map<String, I18nItemDTO> languageType2I18nItemDTO = i18nList.stream().collect(Collectors.toMap(I18nItemDTO::getLanguageType, thi -> thi));
        //库中单个nameCode的多语言类型和对象的关联
        Map<String, LanguageInternation> oldLanguageType2Obj = new LinkedHashMap<>();
        //如果nameCode为空或为0，需要重新生成nameCode
        if(Objects.isNull(oldNameCode) || oldNameCode == 0) {
            oldNameCode = UUIdUtils.getId();
        }else{
            oldLanguageType2Obj = languageInternationService.getLanguageType2Item(dataSource.getCode(), oldNameCode);
        }
        //获取全部多语言类型
        List<LanguageType> languageTypeList = languageTypeService.getLanguageTypeList();
        for (LanguageType language : languageTypeList) {
            //库中多语言
            LanguageInternation oldLanguageInternation = oldLanguageType2Obj.get(language.getLanguageType());
            //本次传入多语言
            I18nItemDTO i18nItemDTO = languageType2I18nItemDTO.get(language.getLanguageType());
            if(null == oldLanguageInternation && null == i18nItemDTO){
                continue;
            }
            LanguageInternation languageInternation = new LanguageInternation();
            if(null != i18nItemDTO){
                BeanUtils.copyProperties(i18nItemDTO, languageInternation);
                if(StringUtils.isBlank(languageInternation.getText())){
                    continue;
                }
            }else{
                if(StringUtils.isBlank(oldLanguageInternation.getText())){
                    languageInternationService.delItem(oldLanguageInternation,linkId);
                    continue;
                }
                //上次有该类型多语言，本次该类型多语言已经不存在，覆盖库中的多语言名称（或者删除）
                BeanUtils.copyProperties(oldLanguageInternation, languageInternation);
                languageInternation.setText("");
            }
            setLanguageInternation(oldLanguageInternation,languageInternation,oldNameCode,dataSource.getCode());

            if(!EntityEqualsUtils.equalsIsObjToString(languageInternation,oldLanguageInternation)){
                languageInternation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                languageInternationList.add(languageInternation);
                if(null != oldLanguageInternation && pushThirdTeamTournmaentUpdate){
                    log.info("linkId=【{}】,国际化变更前信息:{},国际化变更后信息:{}", JSON.toJSONString(oldLanguageInternation),JSON.toJSONString(languageInternation), linkId);
                    redisService.set("THIRD_TEAM_TOURNAMENT_UPDATE:"+oldNameCode, oldNameCode, 5);
                }
            }
        }
        return oldNameCode;
    }

    /**
     * 筛选出需要新增或修改的多语言列表并返回NameCode
     * @param oldLanguageType2Obj      库中单个nameCode的多语言类型和对象的关联
     * @param i18nList                 本次传入的单个nameCode多语言列表
     * @param dataSource               数据来源对象
     * @param oldNameCode              当前多语言nameCode
     */
    public Map<String, LanguageInternation> processLanguageNameCode(Map<String, LanguageInternation> oldLanguageType2Obj, List<I18nItemDTO> i18nList, DataSource dataSource, Long oldNameCode,String linkId) {
        Map<String, LanguageInternation> upLanguageType2Obj = new LinkedHashMap<>();
        //本次传入多语言信息
        Map<String, I18nItemDTO> languageType2I18nItemDTO = i18nList.stream().collect(Collectors.toMap(I18nItemDTO::getLanguageType, thi -> thi));
        //获取全部多语言类型
        List<LanguageType> languageTypeList = languageTypeService.getLanguageTypeList();
        for (LanguageType language : languageTypeList) {
            //库中多语言
            LanguageInternation oldLanguageInternation = oldLanguageType2Obj.get(language.getLanguageType());
            //本次传入多语言
            I18nItemDTO i18nItemDTO = languageType2I18nItemDTO.get(language.getLanguageType());
            if(null == oldLanguageInternation && null == i18nItemDTO){
//                log.info("linkId:{},传入多语言和数据库中多语言为空,跳过不处理!nameCode:{},dataSourceCode:{},LanguageType:{}",linkId,oldNameCode,dataSource.getCode(),language.getLanguageType());
                continue;
            }
            LanguageInternation languageInternation = new LanguageInternation();
            if(null != i18nItemDTO){
                BeanUtils.copyProperties(i18nItemDTO, languageInternation);
                if(StringUtils.isBlank(languageInternation.getText())){
                    log.info("linkId:{},传入text信息为空!nameCode:{},dataSourceCode:{},LanguageType:{}",linkId,oldNameCode,dataSource.getCode(),language.getLanguageType());
                    continue;
                }
            }else{
                if(null != oldLanguageInternation && StringUtils.isBlank(oldLanguageInternation.getText())){
                    languageInternationService.delItem(oldLanguageInternation,linkId);
                    oldLanguageType2Obj.remove(language.getLanguageType());
                    log.info("linkId:{},库中多语言text信息为空,需要删除!nameCode:{},dataSourceCode:{},LanguageType:{}",linkId,oldNameCode,dataSource.getCode(),language.getLanguageType());
                }
                //上次有该类型多语言，本次该类型多语言已经不存在，调整为不处理该数据,需要人工去编辑或者刷脚本
//                BeanUtils.copyProperties(oldLanguageInternation, languageInternation);
//                languageInternation.setText("");
                continue;
            }
            setLanguageInternation(oldLanguageInternation,languageInternation,oldNameCode,dataSource.getCode());

            //如果和数据库数据一致 标记无需修改，修改时间设置为空
            if(EntityEqualsUtils.equalsIsObjToString(languageInternation,oldLanguageInternation)){
                languageInternation.setModifyTime(null);
            }else{
                languageInternation.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                if(null != oldLanguageInternation){
                    log.info("linkId=【{}】,国际化变更前信息:{},国际化变更后信息:{}", JSON.toJSONString(oldLanguageInternation),JSON.toJSONString(languageInternation), linkId);
                    redisService.set("THIRD_TEAM_TOURNAMENT_UPDATE:"+oldNameCode, oldNameCode, 5);
                }
            }
            upLanguageType2Obj.put(language.getLanguageType(),languageInternation);
        }
        return upLanguageType2Obj;
    }

    /**
     *为多语言对象设置属性
     * @param oldLanguageInternation     库中多语言信息
     * @param languageInternation        需要修改的多语言信息
     * @param oldNameCode
     * @param dataSourceCode
     * */
    private void setLanguageInternation(LanguageInternation oldLanguageInternation,LanguageInternation languageInternation,Long oldNameCode,String dataSourceCode){
        // 不存在，insert
        if (Objects.isNull(oldLanguageInternation)) {
            languageInternation.setId(UUIdUtils.getId());
            languageInternation.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        }else{
            languageInternation.setCreateTime(null);
            languageInternation.setFlag(oldLanguageInternation.getFlag());
            languageInternation.setId(oldLanguageInternation.getId());
        }
        languageInternation.setNameCode(oldNameCode);
        languageInternation.setDataSourceCode(dataSourceCode);
    }

    /**
     * 转换多语言列表
     * */
    public<T> List<I18nItemBO> getI18nItemBOList(List<T> languageList){
        List<I18nItemBO> i18nList = new LinkedList<>();
        if(!CollectionUtils.isEmpty(languageList)){
            for (T item: languageList) {
                I18nItemBO i18nItemBO = new I18nItemBO();
                BeanUtils.copyProperties(item, i18nItemBO);
                i18nList.add(i18nItemBO);
            }
        }
        return i18nList;
    }


    /**
     * 转换多语言列表
     * */
    public<T> List<I18nItemBO> getI18nItemBOList(JSONObject languages){
        List<I18nItemBO> i18nList = new LinkedList<>();
        if(!CollectionUtils.isEmpty(languages)){
            for (String languageType: languages.keySet()) {
                I18nItemBO i18nItemBO = new I18nItemBO();
                i18nItemBO.setLanguageType(languageType);
                i18nItemBO.setText(languages.getString(languageType));
                i18nList.add(i18nItemBO);
            }
        }
        return i18nList;
    }


    /**
     * 获取(中文 & 英文)名称国际化数据
     * @param  name             中文名称
     * @param  engName          英文名称
     * @return list             组装后国际化数据列表
     */
    public List<I18nItemDTO> getI18nItemDTOList(String name, String engName){
        I18nItemDTO zsI18nItemDTO = getI18nItemDTO(LanguageTypeEnum.zs, name);
        I18nItemDTO enI18nItemDTO = getI18nItemDTO(LanguageTypeEnum.en, engName);
        return Lists.newArrayList(zsI18nItemDTO,enI18nItemDTO);
    }

    /**
     * 创建指定语言国际化对象
     * @param languageType   语言类型
     * @param name          名称
     * @return
     */
    public I18nItemDTO getI18nItemDTO(LanguageTypeEnum languageType, String name) {
        I18nItemDTO i18nItemDTO = new I18nItemDTO();
        i18nItemDTO.setLanguageType(languageType.name());
        i18nItemDTO.setText(name);
        return i18nItemDTO;
    }


    /**
     * 根据配置的几阶段，获取该玩法的准确关盘时间
     * @param marketCategorySell
     * @param sportId
     * @return
     */
    private Integer getCloseTimeByMarketCategorySellRec(MarketCategorySellBO marketCategorySell,Long sportId){
        //篮球直接返回配置中该节的剩余时间，在掉用此方法前已经判断过阶段，因此不需要再计算已经过去的阶段时间
        if(StandardSportTypeEnum.Basketball.code.equals(sportId)){
            return marketCategorySell.getMatchProgressTime();
        }
        //开始处理足球
        Integer closeTime = 0;
        if(marketCategorySell.getInjuryTime() != null && marketCategorySell.getInjuryTime() != 0){
            List<Long> matchPeriod =  SportPeriodWholeEnum.getSprotPeriodBySportId(sportId).getPeriods();
            int index = matchPeriod.indexOf(Long.valueOf(marketCategorySell.getAutoCloseMarket().toString()));
            index = index > 3 ? 3 : index;
            closeTime += periodTimeIntegerValue.get(index);
            closeTime += marketCategorySell.getInjuryTime();
        }else{
            closeTime += marketCategorySell.getMatchProgressTime();
        }
        return closeTime;
    }

    /**
     * 区分运动类型的玩法自动关盘业务处理（足球事件为正计时）
     *
     * @param linkId
     * @param secondsFromStart  已进行时长
     * @param standardMatchInfo 标准赛事
     * @return sourceType 事件类型
     */
    public Set<Long> getAutoCloseMarketDisposeBySportId(String linkId, Long secondsFromStart, StandardMatchInfo standardMatchInfo, Long nowPeriod, String sourceType) {
        Long sportId = standardMatchInfo.getSportId();
        // 根据下一阶段查询上一个阶段需要关闭的玩法,足球
        Long finallyPeriodId = null;
        boolean needClose = false;
        if (StandardSportTypeEnum.FootBall.code.equals(sportId)) {
            switch (String.valueOf(nowPeriod)) {
                case "31":
                    needClose = true;
                    finallyPeriodId = 6L;
                    break;
                case "100":
                    needClose = true;
                    finallyPeriodId = 7L;
                    break;
                case "33":
                    needClose = true;
                    finallyPeriodId = 41L;
                    break;
                case "110":
                    needClose = true;
                    finallyPeriodId = 42L;
                    break;
                case "120":
                    needClose = true;
                    finallyPeriodId = 50L;
                    break;
                default:
                    finallyPeriodId = nowPeriod;
            }
        }
        // 根据下一阶段查询上一个阶段需要关闭的玩法，篮球
        if (StandardSportTypeEnum.Basketball.code.equals(sportId)) {
            switch (String.valueOf(nowPeriod)) {
                case "301":
                    needClose = true;
                    finallyPeriodId = 13L;
                    break;
                case "302":
                    needClose = true;
                    finallyPeriodId = 14L;
                    break;
                case "31":
                    needClose = true;
                    finallyPeriodId = 1L;
                    break;
                case "303":
                    needClose = true;
                    finallyPeriodId = 15L;
                    break;
                case "100":
                    needClose = true;
                    //NCAA篮球赛事的下半场结束的赛事阶段也为100,故在此判断赛事是否为NCAA赛事，matchLength为“17” 则表示NCAA赛事
                    finallyPeriodId = standardMatchInfo.getMatchLength() == 17 ? 2L : 16L;
                    break;
                case "110":
                    needClose = true;
                    finallyPeriodId = 40L;
                    break;
                default:
                    finallyPeriodId = nowPeriod;
            }
        }
        //查询玩法开售 根据下一阶段查询上一个阶段需要关闭的玩法 （足球，篮球）
        List<MarketCategorySellBO> marketCategorySells = marketCategorySellService.getItemByPrimaryCache(standardMatchInfo.getId(), finallyPeriodId);
        log.info("::{}::getAutoCloseMarketDisposeBySportId,兜底阶段:{},查询玩法开售:{}", linkId, finallyPeriodId, marketCategorySells.size());
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            return null;
        }
        //如果此时needClose 为true 则表示，当前事件的阶段为赛事的休息或结束阶段，直接将上一个结束的阶段的玩法关盘
        if (needClose) {
            List<MarketCategorySell> marketCategorySellsDB = marketCategorySellService.getItemByPrimary(standardMatchInfo.getId(), finallyPeriodId);
            if (CollectionUtils.isEmpty(marketCategorySellsDB)) {
                return null;
            }
            Set<Long> collect = marketCategorySellsDB.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
            log.info("::{}::getAutoCloseMarketDisposeBySportId,兜底阶段:{},关闭玩法:{}", linkId, nowPeriod, collect);
            return collect;
        }
        //UOF正常阶段不做处理 只处理兜底
        if ("0".equals(sourceType)) {
            log.info("::{}::UOF正常阶段不做处理，阶段：{},标准赛事ID：{}", linkId, nowPeriod, standardMatchInfo.getId());
            return null;
        }
        Set<Long> marketCategoryIds = new HashSet<>();
        for (MarketCategorySellBO categorySell : marketCategorySells) {
            if (categorySell.getMatchProgressTime() == null) {
                continue;
            }
            //如果是足球，足球事件为正计时,篮球判断当前比赛进行时间小于关盘时间
            Integer closeTime = getCloseTimeByMarketCategorySellRec(categorySell, sportId);
            //若此时  needClose 为false,表示赛事正处在赛事进行阶段，则判断当前的事件
            needClose = StandardSportTypeEnum.FootBall.code.equals(sportId) ? secondsFromStart >= closeTime :
                    Integer.compare(categorySell.getAutoCloseMarket(), Integer.valueOf(standardMatchInfo.getMatchPeriodId().toString())) == 0 ? secondsFromStart <= closeTime : Boolean.FALSE;
            if (needClose) {
                marketCategoryIds.add(categorySell.getMarketCategoryId());
            }
        }
        return marketCategoryIds;
    }

    /**
     * 子玩法区分运动类型的玩法自动关盘业务处理（足球事件为正计时）
     *
     * @param linkId
     * @param secondsFromStart  已进行时长
     * @param standardMatchInfo 标准赛事
     * @return sourceType 事件类型
     */
    public Pair<Set<Long>, Map<String, JSONObject>> getAutoCloseChildMarketCategoryDisposeBySportId(String linkId, Long secondsFromStart, StandardMatchInfo standardMatchInfo, Long nowPeriod, String sourceType) {
        Long sportId = standardMatchInfo.getSportId();
        if (!StandardSportTypeEnum.Basketball.code.equals(sportId)) {
            return null;
        }
        // 根据下一阶段查询上一个阶段需要关闭的玩法
        Long finallyPeriodId = null;
        boolean needClose = false;
        // 根据下一阶段查询上一个阶段需要关闭的玩法，篮球
        switch (String.valueOf(nowPeriod)) {
            case "301":
                needClose = true;
                finallyPeriodId = 13L;
                break;
            case "302":
                needClose = true;
                finallyPeriodId = 14L;
                break;
            case "31":
                needClose = true;
                finallyPeriodId = 1L;
                break;
            case "303":
                needClose = true;
                finallyPeriodId = 15L;
                break;
            case "100":
                needClose = true;
                //NCAA篮球赛事的下半场结束的赛事阶段也为100,故在此判断赛事是否为NCAA赛事，matchLength为“17” 则表示NCAA赛事
                finallyPeriodId = standardMatchInfo.getMatchLength() == 17 ? 2L : 16L;
                break;
            case "110":
                needClose = true;
                finallyPeriodId = 40L;
                break;
            default:
                finallyPeriodId = nowPeriod;
        }

        //赛事 + 阶段
        String key = Constant.REDIS_KEY.MATCH_EVENT_MARKET_X_CLOSE + standardMatchInfo.getId() + "_" + finallyPeriodId;
        Map<String, JSONObject> closeMap = redisService.hGetAll(key);
        log.info("::{}::getAutoCloseChildMarketCategoryDisposeBySportId,兜底阶段:{},缓存子玩法配置结果:{}", linkId, nowPeriod, closeMap);
        if (closeMap.isEmpty()) {
            return null;
        }
        //如果此时needClose 为true 则表示，当前事件的阶段为赛事的休息或结束阶段，直接将上一个结束的阶段的玩法关盘
        if (needClose) {
            List<JSONObject> collect = new ArrayList<JSONObject>(closeMap.values());
            Set<Long> marketCategoryIds = collect.stream().map(e -> ((JSONObject) e).getLong("marketCategoryId")).collect(Collectors.toSet());
            log.info("::{}::getAutoCloseChildMarketCategoryDisposeBySportId,兜底阶段:{},关闭玩法:{}", linkId, nowPeriod, collect);
            return Pair.of(marketCategoryIds, closeMap);
        }
        //UOF正常阶段不做处理 只处理兜底
        if ("0".equals(sourceType)) {
            log.info("::{}::getAutoCloseChildMarketCategoryDisposeBySportId，UOF正常阶段不做处理，阶段：{},标准赛事ID：{}", linkId, nowPeriod, standardMatchInfo.getId());
            return null;
        }
        Set<Long> marketCategoryIds = new HashSet<>();
        Map<String, JSONObject> collectMap = new HashMap<>();
        for (String json : closeMap.keySet()) {
            JSONObject closeJson = closeMap.get(json);
            if (closeJson.getInteger("matchProgressTime") == null) {
                continue;
            }
            //若此时  needClose 为false,表示赛事正处在赛事进行阶段，则判断当前的事件
            needClose = secondsFromStart <= closeJson.getInteger("matchProgressTime");
            if (needClose) {
                marketCategoryIds.add(closeJson.getLong("marketCategoryId"));
                collectMap.put(closeJson.getString("marketCategoryId"), closeJson);
            }
        }
        return Pair.of(marketCategoryIds, collectMap);
    }

    /**
     * 篮球自动开盘
     * @param linkId
     * @param secondsFromStart 已进行时长
     * @param standardMatchInfo
     * @param nowPeriod
     */
    public Set<Long> autoOpenMarket(String linkId, Long secondsFromStart, StandardMatchInfo standardMatchInfo, Long nowPeriod) {
        Set<Long> marketCategoryIdSet = new HashSet();
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.code)) {
            return marketCategoryIdSet;
        }
        Long finallyPeriodId = nowPeriod;
        switch (String.valueOf(nowPeriod)) {
            case "301":
                finallyPeriodId = 13L;
                break;
            case "302":
                finallyPeriodId = 14L;
                break;
            case "31":
                finallyPeriodId = 1L;
                break;
            case "303":
                finallyPeriodId = 15L;
                break;
            case "100":
                //NCAA篮球赛事的下半场结束的赛事阶段也为100,故在此判断赛事是否为NCAA赛事，matchLength为“17” 则表示NCAA赛事
                finallyPeriodId = standardMatchInfo.getMatchLength() == 17 ? 2L : 16L;
                break;
            case "110":
                finallyPeriodId = 40L;
                break;
        }
        String key = Constant.REDIS_KEY.RONGHE_AUTO_OPEN_MARKET_CATEGORY + standardMatchInfo.getId();
        List<MarketCategorySellBO> marketCategorySells = marketCategorySellService.getItemByPrimaryOpenCache(standardMatchInfo.getId(), finallyPeriodId);
        if (!CollectionUtils.isEmpty(marketCategorySells)) {
            log.info("::{}::篮球自动开盘,阶段:{},进行时间:{},玩法开售信息:{}", linkId, nowPeriod+"_"+finallyPeriodId, secondsFromStart, JSONObject.toJSONString(marketCategorySells));
            for (MarketCategorySellBO categorySell : marketCategorySells) {
                if (null == categorySell.getAutoOpenTime()) {
                    continue;
                }
                //进行事件小于自动开盘时间 为开盘,大于关盘
                if (secondsFromStart <= categorySell.getAutoOpenTime()) {
                    Object o = redisService.hGet(key, categorySell.getMarketCategoryId().toString());
                    if (ObjectUtil.isNull(o) || !Boolean.parseBoolean(o.toString())) {
                        redisService.hSet(key, categorySell.getMarketCategoryId().toString(), Boolean.TRUE, marketCacheTime(standardMatchInfo.getBeginTime()));
                        marketCategoryIdSet.add(categorySell.getMarketCategoryId());
                    }
                } else {
                    redisService.hSet(key, categorySell.getMarketCategoryId().toString(), Boolean.FALSE, marketCacheTime(standardMatchInfo.getBeginTime()));
                }
            }
        }
        return marketCategoryIdSet;
    }

    /**
     * 需求1852兜底 阶段切换时，关闭上个阶段玩法的盘口
     * @param linkId
     * @param standardMatchInfo 标准赛事
     */
    public Set<Long> getAutoCloseBeforePeriodCategory(String linkId, StandardMatchInfo standardMatchInfo, Long nowPeriod){
        Long sportId = standardMatchInfo.getSportId();
        if (!StandardSportTypeEnum.FootBall.code.equals(sportId)) {
            log.info("::{}::getAutoCloseBeforePeriodCategory 赛种种类不匹配,兜底阶段:{},赛种种类:{}", linkId, nowPeriod, sportId);
            return null;
        }
        Long matchPeriodId = (Long) redisService.get(Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID+standardMatchInfo.getId());
        if(matchPeriodId == null || matchPeriodId != nowPeriod) {
            redisService.set(Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID+standardMatchInfo.getId(),nowPeriod,marketCacheTime(standardMatchInfo.getBeginTime()));
        }else {
            log.info("::{}::getAutoCloseBeforePeriodCategory 阶段没有变化,当前阶段:{},历史阶段:{}", linkId, nowPeriod, matchPeriodId);
            return null;
        }
        //查询当前阶段需要兜底关闭的玩法
        List<Long> categoryIds = MarginCategoryConfig.MATCH_PERIOD_CLOS_CATEGORY.get(nowPeriod);
        if (CollectionUtils.isEmpty(categoryIds)) {
            log.info("::{}::getAutoCloseBeforePeriodCategory 当前阶段没有兜底关盘的玩法,当前阶段:{}", linkId, nowPeriod);
            return null;
        }
        return new HashSet(categoryIds);
    }

    /**
     * 验证参数的合法性，缓存是否重复
     * @param serviceType    类型标识
     * @param request        请求参数
     */
    public void validateLinkId(String serviceType, Request request) {
        String key = RedisConfig.REDIS_KEY_LINKID + serviceType + FIX +request.getLinkId();
        if(!redisService.tryLockOnce(key,key,REDIS_FIVE_MINS_TIME)){
            throw new ApiException("参数linkID重复:" + request.getLinkId());
        }else{
            //类型标识存入请求中，方便在异常中释放锁
            request.setDataType(serviceType);
        }
    }

    /**
     * 盘口缓存时间
     * （比赛时间 - 系统时间） + 2天时间
     *
     * @param beginTime 比赛时间
     */
    public Long marketCacheTime(Long beginTime) {
        if (beginTime == null || beginTime == 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //获取剩余开赛时间 =  开赛时间-当前时间
        Long cacheTime = (beginTime - Calendar.getInstance().getTimeInMillis());
        if (cacheTime <= 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //redis过期时间为秒 = 剩余开赛时间 + 2天时间 ，为redis过期时间
        return (cacheTime / 1000) + (2L * RedisConfig.REDIS_DEFAULT_TIME);
    }

    /**
     * 清除盘口水差、玩法水差、坑位水差，篮球的话还有盘口差
     * 清理独赢配置 清概率差，水差
     * @param linkId
     * @param standardMatchId
     * @param categoryList
     * @param sportId
     */
    public void delDiffByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList, Integer sportId) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return;
        }
        if (StandardSportTypeEnum.FootBall.code.equals(sportId.longValue())) {
            ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchId, DataSourceCodeEnum.AO.code);
            if (null != aoMatchInfo) {
                sendClearAoDiffConfig(linkId, standardMatchId, aoMatchInfo.getThirdMatchSourceId(), categoryList);
            }
        }
        log.info("::{}::处理清除水差delDiffByMatchIdAndCategoryList,开始处理", linkId);
        CompletableFuture c1 = CompletableFuture.runAsync(() -> {
            configMarketAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c2 = CompletableFuture.runAsync(() -> {
            headGapService.delCacheByCategoryIdList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c3 = CompletableFuture.runAsync(() -> {
            configCategoryAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c4 = CompletableFuture.runAsync(() -> {
            configPlaceNumAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c6 = CompletableFuture.runAsync(() -> {
            List<Long> thanThreeCategoryIds = getMoreCategoryId(categoryList, true);
            if (!CollectionUtils.isEmpty(thanThreeCategoryIds)) {
                configMarketMarginGapService.upProbabilityByMatchIdAndCategoryIdList(linkId, standardMatchId, thanThreeCategoryIds);
            }
        });
        CompletableFuture c7 = CompletableFuture.runAsync(() -> {
            List<Long> otherCategoryIds = getMoreCategoryId(categoryList, false);
            if (!CollectionUtils.isEmpty(otherCategoryIds)) {
                configMarketMarginGapService.updateByMatchIdAndCategoryList(linkId, standardMatchId, otherCategoryIds);
            }
        });
        CompletableFuture.allOf(c1, c2, c3, c4, c6, c7);
        log.info("::{}::处理清除水差delDiffByMatchIdAndCategoryList,处理完成", linkId);
    }

    public List<Long> getMoreCategoryId(List<Long> categoryList, boolean isTrue) {
        //大于三项盘玩法 清除概率差 ,其他玩法 清除清概率差，水差
        List<Long> thanThreeCategoryIds = Collections.synchronizedList(new ArrayList());
        List<Long> otherCategoryIds = Collections.synchronizedList(new ArrayList());
        categoryList.forEach(categoryId -> {
            if (MarginCategoryConfig.THREE_CATEGORY.contains(categoryId)) {
                thanThreeCategoryIds.add(categoryId);
            } else {
                otherCategoryIds.add(categoryId);
            }
        });
        return isTrue ? thanThreeCategoryIds : otherCategoryIds;
    }

    /**
     * 滚球切换 清除盘口水差、玩法水差、坑位水差 ，清除盘口差
     * 清理独赢配置 清概率差，水差
     * @param linkId
     * @param standardMatchId
     * @param sportId
     */
    public void delDiffByMatchInfoId(String linkId, Long standardMatchId, Long sportId) {
        if (StandardSportTypeEnum.FootBall.code.equals(sportId)) {
            ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchId, DataSourceCodeEnum.AO.code);
            if (null != aoMatchInfo) {
                sendClearAoDiffConfig(linkId, standardMatchId, aoMatchInfo.getThirdMatchSourceId(), null);
            }
        }
        configMarketAutoDiffTradeService.delDiffByMatchInfoId(standardMatchId, linkId);
        headGapService.delCacheByStandardMatchInfoId(standardMatchId, linkId);
        configCategoryAutoDiffTradeService.delDiffByMatchInfoId(standardMatchId, linkId);
        configPlaceNumAutoDiffTradeService.delDiffByMatchInfoId(standardMatchId, linkId);
        configMarketMarginGapService.updateByMatchId(linkId, standardMatchId);
    }

    /**
     * TX统一盘口ID 为 sendData 其他数据源为  relationMarketId
     * 统一转为 relationMarketId
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @return
     */
    public Long convertRelationMarketId(String linkId, StandardMarketDataMessage standardMarketDataMessage) {
        Long relationMarketId = 0L;
        try {
            if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.code) && StringUtils.isNotBlank(standardMarketDataMessage.getSendData())) {
                relationMarketId = Long.valueOf(standardMarketDataMessage.getSendData());
            } else {
                relationMarketId = standardMarketDataMessage.getRelationMarketId();
            }

        } catch (Exception e) {
            relationMarketId = standardMarketDataMessage.getRelationMarketId();
            log.info("::{}::三方盘口数据源ID:{},TX统一盘口ID转换失败,", linkId, standardMarketDataMessage.getThirdMarketSourceId(), e);
        }
        return relationMarketId;
    }

    /**
     * 获取PA数据服务日志对象
     * @param linkId       线路ID
     * @param serviceType  服务类型
     * @param apiCode      接口编码
     * @param apiName      接口名称
     * @param consumeTime  消耗时间（毫秒）
     * @param errorCode    错误编码
     * @param message      描述
     * */
    public PaDataServiceLogDTO getPaDataServiceLogDTO(String linkId,String serviceType,String apiCode,String apiName,Long consumeTime,Integer errorCode,String message){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        PaDataServiceLogDTO item = new PaDataServiceLogDTO();
        item.setLinkId(linkId);
        item.setDate(Long.valueOf(sdf.format(new Date())));
        item.setServiceType(serviceType);
        item.setApiCode(apiCode);
        item.setApiName(apiName);
        item.setConsumeTime(consumeTime);
        item.setErrorCode(errorCode);
        item.setMessage(message);
        return item;
    }

//    public static void main(String[] args) {
//        processOddsValueDecimals(">>", 1900000);
//    }
    /**
     * margin原始赔率和概率赔率小数点处理
     *
     * @param linkId
     * @param oddsValue
     * @return
     */
    public static Integer processOddsValueDecimals(String linkId, Integer oddsValue) {
        Integer paOddsValue = 0;
        if (null == oddsValue || 0 == oddsValue) {
            return paOddsValue;
        }
        BigDecimal bigDecimal = new BigDecimal(oddsValue).divide(new BigDecimal(100000), 2, BigDecimal.ROUND_DOWN);
        int left = bigDecimal.intValue();
        int right = bigDecimal.subtract(new BigDecimal(left)).multiply(new BigDecimal(100)).intValue();
        if (left < 3){
            paOddsValue = oddsValue;
        }
        else if(left >=3 && left < 5)
        {
            if(right < 5){
                paOddsValue = bigDecimal.intValue() * 100000;
            }else{
                BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(5), 0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.05));
                paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
            }
        }
        else if(left >= 5 && left < 10)
        {
            if(right < 10){
                paOddsValue = bigDecimal.intValue() * 100000;
            }else{
                BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(100), 1, BigDecimal.ROUND_DOWN);
                paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
            }
        }
        else if(left >=10 && left < 20)
        {
            if(right < 50){
                paOddsValue = bigDecimal.intValue() * 100000;
            }else{
                paOddsValue = new BigDecimal(left).add(new BigDecimal(0.5)).multiply(new BigDecimal(100000)).intValue();
            }
        }
        else if(left >= 20)
        {
            paOddsValue = left * 100000;
        }
        log.info("::{}::赔率小数点处理,oddsValue:{},paOddsValue:{}",linkId,oddsValue,paOddsValue);
        return paOddsValue;
    }

    /**
     * 主客队对调（如果三方赛事主客队和标准赛事主客队相反，则事件中主客队相关数据需要对调位置，比如比分）
     * @param matchEventInfo  需要转换的事件
     * @param thirdMatchInfo  当前事件列表对应的三方赛事信息
     * */
    public MatchEventInfo matchHomeAwayExchange(MatchEventInfo matchEventInfo,ThirdMatchInfo thirdMatchInfo){
        return matchHomeAwayExchange(Lists.newArrayList(matchEventInfo),thirdMatchInfo).get(0);
    }

    /**
     * 主客队对调（如果三方赛事主客队和标准赛事主客队相反，则事件中主客队相关数据需要对调位置，比如比分）
     * @param matchEventInfos 需要转换的事件列表
     * @param thirdMatchInfo  当前事件列表对应的三方赛事信息
     * */
    public List<MatchEventInfo> matchHomeAwayExchange(List<MatchEventInfo> matchEventInfos,ThirdMatchInfo thirdMatchInfo){
        //目前只处理足球
        if(StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())){
            //如果主客队是相反
            if(ONE.equals(thirdMatchInfo.getHomeAwayOpposite())){
                LinkedList<MatchEventInfo> list = new LinkedList<>();
                for (MatchEventInfo matchEventInfo: matchEventInfos) {
                    MatchEventInfo item = new MatchEventInfo();
                    BeanUtil.copyProperties(matchEventInfo, item);
                    //主客队标识互换
                    item.setHomeAway(TeamTypeEnum.homeAwayExchange(item.getHomeAway()));
                    //主客队比分互换
                    Integer t1 = item.getT1() == null ? ZERO:item.getT1();
                    Integer t2 = item.getT2() == null ? ZERO:item.getT2();
                    if(!t1.equals(t2)){
                        item.setT1(t2);
                        item.setT2(t1);
                    }

                    // 109419
                    Integer firstT1 = item.getFirstT1() == null ? ZERO:item.getFirstT1();
                    Integer firstT2 = item.getFirstT2() == null ? ZERO: item.getFirstT2();
                    if (!firstT1.equals(firstT2)){
                        item.setFirstT1(firstT2);
                        item.setFirstT2(firstT1);
                    }

                    list.add(item);
                }
                return list;
            }
        }
        return matchEventInfos;
    }

    /**
     * 标准赛事开赛时间刷入缓存，到开赛时间下发滚球标识
     *
     * @param linkId
     * @param item
     */
    public void refreshStandardMatchBeginTimeByThirdMatchInfo(String linkId, ThirdMatchInfo item) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(item.getReferenceId());
        if (standardMatchInfo != null) {
            //只处理赛前
            Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId());
            //标准赛事是否已经下发过自动构建赔率key
            String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
            if (Objects.isNull(marketTypeObj) && Objects.isNull(redisService.get(key))) {
                Long standardMatchId = standardMatchInfo.getId();
                StandardSportMarketSell sportMarketSellServiceItem = sportMarketSellService.refreshCache(standardMatchId);
                if (sportMarketSellServiceItem != null) {
                    //标准赛事 主赛事状态源
                    String matchStatusSourceCode = sportMarketSellServiceItem.getMatchStatusSourceCode();
                    if (StringUtils.equals(item.getDataSourceCode(), matchStatusSourceCode)) {
                        String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                        if (TimeUtils.timeCalendar(item.getBeginTime())) {
                            log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,标准赛事ID:{},开赛时间:{},开售赛事状态源:{}",
                                    linkId, standardMatchInfo.getId(), item.getBeginTime(), matchStatusSourceCode);
                            String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                            redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), item.getBeginTime(),marketCacheTime(item.getBeginTime()));
                        }else{
                            log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,大于7天后时间不入缓存,标准赛事ID:{},开赛时间:{},开售赛事状态源:{}",
                                    linkId, standardMatchInfo.getId(), item.getBeginTime(), matchStatusSourceCode);
                        }
                    } else {
                        log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,与设置的主赛事状态源不匹配,标准赛事ID:{},设置赛事状态源:{},三方状态源:{}",
                                linkId, standardMatchInfo.getId(), matchStatusSourceCode, item.getDataSourceCode());
                    }
                } else {
                    log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,标准赛事未开售,标准赛事ID:{}",
                            linkId, standardMatchInfo.getId());
                }
            } else {
                log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,已下发过滚球标识或已下发过自动构建盘口,标准赛事ID:{}",
                        linkId, standardMatchInfo.getId());
            }
        }
    }

    /**
     * 标准赛事开赛时间刷入缓存，到开赛时间下发滚球标识
     *
     * @param linkId
     * @param standardMatchInfo
     */
    public void refreshStandardMatchBeginTimeByMatchId(String linkId, StandardMatchInfo standardMatchInfo) {
        //只处理赛前
        Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId());
        //标准赛事是否已经下发过自动构建赔率key
        String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
        if (Objects.isNull(marketTypeObj) && Objects.isNull(redisService.get(key))) {
            StandardSportMarketSell sportMarketSellServiceItem = sportMarketSellService.getItem(standardMatchInfo.getId());
            if (sportMarketSellServiceItem != null) {
                //标准赛事查库
                StandardMatchInfo refreshStandardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchInfo.getId());
                //标识赛事开售 赛事状态源
                String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                if (TimeUtils.timeCalendar(refreshStandardMatchInfo.getBeginTime())) {
                    log.info("::{}::模板缓存标准赛事开赛时间,标准赛事ID:{},开赛时间:{},赛事状态源:{}",
                            linkId, standardMatchInfo.getId(), refreshStandardMatchInfo.getBeginTime(), sportMarketSellServiceItem.getMatchStatusSourceCode());
                    String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                    redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), refreshStandardMatchInfo.getBeginTime(),marketCacheTime(refreshStandardMatchInfo.getBeginTime()));
                }else {
                    log.info("::{}::模板缓存标准赛事开赛时间,大于7天后时间不入缓存,标准赛事ID:{},开赛时间:{},赛事状态源:{}",
                            linkId, standardMatchInfo.getId(), standardMatchInfo.getBeginTime(), sportMarketSellServiceItem.getMatchStatusSourceCode());
                }
            } else {
                log.info("::{}::模板缓存标准赛事开赛时间,未开售,标准赛事ID:{}", linkId, standardMatchInfo.getId());
            }
        } else {
            log.info("::{}::模板缓存标准赛事开赛时间,已下发过滚球标识或已下发过自动构建盘口,标准赛事ID:{}",
                    linkId, standardMatchInfo.getId());
        }
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

    /**
     * 获取缓存中标准赛事阶段
     * @param standardMatchInfoId   标准赛事ID
     * @param matchPeriodId         赛事信息中赛事阶段
     * @return
     */
    public Long getMatchPeriod(Long standardMatchInfoId,Long matchPeriodId) {
        String standardMatchPeriodKey = String.format(getStandardMatchPeriodKey(), standardMatchInfoId);
        if(redisService.hasKey(standardMatchPeriodKey)){
            matchPeriodId = (Long)redisService.get(standardMatchPeriodKey);
        }
        if(matchPeriodId == null){
            matchPeriodId = 0L;
        }
        return matchPeriodId;
    }

    /**
     * AO 盘口水差/MARGIN 配置下发
     *
     * @param linkId
     * @param standardMatchInfo      标准赛事
     * @param marketDataMessageMap   标准盘口缓存
     * @param uiConfigDTO            ui接口 水差、margin参数
     * @param diffConfigList         水差集合接口
     * @param marketMarginConfigDTOS Margin接口、Margin集合接口
     */
    public List<TradeMarketDiffAndMarginConfigDTO> aoMarketDiffAndMarginConfig(String linkId, StandardMatchInfo standardMatchInfo, Map<String, StandardMarketDataMessage> marketDataMessageMap,
                                                                               TradeMarketUiConfigDTO uiConfigDTO, List<TradeMarketAutoDiffConfigItemDTO> diffConfigList, List<TradeMarketMarginConfigDTO> marketMarginConfigDTOS) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                && !StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())
                && !StandardSportTypeEnum.TableTennis.code.equals(standardMatchInfo.getSportId())) {
            return null;
        }
        Long standardMatchInfoId = standardMatchInfo.getId();
        ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchInfoId, DataSourceCodeEnum.AO.code);
        if (null == aoMatchInfo) {
            return null;
        }
        try {
            List<TradeMarketDiffAndMarginConfigDTO> configList = new ArrayList<>();
            TradeMarketDiffAndMarginConfigDTO diffAndMarginConfigDTO = new TradeMarketDiffAndMarginConfigDTO();
            diffAndMarginConfigDTO.setStandardMatchInfoId(standardMatchInfo.getId());
            diffAndMarginConfigDTO.setAoMatchId(aoMatchInfo.getThirdMatchSourceId());
            diffAndMarginConfigDTO.setLinkId(linkId);

            //ui接口 水差、margin参数处理
            if (null != uiConfigDTO) {
                List<TradeMarketAutoDiffConfigItemDTO> diffConfigs = uiConfigDTO.getDiffConfigs();
                Long standardCategoryId = uiConfigDTO.getStandardCategoryId();
                diffAndMarginConfigDTO.setPlaceNum(uiConfigDTO.getPlaceNum());
                diffAndMarginConfigDTO.setMarketType(uiConfigDTO.getMarketType());
                diffAndMarginConfigDTO.setStandardCategoryId(uiConfigDTO.getStandardCategoryId());
                diffAndMarginConfigDTO.setChildStandardCategoryId(uiConfigDTO.getChildStandardCategoryId());
                //水差处理
                //marketDiffDealWith(diffConfigs, marketDataMessageMap, diffAndMarginConfigDTO);
                //margin
                if(!CollectionUtils.isEmpty(uiConfigDTO.getMarketMarginDtlDTOList())){
                    diffAndMarginConfigDTO.setMarketMarginDtlDTOList(uiConfigDTO.getMarketMarginDtlDTOList());
                }
                if (!CollectionUtils.isEmpty(uiConfigDTO.getMarginGapDtlDTOList())) {
                    List<MarketMarginDtlDTO> marketMarginDtlDTOList = new ArrayList<>();
                    List<MarketMarginGapDtlDTO> marginGapDtlDTOList = uiConfigDTO.getMarginGapDtlDTOList();
                    marginGapDtlDTOList.forEach(margin -> {
                        MarketMarginDtlDTO marginDtlDTO = new MarketMarginDtlDTO();
                        BeanUtils.copyProperties(margin, marginDtlDTO);
                        marketMarginDtlDTOList.add(marginDtlDTO);
                    });
                    diffAndMarginConfigDTO.setMarketMarginDtlDTOList(marketMarginDtlDTOList);
                }
                configList.add(diffAndMarginConfigDTO);
            }
            //margin接口、集合接口 处理
            if (!CollectionUtils.isEmpty(marketMarginConfigDTOS)) {
                marketMarginConfigDTOS.forEach(marginConfigDTO -> {
                    Integer placeNum = marginConfigDTO.getPlaceNum();
                    Integer marketType = marginConfigDTO.getMarketType();
                    diffAndMarginConfigDTO.setPlaceNum(placeNum);
                    diffAndMarginConfigDTO.setMarketType(marketType);
                    diffAndMarginConfigDTO.setMarketMarginDtlDTOList(marginConfigDTO.getMarketMarginDtlDTOList());
                    configList.add(diffAndMarginConfigDTO);
                });
            }
            return configList;
        } catch (Exception e) {
            log.info("::{}::发送AO水差,margin配置异常:" + e, linkId);
        }
        return null;
    }

    /**
     * 处理水差
     */
    public TradeMarketDiffAndMarginConfigDTO marketDiffDealWith(List<TradeMarketAutoDiffConfigItemDTO> diffConfigs, Map<String, StandardMarketDataMessage> marketDataMessageMap,
                                                                TradeMarketDiffAndMarginConfigDTO diffAndMarginConfigDTO) {
        if (!CollectionUtils.isEmpty(diffConfigs)) {
            TradeMarketAutoDiffConfigItemDTO diffConfig = diffConfigs.stream().filter(m -> m.getDiffValue() != 0).findFirst().orElse(null);
            if (null == diffConfig) {
                diffConfig = diffConfigs.get(0);
            }
            StandardMarketDataMessage marketDataMessage = marketDataMessageMap.get(String.valueOf(diffConfig.getMarketId()));
            if (null != marketDataMessage) {
                diffAndMarginConfigDTO.setAddition1(marketDataMessage.getAddition1());
            }
            diffAndMarginConfigDTO.setDiffConfigs(diffConfig);
        }
        return diffAndMarginConfigDTO;
    }

    /**
     * 赛事切换 玩法切换 清除AO配置
     *
     * @param linkId
     * @param standardMatchId
     * @param thirdMatchSourceId
     * @param categoryList
     */
    public void sendClearAoDiffConfig(String linkId, Long standardMatchId, String thirdMatchSourceId, List<Long> categoryList) {
        List<Long> categorys = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            categorys = categoryList.stream()
                    .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(categorys)) {
                return;
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("linkId", linkId);
        obj.put("standardMatchId", standardMatchId);
        obj.put("aoMatchId", thirdMatchSourceId);
        obj.put("categorys", categorys);
        MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(obj).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.asyncSend("AO_DIFF_CONFIG_CLEAR:" + standardMatchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,AO_DIFF_CONFIG_CLEAR，send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "AO_MATCH_DIFF_CONFIG_CLEAR", throwable);
            }
        });
    }

    /**
     * AO icon状态
     *
     * @param linkId
     * @param standardMatchId
     * @param marketPreResultMessageList
     */
    public void aoMatchPreIconStatus(String linkId, Long standardMatchId, List<StandardMatchMarketPreResultMessage> marketPreResultMessageList) {
        int marketType = isOddsLive(standardMatchId);
        //赛事是否支持提前结算
        Integer matchPreStatusRisk = marketPreResultMessageList.get(0).getMatchPreStatusRisk();
        //icon 状态,默认关
        Integer iconStatus = 0;
        //获取系统级提前结算开关参数信息
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map<String, Integer> paramsMap = redisService.hGetAll(SystemThirdMarketPreParams);
        //赛事级别
        ConfigCashOutTradeItem configCashOutTradeItemRace = configCashOutTradeItemService.getItem(standardMatchId, marketType, 1);
        log.info("::{}::提前结算状态,赛事ID:{},类型:{},系统状态:{},赛事是否支持:{},赛事级别数据源:{}",
                linkId, standardMatchId, marketType, paramsMap, matchPreStatusRisk, JSONObject.toJSONString(configCashOutTradeItemRace));
        if (MapUtils.isNotEmpty(paramsMap) && null != configCashOutTradeItemRace) {
            if (matchPreStatusRisk == 1) {
                for (Object k : paramsMap.keySet()) {
                    Integer v = Integer.parseInt(paramsMap.get(k.toString()).toString());
                    if (k.equals(configCashOutTradeItemRace.getDataSourceCode())) {
                        if (v.equals(configCashOutTradeItemRace.getMatchPreStatus())) {
                            iconStatus = 1;
                        }
                    }
                }
            }
        }
        Integer finalIconStatus = iconStatus;
        marketPreResultMessageList.forEach(m -> {
            m.setMatchPreStatusRisk(finalIconStatus);
        });
        //最后兜底
        if (!CollectionUtils.isEmpty(paramsMap)) {
            int AoOnOff = paramsMap.get("AO");
            if (AoOnOff == 0) {
                marketPreResultMessageList.forEach(m -> {
                    m.setMatchPreStatusRisk(0);
                    m.setMatchPreStatus(0);
                });
            }
        }
    }

    /**
     * 初盘 三方盘口转换标准盘口
     *
     * @param thirdMarketDTO
     * @return
     */
    public StandardMarketDataMessage thirdConvertStandardMarket(ThirdMarketDTO thirdMarketDTO) {
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();
        BeanUtil.copyProperties(thirdMarketDTO, standardMarketDataMessage);
        List<ThirdMarketOddsDTO> marketOddsList = thirdMarketDTO.getMarketOddsList();
        if (CollectionUtils.isEmpty(marketOddsList)) {
            return null;
        }
        List<StandardMarketOddsDataMessage> standardMarketOddsDataMessages = new ArrayList<>();
        marketOddsList.forEach(thirdMarketOdds -> {
            StandardMarketOddsDataMessage standardMarketOddsDataMessage = new StandardMarketOddsDataMessage();
            BeanUtil.copyProperties(thirdMarketOdds, standardMarketOddsDataMessage);
            standardMarketOddsDataMessages.add(standardMarketOddsDataMessage);
        });
        standardMarketDataMessage.setMarketOddsList(standardMarketOddsDataMessages);
        return standardMarketDataMessage;
    }


    /**
     * BC事件相关特殊处理 优化单：42254
     * */
    public Boolean bcEventProcessor(String linkId,StandardMatchInfo standardMatchInfo,ThirdMatchInfo oldThirdMatchInfo) {
        //bc事件相关特殊处理 402优化单
        if(DataSourceCodeEnum.BC.getCode().equals(oldThirdMatchInfo.getDataSourceCode())){
            Integer liveOddBusiness = 1;
            if(null != standardMatchInfo){
                liveOddBusiness = standardMatchInfo.getLiveOddBusiness();
            }
            //如果BC不支持滚球或者标准赛事不支持滚球 则不需要接入事件
            if(ZERO.equals(oldThirdMatchInfo.getLiveOddSupport()) || ZERO.equals(liveOddBusiness)){
                log.info("::{}::process2MatchEvent，当前三方赛事不支持滚球不需要接入事件数据，三方数据源赛事id:{},数据来源：{},是否支持滚球：{},{}",
                        linkId,oldThirdMatchInfo.getThirdMatchSourceId(),oldThirdMatchInfo.getDataSourceCode(),oldThirdMatchInfo.getLiveOddSupport(),liveOddBusiness);
                return false;
            }
        }
        return true;
    }

    /**
     * 主客队相反：盘口、投注项内容替换
     *
     * @param linkId
     * @param dataSourceCode
     * @param thirdMarketCategory
     * @param thirdMarketDTO
     */
    public void changeStandardMarketContent(String linkId, String dataSourceCode, ThirdMarketCategory thirdMarketCategory, ThirdMarketDTO thirdMarketDTO) {
        Long standardCategoryId = thirdMarketCategory.getReferenceId();
        log.info("::{}::changeStandardMarketContent盘口信息, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, standardCategoryId, thirdMarketDTO.getAddition1(), thirdMarketDTO.getAddition2(), thirdMarketDTO.getAddition3(), thirdMarketDTO.getAddition4());
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(standardCategoryId)) {
            Long newCategoryId = CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.get(thirdMarketCategory.getReferenceId());
            List<ThirdMarketCategory> marketCategoryList = thirdMarketCategoryService.getItem(dataSourceCode, newCategoryId);
            if (!CollectionUtils.isEmpty(marketCategoryList)) {
                BeanUtils.copyProperties(marketCategoryList.get(0), thirdMarketCategory);
                if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                    List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail(dataSourceCode, thirdMarketCategory.getReferenceId());
                    if (!CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)) {
                        Map<String, String> stringMap = thirdMarketCategoryFieldDetails.stream().collect(Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getThirdSourceId, (o, n) -> n));
                        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                            thirdMarketOddsDTO.setThirdTempletSourceId(stringMap.get(thirdMarketOddsDTO.getOddsType().toLowerCase()));
                        }
                    }
                }
            }
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_2.contains(standardCategoryId)) {
            String add1 = thirdMarketDTO.getAddition1().contains("-") ? thirdMarketDTO.getAddition1().replace("-", "") : "-" + thirdMarketDTO.getAddition1();
            thirdMarketDTO.setAddition1(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_3.contains(standardCategoryId)) {
            String add2 = thirdMarketDTO.getAddition2().contains("-") ? thirdMarketDTO.getAddition2().replace("-", "") : "-" + thirdMarketDTO.getAddition2();
            thirdMarketDTO.setAddition2(add2);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_4.contains(standardCategoryId)) {
            String add3 = thirdMarketDTO.getAddition3();
            String add4 = thirdMarketDTO.getAddition4();
            thirdMarketDTO.setAddition3(add4);
            thirdMarketDTO.setAddition4(add3);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_6.contains(standardCategoryId)) {
            String add1 = thirdMarketDTO.getAddition1();
            String add2 = thirdMarketDTO.getAddition2();
            thirdMarketDTO.setAddition1(add2);
            thirdMarketDTO.setAddition2(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_10.contains(standardCategoryId)) {
            String add3 = thirdMarketDTO.getAddition3();
            String add4 = thirdMarketDTO.getAddition4();
            thirdMarketDTO.setAddition3(add4);
            thirdMarketDTO.setAddition4(add3);
        }
        if (CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
            return;
        }
        Map<String, String> thirdTemplateSourceIdMap = new HashMap<>();
        Map<String, List<I18nItemDTO>> i18Map = new HashMap<>();
        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
            thirdTemplateSourceIdMap.put(thirdMarketOddsDTO.getOddsType(), thirdMarketOddsDTO.getThirdTempletSourceId());
            i18Map.put(thirdMarketOddsDTO.getOddsType(), thirdMarketOddsDTO.getI18nNames());
        }
        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
            /*log.info("::{}::changeStandardMarketContent投注项信息,标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, standardCategoryId,
                    thirdMarketOddsDTO.getAddition1(), thirdMarketOddsDTO.getAddition2(),thirdMarketOddsDTO.getAddition3(),thirdMarketOddsDTO.getAddition4());*/
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_7.contains(standardCategoryId)) {
                String add1 = thirdMarketOddsDTO.getAddition1();
                String add2 = thirdMarketOddsDTO.getAddition2();
                thirdMarketOddsDTO.setAddition1(add2);
                thirdMarketOddsDTO.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_8.contains(standardCategoryId)) {
                String add3 = thirdMarketOddsDTO.getAddition3();
                String add4 = thirdMarketOddsDTO.getAddition4();
                thirdMarketOddsDTO.setAddition3(add4);
                thirdMarketOddsDTO.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_5.contains(standardCategoryId)) {
                if (standardCategoryId == 104L) {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.containsKey(thirdMarketOddsDTO.getOddsType())) {
                        String oddsType = thirdMarketOddsDTO.getOddsType();
                        thirdMarketOddsDTO.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.get(oddsType));
                        thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceIdMap.get(thirdMarketOddsDTO.getOddsType()));
                        thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                    }
                } else if (standardCategoryId == 103L) {
                    String str1 = (thirdMarketOddsDTO.getAddition1() == null || thirdMarketOddsDTO.getAddition1().contains("+")) ? thirdMarketOddsDTO.getAddition1() : thirdMarketOddsDTO.getAddition1() + ":" + thirdMarketOddsDTO.getAddition2();
                    String str2 = (thirdMarketOddsDTO.getAddition3() == null || thirdMarketOddsDTO.getAddition3().contains("+")) ? thirdMarketOddsDTO.getAddition3() : thirdMarketOddsDTO.getAddition3() + ":" + thirdMarketOddsDTO.getAddition4();
                    thirdMarketOddsDTO.setOddsType(str1 + " " + str2);
                    thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                } else {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.containsKey(thirdMarketOddsDTO.getOddsType())) {
                        String oddsType = thirdMarketOddsDTO.getOddsType();
                        thirdMarketOddsDTO.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.get(oddsType));
                        thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceIdMap.get(thirdMarketOddsDTO.getOddsType()));
                        thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                    } else {
                        if (thirdMarketOddsDTO.getOddsType().contains(":")) {
                            String[] strArr = thirdMarketOddsDTO.getOddsType().split(":");
                            if (strArr.length == 2) {
                                thirdMarketOddsDTO.setOddsType(strArr[1] + ":" + strArr[0]);
                                thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                            }
                        }
                    }
                }
            }
        }
    }

    public StandardMarketMessage convertLog(StandardMarketDataMessage standardMarketDataMessage) {
        // 发送操盘日志给风控
        StandardMarketMessage logData = new StandardMarketMessage();
        logData.setMarketCategoryId(standardMarketDataMessage.getMarketCategoryId());
        logData.setChildMarketCategoryId(standardMarketDataMessage.getChildMarketCategoryId());
        logData.setMarketType(standardMarketDataMessage.getMarketType());
        logData.setPaStatus(standardMarketDataMessage.getStatus());
        logData.setThirdMarketSourceStatus(standardMarketDataMessage.getThirdMarketSourceStatus());
        logData.setId(standardMarketDataMessage.getRelationMarketId());
        return logData;
    }

    /**
     * 格式化时间
     *
     * @param modifyTime
     * @return
     */
    public Long format(Long modifyTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format1 = simpleDateFormat.format(modifyTime);
        Date date = null;
        try {
            date = simpleDateFormat.parse(format1);
        } catch (Exception e) {
            return modifyTime;
        }
        return date.getTime();
    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 输出Rocketmq节点参数日志
     * @param ext       MQ推送的原始数据
     * @param spareMq   是否备用MQ
     * @param topic
     * @param linkId    线路ID
     * */
    public void getRocketmqTimeData(MessageExt ext, boolean spareMq, String topic, String linkId,Long now){
        try {
            // 消息生产时间戳（生产者创建消息的时间）
            long bornTime = ext.getBornTimestamp();
            // 消息存储到Broker的时间戳
            long storeTime = ext.getStoreTimestamp();
            //消息重试消费次数（死信队列处理时会递增）
            long reconsumeTimes = ext.getReconsumeTimes();
            // 客户端到消费端时间差
            long pushDelay = now - bornTime;
            // broker到消费端时间差
            long brokerDelay = now - storeTime;
            if(pushDelay > 3000L || brokerDelay > 3000L){
                log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】getRocketmqTimeData,消费存在延迟,spareMq={},messageId={},消息重试消费次数={},客户端到消费端时间={} ms,broker到消费端时间={} ms,消息生产时间戳={},Broker的时间戳={},开始处理事件数据"
                        ,spareMq, ext.getMsgId(),reconsumeTimes,pushDelay,brokerDelay,bornTime,storeTime);
                // 直接通过Bean名称获取（需确保类型匹配）
                ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) applicationContext.getBean("EventInfoThreadPool");
                // 输出线程池状态
                ThreadPoolMonitor.printThreadPoolStats(executor,linkId,"EventInfoThreadPool");
            }else{
                log.info("linkId=【" + linkId + "】【TOPIC=" + topic + "】getRocketmqTimeData,spareMq={},messageId={},消息重试消费次数={},客户端到消费端时间={} ms,broker到消费端时间={} ms,开始处理事件数据"
                        ,spareMq, ext.getMsgId(),reconsumeTimes,pushDelay,brokerDelay);
            }
        }catch (Exception e){
            log.error("linkId=【" + linkId + "】【TOPIC=" + topic + "】getRocketmqTimeData,输出Rocketmq节点参数日志 Exception:",e);
        }

    }

}
