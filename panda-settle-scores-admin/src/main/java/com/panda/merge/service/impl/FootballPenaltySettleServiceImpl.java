package com.panda.merge.service.impl;

import com.panda.merge.mapper.MatchSettleEventMapper;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.service.IFootballPenaltySettleService;
import com.panda.merge.utils.FootballPenaltySettleEventUtils;
import com.panda.merge.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FootballPenaltySettleServiceImpl implements IFootballPenaltySettleService {
    @Autowired
    MatchSettleEventMapper matchSettleEventMapper;
    //1.自动新增事件
  public   void autoAddPenaltySettleEvent(Long standardMatchId){
      List<MatchSettleEvent> list = FootballPenaltySettleEventUtils.createInitMatchSettleEvent(standardMatchId);
      for (MatchSettleEvent matchSettleEvent : list) {
          matchSettleEventMapper.insert(matchSettleEvent);
      }
    }

    //2.手动新增轮数
    //3.回滚比分
    //4.修改比分
    //5.确认比分
    //6.
    public static  void main(String[] x){

        String idS ="500093830445666306";
        idS= idS.substring(idS.length()-15,idS.length());
        System.out.println(Long.parseLong(idS));
    }
}
