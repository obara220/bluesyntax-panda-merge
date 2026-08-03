package com.panda.merge.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodWholeArrayEnum;
import com.panda.merge.dto.MatchScoresDto;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.scores.MatchScoresRequestDTO;
import com.panda.merge.dto.scores.MatchScoresStatusDto;
import com.panda.merge.dto.scores.MatchTimeInfoDTO;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchScoresSearchMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.MatchTimeInfoExample;
import com.panda.merge.service.IScoresCenterService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.BaseBallScoresUtils;
import com.panda.merge.utils.ScoreBuildUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 比分数据中心对外接口实现
 * */
@Service
@Slf4j
public class ScoresCenterApiImpl implements IScoresCenterService {
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    MatchEventInfoMapper matchEventInfoMapper;


    @Autowired
    MatchScoresSearchMapper matchScoresSearchMapper;

    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;

    @Autowired
    IScoresService scoresService;
    @Autowired
    ScoreSearchServiceImpl scoreSearchService;



    /**
     * 根据赛事ID查询比分
     * @param matchIds
     * @return
     */
    @Override
    public List<JSONObject> searchListMatchScores(List<MatchScoresRequestDTO> matchIds) {

        try {
            if (matchIds == null || matchIds.size() > 2000) {
                return null;
            }
            List<JSONObject> list = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            String key =objectMapper.writeValueAsString(matchIds);
            List<MatchScoresBetterDto> matchScoresBetterDtos = scoreSearchService.searchBetterListMatchScores(key,matchIds);
            log.info("searchListMatchScores的查询入参:{}, 返回结果:{}", JSON.toJSONString(matchIds), JSON.toJSONString(matchScoresBetterDtos));

            for (MatchScoresBetterDto matchScoresBetterDto : matchScoresBetterDtos) {
                if(matchScoresBetterDto.getPeriodNow()==null||matchScoresBetterDto.getPeriodNow().equals(0l)){
                    matchScoresBetterDto.setT1(null);
                    matchScoresBetterDto.setT2(null);
                    matchScoresBetterDto.setPeriodT1(null);
                    matchScoresBetterDto.setPeriodT2(null);
                    matchScoresBetterDto.setScoresJson(null);
                }
                addScore(list, matchScoresBetterDto);//未展示右边比分版的比分
            }
//            for (MatchScoresRequestDTO matchId : matchIds) {
//                if (matchId.isAttention()) {
//                    addAttentionScore(list, matchId);//展示右边比分版的比分
//                    break;
//                }
//            }
            //比分合法性过滤如果阶段=0则不展示比分

            //检查比分版和赛事状态如果有问题则打印日志
//            checkMatchStatus(list);
            log.info("推送比分赛事状态和时间:{}", JSON.toJSONString(list));
            return list;
        }catch (Exception e){
            log.error("推送异常：",e);
        }
        return  null;
    }



    /**
     * 组装比分
     * @param list
     * @param matchScoresBetterDto
     */
    private void addScore(List<JSONObject> list, MatchScoresBetterDto matchScoresBetterDto)
    {
        MatchScoresDto matchScoresDto = this.buildMatchScoresDto(matchScoresBetterDto);
        matchScoresDto.setOperator(matchScoresBetterDto.getOperator());
        matchScoresDto.setPdOneInfo(matchScoresBetterDto.getPdOneInfo());
        matchScoresDto.setPdTwoInfo(matchScoresBetterDto.getPdTwoInfo());
        JSONObject jsonObject = (JSONObject)JSONObject.toJSON(matchScoresDto);
        list.add(jsonObject);
    }

