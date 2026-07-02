package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.ITradeTournamentConfigApi;
import com.panda.merge.dto.Response;
import com.panda.merge.config.RedisService;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.*;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  myname
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.impl
 * @Description :  TODO
 * @Date: 2020-07-04 17:14
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@DubboService
public class TradeTournamentConfigApiServiceImpl implements ITradeTournamentConfigApi {

    @Autowired
    private ConfigTemplateDataSourceService templateDataSourceService;
    @Autowired
    private ConfigTemplateCategoryService templateCategoryService;
    @Autowired
    private ConfigTemplateCategoryMarginService configCategoryMarginService;
    @Autowired
    private ConfigTemplateEventService  templateEventService;
    @Autowired
    private ConfigTemplateService configTemplateService;
    @Autowired
    private ConfigTournamentTemplateService tournamentTemplateService;

    @Autowired
    private RedisService redisClient;


    /**
     * 操盘配置联赛与模板配置
     * @param message
     * @return
     */
    @Override
    public Response putTournamentTemplateRelationConfig(Request<TradeTournamentTemplateConfigDTO> message){
        log.info("联赛模板配置变更服务开始---联赛模板配置入参: {}", JSON.toJSONString(message));
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        MDC.put("tracelogId", message.getLinkId());
        TradeTournamentTemplateConfigDTO tournamentTemplateInfo = message.getData();

        Long standardTournamentId = tournamentTemplateInfo.getStandardTournamentId();
        Integer marketType = tournamentTemplateInfo.getMarketType();
        Long templateId = tournamentTemplateInfo.getTemplateId();
        //验证db 中是否已存在对应配置信息
        List<ConfigTournamentTemplate> existTournamentTemplates = tournamentTemplateService.getTournamentTemplateInfoByIdAndMarketType(standardTournamentId, marketType);
        //若无该联赛及盘口类型对应模板配置则新增一条
        if(CollectionUtils.isEmpty(existTournamentTemplates)){
            tournamentTemplateService.save(tournamentTemplateInfo);
        }else{
            //更新.
            ConfigTournamentTemplate relationEntity = existTournamentTemplates.get(0);
            relationEntity.setTemplateId(tournamentTemplateInfo.getTemplateId());
            relationEntity.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            tournamentTemplateService.update(relationEntity);
        }
        //先获取db 中的联赛模板记录，保存成功才继续缓存
        existTournamentTemplates = tournamentTemplateService.getTournamentTemplateInfoByIdAndMarketType(standardTournamentId, marketType);
        if(!CollectionUtils.isEmpty(existTournamentTemplates)){
            log.info("联赛模板绑定配置变保存成功，缓存对应数据！");
            //缓存绑定联赛对应的模板
            syncTournamentIdRelationTemplateId2Redis(standardTournamentId, templateId, marketType);
        }
        log.info("联赛模板配置变更结束，耗时: {}",System.currentTimeMillis() - beginTime );
        return response;
    }

    private void syncTournamentIdRelationTemplateId2Redis(Long standardTournamentId, Long templateId, Integer marketType){
        String tournamentRelationKey = Constant.REDIS_KEY.RONGHE_TOURNAMENT_ID + marketType + ":" + standardTournamentId;
        redisClient.set(tournamentRelationKey, String.valueOf(templateId));
    }

