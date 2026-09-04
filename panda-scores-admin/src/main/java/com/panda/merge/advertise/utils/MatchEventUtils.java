package com.panda.merge.advertise.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.dto.advertise.PDBasketBallSendEventDto;
import com.panda.merge.dto.advertise.PenaltyScoresEditDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.panda.merge.advertise.common.Constant.PD;

public class MatchEventUtils {

    public static MatchEventInfoDTO createMatchScoreEvent(String eventCode,ThirdMatchInfo thirdMatchInfo, MatchScoreCommonVo matchScoreCommonVo, Long startTimeSecond, Long period, String linkedId,String remark){
        MatchEventInfoDTO matchEventInfoDTO=new MatchEventInfoDTO();
        //赛事ID
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setT1(matchScoreCommonVo.getT1());
        matchEventInfoDTO.setT2(matchScoreCommonVo.getT2());
        matchEventInfoDTO.setFirstT1(matchScoreCommonVo.getPeriodT1());
        matchEventInfoDTO.setFirstT2(matchScoreCommonVo.getPeriodT2());
        matchEventInfoDTO.setCopyLinkId(linkedId);
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setEventTime(System.currentTimeMillis());
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setHomeAway(matchScoreCommonVo.getHomeAway());
        matchEventInfoDTO.setMatchPeriodId(period);
        matchEventInfoDTO.setSecondsFromStart(startTimeSecond);
        matchEventInfoDTO.setPeriodRemainingSeconds(startTimeSecond);
        matchEventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setRemark(remark);
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        if ("reboundAttack".equals(eventCode)) {
            eventCode = "rebound_attack";
        }
        if ("reboundDefense".equals(eventCode)) {
            eventCode = "rebound_defense";
        }
        matchEventInfoDTO.setEventCode(eventCode);
        return matchEventInfoDTO;
    }

    public static MatchEventInfoDTO createMatchTimeEvent(MatchScoreAndTimeVo data, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, Long period, String linkedId) {
        MatchEventInfoDTO matchEventInfoDTO=new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        //赛事ID
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setCopyLinkId(linkedId);
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setEventTime(eventTime);
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setMatchPeriodId(period);
        matchEventInfoDTO.setSecondsFromStart(secondFromStart);
        matchEventInfoDTO.setPeriodRemainingSeconds(remainTimeMini);
        matchEventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setExtrainfo(isGo+"");
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        matchEventInfoDTO.setEventCode("time_start");
        if(data.getThirdMatchInfo().getSportId().equals(4l)){
            matchEventInfoDTO.setEventCode("time_start_stop");
        }
        matchEventInfoDTO.setCopyLinkId(data.getMatchScoresInfo().getThirdMatchId()+"_"+matchEventInfoDTO.getEventCode());
        return matchEventInfoDTO;
    }

    public static MatchEventInfoDTO createBasketballEvent(MatchScoreAndTimeVo data, PDBasketBallSendEventDto sendEventDto) {
        MatchEventInfoDTO matchEventInfoDTO=new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        Long period = matchTimeInfo.getPeriod();
        Integer timeGo = matchTimeInfo.getTimeGo();
        //根据事件编码返回前端事件状态
        String eventCode =  MatchEventUtils.getEventCodeByType( sendEventDto.getEventType());
        if ("reboundAttack".equals(eventCode)) {
            eventCode = "rebound_attack";
        }
        if ("reboundDefense".equals(eventCode)) {
            eventCode = "rebound_defense";
        }
        //赛事ID
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setCopyLinkId(sendEventDto.getLinkedId());
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setMatchPeriodId(period);
        matchEventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setExtrainfo(timeGo+"");
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        matchEventInfoDTO.setEventCode(eventCode);
        matchEventInfoDTO.setCopyLinkId(data.getMatchScoresInfo().getThirdMatchId()+"_"+matchEventInfoDTO.getEventCode());
        return matchEventInfoDTO;
    }

