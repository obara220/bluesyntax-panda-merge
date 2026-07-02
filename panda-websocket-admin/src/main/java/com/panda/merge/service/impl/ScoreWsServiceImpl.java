package com.panda.merge.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.api.IScoresCenterApi;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.EffectScoresCode;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.request.ConcernMatchIdEntity;
import com.panda.merge.dto.request.MatchEventRequestVo;
import com.panda.merge.dto.request.RequestVo;
import com.panda.merge.dto.response.QueryEventResponseVo;
import com.panda.merge.dto.scores.MatchScoresRequestDTO;
import com.panda.merge.event.*;
import com.panda.merge.mapper.MatchEventCommonMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchEventCommon;
import com.panda.merge.model.MatchEventCommonExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.service.IScoresCenterService;
import com.panda.merge.service.ScoreEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.panda.merge.constant.DataSourceConstant.DATA_SOURCES;


@Slf4j
@Service
public class ScoreWsServiceImpl implements ScoreEventService {
    @Autowired
    private IScoresCenterService scoresCenterService;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchEventCommonMapper matchEventCommonMapper;
    @Autowired
    RedisService redisService;

    public static String MATCH_LIST_SCORES_KEYS="MATCH_LIST_SCORES_KEYS:";

    @Override
    public Object queryScore(RequestVo requestVo) {
        if (requestVo == null) {
            return null;
        }
        if(requestVo.getPara()==null){
            return null;
        }
        List<JSONObject> response =new ArrayList<>();
        try {
            List<ConcernMatchIdEntity> concernMatchIdEntities = JSONArray.parseArray(requestVo.getPara().toString(), ConcernMatchIdEntity.class);
            //1.拼接查询条件
            List<MatchScoresRequestDTO> list = new ArrayList<>();
            for (ConcernMatchIdEntity concernMatchIdEntity : concernMatchIdEntities) {
                MatchScoresRequestDTO matchScoresRequestDTO = new MatchScoresRequestDTO();
                matchScoresRequestDTO.setStandard(concernMatchIdEntity.getStandard());
                matchScoresRequestDTO.setMatchId(concernMatchIdEntity.getMatchId());
                matchScoresRequestDTO.setAttention(concernMatchIdEntity.isAttention());
                list.add(matchScoresRequestDTO);
            }
            List<JSONObject> listMatchScores= scoresCenterService.searchListMatchScores(list);
            return listMatchScores;
        } catch (Exception e){
            log.error("推送异常：",e);
        }
        return response;
    }

    @Override
    public Object queryEvent(RequestVo requestVo) {

        MatchEventRequestVo request = JSONObject.toJavaObject( JSON.parseObject( requestVo.getPara().toString())  ,MatchEventRequestVo.class);
        Long thirdMatchInfoId =getThirdMatchIdByRequest(request);
        if(thirdMatchInfoId==null){
            return null;
        }
        //0.先查询相关赛事 获得赛事时间以及状态和阶段
        ThirdMatchInfo thirdMatchInfo =thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchInfoId);
        if(thirdMatchInfo==null){
            return null;
        }
        QueryEventResponseVo queryEventResponseVo= new QueryEventResponseVo();

        //1.查询 影响比分的code
        List<String>  codeSet= EffectScoresCode.getEffectCodeBySport(thirdMatchInfo.getSportId());