    /**
     * 缓存模板配置的数据
     * @param templateId
     */
    private void syncTemplateData2Redis(Long templateId){
        ConfigTemplate templateInfo = configTemplateService.getTemplateRecByTemplateId(templateId);
        if(templateInfo == null){
            log.info("联赛配缓存配置,找不到模板号为:{} 的模板", templateId);
            return;
        }
        Long sportId = templateInfo.getSportId();
        Integer marketType = templateInfo.getMarketType();
        Integer tournamentLevel = templateInfo.getTournamentLevel();
        String templateType = templateInfo.getTemplateType();

        String levelTemplateKey = Constant.REDIS_KEY.RONGHE_TEMPLATE_CONFIGURATION_ID + sportId + ":" + tournamentLevel + ":" + marketType + ":" + templateType;
        redisClient.set(levelTemplateKey, String.valueOf(templateId));

        //缓存数据源权重及盘口最大值配置
        ConfigTemplateDataSource dataSourceInfo = templateDataSourceService.getDataSourceConfigurationBytemplateId(templateId);
        if(dataSourceInfo != null){
            String dataSourceKey = getCommonKey("dataSource", templateId);
            redisClient.set(dataSourceKey, JSON.toJSONString(dataSourceInfo));
        }

        //获取对应事件配置信息并缓存事件审核时间配置
        List<ConfigTemplateEvent> eventConfigInfos = templateEventService.getEventConfigurationByTemplateId(templateId);
        if(CollectionUtils.isNotEmpty(eventConfigInfos)){
            String eventCommonKey = getCommonKey("event", templateId);
            for(ConfigTemplateEvent eventConfigInfo: eventConfigInfos){
                String eventKey = eventCommonKey + ":" + eventConfigInfo.getEventCode();
                redisClient.set(eventKey, String.valueOf(eventConfigInfo.getEventAuditTime()));
            }
        }
        //获取玩法配置并缓存
        List<ConfigTemplateCategory> categroyInfos = templateCategoryService.getCategoryConfigurationByTemplateId(templateId);
        if(CollectionUtils.isNotEmpty(categroyInfos)){
            String categroyKey = getCommonKey("category", templateId);
            redisClient.set(categroyKey, JSON.toJSONString(categroyInfos));
        }
        //获取margin值配置并缓存
        if(CollectionUtils.isNotEmpty(categroyInfos)){
            Set<Long> categoryConfigIds = categroyInfos.stream().map(ConfigTemplateCategory :: getId).collect(Collectors.toSet());
            List<ConfigTemplateCategoryMargin> categroyMarginInfos = configCategoryMarginService.getMarginConfigurationsByCategoryIds(categoryConfigIds);
            if(CollectionUtils.isNotEmpty(categroyMarginInfos)){
                String categroyMarginCommonKey = getCommonKey("categoryMargin", templateId);
                Map<Long, List<ConfigTemplateCategoryMargin>> categoryMarginMap = categroyMarginInfos.stream().collect(Collectors.groupingBy(ConfigTemplateCategoryMargin :: getStandardCategoryId));
                categoryMarginMap.forEach((categoryId, categoryMargins) -> {
                    String categroyMarginKey = categroyMarginCommonKey + ":" + categoryId;
                    redisClient.set(categroyMarginKey,  JSON.toJSONString(categoryMargins));
                });
            }
        }
        log.info("联赛配置入库缓存配置完成！");
    }

    private String getCommonKey(String dataType, Long templateId){
        return Constant.REDIS_KEY.RONGHE_TEMPLATE_CONFIGURATION_DETAIL + dataType + ":" + templateId;
    }

    /**
     * 处理联赛与模板数据
     * 1、处理联赛与模板关系
     * 2、处理联赛等级与模板关系
     * @param request
     * @return
     */
    @Override
    @Transactional
    public Response putTradeTournamentConfig(Request<TradeTournamentConfigDTO> request) {
        //开始时间
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        MDC.put("tracelogId", request.getLinkId());
        log.info("联赛配置入参: {}", JSON.toJSONString(request));
        TradeTournamentConfigDTO dto =  request.getData();
        //处理模板数据
        this.dealConfigTemplate(dto);
        log.info("联赛配置入库处理完成，开始缓存配置！");
        
        syncTemplateData2Redis(dto.getTemplateId());
        log.info("联赛配置结束，耗时: {}",System.currentTimeMillis() - beginTime );
        return response;
    }

    /**
     * 处理模板数据
     * @param tournamentConfigDTO
     */
    public void dealConfigTemplate(TradeTournamentConfigDTO tournamentConfigDTO){
        //查询联赛模板设置
        ConfigTemplate   existConfigTemplate =  configTemplateService.getTemplateRec(tournamentConfigDTO);
        if(existConfigTemplate != null){
            this.uptConfigTemplate(tournamentConfigDTO,existConfigTemplate);
        }else{
            //数据库中不存在，则新增联赛模板配置数据
            this.saveConfigTemplate(tournamentConfigDTO);
        }
    }


