package com.panda.merge.advertise.event.impl;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.event.TennisEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.EventListDto;
import com.panda.merge.dto.advertise.PDFootBallEventDto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.panda.merge.advertise.common.Constant.PD;
import static com.panda.merge.advertise.common.Constant.SCORE_EVENT_LIST;

@Slf4j
@Service
public class TennisEventServiceImpl implements TennisEventService {

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private MatchScoresEventInfoMapper matchScoresEventInfoMapper;

    @Override
    public void addScoreChangeEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long startTimeSecond, Long period, String linkedId, String remark) {
        //1.事件转化专用方法
        MatchEventInfoDTO eventInfoDTO = MatchEventUtils.createMatchScoreEvent("tennis_score_change", data.getThirdMatchInfo(), matchScoreCommonVo, startTimeSecond, period, linkedId, remark);
        //4.下发MQ给实时服务
        eventProducer.sendPDEventInfo(eventInfoDTO);
    }

    @Override
    public Response eventList(MatchScoreAndTimeVo data, EventListDto eventListDto) {
        MatchScoresEventInfoExample example =new MatchScoresEventInfoExample();
        example.createCriteria().andThirdMatchSourceIdEqualTo(data.getThirdMatchInfo().getThirdMatchSourceId())
                .andDataSourceCodeEqualTo(data.getThirdMatchInfo().getDataSourceCode());
        List<MatchScoresEventInfo> list= matchScoresEventInfoMapper.selectByExample(example);
        List<PDFootBallEventDto> eventDtoList=new ArrayList<>();
        for (MatchScoresEventInfo matchScoresEventInfo : list) {
            PDFootBallEventDto pdFootBallEventDto=new PDFootBallEventDto();
            BeanUtils.copyProperties(matchScoresEventInfo,pdFootBallEventDto);
            pdFootBallEventDto.setId(matchScoresEventInfo.getId().toString());
            pdFootBallEventDto.setDanger(matchScoresEventInfo.getAddition9()==null||matchScoresEventInfo.getAddition9().equals("false")?false:true );
            eventDtoList.add(pdFootBallEventDto);
        }
        eventDtoList.sort(new Comparator<PDFootBallEventDto>() {
            @Override
            public int compare(PDFootBallEventDto o1, PDFootBallEventDto o2) {
                return o1.getEventTime()-o2.getEventTime()>0? -1:0;
            }
        });

        return Response.success(eventDtoList);
    }

}
