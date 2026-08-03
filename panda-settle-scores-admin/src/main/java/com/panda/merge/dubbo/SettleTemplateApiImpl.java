//package com.panda.merge.dubbo;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.Generator;
//import com.panda.merge.api.ISettleTemplateApi;
//import com.panda.merge.common.utils.TimeUtils;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.SettleTemplateTypeEnum;
//import com.panda.merge.dto.*;
//import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.mapper.*;
//import com.panda.merge.model.*;
//import com.panda.merge.respository.MatchTemplateRepository;
//import com.panda.merge.respository.StandardMatchInfoRepository;
//import com.panda.merge.service.IMatchSettleLogService;
//import com.panda.merge.service.StandardMatchInfoService;
//import com.panda.merge.service.StandardSportTournamentService;
//import com.panda.merge.service.impl.MatchSettleDataSourceConfigServiceImpl;
//import com.panda.merge.util.CategoryUtils;
//import com.panda.merge.utils.IdGenerator;
//import com.panda.merge.utils.SettleTemplateJsonUtils;
//import io.netty.util.internal.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.panda.merge.constant.RepositoryConstant.*;
//
//@Service
//@DubboService
//@Slf4j
//public class SettleTemplateApiImpl implements ISettleTemplateApi {
//
//    @Autowired
//    SettleTemplateExtMappper settleTemplateExtMappper;
//    @Autowired
//    MatchSettleTemplateMapper matchSettleTemplateMapper;
//    @Autowired
//    MatchSettleTemplateRelationMapper matchSettleTemplateRelationMapper;
////    @Autowired
////    StandardSportTournamentMapper standardSportTournamentMapper;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    IMatchSettleLogService matchSettleLogService;
//    @Autowired
//    MatchSettleDataSourceSwitchMapper matchSettleDataSourceSwitchMapper;
//    @Autowired
//    MatchSettleDataSourceWeightConfigMapper matchSettleDataSourceWeightConfigMapper;
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchTemplateRepository matchTemplateRepository;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//    @Autowired
//    StandardSportTournamentService standardSportTournamentService;
//
//    @Autowired
//    MatchSettleDataSourceConfigServiceImpl matchSettleDataSourceConfigService;
//
//
//    @Override
//    public Response list(TemplateListSearchDto templateListSearchDto) {
//        if(templateListSearchDto.getPage()==null||templateListSearchDto.getPage()==0){
//            templateListSearchDto.setPage(1);
//        }
//        if(templateListSearchDto.getSize()==null||templateListSearchDto.getSize()==0){
//            templateListSearchDto.setSize(50);
//        }
//        //将一些词汇做模糊配置
//        SettleTemplateJsonUtils.transforListKeyWord(templateListSearchDto);
//        Integer start = (templateListSearchDto.getPage()-1)*templateListSearchDto.getSize();
//        Integer end = templateListSearchDto.getSize();
//        templateListSearchDto.setStart(start);
//        templateListSearchDto.setEnd(end);
//        List<MatchSettleTemplateTournamentDto> list=null;
//        if(templateListSearchDto.getDataSourceWeightLevel()!=null||templateListSearchDto.getGrayAreaSetLevel()!=null||templateListSearchDto.getCountDownLevel()!=null){
//            templateListSearchDto.setGrayAreaSet(null);
//            templateListSearchDto.setDataSourceWeight(null);
//            templateListSearchDto.setCountDown(null);
//            list=settleTemplateExtMappper.listAndLevel(templateListSearchDto);
//        }else {
//            list=  settleTemplateExtMappper.list(templateListSearchDto);
//        }
//        for (MatchSettleTemplateTournamentDto matchSettleTemplateTournamentDto : list) {
//            if(matchSettleTemplateTournamentDto.getDataSourceWeightId()==null){
//                matchSettleTemplateTournamentDto.setDataSourceWeightName(matchSettleTemplateTournamentDto.getTournamentLevel()+"");
//            }
//            if(matchSettleTemplateTournamentDto.getGrayAreaSetId()==null){
//                matchSettleTemplateTournamentDto.setGrayAreaSetName(matchSettleTemplateTournamentDto.getTournamentLevel()+"");
//            }
//            if(matchSettleTemplateTournamentDto.getCountDownName()==null){
//                matchSettleTemplateTournamentDto.setCountDownName(matchSettleTemplateTournamentDto.getTournamentLevel()+"");
//            }
//        }
//        Integer total=0;
//        if(templateListSearchDto.getDataSourceWeightLevel()!=null||templateListSearchDto.getGrayAreaSetLevel()!=null||templateListSearchDto.getCountDownLevel()!=null){
//            templateListSearchDto.setGrayAreaSet(null);
//            templateListSearchDto.setDataSourceWeight(null);
//            templateListSearchDto.setCountDown(null);
//            total=settleTemplateExtMappper.listAndLevelTotal(templateListSearchDto);
//        }else {
//            total = settleTemplateExtMappper.listTotal(templateListSearchDto);
//        }
//        SettleTemplateListResponse settleTemplateListResponse =new SettleTemplateListResponse();
//        settleTemplateListResponse.setList(list);
//        settleTemplateListResponse.setTotal(total);
//        settleTemplateListResponse.setPage(templateListSearchDto.getPage());
//        settleTemplateListResponse.setSize(templateListSearchDto.getSize());
//        return Response.success(settleTemplateListResponse);
//    }
//
//    @Override
//    public Response searchTemplate(DataSourceWeightSearchDto dataSourceWeightSearchDto) {
//        StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(dataSourceWeightSearchDto.getTournamentId());
//        if(standardSportTournament==null){
//            return Response.failed("联赛不存在");
//        }
//        //1.查询当前联赛相关数据商权重模板
//        MatchSettleTemplate  matchSettleTemplate = this.getMatchSettleTemplateByTypeAndTouramentId(dataSourceWeightSearchDto.getTemplateType(),dataSourceWeightSearchDto.getTournamentId());
//        //当前不存在，则寻找联赛模版，联赛模版设置为默认设置，没有数据都关联联赛模版，有关联则走正常逻辑
//        if(matchSettleTemplate==null){
//            matchSettleTemplate= this.getMatchSettleTemplateByTypeAndTouramentLevel(dataSourceWeightSearchDto.getTemplateType(),standardSportTournament.getTournamentLevel(), dataSourceWeightSearchDto.getSportId());
//        }
//        if(matchSettleTemplate==null){
//            return Response.failed("当前选中的模版不存在");
//        }
//        //2.查询名称得到数据商权重模板
//        List<MatchSettleTemplate> list =  settleTemplateExtMappper.selectDiySettleTemplateByTypeAndName(dataSourceWeightSearchDto.getTemplateType(),dataSourceWeightSearchDto.getTemplateName(), dataSourceWeightSearchDto.getSportId());
//        //3.组装数据
//        DataSourceWeightResponse dataSourceWeightResponse = new DataSourceWeightResponse();
//        dataSourceWeightResponse.setTemplateId(matchSettleTemplate.getId());
//        dataSourceWeightResponse.setTemplateName(matchSettleTemplate.getTemplateName());
//        dataSourceWeightResponse.setTemplateList(list);
//        dataSourceWeightResponse.setTournamentId(dataSourceWeightSearchDto.getTournamentId());
//        dataSourceWeightResponse.setTemplateJson(JSONArray.parseArray(matchSettleTemplate.getTemplateJson()));
//        dataSourceWeightResponse.setTournamentIdList(dataSourceWeightSearchDto.getTournamentIdList());
//        //查询当前联赛等级模版
//        MatchSettleTemplate template = this.getMatchSettleTemplateByTypeAndTouramentLevel(dataSourceWeightSearchDto.getTemplateType(),standardSportTournament.getTournamentLevel(), dataSourceWeightSearchDto.getSportId());
//        //2858需求[将权重上限封装到模板JSON给前端]
//        if (null!=template){
//            String templateJ = template.getTemplateJson();
//            //查询当前联赛等级各数据商的权重上限
//            List<MatchSettleDataSourceWeightConfigDto> dataSourceWeightConfigs = new ArrayList<>();
//            MatchSettleDataSourceWeightConfigExample dataSourceWeightConfigExample = new MatchSettleDataSourceWeightConfigExample();
//            dataSourceWeightConfigExample.createCriteria().andTournamentLevelEqualTo(standardSportTournament.getTournamentLevel()).andSportIdEqualTo(standardSportTournament.getSportId());
//            List<MatchSettleDataSourceWeightConfig> configs = matchSettleDataSourceWeightConfigMapper.selectByExample(dataSourceWeightConfigExample);
//            if (!configs.isEmpty()){
//                configs.forEach(c->{
//                    MatchSettleDataSourceWeightConfigDto dto = new MatchSettleDataSourceWeightConfigDto();
//                    dto.setSportId(c.getSportId());
//                    dto.setTournamentLevel(c.getTournamentLevel());
//                    dto.setDataSourceCode(c.getDataSourceCode());
//                    dto.setWeightNum(c.getWeightNum());
//                    dataSourceWeightConfigs.add(dto);
//                });
//            }
//
//            if (dataSourceWeightSearchDto.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)){
//                List<DataSourceSettleWeightAndConfigDto> weightAndConfigDtos = SettleTemplateJsonUtils.dataSourceSettleWeightAndConfigDto(templateJ);
//                if (!weightAndConfigDtos.isEmpty()){
//                    weightAndConfigDtos.forEach(w->{
//                        if (!dataSourceWeightConfigs.isEmpty()){
//                            dataSourceWeightConfigs.forEach(d -> {
//                                if (w.getDataSourceCode().equals(d.getDataSourceCode())){
//                                    w.setWeightNum(d.getWeightNum());
//                                }
//                            });
//                        }
//                    });
//                }
//                template.setTemplateJson(JSONArray.toJSONString(weightAndConfigDtos));
//            }else if (dataSourceWeightSearchDto.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)){
//                List<GrayAreaSettleAndConfigDto> grayAndConfigDtos = SettleTemplateJsonUtils.dataSourceSettleGrayAndConfigDto(templateJ);
//                if (!grayAndConfigDtos.isEmpty()){
//                    grayAndConfigDtos.forEach(w->{
//                        if (!dataSourceWeightConfigs.isEmpty()){
//                            dataSourceWeightConfigs.forEach(d -> {
//                                if (w.getDataSourceCode().equals(d.getDataSourceCode())){
//                                    w.setWeightNum(d.getWeightNum());
//                                }
//                            });
//                        }
//                    });
//                }
//                template.setTemplateJson(JSONArray.toJSONString(grayAndConfigDtos));
//            }
//            dataSourceWeightResponse.setTournamentTemplate(template);
//        }
//        //4.返回前端
//        return Response.success(dataSourceWeightResponse);
//    }
//
//    @Override
//    public Response templateBatchUpdate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {
//        MatchSettleTemplate template = matchSettleTemplateMapper.selectByPrimaryKey(settleTemplateUpdateDto.getTemplateId());
//        if (template == null || template.getTemplateType() != settleTemplateUpdateDto.getTemplateType()) {
//            return Response.failed("模板类型不匹配");
//        }
//        if(settleTemplateUpdateDto.getOperationType()==null||settleTemplateUpdateDto.getOperationType()==0) {
//            for (Long tournamentId : settleTemplateUpdateDto.getTournamentIdList()) {
//                /**
//                 * 联赛级数据 所以id 可以与 联赛id相等，同时避免数据重复情况
//                 * */
//                MatchSettleTemplateRelation matchSettleTemplateRelation = matchSettleTemplateRelationMapper.selectByPrimaryKey(tournamentId);
//                if (matchSettleTemplateRelation == null) {
//                    matchSettleTemplateRelation = this.initMatchSettleTemplateRelation(tournamentId);
//                }
//                if (matchSettleTemplateRelation == null) {
//                    return Response.failed("初始化联赛关联结算模版失败: 联赛id:" + tournamentId);
//                }
//                //如果是等级联赛，则清零配置
//                if (template.getTournamentLevel() != -1) {
//                    if (settleTemplateUpdateDto.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {
//                        matchSettleTemplateRelation.setTemplateSettleWeightId(null);
//                    } else if (settleTemplateUpdateDto.getTemplateType().equals(SettleTemplateTypeEnum.COUNT_DOWEN.code)) {
//                        matchSettleTemplateRelation.setTemplateCountDowenId(null);
//                    } else if (settleTemplateUpdateDto.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
//                        matchSettleTemplateRelation.setTemplateGrayAreaId(null);
//                    }
//                } else {
//                    if (settleTemplateUpdateDto.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {
//                        matchSettleTemplateRelation.setTemplateSettleWeightId(settleTemplateUpdateDto.getTemplateId());
//                    } else if (settleTemplateUpdateDto.getTemplateType().equals(SettleTemplateTypeEnum.COUNT_DOWEN.code)) {
//                        matchSettleTemplateRelation.setTemplateCountDowenId(settleTemplateUpdateDto.getTemplateId());
//                    } else if (settleTemplateUpdateDto.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
//                        matchSettleTemplateRelation.setTemplateGrayAreaId(settleTemplateUpdateDto.getTemplateId());
//                    }
//                }
//                matchSettleTemplateRelation.setModifyTime(System.currentTimeMillis());
//                if (settleTemplateUpdateDto.getTournamentIdList().size()==1){
//                    matchSettleLogService.templateBatchSingleUpdateLog(settleTemplateUpdateDto);
//                }
//                matchSettleTemplateRelationMapper.updateByPrimaryKey(matchSettleTemplateRelation);
//                //同步更新redis
//                matchTemplateRepository.insertTemplateRelationToRedis(matchSettleTemplateRelation);
//
//            }
//            //批量修改，不重复打印日志
//            if (settleTemplateUpdateDto.getTournamentIdList().size() > 1){
//                matchSettleLogService.templateBatchUpdateLog(settleTemplateUpdateDto);
//            }
//        }else {
//            //批量刷数据商权重
//            if(settleTemplateUpdateDto.getOperationType()==1){
//                if (settleTemplateUpdateDto.getTournamentLevel() != -1) {
//                    settleTemplateExtMappper.updateBatchRelationWeightIdToLevel(settleTemplateUpdateDto.getTournamentLevel());
//                    //更新redis
//                    matchTemplateRepository.batchInsertTemplateRelationToRedis(settleTemplateUpdateDto.getTournamentLevel());
//                }else {
//                    settleTemplateExtMappper.updateBatchRelationWeightId(settleTemplateUpdateDto.getTournamentLevel(),template.getId());
//                    //更新redis
//                    matchTemplateRepository.batchInsertTemplateRelationToRedis(settleTemplateUpdateDto.getTournamentLevel());
//                }
//            }else if(settleTemplateUpdateDto.getOperationType()==3){
//                if (template.getTournamentLevel() != -1) {
//                    settleTemplateExtMappper.updateBatchRelationGrayIdToLevel(settleTemplateUpdateDto.getTournamentLevel());
//
//                    matchTemplateRepository.batchInsertTemplateRelationToRedis(settleTemplateUpdateDto.getTournamentLevel());
//                }else {
//                    settleTemplateExtMappper.updateBatchRelationGrayId(settleTemplateUpdateDto.getTournamentLevel(),template.getId());
//                    matchTemplateRepository.batchInsertTemplateRelationToRedis(settleTemplateUpdateDto.getTournamentLevel());
//                }
//            }
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response addTemplate(MatchSettleTemplateDto matchSettleTemplateDto) {
//        if(matchSettleTemplateDto.getTemplateType()==null||matchSettleTemplateDto.getTournamentLevel()==null){
//              return Response.failed("模版格式有误!");
//        }
//        if(matchSettleTemplateDto.getTournamentLevel()!=-1){
//              return Response.failed("模版格式有误!");
//        }
//        if(matchSettleTemplateDto.getTemplateType()==SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code){
//            try {
//                List l = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateDto.getTemplateJson());
//            }catch (Exception e){
//                e.printStackTrace();
//                return Response.failed("模版格式有误!");
//            }
//        }else if(matchSettleTemplateDto.getTemplateType()==SettleTemplateTypeEnum.GRAY_AREA.code){
//            try {
//                List l = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplateDto.getTemplateJson());
//            }catch (Exception e){
//                e.printStackTrace();
//                return Response.failed("模版格式有误!");
//            }
//        }else if(matchSettleTemplateDto.getTemplateType()==SettleTemplateTypeEnum.COUNT_DOWEN.code){
//            try {
//                List l = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplateDto.getTemplateJson());
//            }catch (Exception e){
//                e.printStackTrace();
//                return Response.failed("模版格式有误!");
//            }
//        }
//
//        //名字唯一性校验
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        example.createCriteria().andTournamentLevelEqualTo(-1).andTemplateNameEqualTo(matchSettleTemplateDto.getTemplateName()).andSportIdEqualTo(matchSettleTemplateDto.getSportId());
//        List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(example);
//        if(list.size()!=0){
//            return Response.failed("模版名称重复!");
//        }
//        MatchSettleTemplate template = initSettleTemplate();
//        template.setTemplateName(matchSettleTemplateDto.getTemplateName());
//        template.setTemplateJson(matchSettleTemplateDto.getTemplateJson());
//        template.setSportId(matchSettleTemplateDto.getSportId());
//        //先默认足球
//        if(template.getSportId()==null){
//            template.setSportId(1L);
//        }
//        //新增的模版都是自定义模版，默认为-1
//        template.setTournamentLevel(-1);
//        template.setTemplateType(matchSettleTemplateDto.getTemplateType());
//        matchSettleTemplateMapper.insert(template);
//        matchTemplateRepository.insertTemplateToRedis(template);
//        matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(template);
//        matchSettleTemplateDto.setId(template.getId());
//        matchSettleLogService.addSettleTemplateLog(matchSettleTemplateDto);
//        return Response.success();
//    }
//
//    @Override
//    public Response deleteTemplate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {
//        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(settleTemplateUpdateDto.getTemplateId());
//        if(matchSettleTemplate!=null&& matchSettleTemplate.getTournamentLevel()!=-1){
//            return Response.failed("联赛等级结算模版无法删除");
//        }
//        MatchSettleTemplateRelationExample example1 =new MatchSettleTemplateRelationExample();
//        example1.createCriteria().andTemplateGrayAreaIdEqualTo(matchSettleTemplate.getId()).andSportIdEqualTo(settleTemplateUpdateDto.getSportId());
//
//
//
//        MatchSettleTemplateRelationExample example2 =new MatchSettleTemplateRelationExample();
//        example2.createCriteria().andTemplateCountDowenIdEqualTo(matchSettleTemplate.getId()).andSportIdEqualTo(settleTemplateUpdateDto.getSportId());
//
//
//        MatchSettleTemplateRelationExample example3 =new MatchSettleTemplateRelationExample();
//        example3.createCriteria().andTemplateSettleWeightIdEqualTo(matchSettleTemplate.getId()).andSportIdEqualTo(settleTemplateUpdateDto.getSportId());
//
//
//        matchSettleLogService.deleteTemplateLog(settleTemplateUpdateDto);
//        matchSettleTemplateMapper.deleteByPrimaryKey(settleTemplateUpdateDto.getTemplateId());
//
//        matchSettleTemplateRelationMapper.deleteByExample(example1);
//        matchSettleTemplateRelationMapper.deleteByExample(example2);
//        matchSettleTemplateRelationMapper.deleteByExample(example3);
//
//        matchTemplateRepository.delTemplateByPrimaryKey(settleTemplateUpdateDto.getTemplateId());
//        matchTemplateRepository.delTemplateByByTypeAndLevel(settleTemplateUpdateDto.getTemplateType(),settleTemplateUpdateDto.getTournamentLevel(),settleTemplateUpdateDto.getSportId());
//        matchTemplateRepository.delTemplateRelationByExample(example1);
//        matchTemplateRepository.delTemplateRelationByExample(example2);
//        matchTemplateRepository.delTemplateRelationByExample(example3);
//        return Response.success();
//    }
//
//    @Override
//    public Response editWeightTemplate(SettleWeightTemplateUpdateDto settleTemplateUpdateDto) {
//        //1.模版转化json 验证
//        //2.如果失败则 说明模版传值错误
//        try {
//            List<DataSourceSettleWeightDto> l = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(settleTemplateUpdateDto.getWeightJson());
//            for (DataSourceSettleWeightDto dto:l){
//                if (settleTemplateUpdateDto.getSportId()==1 && (null==dto.getDataSourceCode()||null==dto.getGrayWeight()||null==dto.getBookingWeight()||null==dto.getCornerWeight()||null==dto.getGoalWeight())){
//                    return Response.failed("模版格式有误!");
//                } else if (settleTemplateUpdateDto.getSportId()==2 && (null==dto.getDataSourceCode()||null==dto.getGrayWeight()||null==dto.getGoalWeight())){
//                    return Response.failed("模版格式有误!");
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return Response.failed("模版格式有误!");
//        }
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        example.createCriteria().andTemplateNameEqualTo(settleTemplateUpdateDto.getTemplateName()).andTemplateTypeEqualTo(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code).andSportIdEqualTo(settleTemplateUpdateDto.getSportId());
//        List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(example);
//        for (MatchSettleTemplate matchSettleTemplate : list) {
//            if(!matchSettleTemplate.getId().equals(settleTemplateUpdateDto.getTemplateId()))
//                return Response.failed("模版名称重复!");
//        }
//        //3.如果成功则 进行更新如下
//        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(settleTemplateUpdateDto.getTemplateId());
//        String matchSettleTemplateJson = JSONObject.toJSONString(matchSettleTemplate);
//
//        MatchSettleTemplateDto matchSettleTemplateOld = JSONObject.parseObject(matchSettleTemplateJson,MatchSettleTemplateDto.class);
//        matchSettleTemplate.setModifyTime(System.currentTimeMillis());
//        matchSettleTemplate.setTemplateJson(settleTemplateUpdateDto.getWeightJson());
//        matchSettleTemplate.setTemplateName(settleTemplateUpdateDto.getTemplateName());
//
//        matchSettleTemplateMapper.updateByPrimaryKey(matchSettleTemplate);
//        matchSettleTemplate.setTournamentLevel(settleTemplateUpdateDto.getTournamentLevel());
//
//        matchTemplateRepository.insertTemplateToRedis(matchSettleTemplate);
//        matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(matchSettleTemplate);
//        matchSettleLogService.editWeightTemplateLog(matchSettleTemplateOld,settleTemplateUpdateDto);
//        return Response.success();
//    }
//
//    @Override
//    public Response editGrayAreaTemplate(SettleGrayTemplateUpdateDto settleGrayTemplateUpdateDto) {
//        try {
//            List<GrayAreaSettleDto> l = SettleTemplateJsonUtils.tansferGrayAreaList(settleGrayTemplateUpdateDto.getGrayJson());
//            for (GrayAreaSettleDto dto:l){
//                if (settleGrayTemplateUpdateDto.getSportId() == 1 && (null==dto.getDataSourceCode()||null==dto.getGoal5Min()||null==dto.getGoal15Min()||null==dto.getCorner15Min()||null==dto.getBooking15Min())){
//                    return Response.failed("模版格式有误!");
//                } else if (settleGrayTemplateUpdateDto.getSportId() == 2 && (null==dto.getDataSourceCode()||null==dto.getGoal6Min())){
//                    return Response.failed("模版格式有误!");
//                }
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//            return Response.failed("模版格式有误!");
//        }
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        example.createCriteria().andTemplateNameEqualTo(settleGrayTemplateUpdateDto.getTemplateName()).andTemplateTypeEqualTo(SettleTemplateTypeEnum.GRAY_AREA.code).andSportIdEqualTo(settleGrayTemplateUpdateDto.getSportId());
//        List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(example);
//        if(list.size()!=0){
//            for (MatchSettleTemplate matchSettleTemplate : list) {
//                if(!matchSettleTemplate.getId().equals(settleGrayTemplateUpdateDto.getTemplateId()))
//                    return Response.failed("模版名称重复!");
//            }
//        }
//        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(settleGrayTemplateUpdateDto.getTemplateId());
//        String matchSettleGrayAreaTemplateJson = JSONObject.toJSONString(matchSettleTemplate);
//
//        MatchSettleTemplateDto matchSettleGrayAreaTemplateOld = JSONObject.parseObject(matchSettleGrayAreaTemplateJson,MatchSettleTemplateDto.class);
//        matchSettleTemplate.setTemplateJson(settleGrayTemplateUpdateDto.getGrayJson());
//        matchSettleTemplate.setTemplateName(settleGrayTemplateUpdateDto.getTemplateName());
//        matchSettleTemplate.setModifyTime(System.currentTimeMillis());
//
//        matchSettleTemplateMapper.updateByPrimaryKey(matchSettleTemplate);
//        matchTemplateRepository.insertTemplateToRedis(matchSettleTemplate);
//        matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(matchSettleTemplate);
//        matchSettleLogService.editGrayAreaTemplateLog(matchSettleGrayAreaTemplateOld,settleGrayTemplateUpdateDto);
//        return Response.success();
//    }
//
//    @Override
//    public Response getMatchTemplateByMatchId(Long standardMatchId) {
//        StandardMatchInfo standardMatchInfo =  standardMatchInfoService.getItem(standardMatchId);
//        Long touranmentId = standardMatchInfo.getStandardTournamentId();
//        StandardSportTournament standardSportTournament=  standardSportTournamentService.getItem(touranmentId);
//        //1.查询结算模版联赛关联表，类型为数据商权重
//        MatchSettleTemplateRelationExample example = new MatchSettleTemplateRelationExample();
//        example.createCriteria().andStandardTournamentIdEqualTo(touranmentId);
//        List<MatchSettleTemplateRelation> list = matchSettleTemplateRelationMapper.selectByExample(example);
//        MatchSettleTemplate matchSettleTemplate =null;
//        //如果没有配置则走联赛等级
//        if(list.size()==0){
//            matchSettleTemplate =getMatchSettleTemplateByLevelAndType(standardSportTournament.getTournamentLevel(),SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code, standardMatchInfo.getSportId());
//        }else {
//            //有则查询联赛相关联数据商权重模版
//            MatchSettleTemplateRelation relation = list.get(0);
//            Long templateId = relation.getTemplateSettleWeightId();
//            if (templateId == null) {
//                matchSettleTemplate = getMatchSettleTemplateByLevelAndType(standardSportTournament.getTournamentLevel(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code, standardMatchInfo.getSportId());
//            } else {
//                matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(templateId);
//            }
//        }
//        if(matchSettleTemplate==null){
//            return Response.failed("无联赛关联结算模版配置");
//        }
//        //2858需求[将权重上限封装到模板JSON给前端]
//        String templateJ = matchSettleTemplate.getTemplateJson();
//        List<DataSourceSettleWeightAndConfigDto> weightAndConfigDtos = SettleTemplateJsonUtils.dataSourceSettleWeightAndConfigDto(templateJ);
//        //查询当前联赛等级各数据商的权重上限
//        List<MatchSettleDataSourceWeightConfigDto> dataSourceWeightConfigs = new ArrayList<>();
//        MatchSettleDataSourceWeightConfigExample dataSourceWeightConfigExample = new MatchSettleDataSourceWeightConfigExample();
//        dataSourceWeightConfigExample.createCriteria().andTournamentLevelEqualTo(matchSettleTemplate.getTournamentLevel()).andSportIdEqualTo(matchSettleTemplate.getSportId());
//        List<MatchSettleDataSourceWeightConfig> configs = matchSettleDataSourceWeightConfigMapper.selectByExample(dataSourceWeightConfigExample);
//        if (!configs.isEmpty()){
//            configs.forEach(c->{
//                MatchSettleDataSourceWeightConfigDto dto = new MatchSettleDataSourceWeightConfigDto();
//                dto.setSportId(c.getSportId());
//                dto.setTournamentLevel(c.getTournamentLevel());
//                dto.setDataSourceCode(c.getDataSourceCode());
//                dto.setWeightNum(c.getWeightNum());
//                dataSourceWeightConfigs.add(dto);
//            });
//        }
//        if (!weightAndConfigDtos.isEmpty()){
//            weightAndConfigDtos.forEach(w->{
//                if (!dataSourceWeightConfigs.isEmpty()){
//                    dataSourceWeightConfigs.forEach(d -> {
//                        if (w.getDataSourceCode().equals(d.getDataSourceCode())){
//                            w.setWeightNum(d.getWeightNum());
//                        }
//                    });
//                }
//            });
//        }
//        matchSettleTemplate.setTemplateJson(JSONArray.toJSONString(weightAndConfigDtos));
//
//        return Response.success(matchSettleTemplate);
//    }
//    private  MatchSettleTemplate getMatchSettleTemplateByLevelAndType(Integer level ,Integer type, Long sportId){
//        MatchSettleTemplateExample matchSettleTemplateExample =new MatchSettleTemplateExample();
//        matchSettleTemplateExample.createCriteria().andTournamentLevelEqualTo(level)
//                .andTemplateTypeEqualTo(type).andSportIdEqualTo(sportId);
//        List< MatchSettleTemplate> settleTemplates = matchSettleTemplateMapper.selectByExample(matchSettleTemplateExample);
//        if(settleTemplates.size()==0){
//            return null;
//        }else {
//           return settleTemplates.get(0);
//        }
//    }
//    private MatchSettleTemplate initSettleTemplate() {
//        MatchSettleTemplate matchSettleTemplate =new MatchSettleTemplate();
//        matchSettleTemplate.setId(IdGenerator.nextId());
//        matchSettleTemplate.setCreateTime(System.currentTimeMillis());
//        matchSettleTemplate.setModifyTime(System.currentTimeMillis());
//        return matchSettleTemplate;
//    }
//
//    private MatchSettleTemplateRelation initMatchSettleTemplateRelation(Long tournamentId) {
//        StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(tournamentId);
//        if(standardSportTournament==null){
//            return null;
//        }
//        MatchSettleTemplateRelation matchSettleTemplateRelation =new MatchSettleTemplateRelation();
//        matchSettleTemplateRelation.setId(tournamentId);
//        matchSettleTemplateRelation.setCreateTime(System.currentTimeMillis());
//        matchSettleTemplateRelation.setModifyTime(System.currentTimeMillis());
//        matchSettleTemplateRelation.setSportId(standardSportTournament.getSportId());
//        matchSettleTemplateRelation.setStandardTournamentId(tournamentId);
//        matchSettleTemplateRelationMapper.insert(matchSettleTemplateRelation);
//
//        matchTemplateRepository.insertTemplateRelationToRedis(matchSettleTemplateRelation);
//
//        return matchSettleTemplateRelation;
//    }
//
//    /**
//     * 根据联赛等级和类型查询结算模版
//     * */
//    private MatchSettleTemplate getMatchSettleTemplateByTypeAndTouramentLevel(Integer code, Integer tournamentLevel, Long sportId) {
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        example.createCriteria().andTemplateTypeEqualTo(code).andTournamentLevelEqualTo(tournamentLevel).andSportIdEqualTo(sportId);
//        List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(example);
//        if(list.size()==0){
//            return null;
//        }
//        return list.get(0);
//    }
//
//    private MatchSettleTemplate getMatchSettleTemplateByTypeAndTouramentId(Integer code, Long tournamentId) {
//        MatchSettleTemplateRelationExample example =new MatchSettleTemplateRelationExample();
//        example.createCriteria().andStandardTournamentIdEqualTo(tournamentId);
//        List<MatchSettleTemplateRelation> list = matchSettleTemplateRelationMapper.selectByExample(example);
//        if(list.size()==0){
//            return null;
//        }
//        MatchSettleTemplateRelation relation= list.get(0);
//        Long  templateId =null;
//        switch (code){
//            case 1:
//                templateId=relation.getTemplateSettleWeightId();
//                break;
//            case 2:
//                templateId=relation.getTemplateCountDowenId();
//                break;
//            case 3:
//                templateId=relation.getTemplateGrayAreaId();
//                break;
//        }
//        if(templateId==null){
//            return null;
//        }
//        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(templateId);
//        return matchSettleTemplate;
//    }
//
//
//    /**
//     * 根据球种类型和数据源编码获取数据源的开关和各联赛级别的权重信息
//     * */
//    @Override
//    public Response getDataSourceAllWeightByCode(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto){
//        //1.获取sportId下的所有模板信息
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        //页面只展示联赛模板数据,专属模板不予展示
//        example.createCriteria().andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId()).andTournamentLevelGreaterThan(CategoryUtils.UN_LEVEL);
//        List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(example);
//        if(list.size()==0){
//            return Response.failed("无联赛关联结算模版配置");
//        }
//        List<MatchSettleTemplate> settleTemplates = new ArrayList<>();
//        List<MatchSettleTemplate> grayTemplates = new ArrayList<>();
//        List<AbstructMatchSettleDto> dataSourceWeightDtos = new ArrayList<>(); //没有包含灰色区间权重
//        List<AbstructMatchSettleDto> dataSourceAllWeightDtos = new ArrayList<>();//包含灰色区间权重
//        //先只取两种情况DATA_SOURCE_WEIGHT 与 GRAY_AREA  后续需要增加倒计时 COUNT_DOWEN
//        list.forEach(e->{
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)){
//                settleTemplates.add(e);
//            }
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)){
//                grayTemplates.add(e);
//            }
//        });
//        try{
//            getSettleTemplatesData(matchSettleDataSourceWeightAndSwitchDto, settleTemplates, dataSourceWeightDtos);
//        }catch (Exception e){
//            e.printStackTrace();
//            return Response.failed("数据商结算权重模板数据查询有误");
//        }
//
//        try {
//            getGrayTempatesData(grayTemplates, dataSourceWeightDtos, dataSourceAllWeightDtos, matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        }catch (Exception e){
//            e.printStackTrace();
//            return Response.failed("灰色区间模板数据查询有误");
//        }
//        matchSettleDataSourceWeightAndSwitchDto.setWeightDtoList(dataSourceAllWeightDtos);
//        //获取开关状态
//        MatchSettleDataSourceSwitchExample switchExample =new MatchSettleDataSourceSwitchExample();
//        switchExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()).andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        List<MatchSettleDataSourceSwitch> switches  = matchSettleDataSourceSwitchMapper.selectByExample(switchExample);
//        if (switches.size()>0){
//            MatchSettleDataSourceSwitchDto switchDto = new MatchSettleDataSourceSwitchDto();
//            BeanUtils.copyProperties(switches.get(0),switchDto);
//            matchSettleDataSourceWeightAndSwitchDto.setSwitchDto(switchDto);
//        }
//
//        //封装各个联赛的权重上限
//        MatchSettleDataSourceWeightConfigExample weightConfigExample = new MatchSettleDataSourceWeightConfigExample();
//        weightConfigExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()).andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        List<MatchSettleDataSourceWeightConfig> weightConfigs = matchSettleDataSourceWeightConfigMapper.selectByExample(weightConfigExample);
//        if (!dataSourceAllWeightDtos.isEmpty()&&!weightConfigs.isEmpty()){
//            if(matchSettleDataSourceWeightAndSwitchDto.getSportId()==1) {
//                dataSourceAllWeightDtos.forEach(d->{
//                    weightConfigs.forEach(w->{
//                        MatchSettleDataSourceAllWeightDto weightDto = (MatchSettleDataSourceAllWeightDto)d;
//                        if (weightDto.getTournamentLevel().equals(w.getTournamentLevel())){
//                            weightDto.setWeightNum(w.getWeightNum());
//                        }
//                    });
//                });
//            } else if (matchSettleDataSourceWeightAndSwitchDto.getSportId()==2) {
//                dataSourceAllWeightDtos.forEach(d->{
//                    weightConfigs.forEach(w->{
//                        MatchSettleBasketballDataSourceAllWeightDto weightDto = (MatchSettleBasketballDataSourceAllWeightDto)d;
//                        if (weightDto.getTournamentLevel().equals(w.getTournamentLevel())){
//                            weightDto.setWeightNum(w.getWeightNum());
//                        }
//                    });
//                });
//            }
//        }
//        return Response.success(matchSettleDataSourceWeightAndSwitchDto);
//    }
//@Override
////public  Response getDataSourceAllWeightNumByCode(DataSourceWeightUpdateDto dataSourceWeightUpdateDto){
////    //封装各个联赛的权重上限
////    MatchSettleDataSourceWeightConfigExample weightConfigExample = new MatchSettleDataSourceWeightConfigExample();
////    weightConfigExample.createCriteria().andDataSourceCodeEqualTo(dataSourceWeightUpdateDto.getDataSourceCode()).andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId());
////    List<MatchSettleDataSourceWeightConfig> weightConfigs = matchSettleDataSourceWeightConfigMapper.selectByExample(weightConfigExample);
////    List<DataScoreWeightNumDto> list = new ArrayList<>();
////    if (!CollectionUtils.isEmpty(weightConfigs)){
////        weightConfigs.forEach(d->{
////            DataScoreWeightNumDto dto = new DataScoreWeightNumDto();
////            dto.setDataScoreCode(d.getDataSourceCode());
////            dto.setWeightNum(d.getWeightNum());
////            dto.setLevel(d.getTournamentLevel());
////            list.add(dto);
////        });
////    }
////        return Response.success(list);
////}
//
//    /**
//     * 根据数据源编码编辑该数据源的各联赛级别权重与开关
//     * */
//    public Response editDataSourceAllWeight(DataSourceWeightUpdateDto dataSourceWeightUpdateDto) {
//        // 解析json
//        List<MatchSettleDataSourceAllWeightDto> list;
//        try {
//            list = SettleTemplateJsonUtils.tansferMatchSettleDataSourceAllWeightDtoList(dataSourceWeightUpdateDto.getUpdateJson());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Response.failed("模版格式有误!");
//        }
//        //先只取两种情况DATA_SOURCE_WEIGHT 与 GRAY_AREA  后续需要增加倒计时 COUNT_DOWEN
//        MatchSettleTemplateExample example = new MatchSettleTemplateExample();
//        List<Integer> templateTypes = new ArrayList<>();
//        templateTypes.add(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//        templateTypes.add(SettleTemplateTypeEnum.GRAY_AREA.code);
//        //编辑数据商,不影响专属模板,只编辑联赛模板
//        example.createCriteria().andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId()).andTemplateTypeIn(templateTypes).andTournamentLevelGreaterThan(CategoryUtils.UN_LEVEL);
//        List<MatchSettleTemplate> allTemplates = matchSettleTemplateMapper.selectByExample(example);
//        List<MatchSettleTemplate> settleTemplates = new ArrayList<>();
//        List<MatchSettleTemplate> grayTemplates = new ArrayList<>();
//        allTemplates.forEach(e -> {
//            if (e.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {
//                settleTemplates.add(e);
//            }
//            if (e.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
//                grayTemplates.add(e);
//            }
//        });
//        //更新数据商结算权重模板
//        settleTemplates.forEach(s -> {
//            String matchSettleTemplateJson = JSONObject.toJSONString(s);
//            MatchSettleTemplateDto matchSettleTemplateOld = JSONObject.parseObject(matchSettleTemplateJson, MatchSettleTemplateDto.class);
//            List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateOld.getTemplateJson());
//            s.setModifyTime(System.currentTimeMillis());
//            list.forEach(l -> {
//                //模板类型和联赛等级相同
//                if (l.getTournamentLevel().equals(s.getTournamentLevel()) && l.getSportId().equals(s.getSportId())) {
//                    dataSourceSettleWeightDtos.forEach(d -> {
//                        if (d.getDataSourceCode().equals(l.getDataSourceCode())) {
//                            d.setDataSourceCode(l.getDataSourceCode());
//                            d.setCornerWeight(l.getCornerWeight());
//                            d.setBookingWeight(l.getBookingWeight());
//                            d.setGoalWeight(l.getGoalWeight());
//                            d.setGrayWeight(l.getGrayWeight());
//                        }
//                    });
//                    String updateJson = JSONObject.toJSONString(dataSourceSettleWeightDtos);
//                    s.setTemplateJson(updateJson);
//                }
//            });
//            matchSettleTemplateMapper.updateByPrimaryKey(s);
//            matchTemplateRepository.insertTemplateToRedis(s);
//            matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(s);
//            SettleWeightTemplateUpdateDto settleWeightTemplateUpdateDto = new SettleWeightTemplateUpdateDto();
//            settleWeightTemplateUpdateDto.setTemplateName(s.getTemplateName());//此逻辑不涉及改模板名,避免业务冲突赋值原有的模板名
//            settleWeightTemplateUpdateDto.setIpAddress(dataSourceWeightUpdateDto.getIpAddress());
//            settleWeightTemplateUpdateDto.setOperatorName(dataSourceWeightUpdateDto.getOperatorName());
//            settleWeightTemplateUpdateDto.setWeightJson(s.getTemplateJson());
//            if(dataSourceWeightUpdateDto.getSportId() != null) {
//                settleWeightTemplateUpdateDto.setSportId(dataSourceWeightUpdateDto.getSportId());
//            }
//            matchSettleLogService.editMatchSettleDataSourceWeightLog(matchSettleTemplateOld, settleWeightTemplateUpdateDto, dataSourceWeightUpdateDto.getDataSourceCode());
//        });
//        //更新灰色区间模板
//        grayTemplates.forEach(g -> {
//            String matchSettleTemplateJson = JSONObject.toJSONString(g);
//            MatchSettleTemplateDto matchSettleGrayAreaTemplateOld = JSONObject.parseObject(matchSettleTemplateJson, MatchSettleTemplateDto.class);
//            List<GrayAreaSettleDto> grayAreaSettleDtos = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleGrayAreaTemplateOld.getTemplateJson());
//
//            g.setModifyTime(System.currentTimeMillis());
//            list.forEach(l -> {
//                //模板类型和联赛等级相同
//                if (l.getTournamentLevel().equals(g.getTournamentLevel()) && l.getSportId().equals(g.getSportId())) {
//                    grayAreaSettleDtos.forEach(d -> {
//                        if (d.getDataSourceCode().equals(l.getDataSourceCode())) {
//                            d.setDataSourceCode(l.getDataSourceCode());
//                            d.setGoal15Min(l.getGoal15Min());
//                            d.setBooking15Min(l.getBooking15Min());
//                            d.setCorner15Min(l.getCorner15Min());
//                            d.setGoal5Min(l.getGoal5Min());
//                            d.setGoal6Min(l.getGoal6Min());
//                        }
//
//                    });
//                    String updateJson = JSONObject.toJSONString(grayAreaSettleDtos);
//                    g.setTemplateJson(updateJson);
//                }
//            });
//            matchSettleTemplateMapper.updateByPrimaryKey(g);
//            matchTemplateRepository.insertTemplateToRedis(g);
//            matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(g);
//            SettleGrayTemplateUpdateDto settleGrayTemplateUpdateDto = new SettleGrayTemplateUpdateDto();
//            settleGrayTemplateUpdateDto.setTemplateName(g.getTemplateName());//此逻辑不涉及改模板名,避免业务冲突赋值原有的模板名
//            settleGrayTemplateUpdateDto.setIpAddress(dataSourceWeightUpdateDto.getIpAddress());
//            settleGrayTemplateUpdateDto.setOperatorName(dataSourceWeightUpdateDto.getOperatorName());
//            settleGrayTemplateUpdateDto.setGrayJson(g.getTemplateJson());
//            if(dataSourceWeightUpdateDto.getSportId() != null) {
//                settleGrayTemplateUpdateDto.setSportId(dataSourceWeightUpdateDto.getSportId());
//            }
//            matchSettleLogService.editMatchSettleDataSourceGrayAreaLog(matchSettleGrayAreaTemplateOld, settleGrayTemplateUpdateDto, dataSourceWeightUpdateDto.getDataSourceCode());
//        });
//
//
//        //获取原有开关状态
//        MatchSettleDataSourceSwitchExample switchExample = new MatchSettleDataSourceSwitchExample();
//        switchExample.createCriteria().andDataSourceCodeEqualTo(dataSourceWeightUpdateDto.getDataSourceCode()).andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId());
//        List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchMapper.selectByExample(switchExample);
//        //更新开关
//        if (null != switches && switches.size() > 0) {
//            updateSwitches(dataSourceWeightUpdateDto, switches);
//        } else {
//            MatchSettleDataSourceSwitchDto dto = JSONObject.parseObject(dataSourceWeightUpdateDto.getSwitchJson(), MatchSettleDataSourceSwitchDto.class);
//            initDataSourceSwitch(dto, dataSourceWeightUpdateDto.getDataSourceCode(),dataSourceWeightUpdateDto.getSportId());
//
//        }
//
////        //更新权重上限
////        if (null!=dataSourceWeightUpdateDto.getWeightConfigJson()){
////            updateDataSourceWeightConfig(dataSourceWeightUpdateDto);
////        }
//
//        return Response.success();
//    }
//
//    public Response updateDataSourceWeightConfig(DataSourceWeightUpdateDto dataSourceWeightUpdateDto) {
//        MatchSettleDataSourceWeightConfigExample configExample = new MatchSettleDataSourceWeightConfigExample();
//        configExample.createCriteria().andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId()).andDataSourceCodeEqualTo(dataSourceWeightUpdateDto.getDataSourceCode());
//        List<MatchSettleDataSourceWeightConfig> configList = matchSettleDataSourceWeightConfigMapper.selectByExample(configExample);
//        List<MatchSettleDataSourceWeightConfigDto> configs = SettleTemplateJsonUtils.tansferMatchSettleDataSourceWeightConfigDtoList(dataSourceWeightUpdateDto.getWeightConfigJson());
//        if (null != configList && !configList.isEmpty()) {
//            configList.forEach(c -> {
//                if (!configs.isEmpty()) {
//                    configs.forEach(s -> {
//                        if (c.getSportId().equals(s.getSportId()) && c.getDataSourceCode().equals(s.getDataSourceCode()) && c.getTournamentLevel().equals(s.getTournamentLevel()) && !c.getWeightNum().equals(s.getWeightNum())) {
//                            MatchSettleDataSourceWeightConfig newConfig = new MatchSettleDataSourceWeightConfig();
//                            BeanUtils.copyProperties(c, newConfig);
//                            newConfig.setWeightNum(s.getWeightNum());
//                            newConfig.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                            matchSettleDataSourceWeightConfigMapper.updateByPrimaryKey(newConfig);
//                            MatchSettleDataSourceWeightConfigDto newDto = new MatchSettleDataSourceWeightConfigDto();
//                            newDto.setIpAddress(dataSourceWeightUpdateDto.getIpAddress());
//                            newDto.setOperatorName(dataSourceWeightUpdateDto.getOperatorName());
//                            newDto.setSportId(dataSourceWeightUpdateDto.getSportId());
//                            newDto.setWeightNum(newConfig.getWeightNum());
//                            //更新日志
//                            matchSettleLogService.editMatchDataSourceWeightConfigLog(c, newDto);
//                        }
//                    });
//                }
//            });
//
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response editDownTemplate(SettleDownTemplateUpdateDto settleDownTemplateUpdateDto) {
//        try {
//            List<DownSettleDto> l = SettleTemplateJsonUtils.tansferDownList(settleDownTemplateUpdateDto.getDownJson());
//            for (DownSettleDto dto:l){
//                if (settleDownTemplateUpdateDto.getSportId() == 1 && (null==dto.getBooking15Min()||null==dto.getCorner15Min()||null==dto.getGoal15Min())){
//                    return Response.failed("模版格式有误!");
//                } else if (settleDownTemplateUpdateDto.getSportId() != 1){
//                    return Response.failed("倒计时模板不支持该球种!");
//                }
//            }
//
//        }catch (Exception e){
//            log.error("解析倒计时模板参数有误: {}",e.getMessage());
//            return Response.failed("模版格式有误!");
//        }
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        example.createCriteria().andTemplateNameEqualTo(settleDownTemplateUpdateDto.getTemplateName()).andTemplateTypeEqualTo(SettleTemplateTypeEnum.COUNT_DOWEN.code);
//        List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(example);
//        if(list.size()!=0){
//            for (MatchSettleTemplate matchSettleTemplate : list) {
//                if(!matchSettleTemplate.getId().equals(settleDownTemplateUpdateDto.getTemplateId()))
//                    return Response.failed("模版名称重复!");
//            }
//        }
//        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(settleDownTemplateUpdateDto.getTemplateId());
//        String matchSettleDownTemplateJson = JSONObject.toJSONString(matchSettleTemplate);
//
//        MatchSettleTemplateDto matchSettleDownTemplateOld = JSONObject.parseObject(matchSettleDownTemplateJson,MatchSettleTemplateDto.class);
//        matchSettleTemplate.setTemplateJson(settleDownTemplateUpdateDto.getDownJson());
//        matchSettleTemplate.setTemplateName(settleDownTemplateUpdateDto.getTemplateName());
//        matchSettleTemplate.setModifyTime(System.currentTimeMillis());
//
//        matchSettleTemplateMapper.updateByPrimaryKey(matchSettleTemplate);
//        matchTemplateRepository.insertTemplateToRedis(matchSettleTemplate);
//        matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(matchSettleTemplate);
//        matchSettleLogService.editDownTemplateLog(matchSettleDownTemplateOld,settleDownTemplateUpdateDto);
//        return Response.success();
//    }
//
//    /**
//     * 根据球种获取模板里的数据源编码
//     * @param sportId
//     * @return
//     */
//    public Response getDataSourceFromTemlate(Long sportId){
//        if (sportId==null){
//            return  Response.failed("缺少必要参数球种类型");
//        }
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        List<Integer> templateTypes = new ArrayList<>();
//        templateTypes.add(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//        templateTypes.add(SettleTemplateTypeEnum.GRAY_AREA.code);
//        example.createCriteria().andSportIdEqualTo(sportId).andTemplateTypeIn(templateTypes).andTournamentLevelGreaterThan(CategoryUtils.UN_LEVEL);
//        List<MatchSettleTemplate> allTemplates = matchSettleTemplateMapper.selectByExample(example);
//        List<MatchSettleTemplate> settleTemplates = new ArrayList<>();
//        List<MatchSettleTemplate> grayTemplates = new ArrayList<>();
//        Set<String> codes = new HashSet<>();
//        allTemplates.forEach(e->{
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)){
//                settleTemplates.add(e);
//            }
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)){
//                grayTemplates.add(e);
//            }
//        });
//        settleTemplates.forEach(s->{
//            List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(s.getTemplateJson());
//            dataSourceSettleWeightDtos.forEach(d ->{
//                codes.add(d.getDataSourceCode());
//            });
//        });
//        grayTemplates.forEach(g->{
//            List<GrayAreaSettleDto> grayDtos = SettleTemplateJsonUtils.tansferGrayAreaList(g.getTemplateJson());
//            grayDtos.forEach(y->{
//                codes.add(y.getDataSourceCode());
//            });
//        });
//        List<String> dataSourceCodes = new ArrayList<>(codes);
//        return Response.success(dataSourceCodes);
//    }
//
//    /**
//     * 新增数据商及初始化参数
//     * @param matchSettleDataSourceWeightAndSwitchDto
//     * @return
//     */
//
//    public Response addNewDataSource(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto){
//
//        MatchSettleDataSourceSwitchExample switchExample =new MatchSettleDataSourceSwitchExample();
//        //新增数据商,专属模板也需要更新
//        switchExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()).andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        List<MatchSettleDataSourceSwitch> switches  = matchSettleDataSourceSwitchMapper.selectByExample(switchExample);
//        //初始化该数据商开关
//        if (switches==null||switches.isEmpty()){
//            initDataSourceSwitch(null,matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode(), matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        }
//        //新增权重上限
//        batchInsetDataSourceWeightConfig(matchSettleDataSourceWeightAndSwitchDto);
//
//        //更改所有联赛模板
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        List<Integer> templateTypes = new ArrayList<>();
//        templateTypes.add(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//        templateTypes.add(SettleTemplateTypeEnum.GRAY_AREA.code);
//        example.createCriteria().andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId()).andTemplateTypeIn(templateTypes);
//        List<MatchSettleTemplate> allTemplates = matchSettleTemplateMapper.selectByExample(example);
//        List<MatchSettleTemplate> settleTemplates = new ArrayList<>();
//        List<MatchSettleTemplate> grayTemplates = new ArrayList<>();
//        allTemplates.forEach(e->{
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)){
//                settleTemplates.add(e);
//            }
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)){
//                grayTemplates.add(e);
//            }
//        });
//        //更新权重模板
//        updateSettleTemlates(matchSettleDataSourceWeightAndSwitchDto, settleTemplates);
//        //更新灰色区间模板
//        updateGrayTemplates(matchSettleDataSourceWeightAndSwitchDto, grayTemplates);
//        //新增数据源日志
//        matchSettleLogService.addOrDelDataSourceLog(matchSettleDataSourceWeightAndSwitchDto,0);
//        return Response.success();
//    }
//
//    private void batchInsetDataSourceWeightConfig(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto) {
//        List<MatchSettleDataSourceWeightConfig> configs = new ArrayList<>();
//        int tourmentLevel = matchSettleDataSourceWeightAndSwitchDto.getSportId() == 1 ? SettleTemplateTypeEnum.MAX_LEVEL.code : SettleTemplateTypeEnum.BASKETBALL_MAX_LEVEL.code;
//        for (int i =1 ;i<=tourmentLevel;i++){
//            MatchSettleDataSourceWeightConfig config = new MatchSettleDataSourceWeightConfig();
//            config.setDataSourceCode(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode());
//            config.setTournamentLevel(i);
//            config.setStatus(SettleTemplateTypeEnum.ON_CODE.code);
//            config.setSportId(matchSettleDataSourceWeightAndSwitchDto.getSportId());
//            config.setWeightNum(SettleTemplateTypeEnum.INIT_TOP_WEIGHT.code);
//            config.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            config.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            configs.add(config);
//
//        }
//        matchSettleDataSourceWeightConfigMapper.batchInsert(configs);
//    }
//
//
//    /**
//     * 删除数据商
//     * @param matchSettleDataSourceWeightAndSwitchDto
//     * @return
//     */
//    public Response delDataSource(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto){
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        List<Integer> templateTypes = new ArrayList<>();
//        templateTypes.add(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//        templateTypes.add(SettleTemplateTypeEnum.GRAY_AREA.code);
//        //删除数据商,专属模板也需要更新
//        example.createCriteria().andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId()).andTemplateTypeIn(templateTypes);
//        List<MatchSettleTemplate> allTemplates = matchSettleTemplateMapper.selectByExample(example);
//        List<MatchSettleTemplate> settleTemplates = new ArrayList<>();
//        List<MatchSettleTemplate> grayTemplates = new ArrayList<>();
//        allTemplates.forEach(e->{
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)){
//                settleTemplates.add(e);
//            }
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)){
//                grayTemplates.add(e);
//            }
//        });
//        //模板中删除数据商
//        settleTemplates.forEach(s->{
//            List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(s.getTemplateJson());
//            dataSourceSettleWeightDtos.removeIf(dto->dto.getDataSourceCode().equals(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()));
//            s.setTemplateJson(JSONObject.toJSONString(dataSourceSettleWeightDtos));
//            s.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            matchSettleTemplateMapper.updateByPrimaryKey(s);
//            matchTemplateRepository.insertTemplateToRedis(s);
//            matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(s);
//        });
//        grayTemplates.forEach(g->{
//            List<GrayAreaSettleDto> grayDtos = SettleTemplateJsonUtils.tansferGrayAreaList(g.getTemplateJson());
//            grayDtos.removeIf(dto->dto.getDataSourceCode().equals(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()));
//            g.setTemplateJson(JSONObject.toJSONString(grayDtos));
//            g.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            matchSettleTemplateMapper.updateByPrimaryKey(g);
//            matchTemplateRepository.insertTemplateToRedis(g);
//            matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(g);
//        });
//        //开关里删除数据商
//        MatchSettleDataSourceSwitchExample switchExample =new MatchSettleDataSourceSwitchExample();
//        switchExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()).andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        matchSettleDataSourceSwitchMapper.deleteByExample(switchExample);
//
//        //删除数据商权重上限
//        MatchSettleDataSourceWeightConfigExample configExample = new MatchSettleDataSourceWeightConfigExample();
//        configExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()).andSportIdEqualTo(matchSettleDataSourceWeightAndSwitchDto.getSportId());
//        matchSettleDataSourceWeightConfigMapper.deleteByExample(configExample);
//
//        //删除数据源日志
//        matchSettleLogService.addOrDelDataSourceLog(matchSettleDataSourceWeightAndSwitchDto,1);
//
//        return Response.success();
//    }
//
//    /**
//     * 修改数据商编码
//     * @param dataSourceWeightUpdateDto
//     * @return
//     */
//    public Response editDataSourceCode(DataSourceWeightUpdateDto dataSourceWeightUpdateDto){
//        MatchSettleTemplateExample example =new MatchSettleTemplateExample();
//        List<Integer> templateTypes = new ArrayList<>();
//        templateTypes.add(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//        templateTypes.add(SettleTemplateTypeEnum.GRAY_AREA.code);
//        example.createCriteria().andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId()).andTemplateTypeIn(templateTypes);
//        List<MatchSettleTemplate> allTemplates = matchSettleTemplateMapper.selectByExample(example);
//        List<MatchSettleTemplate> settleTemplates = new ArrayList<>();
//        List<MatchSettleTemplate> grayTemplates = new ArrayList<>();
//        allTemplates.forEach(e->{
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)){
//                settleTemplates.add(e);
//            }
//            if(e.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)){
//                grayTemplates.add(e);
//            }
//        });
//        //编辑模板里的数据商
//        settleTemplates.forEach(s->{
//            List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(s.getTemplateJson());
//            dataSourceSettleWeightDtos.forEach(d->{
//                if (d.getDataSourceCode().equals(dataSourceWeightUpdateDto.getDataSourceCode())){
//                    d.setDataSourceCode(dataSourceWeightUpdateDto.getNewDataSourceCode());
//                }
//            });
//            s.setTemplateJson(JSONObject.toJSONString(dataSourceSettleWeightDtos));
//            matchSettleTemplateMapper.updateByPrimaryKey(s);
//            matchTemplateRepository.insertTemplateToRedis(s);
//            matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(s);
//        });
//        grayTemplates.forEach(g->{
//            List<GrayAreaSettleDto> grayDtos = SettleTemplateJsonUtils.tansferGrayAreaList(g.getTemplateJson());
//            grayDtos.forEach(y->{
//                if (y.getDataSourceCode().equals(dataSourceWeightUpdateDto.getDataSourceCode())){
//                    y.setDataSourceCode(dataSourceWeightUpdateDto.getNewDataSourceCode());
//                }
//            });
//            g.setTemplateJson(JSONObject.toJSONString(grayDtos));
//            matchSettleTemplateMapper.updateByPrimaryKey(g);
//            matchTemplateRepository.insertTemplateToRedis(g);
//            matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(g);
//        });
//        //编辑开关里的数据商
//        MatchSettleDataSourceSwitchExample switchExample =new MatchSettleDataSourceSwitchExample();
//        switchExample.createCriteria().andDataSourceCodeEqualTo(dataSourceWeightUpdateDto.getDataSourceCode()).andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId());
//        List<MatchSettleDataSourceSwitch> sourceSwitchs = matchSettleDataSourceSwitchMapper.selectByExample(switchExample);
//        if (sourceSwitchs!=null&&sourceSwitchs.size()>0){
//            MatchSettleDataSourceSwitch sourceSwitch = sourceSwitchs.get(0);
//            sourceSwitch.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            sourceSwitch.setDataSourceCode(dataSourceWeightUpdateDto.getNewDataSourceCode());
//            matchSettleDataSourceSwitchMapper.updateByPrimaryKey(sourceSwitch);
//        }
//        //修改数据商编码日志
//        matchSettleLogService.updateDataSourceCodeLog(dataSourceWeightUpdateDto);
//        return Response.success();
//    }
//
//    private void updateGrayTemplates(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto, List<MatchSettleTemplate> grayTemplates) {
//        grayTemplates.forEach(g->{
//            List<GrayAreaSettleDto> grayDtos = SettleTemplateJsonUtils.tansferGrayAreaList(g.getTemplateJson());
//            GrayAreaSettleDto dto = new GrayAreaSettleDto();
//            if(matchSettleDataSourceWeightAndSwitchDto.getSportId()==1) {
//                dto.setGoal5Min(SettleTemplateTypeEnum.INIT_GARY_TEMPLATE_SECOND.code);
//                dto.setGoal15Min(SettleTemplateTypeEnum.INIT_GARY_TEMPLATE_SECOND.code);
//                dto.setBooking15Min(SettleTemplateTypeEnum.INIT_GARY_TEMPLATE_SECOND.code);
//                dto.setCorner15Min(SettleTemplateTypeEnum.INIT_GARY_TEMPLATE_SECOND.code);
//            } else if (matchSettleDataSourceWeightAndSwitchDto.getSportId()==2) {
//                dto.setGoal6Min(SettleTemplateTypeEnum.INIT_GARY_TEMPLATE_SECOND.code);
//            }
//            dto.setDataSourceCode(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode());
//            String oldJson = JSONObject.toJSONString(grayDtos);
//            if (!oldJson.contains(dto.getDataSourceCode())){ //避免重复添加
//                grayDtos.add(dto);
//                String updateJson = JSONObject.toJSONString(grayDtos);
//                g.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                g.setTemplateJson(updateJson);
//                matchSettleTemplateMapper.updateByPrimaryKey(g);
//                matchTemplateRepository.insertTemplateToRedis(g);
//                matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(g);
//            }
//
//        });
//    }
//
//    private void updateSettleTemlates(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto, List<MatchSettleTemplate> settleTemplates) {
//        settleTemplates.forEach(s->{
//            List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(s.getTemplateJson());
//            DataSourceSettleWeightDto dto = new DataSourceSettleWeightDto();
//            dto.setGoalWeight(SettleTemplateTypeEnum.INIT_TEMPLATE_WEIGHT.code);
//            dto.setGrayWeight(SettleTemplateTypeEnum.INIT_TEMPLATE_WEIGHT.code);
//            if(matchSettleDataSourceWeightAndSwitchDto.getSportId()==1) {
//                dto.setCornerWeight(SettleTemplateTypeEnum.INIT_TEMPLATE_WEIGHT.code);
//                dto.setBookingWeight(SettleTemplateTypeEnum.INIT_TEMPLATE_WEIGHT.code);
//            }
//            dto.setDataSourceCode(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode());
//            String oldJson = JSONObject.toJSONString(dataSourceSettleWeightDtos);
//            if (!oldJson.contains(dto.getDataSourceCode())){ //避免重复添加
//                dataSourceSettleWeightDtos.add(dto);
//                String updateJson = JSONObject.toJSONString(dataSourceSettleWeightDtos);
//                s.setTemplateJson(updateJson);
//                s.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                matchSettleTemplateMapper.updateByPrimaryKey(s);
//                matchTemplateRepository.insertTemplateToRedis(s);
//                matchTemplateRepository.insertMatchSettleTemplateByTypeAndLevel(s);
//            }
//        });
//    }
//
//    private void initDataSourceSwitch(MatchSettleDataSourceSwitchDto dto,String dataSourceCode, Long sportId) {
//        MatchSettleDataSourceSwitch sourceSwitch = new MatchSettleDataSourceSwitch();
//        if (dto==null){
//            if (sportId == 1) {
//                sourceSwitch.setGray(SettleTemplateTypeEnum.OFF_CODE.code);
//                sourceSwitch.setGoal(SettleTemplateTypeEnum.OFF_CODE.code);
//                sourceSwitch.setBooking(SettleTemplateTypeEnum.OFF_CODE.code);
//                sourceSwitch.setCorner(SettleTemplateTypeEnum.OFF_CODE.code);
//                sourceSwitch.setTopWeight(SettleTemplateTypeEnum.OFF_CODE.code);
//            } else if (sportId == 2) {
//                sourceSwitch.setGray(SettleTemplateTypeEnum.OFF_CODE.code);
//                sourceSwitch.setGoal(SettleTemplateTypeEnum.OFF_CODE.code);
//                sourceSwitch.setTopWeight(SettleTemplateTypeEnum.OFF_CODE.code);
//            }
//        }else {
//            if (sportId == 1) {
//                sourceSwitch.setGray(dto.getGray());
//                sourceSwitch.setGoal(dto.getGoal());
//                sourceSwitch.setBooking(dto.getBooking());
//                sourceSwitch.setCorner(dto.getCorner());
//                sourceSwitch.setTopWeight(dto.getTopWeight());
//            } else if (sportId == 2) {
//                sourceSwitch.setGray(dto.getGray());
//                sourceSwitch.setGoal(dto.getGoal());
//                sourceSwitch.setTopWeight(dto.getTopWeight());
//            }
//        }
//        sourceSwitch.setDataSourceCode(dataSourceCode);
//        sourceSwitch.setSportId(sportId);
//        sourceSwitch.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        sourceSwitch.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleDataSourceSwitchMapper.insert(sourceSwitch);
//        matchSettleDataSourceConfigService.forceUpdateSwitchRedis(sourceSwitch.getSportId(), sourceSwitch.getDataSourceCode(), Arrays.asList(sourceSwitch));
//    }
//
//
//    private void updateSwitches(DataSourceWeightUpdateDto dataSourceWeightUpdateDto, List<MatchSettleDataSourceSwitch> switches) {
//        MatchSettleDataSourceSwitch oldSwitch = switches.get(0);
//        MatchSettleDataSourceSwitch newSwitch = new MatchSettleDataSourceSwitch();
//        BeanUtils.copyProperties(oldSwitch,newSwitch);
//        MatchSettleDataSourceSwitchDto switchDto = JSONObject.parseObject(dataSourceWeightUpdateDto.getSwitchJson(),MatchSettleDataSourceSwitchDto.class);
//        newSwitch.setGray(switchDto.getGray());
//        newSwitch.setBooking(switchDto.getBooking());
//        newSwitch.setCorner(switchDto.getCorner());
//        newSwitch.setGoal(switchDto.getGoal());
//        newSwitch.setTopWeight(switchDto.getTopWeight());
//        newSwitch.setDataSourceCode(switchDto.getDataSourceCode());
//        newSwitch.setId(oldSwitch.getId());
//        boolean tag =  JSONObject.toJSONString(newSwitch).equals(JSONObject.toJSONString(oldSwitch));
//        if (!tag){
//            newSwitch.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            matchSettleDataSourceSwitchMapper.updateByPrimaryKey(newSwitch);
//            matchSettleDataSourceConfigService.forceUpdateSwitchRedis(newSwitch.getSportId(), newSwitch.getDataSourceCode(), Arrays.asList(newSwitch));
//            switchDto.setIpAddress(dataSourceWeightUpdateDto.getIpAddress());
//            switchDto.setOperatorName(dataSourceWeightUpdateDto.getOperatorName());
//            matchSettleLogService.editMatchSettleDataSourceSwitchLog(oldSwitch,switchDto);
//        }
//
//    }
//
//    private void getGrayTempatesData(List<MatchSettleTemplate> grayTemplates, List<AbstructMatchSettleDto> dataSourceWeightDtos, List<AbstructMatchSettleDto> dataSourceAllWeightDtos, Long sportId) {
//        grayTemplates.forEach(g->{
//            List<GrayAreaSettleDto> grayDtos = SettleTemplateJsonUtils.tansferGrayAreaList(g.getTemplateJson());
//            grayDtos.forEach(y->{
//                dataSourceWeightDtos.forEach(d->{
//                    if (sportId == 1) {
//                        MatchSettleDataSourceAllWeightDto dT = (MatchSettleDataSourceAllWeightDto)d;
//                        if (dT.getDataSourceCode().equals(y.getDataSourceCode())&&(dT.getTournamentLevel().equals(g.getTournamentLevel()))){
//                            dT.setBooking15Min(y.getBooking15Min());
//                            dT.setGoal15Min(y.getGoal15Min());
//                            dT.setCorner15Min(y.getCorner15Min());
//                            dT.setGoal5Min(y.getGoal5Min());
//                            dataSourceAllWeightDtos.add(d);
//                        }
//                    } else if (sportId == 2) {
//                        MatchSettleBasketballDataSourceAllWeightDto dT = (MatchSettleBasketballDataSourceAllWeightDto)d;
//                        if (dT.getDataSourceCode().equals(y.getDataSourceCode())&&(dT.getTournamentLevel().equals(g.getTournamentLevel()))){
//                            dT.setGoal6Min(y.getGoal6Min());
//                            dataSourceAllWeightDtos.add(d);
//                        }
//                    }
//                });
//            });
//        });
//    }
//
//    private void getSettleTemplatesData(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto, List<MatchSettleTemplate> settleTemplates, List<AbstructMatchSettleDto> dataSourceWeightDtos) {
//        settleTemplates.forEach(f->{
//            List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(f.getTemplateJson());
//            dataSourceSettleWeightDtos.forEach(s ->{
//                if (s.getDataSourceCode().equals(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode())){
//                    if (matchSettleDataSourceWeightAndSwitchDto.getSportId() == 1) {
//                        MatchSettleDataSourceAllWeightDto dto = new MatchSettleDataSourceAllWeightDto();
//                        dto.setSportId(f.getSportId());
//                        dto.setTournamentLevel(f.getTournamentLevel());
//                        dto.setDataSourceCode(s.getDataSourceCode());
//                        dto.setBookingWeight(s.getBookingWeight());
//                        dto.setCornerWeight(s.getCornerWeight());
//                        dto.setGrayWeight(s.getGrayWeight());
//                        dto.setGoalWeight(s.getGoalWeight());
//                        dataSourceWeightDtos.add(dto);
//                    } else if (matchSettleDataSourceWeightAndSwitchDto.getSportId() == 2) {
//                        MatchSettleBasketballDataSourceAllWeightDto dto = new MatchSettleBasketballDataSourceAllWeightDto();
//                        dto.setSportId(f.getSportId());
//                        dto.setTournamentLevel(f.getTournamentLevel());
//                        dto.setDataSourceCode(s.getDataSourceCode());
//                        dto.setGrayWeight(s.getGrayWeight());
//                        dto.setGoalWeight(s.getGoalWeight());
//                        dataSourceWeightDtos.add(dto);
//                    }
//                }
//            });
//        });
//    }
//
//
//}