    public static MatchEventInfoDTO createFootballMatchTimeEvent(MatchScoreAndTimeVo data, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, Long period, String linkedId) {
        MatchEventInfoDTO matchEventInfoDTO=new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        //赛事ID
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setCopyLinkId(linkedId);
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setEventTime(eventTime);
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setMatchPeriodId(period);
        matchEventInfoDTO.setSecondsFromStart(secondFromStart);
        matchEventInfoDTO.setPeriodRemainingSeconds(remainTimeMini);
        matchEventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setExtrainfo(isGo+"");
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        matchEventInfoDTO.setEventCode("time_start");
        if(data.getThirdMatchInfo().getSportId().equals(4l)){
            matchEventInfoDTO.setEventCode("time_start_stop");
        }
        Long sportId = data.getThirdMatchInfo().getSportId();
        if (sportId.equals(1L) && (isGo.equals(0))) {
            matchEventInfoDTO.setEventCode("temporary_interruption");
        }
        if (sportId.equals(1L) && (isGo.equals(1))) {
            matchEventInfoDTO.setEventCode("game_on");
        }
        matchEventInfoDTO.setCopyLinkId(data.getMatchScoresInfo().getThirdMatchId()+"_"+matchEventInfoDTO.getEventCode());
        return matchEventInfoDTO;
    }

    public static MatchEventInfoDTO createMatchStatusEvent(MatchScoreAndTimeVo data, Long secondFromStart, Long remainTimeMini,
                                                           Long eventTime, MatchScoreCommonVo matchScoreCommonVo, Long period, String linkedId,String userName)
    {
        MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setT1(matchScoreCommonVo.getT1());
        matchEventInfoDTO.setT2(matchScoreCommonVo.getT2());
        matchEventInfoDTO.setFirstT1(matchScoreCommonVo.getPeriodT1());
        matchEventInfoDTO.setFirstT2(matchScoreCommonVo.getPeriodT2());
        matchEventInfoDTO.setCopyLinkId(linkedId);
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setEventTime(eventTime);
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setHomeAway(matchScoreCommonVo.getHomeAway());
        matchEventInfoDTO.setMatchPeriodId(period);
        matchEventInfoDTO.setSecondsFromStart(secondFromStart);
        matchEventInfoDTO.setPeriodRemainingSeconds(remainTimeMini);
        matchEventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setRemark(userName);
        // 中场休息
        Long halfTime = data.getMatchTimeInfo().getHalfTime();
        if (halfTime != null && halfTime >= 0) {
            matchEventInfoDTO.setExtrainfo(String.valueOf(halfTime));
        }
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        matchEventInfoDTO.setEventCode("match_status");
        return matchEventInfoDTO;
    }

