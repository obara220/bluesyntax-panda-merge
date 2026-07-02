package com.panda.merge.dto;

import com.panda.merge.dto.advertise.TennisEditSecondScoreDto;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class TennisExtryScores  implements Serializable {
    //每局比分  key1 =firstNum 盘数  key2 =secondNum 局数
    private Map<Integer, Map<Integer,CommonItem>> currentScoresMap =new HashMap<>();

    public void doCalculation(MatchEventInfo data) {
        if(data.getEventCode().equals("tennis_score_change")){
            Map<Integer,CommonItem> map =currentScoresMap.get(data.getFirstNum());
            if(map==null){
                map =new HashMap<>();
                currentScoresMap.put(data.getFirstNum(),map);
            }
            CommonItem commonItem =map.get(data.getSecondNum());
            if(commonItem==null){
                commonItem =new CommonItem();
                map.put(data.getSecondNum(),commonItem);
            }
            if(data.getSecondT1()!=0||data.getSecondT2()!=0){
                commonItem.setHome(data.getSecondT1());
                commonItem.setAway(data.getSecondT2());
            }
        }
        log.info("{}::处理完毕:局比分为:{}",data.getLinkId(), currentScoresMap);
    }

    public void doCalculation(TennisEditSecondScoreDto tennisEditSecondScoreDto) {

            Map<Integer,CommonItem> map =currentScoresMap.get(tennisEditSecondScoreDto.getCurrentSet());
            if(map==null){
                map =new HashMap<>();
                currentScoresMap.put(tennisEditSecondScoreDto.getCurrentSet(),map);
            }
            CommonItem commonItem =map.get(tennisEditSecondScoreDto.getCurrentRound());
            if(commonItem==null){
                commonItem =new CommonItem();
                map.put(tennisEditSecondScoreDto.getCurrentRound(),commonItem);
            }
            if(tennisEditSecondScoreDto.getT1()!=0||tennisEditSecondScoreDto.getT2()!=0){
                commonItem.setHome(tennisEditSecondScoreDto.getT1());
                commonItem.setAway(tennisEditSecondScoreDto.getT2());
            }
    }
}