    /**
     * 保存模板数据
     * @param dto
     */
    public void saveConfigTemplate(TradeTournamentConfigDTO dto){
        ConfigTemplate configTemplate = new ConfigTemplate();
        configTemplate.setId(IdWorker.getId());
        //dto入参对象赋值
        BeanUtils.copyProperties(dto, configTemplate);
        configTemplate.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTemplate.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //保存模板
        boolean resultTemp = configTemplateService.save(configTemplate);
        boolean result = this.saveTemplateDataSource(dto);
        log.info("保存模板结果:{},数据源配置数据结果：{}",resultTemp,result);
        //模板下设置的玩法不为空
        List<ConfigTemplateCategoryDTO> categoryMarginDTOList = dto.getTemplateCategoryDTOList();
        if(CollectionUtils.isNotEmpty(categoryMarginDTOList)){
            //新增模板玩法集合
            List<ConfigTemplateCategory> addTournamentCategoryList = new ArrayList<>();
            //新增玩法margin集合
            List<ConfigTemplateCategoryMargin> addCategoryMarginAllList = new ArrayList<>();
            for(ConfigTemplateCategoryDTO categoryMarginDTO : categoryMarginDTOList){
                //定义模板玩法对象
                ConfigTemplateCategory  configTemplateCategory  = new ConfigTemplateCategory();
                BeanUtils.copyProperties(categoryMarginDTO, configTemplateCategory);
                //设置模板id
                configTemplateCategory.setTemplateId(dto.getTemplateId());
                configTemplateCategory.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                configTemplateCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                //生成主键id
                long id = IdWorker.getId();
                configTemplateCategory.setId(id);
                addTournamentCategoryList.add(configTemplateCategory);
                //玩法分时margin 不为空
                if(null != categoryMarginDTO.getCategoryMarginDtlList()){
                    //保存玩法分时
                    List<ConfigTemplateCategoryMargin> configCategoryMarginList = this.saveConfigCategoryMargin(id,categoryMarginDTO);
                    addCategoryMarginAllList.addAll(configCategoryMarginList);
                }else{
                    log.info("入参玩法下没有设置margin值");
                }
            }
            //保存模板下玩法设置
            templateCategoryService.saveBatch(addTournamentCategoryList);
            if(CollectionUtils.isNotEmpty(addCategoryMarginAllList)){
                //玩法分时margin
                configCategoryMarginService.saveBatch(addCategoryMarginAllList);
            }
            log.info("模板id:{},数据保存完毕！",dto.getTemplateId());
        }else{
            log.info("入参模板下没有配置玩法数据");
        }
        //模板下设置事件审核时间
        if(CollectionUtils.isNotEmpty(dto.getTemplateEventDTOList())){
            this.saveTournamentEvent(dto);
        }else{
            log.info("入参模板下没有配置事件审核时间数据");
        }
    }

    /**
     * 保存模板数据源
     * @param dto
     * @return
     */
    public boolean saveTemplateDataSource(TradeTournamentConfigDTO dto){
        ConfigTemplateDataSource configTemplateDataSource = new ConfigTemplateDataSource();
        //dto入参对象赋值
        BeanUtils.copyProperties(dto, configTemplateDataSource);
        configTemplateDataSource.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTemplateDataSource.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //保存联赛设置
        boolean result = templateDataSourceService.save(configTemplateDataSource);
        return result;
    }