        //2.查询这个赛事对应code的所有common事件且按时间先后排序
        MatchEventCommonExample example= new MatchEventCommonExample();
        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfoId).andEventCodeIn(codeSet).andDataSourceCodeIn(DATA_SOURCES);
        //3.返回 eventList
        List<MatchEventCommon> eventCommons= matchEventCommonMapper.selectByExample(example);
        changeHomeAway(eventCommons,thirdMatchInfo);
        eventCommons=queryEventName(eventCommons);
        //4.根据事件计算当前是否暂停，当前时间，当前的阶段
        eventCommons.sort(
                new Comparator<MatchEventCommon>() {
                    @Override
                    public int compare(MatchEventCommon o1, MatchEventCommon o2) {
                        if(o1.getEventTime()>=o2.getEventTime()){
                            return -1;
                        }else {
                            return 1;
                        }
                    }
                }

        );
        queryEventResponseVo.setData(eventCommons);
        return queryEventResponseVo;
    }

    private void changeHomeAway(List<MatchEventCommon> eventCommons,  ThirdMatchInfo thirdMatchInfo) {
        if(thirdMatchInfo.getSportId()!=null&&thirdMatchInfo.getSportId().equals(1l)){
            if(thirdMatchInfo.getHomeAwayOpposite()!=null&&1==thirdMatchInfo.getHomeAwayOpposite()){
                for ( int i=0; i<=eventCommons.size()-1;i++ ) {
                    MatchEventCommon eventCommon =eventCommons.get(i);
                    if(StringUtils.isEmpty(eventCommon.getHomeAway())){
                        continue;
                    }
                    eventCommon.setHomeAway(TeamTypeEnum.homeAwayExchange(eventCommon.getHomeAway()));
                    Integer t1 =eventCommon.getHomeFirstNumber();
                    Integer t2 =eventCommon.getAwayFirstNumber();
                    Integer periodT1= eventCommon.getHomeSecondNumber();
                    Integer periodT2= eventCommon.getAwaySecondNumber();
                    Integer eventT1= eventCommon.getEventHomeNumber();
                    Integer eventT2=eventCommon.getEventAwayNumber();
                    eventCommon.setHomeFirstNumber(t2);
                    eventCommon.setAwayFirstNumber(t1);
                    eventCommon.setHomeSecondNumber(periodT2);
                    eventCommon.setAwaySecondNumber(periodT1);
                    eventCommon.setEventHomeNumber(eventT2);
                    eventCommon.setEventAwayNumber(eventT1);
                }
            }
        }
    }


    private Long getThirdMatchIdByRequest(MatchEventRequestVo request) {
        if(!request.isStandard()){
            return request.getMatchId();
        }else {
            ThirdMatchInfoExample example = new ThirdMatchInfoExample();
            example.createCriteria().andReferenceIdEqualTo(request.getMatchId()).andDataSourceCodeEqualTo(request.getDataSourceCode());
            List<ThirdMatchInfo> list = thirdMatchInfoMapper.selectByExample(example);
            if(list.size()!=0){
                return list.get(0).getId();
            }
        }
        return null;
    }

    @Override
    public List<MatchEventCommon> queryEventName( List<MatchEventCommon> list){
        if(list.size()==0){
            return list;
        }
        //1.根据赛种获得赛种 事件获取类
        List<MatchEventCommon> commonList =new ArrayList<>();
        //1.2 根据赛种事件名称类 获得事件名称
        for (MatchEventCommon matchEventCommon : list) {
            String eventName =getEventNameByEvent(matchEventCommon);
            matchEventCommon.setEventName(eventName);
            if(StringUtils.isNotEmpty(eventName)||matchEventCommon.getEventCode().equals("match_status")
                    ||matchEventCommon.getSportId().equals(8l)||matchEventCommon.getSportId().equals(9l)||matchEventCommon.getSportId().equals(10l)){
                if(matchEventCommon.getEventCode().equals("current_serve_volleyball")){
                    continue;
                }
                commonList.add(matchEventCommon);
            }
        }
        //4. 数数
        getEventNumberByEvent(commonList);
        return commonList;
    }



    private String getEventNameByEvent(MatchEventCommon matchEventCommon) {
        if(matchEventCommon.getSportId().equals(SportTypeEnum.FOOTBALL.getValue())){
            String eventName= FootballEventConstant.eventNameMap.get(matchEventCommon.getEventCode());
            return eventName;
        }
        if(matchEventCommon.getSportId().equals(SportTypeEnum.TENNIS.getValue())){
            String eventName= TennisEventConstant.eventNameMap.get(matchEventCommon.getEventCode());
            if(matchEventCommon.getEventCode().equals("tennis_score_change")){
                if(matchEventCommon.getHomeSecondNumber()!=null&&matchEventCommon.getHomeSecondNumber()==0&&matchEventCommon.getAwaySecondNumber()!=null&&matchEventCommon.getAwaySecondNumber()==0){
                    if(("1").equals(matchEventCommon.getExtraInfo())){
                        //ACE
                        return TennisEventConstant.ACE;
                    }
                    if(("2").equals(matchEventCommon.getExtraInfo())){
                        //双发失误
                        return TennisEventConstant.FAULT;
                    }
                    if(matchEventCommon.getHomeAway().equals(matchEventCommon.getAddition3())){
                        //破发
                        return TennisEventConstant.BREAK;
                    }else {
                        //保发
                        return TennisEventConstant.SAVE;
                    }
                }
            }
            //得分
            return eventName;
        }
        //棒球
        if(matchEventCommon.getSportId().equals(SportTypeEnum.BASEBALL.getValue())){
            String eventName= BaseballEventConstant.getEventName(matchEventCommon);
            return eventName;
        }
        //斯诺克
        if(matchEventCommon.getSportId().equals(SportTypeEnum.SNOOKER.getValue())){
            if(matchEventCommon.getEventCode().equals("snooker_foul")){
                return "{\"zs\":\"犯规\",\"en\":\"Foul\"}";
            }
            String eventName= SnookerEventConstant.getEventName(matchEventCommon.getExtraInfo());
            return eventName;
        }
        //冰球
        if(matchEventCommon.getSportId().equals(SportTypeEnum.ICE_HOCKEY.getValue())){
            if(matchEventCommon.getEventCode().equals("suspension")){
                return "{\"zs\":\"罚停\",\"en\":\"Suspension\"}";
            }
            if(matchEventCommon.getEventCode().equals("goal")){
                return IceHockeyEventConstant.getEventName(matchEventCommon.getExtraInfo());
            }
        }
        //美式足球
        if(matchEventCommon.getSportId().equals(SportTypeEnum.AMERICAN_FOOTBALL.getValue())){
            String eventName=  AmericanFootballEventConstant.getEventName(matchEventCommon);
            return eventName;
        }
        return "";
    }
    private void getEventNumberByEvent(List<MatchEventCommon> list) {
        Map<String, Integer> eventCodeNumberMap = new HashMap<>();
        int snooker_foul_away =0;
        int snooker_foul_home =0;
        for (MatchEventCommon eventCommon : list) {
            String key = "";
            if (eventCommon.getSportId().equals(7L)&&(eventCommon.getEventCode().equals("ball_pot")||eventCommon.getEventCode().equals("snooker_score_change"))) {
                key =  eventCommon.getFirstNumber().toString();
            } else {
                key = eventCommon.getEventCode();
            }
            if(eventCommon.getSportId().equals(10L)&&eventCommon.getEventCode().equals("badminton_score_change")){
                key =  eventCommon.getFirstNumber().toString();
            }
            if(eventCommon.getSportId().equals(8L)&&eventCommon.getEventCode().equals("table_tennis_score_change")){
                key =  eventCommon.getFirstNumber().toString();
            }
            if(eventCommon.getSportId().equals(9L)&&eventCommon.getEventCode().equals("volleyball_score_change")){
                key =  eventCommon.getFirstNumber().toString();
            }
            if (eventCodeNumberMap.get(key) == null) {
                eventCodeNumberMap.put(key, 1);
                eventCommon.setRemark("1");
            } else {
                Integer number = eventCodeNumberMap.get(key);
                number++;
                eventCodeNumberMap.put(key, number);
                eventCommon.setRemark(number.toString());
            }
            if(eventCommon.getEventCode().equals("snooker_foul")){
                if(eventCommon.getHomeAway().equals("home")){
                    snooker_foul_home++;
                }else {
                    snooker_foul_away++;
                }
                eventCommon.setEventHomeNumber(snooker_foul_home);
                eventCommon.setEventAwayNumber(snooker_foul_away);
            }
        }
    }
}