    /**
     * 组装赛事比分数据
     * @param matchScoresBetterDto
     * @return
     */
    private MatchScoresDto buildMatchScoresDto(MatchScoresBetterDto matchScoresBetterDto) {
        MatchScoresDto matchScoresDto =new MatchScoresDto(matchScoresBetterDto);
        if(StringUtils.isNotEmpty( matchScoresBetterDto.getScoresJson())){
            if(matchScoresBetterDto.getSportId().equals(1l)||matchScoresBetterDto.getSportId().equals(11l)){
                matchScoresDto.setAllScore(ScoreBuildUtils.buildMatchScore2ByMap(matchScoresBetterDto.getScoresJson()));
            }
//            if(matchScoresBetterDto.getSportId().equals(11L)){
//                matchScoresDto.setAllScore(ScoreBuildUtils.buildMatchScoreByMap(matchScoresBetterDto.getScoresJson()));
//            }
            if(matchScoresBetterDto.getSportId().equals(2L)){
                matchScoresDto.setAllScore(ScoreBuildUtils.buildBasketballMatchScoreByMap(matchScoresBetterDto.getScoresJson()));
                //UOF 篮球 时间字段颠倒
                if(matchScoresDto.getDataSourceType().equals(SourceTypeEnum.UOF.getCode())){
                    matchScoresBetterDto.setSecondsMatchStart(matchScoresBetterDto.getRemainingTime());
                }
            }
            if(matchScoresBetterDto.getSportId().equals(3L)){
                matchScoresDto.setAllScore(BaseBallScoresUtils.getBaseBallAllScores(matchScoresBetterDto.getScoresJson()));
            }
            if(matchScoresBetterDto.getSportId().equals(37L)){
                matchScoresDto.setPushOver(matchScoresBetterDto.getScoresJsonExtra());
                log.info("板球轮数：{}",JSON.toJSONString(matchScoresBetterDto));
            }
        }

        return matchScoresDto;
    }






    /**
     * 查询赛事时间
     * @param matchIds
     * @return
     */
    @Override
    public List<JSONObject> searchListMatchTime(List<MatchScoresRequestDTO> matchIds) {
        List<JSONObject> list= new ArrayList<>();
        Map<MatchScoresRequestDTO,Long> matchScoresRequestDTOLongMap =new HashMap<>();
        for (MatchScoresRequestDTO dto : matchIds) {
            Long thirdMatchId = getThirdMatchId(dto);
            matchScoresRequestDTOLongMap.put(dto,thirdMatchId);
            MatchTimeInfoExample example= new MatchTimeInfoExample();
            if(scoresService.isLivedataStoped(thirdMatchId)){
                example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo("0");
                List<MatchTimeInfo> times= matchTimeInfoMapper.selectByExample(example);
                if(times.size()!=0){
                    MatchTimeInfo time=times.get(0);
                    MatchTimeInfoDTO matchTimeInfoDTO =new MatchTimeInfoDTO();
                    matchTimeInfoDTO.setPeriod(time.getPeriod());
                    matchTimeInfoDTO.setRemainingTime(time.getRemainingTime());
                    matchTimeInfoDTO.setSecondFromStart(time.getSecondFromStart());
                    matchTimeInfoDTO.setTimeIsGo(time.getTimeGo());

                }
            }else {

            }
        }
        return null;
    }




    /**
     * 查询标准赛事
     * @param dto
     * @return
     */
    private Long getThirdMatchId(MatchScoresRequestDTO dto) {
        if(!dto.isStandard()){
            return dto.getMatchId();
        }
//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(dto.getMatchId());
//        if(standardMatchInfo==null){
//            return null;
//        }
        return  scoresService.checkStandardScore(dto.getMatchId());
//        return standardMatchInfo.getThirdMatchId();
    }






    /**
     * 判断阶段  是否停止
     * @param matchScoresInfo
     * @return
     */
    private boolean periodIsNotStop(MatchScoresInfo matchScoresInfo) {

        Long thirdPeriodId= matchScoresInfo.getPeriod();
        //如果 阶段不是
        Long[] arr= SportPeriodWholeArrayEnum.getPeriodsBySportId(matchScoresInfo.getSportId());
        try {
            for (Long aLong : arr) {
                //只要找到一个 对应的值，代表就是滚球阶段里面 的某一节
                if (aLong.equals(thirdPeriodId)) {
                    return true;
                }
            }
        }catch (Exception e){
            //报错说明 赛事阶段异常 默认不在打状态
            return false;
        }
        return false;
    }






}
