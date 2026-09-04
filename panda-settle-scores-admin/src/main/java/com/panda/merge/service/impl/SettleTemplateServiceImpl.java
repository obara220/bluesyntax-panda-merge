package com.panda.merge.service.impl;

import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.dto.GrayAreaSettleDto;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import com.panda.merge.v2.repository.MatchSettleTemplateRelationRepository;
import com.panda.merge.v2.repository.MatchSettleTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettleTemplateServiceImpl implements ISettleTemplateService {

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    StandardSportTournamentMapper standardSportTournamentMapper;
    @Autowired
    MatchSettleTemplateMapper matchSettleTemplateMapper;
    @Autowired
    MatchSettleTemplateRelationMapper matchSettleTemplateRelationMapper;
    @Autowired
    MatchSettleGrayWeightMapper matchSettleGrayWeightMapper;
    @Autowired
    MatchSettleScoreMapper matchSettleScoreMapper;
    @Autowired
    MatchSettleTemplateRepository matchSettleTemplateRepository;
    @Autowired
    MatchSettleTemplateRelationRepository matchSettleTemplateRelationRepository;

    @Autowired
    private IMatchSettleGrayWeightService matchSettleGrayWeightService;

    @Autowired
    private IMatchSettleScoreService matchSettleScoreService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;

    private static List<String> DATA_SOURCE_CODE_LIST = new ArrayList<>();
    static {
        DATA_SOURCE_CODE_LIST.add("BG");
        DATA_SOURCE_CODE_LIST.add("KO");
        DATA_SOURCE_CODE_LIST.add("RB");
        DATA_SOURCE_CODE_LIST.add("LS");
        DATA_SOURCE_CODE_LIST.add("TS");
        DATA_SOURCE_CODE_LIST.add("BT");
        DATA_SOURCE_CODE_LIST.add("PD");
        DATA_SOURCE_CODE_LIST.add("PD2");
    }
    @Override
    public MatchSettleTemplate getTemplateByStandardMatchId(Long standardMatchId , Integer templateType) {
        if(templateType==null){
            return null;
        }
        if(templateType!= SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code&&templateType!= SettleTemplateTypeEnum.GRAY_AREA.code&&templateType!= SettleTemplateTypeEnum.COUNT_DOWEN.code){
            return null;
        }
        //1.获取缓存中的联赛结算模版
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(standardMatchId);
        StandardSportTournament standardSportTournament =standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
        MatchSettleTemplate matchSettleTemplate =null;
        //有关联记录则查询关联的联赛模版
        MatchSettleTemplateRelation matchSettleTemplateRelation = matchSettleTemplateRelationRepository.getMatchSettleTemplateRelation(standardMatchInfo.getStandardTournamentId());
        if (null!= matchSettleTemplateRelation){
            matchSettleTemplate = this.getTemplateByRelationAndType(matchSettleTemplateRelation,templateType);
        }
        //如果为空则取联赛等级模版
        if(matchSettleTemplate==null){
            MatchSettleTemplate template = matchSettleTemplateRepository.getMatchSettleTemplateByTypeAndLevel(templateType,standardSportTournament.getTournamentLevel(), standardMatchInfo.getSportId());
            if(null==template){
                return null;
            }else {
                return template;
            }
        }
        return matchSettleTemplate;
    }

    private MatchSettleTemplate getTemplateByRelationAndType(MatchSettleTemplateRelation matchSettleTemplateRelation, Integer templateType) {
        if(matchSettleTemplateRelation==null){
            return null;
        }
        Long templateId = null;
        if(templateType== SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code){
            templateId =matchSettleTemplateRelation.getTemplateSettleWeightId();
        }else if(templateType== SettleTemplateTypeEnum.GRAY_AREA.code){
            templateId =matchSettleTemplateRelation.getTemplateGrayAreaId();
        }else if(templateType== SettleTemplateTypeEnum.COUNT_DOWEN.code){
            templateId =matchSettleTemplateRelation.getTemplateCountDowenId();
        }
        //id = null 取 联赛等级模版
        if(templateId==null){
            return null;
        }
        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateRepository.getMatchSettleTemplateByPrimaryKey(templateId);
        return matchSettleTemplate;
    }
//    /**
//     * true 为灰色区间
//     * false 为非灰色区间
//     * */
//    @Override
//    public CheckIsGreyDto oneIsGray(MatchSettleTemplate template, String eventCode , Integer secondFromStart, String dataSourceCode) {
//        CheckIsGreyDto checkIsGreyDto =new CheckIsGreyDto();
//        List<GrayAreaSettleDto> list  = SettleTemplateJsonUtils.tansferGrayAreaList(template.getTemplateJson());
//        GrayAreaSettleDto grayAreaSettleDto =null;
//        for (GrayAreaSettleDto dto : list) {
//            if(dataSourceCode.equals(dto.getDataSourceCode())){
//                grayAreaSettleDto=dto;
//                break;
//            }
//        }
//        //如果没有匹配到则判断为非灰色区间
//        if(grayAreaSettleDto==null){
//            return checkIsGreyDto;
//        }
//        Integer min15SecondAdd= null;
//        Integer min5SecondAdd= null;
//        //1.事件类型
//        if(dataSourceCode.equals("corner")){
//            min15SecondAdd = grayAreaSettleDto.getCorner15Min();
//        }else if(dataSourceCode.equals("goal")){
//            min15SecondAdd = grayAreaSettleDto.getGoal15Min();
//            min5SecondAdd = grayAreaSettleDto.getGoal5Min();
//            //其他为发牌类型
//        }else {
//            min15SecondAdd = grayAreaSettleDto.getBooking15Min();
//        }
//        //2.15分钟灰色
//        //3.5分钟灰色
//        return null;
//    }

    @Override
    public Boolean judgeGrayStatus(MatchEventInfo matchEventInfo, MatchSettleTemplate template, String dataSourceCode, String grayType, Integer min, Long sportId) {
        //1.插入当前数据商的灰色区间 去重方式:  id =  standardMatchId+grayType(1 2 3  15进球 5进球 15角球)+min(2位)
        Long grayId =  this.getGrayId(matchEventInfo.getStandardMatchId(),dataSourceCode,grayType,min);
        //查询是否有，如果没有则新增
        if(grayId==null){
            return false;
        }
        MatchSettleGrayWeight matchSettleGrayWeight = matchSettleGrayWeightMapper .selectByPrimaryKey(grayId);
        // 如果有则更新事件list
        if(matchSettleGrayWeight==null){
             matchSettleGrayWeight =new MatchSettleGrayWeight();
            matchSettleGrayWeight.setId(grayId);
            matchSettleGrayWeight.setCreateTime(System.currentTimeMillis());
            matchSettleGrayWeight.setModifyTime(System.currentTimeMillis());
            matchSettleGrayWeight.setSportId(sportId);
            matchSettleGrayWeight.setStandardMatchId(matchEventInfo.getStandardMatchId());
            matchSettleGrayWeight.setDataSourceCode(dataSourceCode);
            matchSettleGrayWeight.setGrayAreaMin(min);
            matchSettleGrayWeight.setGrayCode(grayType);
            matchSettleGrayWeight.setGrayStatus(1);
            matchSettleGrayWeightMapper.insert(matchSettleGrayWeight);
        }else {
            matchSettleGrayWeight.setGrayStatus(1);
            matchSettleGrayWeightMapper.updateByPrimaryKey(matchSettleGrayWeight);
        }

        //2.根据赛事id,min grayType 查询
        MatchSettleGrayWeightExample example =new MatchSettleGrayWeightExample();
        example.createCriteria()
                .andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId())
                .andSportIdEqualTo(sportId)
                .andGrayCodeEqualTo(grayType)
                .andGrayAreaMinEqualTo(min)
                .andGrayStatusEqualTo(1);
        List<MatchSettleGrayWeight> list =matchSettleGrayWeightMapper.selectByExample(example);
        //模版验证上述数据商是否通过权限
        List<DataSourceSettleWeightDto> weightDtos = new ArrayList<>();
        if(template != null) {
            weightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(template.getTemplateJson());
        }
        //计算权重是否>=50
        Map<String,Integer>  map = new HashMap<>();
        for (DataSourceSettleWeightDto weightDto : weightDtos) {
            map.put(weightDto.getDataSourceCode(),weightDto.getGrayWeight());
        }
        Integer weight = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list) {
            Integer w= map.getOrDefault(settleGrayWeight.getDataSourceCode(), 0);
            weight+=w;
        }
        //非灰色区间权重计算
        MatchSettleGrayWeightExample example2 =new MatchSettleGrayWeightExample();
        example2.createCriteria()
                .andGrayAreaMinEqualTo(min)
                .andSportIdEqualTo(sportId)
                .andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId())
                .andGrayCodeEqualTo(grayType)
                .andGrayStatusEqualTo(0);
        List<MatchSettleGrayWeight> list2 =matchSettleGrayWeightMapper.selectByExample(example2);
        Integer weight2 = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list2) {
            Integer w= map.getOrDefault(settleGrayWeight.getDataSourceCode(), 0);
            weight2+=w;
        }

        log.info("Template:matchId:{},matchEventId:{},GrayWeightSum:{},NoGrayWeightSum:{}",matchEventInfo.getStandardMatchId(),matchEventInfo.getId(), weight,weight2);
        return weight>=50&&weight>weight2;
    }

    private Long getGrayId(Long standardMatchId,String dataSourceCode, String grayType, Integer min) {
        Integer i=DATA_SOURCE_CODE_LIST.indexOf(dataSourceCode.trim());
        Integer type=0;
        if("min15Goal".equals(grayType)){
            type=1;
        }
        if("min5Goal".equals(grayType)){
            type=2;
        }
        if("min15Corner".equals(grayType)){
            type=3;
        }
        if("booking15Min".equals(grayType)){
            type=4;
        }
        if(CommonConstant.BASKETBALL_GRAY_GAOL_6MIN.equals(grayType)){
            type=5;
        }
        String minStr ="";
        if(min<10){
            minStr="0"+min;
        }else {
            minStr=min.toString();
        }
        String idStr = ""+standardMatchId+""+type+minStr+i;
        if(i==-1){
            return null;
        }
        return Long.parseLong(idStr);
    }

    @Override
    public Boolean judgeSettleWeight(MatchSettleTemplate template, List<MatchSettleCheckInfo> checkInfoList) {
        return null;
    }

    @Override
    public Integer getTemplateGraySeconds(String type,String dataSourceCode, MatchSettleTemplate template) {
        //模版为空 则不用查询
        if(template==null){
            return null;
        }
        List<GrayAreaSettleDto> list  = SettleTemplateJsonUtils.tansferGrayAreaList(template.getTemplateJson());
        GrayAreaSettleDto grayAreaSettleDto =null;
        for (GrayAreaSettleDto dto : list) {
            if(dataSourceCode.equals(dto.getDataSourceCode())){
                grayAreaSettleDto=dto;
                break;
            }
        }
        if(grayAreaSettleDto==null){
            return null;
        }
        if(type==null){
            return null;
        }else if("min15Goal".equals(type)){
            //15分钟进球
            return grayAreaSettleDto.getGoal15Min();
        }else if("min5Goal".equals(type)){
            //5分钟进球
            return grayAreaSettleDto.getGoal5Min();
        }else if("min15Corner".equals(type)){
            return grayAreaSettleDto.getCorner15Min();
        }else if("booking15Min".equals(type)){
            //15分钟罚牌
            return grayAreaSettleDto.getBooking15Min();
        }else if(CommonConstant.BASKETBALL_GRAY_GAOL_6MIN.equals(type)){
            return grayAreaSettleDto.getGoal6Min();
        }
        return null;
    }
    /**
     * 当前数据商不是灰色比分入库的时候
     * 查询灰色关系表的所有关联比分
     * 1.根据权重判断是否属于灰色区间，或者非灰色区间权重>灰色区间权重
     * 2.如果是则修改当前比分区域不是灰色区间
     * 3.如果不是则不变
     * */
    @Override
    public void cancelGrayStatus(MatchSettleThirdScore matchSettleThirdScore ,MatchSettleScore matchSettleScore) {
        //1.插入当前数据商的灰色区间 去重方式:  id =  standardMatchId+grayType(1 2 3  15进球 5进球 15角球)+min(2位)
        //根据比分计算  grayType  min
        String grayType = SettleNumUtils.getGrayType(matchSettleScore.getEventCode(),matchSettleScore.getSettleNum());
        Integer min = SettleNumUtils.getGrayMin(matchSettleScore.getSettleNum());
        if(StringUtils.isEmpty(grayType)||min==0){
            return;
        }
        Long grayId =  this.getGrayId(matchSettleScore.getStandardMatchId(),matchSettleThirdScore.getDataSourceCode(),grayType,min);
        if(grayId==null){
            return ;
        }
        //查询是否有，如果没有则新增
        MatchSettleGrayWeight matchSettleGrayWeight = matchSettleGrayWeightMapper .selectByPrimaryKey(grayId);
        // 如果有则更新事件list
        if(matchSettleGrayWeight==null){
            matchSettleGrayWeight =new MatchSettleGrayWeight();
            matchSettleGrayWeight.setId(grayId);
            matchSettleGrayWeight.setCreateTime(System.currentTimeMillis());
            matchSettleGrayWeight.setModifyTime(System.currentTimeMillis());
            matchSettleGrayWeight.setSportId(matchSettleScore.getSportId());
            matchSettleGrayWeight.setStandardMatchId(matchSettleScore.getStandardMatchId());
            matchSettleGrayWeight.setDataSourceCode(matchSettleThirdScore.getDataSourceCode());
            matchSettleGrayWeight.setGrayAreaMin(min);
            matchSettleGrayWeight.setGrayCode(grayType);
            matchSettleGrayWeight.setGrayStatus(0);
            matchSettleGrayWeightMapper.insert(matchSettleGrayWeight);
        }else {
            return ;
        }
        //灰色区间恢复，消耗性能如果不是灰色区间，则不重复进入
        if(matchSettleScore.getIsGrey()==null||matchSettleScore.getIsGrey()==0){
            return;
        }
        MatchSettleTemplate weithtTemplate = this.getTemplateByStandardMatchId(matchSettleScore.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        //2.根据赛事id,min grayType 查询
        MatchSettleGrayWeightExample example =new MatchSettleGrayWeightExample();
        example.createCriteria()
                .andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId())
                .andSportIdEqualTo(matchSettleScore.getSportId())
                .andGrayAreaMinEqualTo(min)
                .andGrayCodeEqualTo(grayType)
                .andGrayStatusEqualTo(1);
        List<MatchSettleGrayWeight> list =matchSettleGrayWeightMapper.selectByExample(example);
        //模版验证上述数据商是否通过权限
        List<DataSourceSettleWeightDto> weightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(weithtTemplate.getTemplateJson());
        //计算权重是否>=50
        Map<String,Integer>  map = new HashMap<>();
        for (DataSourceSettleWeightDto weightDto : weightDtos) {
            map.put(weightDto.getDataSourceCode(),weightDto.getGrayWeight());
        }
        Integer weight = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list) {
            Integer w= map.get(settleGrayWeight.getDataSourceCode());
            weight+=w;
        }
        //非灰色区间权重计算
        MatchSettleGrayWeightExample example2 =new MatchSettleGrayWeightExample();
        example2.createCriteria()
                .andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId())
                .andSportIdEqualTo(matchSettleScore.getSportId())
                .andGrayAreaMinEqualTo(min)
                .andGrayCodeEqualTo(grayType)
                .andGrayStatusEqualTo(0);
        List<MatchSettleGrayWeight> list2 =matchSettleGrayWeightMapper.selectByExample(example2);
        Integer weight2 = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list2) {
            Integer w= map.get(settleGrayWeight.getDataSourceCode());
            weight2+=w;
        }
        log.info("Template:matchId:{},cancelGrayStatus:,weight:{},weight2:{}",matchSettleScore.getStandardMatchId(), weight,weight2);
       if(weight2>weight){
           //恢复当前区域为非灰色区间
           matchSettleScore.setIsGrey(0);
           matchSettleScore.setModifyTime(System.currentTimeMillis());
           matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
           log.info("Template:matchId:{},更新为非灰色区间:{}",matchSettleScore.getStandardMatchId(),matchSettleScore.getEventName());
       }
    }

    @Override
    public void batchCancelGrayStatus(String dataSourceCode ,List<MatchSettleScore> matchSettleScores) {
        //1.插入当前数据商的灰色区间 去重方式:  id =  standardMatchId+grayType(1 2 3  15进球 5进球 15角球)+min(2位)
        //根据比分计算  grayType  min
        Map<Long, Map<String, Object>> grayIds = new HashMap<>();
        for (MatchSettleScore matchSettleScore : matchSettleScores) {
            String grayType = SettleNumUtils.getGrayType(matchSettleScore.getEventCode(),matchSettleScore.getSettleNum());
            Integer min = SettleNumUtils.getGrayMin(matchSettleScore.getSettleNum());
            if(StringUtils.isEmpty(grayType)||min==0){
                continue;
            }
            Long grayId =  this.getGrayId(matchSettleScore.getStandardMatchId(),dataSourceCode,grayType,min);
            if(grayId==null){
                continue ;
            }
            Map<String, Object> valueMaps = new HashMap<>();
            valueMaps.put(CommonConstant.GRAY_MIN, min);
            valueMaps.put(CommonConstant.GRAY_TYPE, grayType);
            valueMaps.put(CommonConstant.GRAY_Score, matchSettleScore);
            grayIds.put(grayId, valueMaps);
        }

        //查询是否有，如果没有则新增
        List<MatchSettleGrayWeight> matchSettleGrayWeights = matchSettleGrayWeightService.getByIds(grayIds.keySet());
        Map<Long, Integer> oldIdMaps = matchSettleGrayWeights.stream().collect(Collectors.toMap(t->t.getId(), t->1));
        // 如果有则更新事件list
        List<MatchSettleGrayWeight> batchGrayWeights = new ArrayList<>();
        Long sportId = matchSettleScores.get(0).getSportId();
        Long standardMatchId = matchSettleScores.get(0).getStandardMatchId();
        Map<Long, Map<String, Object>> newGrayIds = new HashMap<>();
        for(Map.Entry<Long, Map<String, Object>> entry : grayIds.entrySet()) {
            if (!oldIdMaps.containsKey(entry.getKey())) {
                MatchSettleGrayWeight matchSettleGrayWeight =new MatchSettleGrayWeight();
                matchSettleGrayWeight.setId(entry.getKey());
                matchSettleGrayWeight.setCreateTime(System.currentTimeMillis());
                matchSettleGrayWeight.setModifyTime(System.currentTimeMillis());
                matchSettleGrayWeight.setSportId(sportId);
                matchSettleGrayWeight.setStandardMatchId(standardMatchId);
                matchSettleGrayWeight.setDataSourceCode(dataSourceCode);
                matchSettleGrayWeight.setGrayAreaMin(Integer.valueOf(String.valueOf(entry.getValue().get(CommonConstant.GRAY_MIN))));
                matchSettleGrayWeight.setGrayCode(String.valueOf(entry.getValue().get(CommonConstant.GRAY_TYPE)));
                matchSettleGrayWeight.setGrayStatus(0);
                batchGrayWeights.add(matchSettleGrayWeight);
                MatchSettleScore matchSettleScore = (MatchSettleScore) entry.getValue().get(CommonConstant.GRAY_Score);
                //灰色区间恢复，消耗性能如果不是灰色区间，则不重复进入
                if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()!=0){
                    newGrayIds.put(entry.getKey(), entry.getValue());
                }
            }
        }
        matchSettleGrayWeightService.saveOrUpdateBatch(batchGrayWeights);

        if (CollectionUtils.isEmpty(newGrayIds)) {
            return;
        }

        MatchSettleTemplate weithtTemplate = this.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        //2.根据赛事id,min grayType 查询
        MatchSettleGrayWeightExample example =new MatchSettleGrayWeightExample();
        example.createCriteria()
                .andStandardMatchIdEqualTo(standardMatchId)
                .andSportIdEqualTo(sportId);
        List<MatchSettleGrayWeight> list =matchSettleGrayWeightMapper.selectByExample(example);
        //模版验证上述数据商是否通过权限
        List<DataSourceSettleWeightDto> weightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(weithtTemplate.getTemplateJson());
        //计算权重是否>=50
        Map<String,Integer>  map = new HashMap<>();
        for (DataSourceSettleWeightDto weightDto : weightDtos) {
            map.put(weightDto.getDataSourceCode(),weightDto.getGrayWeight());
        }

        Map<String, Map<Integer, List<MatchSettleGrayWeight>>> grayWeightMap = list.stream().collect(Collectors.groupingBy(t->t.getGrayAreaMin()+"-"+t.getGrayCode(), Collectors.groupingBy(t->t.getGrayStatus())));
        Map<String, Object> convertNewGrayIds = newGrayIds.entrySet().stream().collect(Collectors.toMap(t->t.getValue().get(CommonConstant.GRAY_MIN)+"-"+t.getValue().get(CommonConstant.GRAY_TYPE), t->t.getValue().get(CommonConstant.GRAY_Score), (v1, v2)->v1));

        List<MatchSettleScore> batchGrayUpdates = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, List<MatchSettleGrayWeight>>> entry : grayWeightMap.entrySet()) {
            if (convertNewGrayIds.containsKey(entry.getKey())) {
                List<MatchSettleGrayWeight> withgrayStatus = entry.getValue().get(1);
                List<MatchSettleGrayWeight> withoutgrayStatus = entry.getValue().get(0);
                Integer weight = 0;
                for (MatchSettleGrayWeight settleGrayWeight : withgrayStatus) {
                    Integer w= map.get(settleGrayWeight.getDataSourceCode());
                    weight+=w;
                }
                //非灰色区间权重计算
                Integer weight2 = 0;
                for (MatchSettleGrayWeight settleGrayWeight : withoutgrayStatus) {
                    Integer w= map.get(settleGrayWeight.getDataSourceCode());
                    weight2+=w;
                }
                if(weight2>weight){
                    //恢复当前区域为非灰色区间
                    MatchSettleScore matchSettleScore = (MatchSettleScore) convertNewGrayIds.get(entry.getKey());
                    matchSettleScore.setIsGrey(0);
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    batchGrayUpdates.add(matchSettleScore);
                    log.info("Template:matchId:{},更新为非灰色区间:{}",matchSettleScore.getStandardMatchId(),matchSettleScore.getEventName());
                }
            }
        }
        matchSettleScoreService.saveOrUpdateBatch(batchGrayUpdates);
    }
}