    /**
     * 组装玩法分时
     * @param id
     * @param categoryMarginDTO
     * @return
     */
    public List<ConfigTemplateCategoryMargin> saveConfigCategoryMargin(long id, ConfigTemplateCategoryDTO categoryMarginDTO){
        //获取玩法分时margin
        List<CategoryMarginDtlDTO> categoryMarginDtlMsgList = categoryMarginDTO.getCategoryMarginDtlList();
        List<ConfigTemplateCategoryMargin> configCategoryMarginList = new ArrayList<>();
        for(CategoryMarginDtlDTO dtl : categoryMarginDtlMsgList){
            ConfigTemplateCategoryMargin categoryMargin = new ConfigTemplateCategoryMargin();
            BeanUtils.copyProperties(dtl, categoryMargin);
            categoryMargin.setId(IdWorker.getId());
            //设置玩法id
            categoryMargin.setStandardCategoryId(categoryMarginDTO.getStandardCategoryId());
            //设置为模板玩法主键id
            categoryMargin.setTemplateCategoryId(id);
            categoryMargin.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            categoryMargin.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configCategoryMarginList.add(categoryMargin);
        }
        return configCategoryMarginList;
    }

    /**
     * 保存联赛事件
     * @param dto
     */
    public void saveTournamentEvent(TradeTournamentConfigDTO dto){
        //模板下设置的事件审核
        List<ConfigTemplateEventDTO>  tournamentEventDTOList = dto.getTemplateEventDTOList();
        //新增联赛事件审核集合
        List<ConfigTemplateEvent> addTournamentEventList = new ArrayList<>();
        for(ConfigTemplateEventDTO tournamentEventDTO : tournamentEventDTOList){
            //定义联赛事件对象
            ConfigTemplateEvent  configTemplateEvent  = new ConfigTemplateEvent();
            BeanUtils.copyProperties(tournamentEventDTO, configTemplateEvent);
            //设置模板id
            configTemplateEvent.setTemplateId(dto.getTemplateId());
            configTemplateEvent.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configTemplateEvent.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            addTournamentEventList.add(configTemplateEvent);
        }
        //保存模板下设置
        templateEventService.saveBatch(addTournamentEventList);
    }


