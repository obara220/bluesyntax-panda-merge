package com.panda.merge.advertise.utils;


import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.service.FootBallAdvertiseService;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.panda.merge.advertise.common.Constant.MATCH_EVENT_CONSUMPTION_LINK_ID;


@Service
public class RedisUtils {
    @Autowired
    RedisTemplate redisTemplate;
    private static String  PD_FOOTBALL_SCORE="PD_FOOTBALL_SCORE";
    private static String  PD_FOOTBALL_EVENT="PD_FOOTBALL_EVENT";
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    FootBallAdvertiseService footBallAdvertiseService;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    private RedisService redisService;

    /**
     * 网球推送广播
     * */
    public   void pushTennisScore(Long thirdMatchId){
        eventProducer.pushFootBallScore(thirdMatchId);
//        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
//        Response<FootBallAdvertiseVo> footBallAdvertiseVoResponse= footBallAdvertiseService.buildFootBallAdvertiseVo(response.getData());
//        redisTemplate.convertAndSend(PD_FOOTBALL_SCORE, JSONObject.toJSONString(footBallAdvertiseVoResponse.getData()));

    }
    /**
     * 推送广播
     * */
    public void pushFootBallScore(Long thirdMatchId){
        eventProducer.pushFootBallScore(thirdMatchId);
//        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
//        Response<FootBallAdvertiseVo> footBallAdvertiseVoResponse= footBallAdvertiseService.buildFootBallAdvertiseVo(response.getData());
//        redisTemplate.convertAndSend(PD_FOOTBALL_SCORE, JSONObject.toJSONString(footBallAdvertiseVoResponse.getData()));

    }
    /**
     * 推送广播
     * */
    public   void pushFootBallEvent(Long thirdMatchId){
        eventProducer.pushFootBallEvent(thirdMatchId);
//        MatchScoresEventInfoExample example =new MatchScoresEventInfoExample();
//        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId)
//                .andEventCodeIn(SCORE_EVENT_LIST);
//        List<MatchScoresEventInfo> list= matchScoresEventInfoMapper.selectByExample(example);
//        List<PDFootBallEventDto> eventDtoList=new ArrayList<>();
//        for (MatchScoresEventInfo matchScoresEventInfo : list) {
//            PDFootBallEventDto pdFootBallEventDto=new PDFootBallEventDto();
//            BeanUtils.copyProperties(matchScoresEventInfo,pdFootBallEventDto);
//            pdFootBallEventDto.setDanger(matchScoresEventInfo.getAddition9()==null||matchScoresEventInfo.getAddition9().equals("false")?false:true );
//            eventDtoList.add(pdFootBallEventDto);
//        }
//        eventDtoList.sort(new Comparator<PDFootBallEventDto>() {
//            @Override
//            public int compare(PDFootBallEventDto o1, PDFootBallEventDto o2) {
//                return o1.getEventTime()-o2.getEventTime()>0? 0:-1;
//            }
//        });
//        PDFootBallMatchEventDto PDFootBallMatchEventDto =new PDFootBallMatchEventDto();
//        PDFootBallMatchEventDto.setThirdMatchId(thirdMatchId);
//        PDFootBallMatchEventDto.setEventDtoList(eventDtoList);
//        redisTemplate.convertAndSend(PD_FOOTBALL_EVENT, JSONObject.toJSONString(PDFootBallMatchEventDto));

    }

    public void pushFootBallCancelEndEvent(Long thirdMatchId, Long periodId) {
        eventProducer.pushFootBallCancelEndEvent(thirdMatchId, periodId);
    }

    public void pushFootBallPauseContinueEvent(Long thirdMatchId, Integer controlType) {
        eventProducer.pushFootBallPauseContinueEvent(thirdMatchId, controlType);
    }

    public   void pushFootBallEventPABoard(Long thirdMatchId, Long periodId){
        eventProducer.pushFootBallEventPABoard(thirdMatchId,periodId);
    }

    /**
     * 网球推送广播
     * */
    public   void pushTenniseScore(Long thirdMatchId){
        eventProducer.pushTenniseScore(thirdMatchId);

    }

    /**
     * 检查请求的linkId是否被消费，被消费返回true,没有消费返回false
     * @param linkId
     * @return
     */
    public Boolean checkRequestLinkId(String linkId){

        Object linkIdKey = redisService.get(MATCH_EVENT_CONSUMPTION_LINK_ID + linkId);
        return linkIdKey != null && !StringUtils.isAnyEmpty(linkIdKey.toString());
    }

    /**
     * 缓存linkId，防止重复消费
     * 5分钟过期
     * @param linkId
     */
    public void cacheRequestLinkId(String linkId){

        redisService.set(MATCH_EVENT_CONSUMPTION_LINK_ID + linkId,linkId, RedisConfig.REDIS_FIVE_MINS_TIME);
    }

}