    public static MatchEventInfoDTO createSimpleMatchEvent( MatchScoreAndTimeVo data, String homeAway, Long secondFromStart, Long remainTimeMini, Long eventTime,
                                                            String eventCode , Long period, String linkedId,String remark) {
        MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setCopyLinkId(linkedId);
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setEventTime(eventTime);
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setHomeAway(homeAway);
        matchEventInfoDTO.setMatchPeriodId(period);
        matchEventInfoDTO.setSecondsFromStart(secondFromStart);
        matchEventInfoDTO.setPeriodRemainingSeconds(remainTimeMini);
        matchEventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setRemark(remark);
        // LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        matchEventInfoDTO.setEventCode(eventCode);
        matchEventInfoDTO.setExtrainfo("1001");
        matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getThirdEventId());
        return matchEventInfoDTO;
    }
    public static  MatchScoreCommonVo getMatchScoreCommonVo(MatchScoreAndTimeVo data){
        MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(data.getMatchScoresInfo().getT1());
        matchScoreCommonVo.setT2(data.getMatchScoresInfo().getT2());
        matchScoreCommonVo.setPeriodT1(data.getMatchScoresInfo().getPeriodT1());
        matchScoreCommonVo.setPeriodT2(data.getMatchScoresInfo().getPeriodT2());
        matchScoreCommonVo.setHomeAway("none");
        return matchScoreCommonVo;
    }

    /**
     * 创建伤补时间对象，封装下发下游数据格式
     *
     * @param injuryTimeEventDto 伤补对象，事件触发初始数据
     * @param matchTimeInfo      查出的赛事时间数据
     * @param data               响应数据
     * @return 赛事事件信息
     */
    public static MatchEventInfoDTO createInjuryStopEvent(InjuryTimeEventDto injuryTimeEventDto,
                                                          MatchTimeInfo matchTimeInfo, MatchScoreAndTimeVo data) {
        MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
        //赛事ID
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setEventTime(System.currentTimeMillis());
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setMatchPeriodId(matchTimeInfo.getPeriod());
        matchEventInfoDTO.setSecondsFromStart(injuryTimeEventDto.getTimeFromStartSecond());
        matchEventInfoDTO.setThirdEventId(PD + "_" + UUID.randomUUID());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        // 伤补时间event_code: injury_time
        matchEventInfoDTO.setEventCode("injury_time");
        // 备注字段-扩展信息-extrainfo: 存放超时时间
        matchEventInfoDTO.setExtrainfo(injuryTimeEventDto.getTimeOut() + "");
//        matchEventInfoDTO.setCopyLinkId(matchScoresInfo.getThirdMatchId() + "_" + matchEventInfoDTO.getEventCode());
        matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getThirdEventId());
        matchEventInfoDTO.setRemark(injuryTimeEventDto.getOperatorName());
        return matchEventInfoDTO;
    }

    public static MatchEventInfoDTO createPenaltyEvent(PenaltyScoresEditDto penaltyScoresEditDto, MatchScoreAndTimeVo data) {
        MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
        ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        //赛事ID
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
        matchEventInfoDTO.setEventTime(System.currentTimeMillis());
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setMatchPeriodId(matchTimeInfo.getPeriod());
        matchEventInfoDTO.setSecondsFromStart(matchTimeInfo.getSecondFromStart() + (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000);
        matchEventInfoDTO.setThirdEventId(PD + "_" + UUID.randomUUID());
        //LIVE_DATA
        matchEventInfoDTO.setSourceType("1");
        if ("home".equals(penaltyScoresEditDto.getHomeAway())) {
            if (Integer.valueOf(1).equals(penaltyScoresEditDto.getHome())) {
                matchEventInfoDTO.setEventCode("goal");
            } else {
                matchEventInfoDTO.setEventCode("penalty_missed");
            }
        }
        if ("away".equals(penaltyScoresEditDto.getHomeAway())) {
            if (Integer.valueOf(1).equals(penaltyScoresEditDto.getAway())) {
                matchEventInfoDTO.setEventCode("goal");
            } else {
                matchEventInfoDTO.setEventCode("penalty_missed");
            }
        }
        matchEventInfoDTO.setHomeAway(penaltyScoresEditDto.getHomeAway());
        matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getThirdEventId());
        matchEventInfoDTO.setRemark(penaltyScoresEditDto.getOperatorName());
        String scoresJson = data.getMatchScoresInfo().getScoresJson();
        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores periodScores = scoresMap.get(50L);
        if (!ObjectUtils.isEmpty(periodScores) && !ObjectUtils.isEmpty(periodScores.getGoal())) {
            matchEventInfoDTO.setT1(periodScores.getGoal().getHome());
            matchEventInfoDTO.setT2(periodScores.getGoal().getAway());
        }
        return matchEventInfoDTO;
    }

    private static Map<String,String> EVENT_CODE_MAP_TEM= new HashMap<>();
    static {
        // 助攻
        EVENT_CODE_MAP_TEM.put("1","assist");
        // 失误
        EVENT_CODE_MAP_TEM.put("2","turnover");
        // 推断
        EVENT_CODE_MAP_TEM.put("3","steal");
        // 盖帽
        EVENT_CODE_MAP_TEM.put("4","block");
        // 犯规
        EVENT_CODE_MAP_TEM.put("5","foul");
        // 进攻
        EVENT_CODE_MAP_TEM.put("6","reboundAttack");
        // 防守
        EVENT_CODE_MAP_TEM.put("7","reboundDefense");
        //控球权
        EVENT_CODE_MAP_TEM.put("8","possession");
    }
    // confirmOrCancel  1 可能事件  2确认事件  3取消事件
    //eventType ;  (1) 助攻  assist  2）失误 turnover 3）抢断 steal 4）盖帽 block 5)犯规 foul 6) 进攻篮板 rebound extyInfo =1  7)防守篮板 rebound extyInfo =0 8)控球权 Possession
    public static String getEventCodeByType( String eventType) {
        String eventCode =EVENT_CODE_MAP_TEM.get(eventType);
        return eventCode;
    }
    //  //  1 未命中  2投篮命中  3取消投篮
    public static String getScoreCodeByEventType( Integer eventType) {
        if(1==eventType){
            return "score_miss";
        }
        if(2==eventType){
            return "score_change";
        }
        return null;
    }

}