    /**
     * 更新模板数据
     * @param dto
     * @param existConfigTemplate
     */
    public void uptConfigTemplate(TradeTournamentConfigDTO dto, ConfigTemplate  existConfigTemplate){
        //修改模板设置
        Long templateId = dto.getTemplateId();
        existConfigTemplate.setTemplateId(templateId);
        existConfigTemplate.setTournamentLevel(dto.getTournamentLevel());
        existConfigTemplate.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        boolean result = configTemplateService.updateByTemlateId(existConfigTemplate);
        log.info("模板主键:{},模板id:{},更新模板结果：{}",existConfigTemplate.getId(),existConfigTemplate.getTemplateId(),result);
        //查询模板数据源
        ConfigTemplateDataSource  templateDataSourceDb =  templateDataSourceService.getDataSourceConfigurationBytemplateId(templateId);
        if(templateDataSourceDb != null){
            //数据库中查询出的模板数据源对象
            ConfigTemplateDataSource configTemplateDataSource = new ConfigTemplateDataSource();
            //dto入参对象复制
            BeanUtils.copyProperties(dto, configTemplateDataSource);
            //设置主键id
            configTemplateDataSource.setId(templateDataSourceDb.getId());
            configTemplateDataSource.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //修改模板数据源
            templateDataSourceService.updateById(configTemplateDataSource);
            log.info("模板数据源主键:{},模板id:{},更新到redis中",configTemplateDataSource.getId(),configTemplateDataSource.getTemplateId());
        }else{
            //数据库中不存在，则新增模板数据源配置数据
           boolean addDataSource = this.saveTemplateDataSource(dto);
           log.info("没有查询到模板数据源数据，进行新增操作,结果:{}",addDataSource);
        }
        //入参的模板玩法不为空
        if(null != dto.getTemplateCategoryDTOList()){
            //查询数据库中模板玩法设置
            List<ConfigTemplateCategory>  configTemplateCategoryDbList =  this.queryConfigTemplateCategory(dto);
            //定义标准玩法和模板玩法集合
            Map<String, ConfigTemplateCategory> categoryDbMap = new HashMap();
            //定义模板玩法主键id和模板玩法集合
            Map<Long, ConfigTemplateCategory> categoryIdDbMap = new HashMap();
            //定义模板玩法主键id和玩法margin集合
            Map<String, ConfigTemplateCategoryMargin> categoryMarginIdDbMap = new HashMap();
            //数据库中模板玩法不为空
            List<ConfigTemplateCategoryMargin>  categoryMarginDbList = null;
            if(null != configTemplateCategoryDbList && configTemplateCategoryDbList.size() > 0){
                for(ConfigTemplateCategory t : configTemplateCategoryDbList){
                    //玩法id作为key
                    String key = t.getTemplateId()+"_"+ t.getStandardCategoryId();
                    categoryDbMap.put(key, t);
                    //主键id作为key
                    categoryIdDbMap.put(t.getId(),t);
                }
                //根据玩法表选择出玩法主键id集合
                Set<Long> categoryIDSet = configTemplateCategoryDbList.stream().map(ConfigTemplateCategory::getId).collect(Collectors.toSet());
                //根据玩法表主键id查询玩法margin数据
                categoryMarginDbList =  configCategoryMarginService.getMarginConfigurationsByCategoryIds(categoryIDSet);
                for(ConfigTemplateCategoryMargin t : categoryMarginDbList){
                    ConfigTemplateCategory tournamentCategory = categoryIdDbMap.get(t.getTemplateCategoryId());
                    if(null != tournamentCategory){
                        //模板玩法主键id作为key
                        String key = tournamentCategory.getTemplateId()+"_"+t.getStandardCategoryId()+"_"+t.getTimeFrame();
                        categoryMarginIdDbMap.put(key, t);
                    }else{
                        log.info("根据玩法主键id:{},没有查询到模板玩法",t.getTemplateCategoryId());
                    }
                }
            }else{
                log.info("根据模板id:{}，查询联赛赛下没有玩法数据",dto.getTemplateId());
            }
            //判断db中模板对应的玩法是否需要取消，当前接收到的模板玩法不包含DB 中的玩法，需要被取消
            cancelTemplateCategorysAndMargins(dto, configTemplateCategoryDbList, categoryMarginDbList);
            List<ConfigTemplateCategoryDTO> tempeCategorys =  dto.getTemplateCategoryDTOList();
            if(CollectionUtils.isNotEmpty(tempeCategorys)){
                List<ConfigTemplateCategoryDTO> updateCategorys = tempeCategorys.stream().filter( e -> 1L == e.getIsSell()).collect(Collectors.toList());
                dto.setTemplateCategoryDTOList(updateCategorys);
            }
            this.dealUptConfigTemplateCategory(dto,categoryDbMap,categoryMarginIdDbMap);
        }else{
            log.info("入参中，模板下没有配置玩法数据");
        }
        //模板下设置事件审核时间
        if(null != dto.getTemplateEventDTOList() && dto.getTemplateEventDTOList().size() > 0){
            this.uptTournamentEvent(dto);
        }else{
            log.info("入参中，模板下没有配置事件审核时间数据");
        }
    }

