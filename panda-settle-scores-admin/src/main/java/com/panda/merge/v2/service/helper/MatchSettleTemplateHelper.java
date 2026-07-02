package com.panda.merge.v2.service.helper;

import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;
import com.panda.merge.v2.repository.MatchSettleGrayWeightV2Repository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.repository.MatchSettleTemplateRelationRepository;
import com.panda.merge.v2.repository.MatchSettleTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MatchSettleTemplateHelper {
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private MatchSettleTemplateRepository matchSettleTemplateRepository;
    @Autowired
    private MatchSettleTemplateRelationRepository matchSettleTemplateRelationRepository;
    @Autowired
    private MatchSettleGrayWeightV2Repository matchSettleGrayWeightRepository;


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

    /**
     * 当前数据商不是灰色比分入库的时候
     * 查询灰色关系表的所有关联比分
     * 1.根据权重判断是否属于灰色区间，或者非灰色区间权重>灰色区间权重
     * 2.如果是则修改当前比分区域不是灰色区间
     * 3.如果不是则不变
     * */
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
        List<MatchSettleGrayWeight> matchSettleGrayWeights = matchSettleGrayWeightRepository.getByIds(grayIds.keySet());
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
        matchSettleGrayWeightRepository.saveOrUpdateBatch(batchGrayWeights);
        if (CollectionUtils.isEmpty(newGrayIds)) {
            return;
        }

        MatchSettleTemplate weithtTemplate = this.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        //2.根据赛事id,min grayType 查询
        List<MatchSettleGrayWeight> list =matchSettleGrayWeightRepository.getByItems(standardMatchId,sportId,null,null,null);
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
        matchSettleScoreRepository.saveOrUpdateBatch(batchGrayUpdates);
    }
}