    /**
     * 根据传回的数据，取消不需要的模板对应玩法及margin
     * @param dto
     * @param configTemplateCategoryDbList 根据传回的模板号 templateId从DB里获取到的玩法配置信息
     * @param categoryMarginDbList 根据传回的模板号 templateId从DB里获取到的玩法配置对应的margin配置信息
     */
    private void cancelTemplateCategorysAndMargins(TradeTournamentConfigDTO dto, List<ConfigTemplateCategory> configTemplateCategoryDbList, List<ConfigTemplateCategoryMargin>  categoryMarginDbList) {
        if(configTemplateCategoryDbList == null){
            return;
        }
        List<ConfigTemplateCategoryDTO> templateCategoryDTO = dto.getTemplateCategoryDTOList();
        //由于页面传回的值为修改，或新增的值，这里为空则不作处理
        if(CollectionUtils.isEmpty(templateCategoryDTO)){
            return;
        }
        //页面传入的需要删除的玩法id
        Set<Long> paramCancelCategoryIds = templateCategoryDTO.stream().filter(e -> !(1L == e.getIsSell())).map(ConfigTemplateCategoryDTO :: getStandardCategoryId).collect(Collectors.toSet());
        Set<Long> cancaleTemplateCatagoryIds = configTemplateCategoryDbList.stream().filter(
                e -> paramCancelCategoryIds.contains(e.getStandardCategoryId())
        ).map(ConfigTemplateCategory :: getId).collect(Collectors.toSet());

        //更新category玩法及margin
        Long now = System.currentTimeMillis();
        if(CollectionUtils.isNotEmpty(cancaleTemplateCatagoryIds)){
            templateCategoryService.cancelRecs(cancaleTemplateCatagoryIds);
            configCategoryMarginService.cancelRecsByCategoryIds(cancaleTemplateCatagoryIds);

        }

        if(categoryMarginDbList == null){
            return;
        }
        //获取到传入需要修改的玩法信息
        templateCategoryDTO = templateCategoryDTO.stream().filter(e -> 1L == e.getIsSell()).collect(Collectors.toList());
        //处理需要修改玩法的对应margin值
        if(CollectionUtils.isNotEmpty(templateCategoryDTO)){
            //config_template_category  template_id = 当前templateId  standardCategoryId in (需要取消的玩法id)
            Set<Long> cancaleMarginIds = new HashSet();

            //根据传入的参数中需要变更的玩法的 margin 与db 中的margin 对比frame 值
            for(ConfigTemplateCategoryDTO paramCategory : templateCategoryDTO){
               List<ConfigTemplateCategory> tempCategorys = configTemplateCategoryDbList.stream().filter(e -> paramCategory.getStandardCategoryId().equals(e.getStandardCategoryId())).collect(Collectors.toList());
               if(CollectionUtils.isNotEmpty(tempCategorys)){
                   ConfigTemplateCategory finalDbCategory = tempCategorys.get(0);
                   List<ConfigTemplateCategoryMargin> dBmargins = categoryMarginDbList.stream().filter(e -> e.getTemplateCategoryId().equals(finalDbCategory.getId())).collect(Collectors.toList());
                   Set marginFrames = paramCategory.getCategoryMarginDtlList().stream().map(CategoryMarginDtlDTO :: getTimeFrame).collect(Collectors.toSet());
                   cancaleMarginIds.addAll(dBmargins.stream().filter(e -> !marginFrames.contains(e.getTimeFrame())).map(ConfigTemplateCategoryMargin :: getId).collect(Collectors.toSet()));
               }

            }

            //更新
            if(CollectionUtils.isNotEmpty(cancaleMarginIds)){
                configCategoryMarginService.cancelRecsByMarginIds(cancaleMarginIds);
            }
        }
    }

    /**
     * 查询模板玩法数据
     * @param dto
     * @return
     */
    public  List<ConfigTemplateCategory>  queryConfigTemplateCategory(TradeTournamentConfigDTO dto){
        //查询数据中模板玩法设置
        List<ConfigTemplateCategory>  configTemplateCategoryDbList =  templateCategoryService.getCategoryConfigurationByTemplateId(dto.getTemplateId());
        return configTemplateCategoryDbList;
    }


    /**
     * 处理模板玩法、玩法分时数据
     * @param dto 入参
     * @param categoryDbMap key 联赛+玩法id，对象模板玩法
     * @param categoryMarginIdDbMap key 联赛+玩法+分时 对象为玩法分时
     */
    public void dealUptConfigTemplateCategory(TradeTournamentConfigDTO dto, Map<String, ConfigTemplateCategory> categoryDbMap,
                                  Map<String, ConfigTemplateCategoryMargin> categoryMarginIdDbMap){
        //获取模板下设置的玩法
        List<ConfigTemplateCategoryDTO>  categoryMarginDTOList = dto.getTemplateCategoryDTOList();
        //新增玩法
        List<ConfigTemplateCategory> addTournamentCategoryList = new ArrayList<>();
        //修改玩法
        List<ConfigTemplateCategory> uptTournamentCategoryList = new ArrayList<>();
        //新增玩法margin集合
        List<ConfigTemplateCategoryMargin> addCategoryMarginAllList = new ArrayList<>();
        //修改玩法margin集合
        List<ConfigTemplateCategoryMargin> uptCategoryMarginAllList = new ArrayList<>();
        for(ConfigTemplateCategoryDTO categoryMarginDTO : categoryMarginDTOList){
            ConfigTemplateCategory  tournamentCategory  = new ConfigTemplateCategory();
            BeanUtils.copyProperties(categoryMarginDTO, tournamentCategory);
            //定义key：模板id、玩法id
            String key = dto.getTemplateId()+"_" + categoryMarginDTO.getStandardCategoryId();
            if (null != categoryDbMap && categoryDbMap.containsKey(key)) {
                ConfigTemplateCategory  category = categoryDbMap.get(key);
                long id = category.getId();
                tournamentCategory.setId(id);
                tournamentCategory.setCanceled(0);
                tournamentCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                uptTournamentCategoryList.add(tournamentCategory);
                //玩法下的所属分时margin集合数据
                if(null != categoryMarginDTO.getCategoryMarginDtlList() && categoryMarginDTO.getCategoryMarginDtlList().size() > 0){
                    //获取玩法分时margin
                    List<CategoryMarginDtlDTO> categoryMarginDtlMsgList = categoryMarginDTO.getCategoryMarginDtlList();
                    List<ConfigTemplateCategoryMargin> addCategoryMarginList = new ArrayList<>();
                    List<ConfigTemplateCategoryMargin> uptCategoryMarginList = new ArrayList<>();
                    //遍历玩法分时
                    for(CategoryMarginDtlDTO dtl : categoryMarginDtlMsgList){
                        ConfigTemplateCategoryMargin categoryMargin = new ConfigTemplateCategoryMargin();
                        BeanUtils.copyProperties(dtl, categoryMargin);
                        //key:模板id、玩法id、分时
                        String marginkey =dto.getTemplateId()+"_"+
                                tournamentCategory.getStandardCategoryId()+"_"+dtl.getTimeFrame();
                        if (null != categoryMarginIdDbMap && categoryMarginIdDbMap.containsKey(marginkey)) {
                            categoryMargin.setTimeFrame(dtl.getTimeFrame());
                            categoryMargin.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                            //设置主键id
                            categoryMargin.setId(categoryMarginIdDbMap.get(marginkey).getId());
                            //设置玩法id
                            categoryMargin.setStandardCategoryId(categoryMarginIdDbMap.get(marginkey).getStandardCategoryId());
                            categoryMargin.setCanceled(0);
                            uptCategoryMarginList.add(categoryMargin);
                        }else{
                            categoryMargin.setId(IdWorker.getId());
                            //设置玩法id
                            categoryMargin.setStandardCategoryId(categoryMarginDTO.getStandardCategoryId());
                            //设置玩法分时margin关联模板玩法的主键id
                            categoryMargin.setTemplateCategoryId(id);
                            categoryMargin.setCanceled(0);
                            categoryMargin.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                            categoryMargin.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                            addCategoryMarginList.add(categoryMargin);
                        }
                    }
                    addCategoryMarginAllList.addAll(addCategoryMarginList);
                    uptCategoryMarginAllList.addAll(uptCategoryMarginList);
                }else{
                    log.info("入参玩法:{},下没有设置margin值",categoryMarginDTO.getStandardCategoryId());
                }
            }else{
                log.info("根据模板id:{},玩法id:{},没有查询到玩法数据,做新增操作",dto.getTemplateId(),categoryMarginDTO.getStandardCategoryId());
                //生成主键id
                long id = IdWorker.getId();
                tournamentCategory.setId(id);
                tournamentCategory.setTemplateId(dto.getTemplateId());
                tournamentCategory.setCanceled(0);
                tournamentCategory.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                tournamentCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                addTournamentCategoryList.add(tournamentCategory);
                if(null != categoryMarginDTO.getCategoryMarginDtlList() && categoryMarginDTO.getCategoryMarginDtlList().size() >0){
                    //保存玩法分时
                    List<ConfigTemplateCategoryMargin> configCategoryMarginList = this.saveConfigCategoryMargin(id,categoryMarginDTO);
                    addCategoryMarginAllList.addAll(configCategoryMarginList);
                }else{
                    log.info("入参玩法:{}下没有设置margin值",categoryMarginDTO.getStandardCategoryId());
                }
            }

            if(null != addTournamentCategoryList  && addTournamentCategoryList.size() > 0){
                //保存模板下玩法设置
                templateCategoryService.saveBatch(addTournamentCategoryList);
            }
            if(null != uptTournamentCategoryList && uptTournamentCategoryList.size() > 0){
                templateCategoryService.updateBatch(uptTournamentCategoryList);

                Set<Long> templateCategoryIds = uptTournamentCategoryList.stream().map(ConfigTemplateCategory::getId).collect(Collectors.toSet());
                configCategoryMarginService.activateMarginConfiguration(templateCategoryIds);
            }
            if(null != addCategoryMarginAllList && addCategoryMarginAllList.size() > 0){
                configCategoryMarginService.saveBatch(addCategoryMarginAllList);
            }
            if(null != uptCategoryMarginAllList && uptCategoryMarginAllList.size() > 0){
                configCategoryMarginService.updateRecs(uptCategoryMarginAllList);
            }
            log.info("模板下玩法新增List大小:{},更新List大小：{},新增玩法margin的List大小：{},更新玩法margin的大小:{}",addTournamentCategoryList.size(),
                    uptTournamentCategoryList.size(),addCategoryMarginAllList.size(),uptCategoryMarginAllList.size());
        }
    }


    /**
     * 修改模板下设置事件审核时间
     * @param dto
     */
    public  void uptTournamentEvent(TradeTournamentConfigDTO dto){
        //查询配置数据
        List<ConfigTemplateEvent>  tournamentEventDbList =  templateEventService.getEventConfigurationByTemplateId(dto.getTemplateId());
        //定义模板id
        Map<String, ConfigTemplateEvent> tournamentIdDbMap = new HashMap();
        //数据库中联赛事件不为空
        if(CollectionUtils.isNotEmpty(tournamentEventDbList)){
            for(ConfigTemplateEvent t : tournamentEventDbList){
                //模板id、事件模板code作为key
                String key = t.getTemplateId()+"_"+ t.getEventCode();
                tournamentIdDbMap.put(key,t);
            }
            //模板下设置的事件审核
            List<ConfigTemplateEventDTO>  tournamentEventDTOList = dto.getTemplateEventDTOList();
            //新增联赛事件审核集合
            List<ConfigTemplateEvent> addTournamentEventList = new ArrayList<>();
            //修改联赛事件审核集合
            List<ConfigTemplateEvent> uptTournamentEventList = new ArrayList<>();
            for(ConfigTemplateEventDTO tournamentEventDTO : tournamentEventDTOList){
                //定义模板事件对象
                ConfigTemplateEvent  configTemplateEvent  = new ConfigTemplateEvent();
                BeanUtils.copyProperties(tournamentEventDTO, configTemplateEvent);
                //定义key:模板id、事件模板id
                String key = dto.getTemplateId()+"_" + tournamentEventDTO.getEventCode();
                if (null != tournamentIdDbMap && tournamentIdDbMap.containsKey(key)) {
                    configTemplateEvent.setId(tournamentIdDbMap.get(key).getId());
                    configTemplateEvent.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    uptTournamentEventList.add(configTemplateEvent);
                }else{
                    //设置模板id
                    configTemplateEvent.setTemplateId(dto.getTemplateId());
                    configTemplateEvent.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    configTemplateEvent.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    addTournamentEventList.add(configTemplateEvent);
                }
            }
            log.info("模板下事件新增List大小:{},更新List大小:{}",addTournamentEventList.size(),uptTournamentEventList.size());
            if(CollectionUtils.isNotEmpty(addTournamentEventList)){
                //保存模板下设置
                templateEventService.saveBatch(addTournamentEventList);
            }
            if(CollectionUtils.isNotEmpty(uptTournamentEventList) ){
                templateEventService.updateBatch(uptTournamentEventList);
            }
        }else{
            this.saveTournamentEvent(dto);
        }
    }
}
