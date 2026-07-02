package com.panda.merge.timer;

import com.panda.merge.service.IWsPushService;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

public class StandardSettleScoresPushTimer implements TimerTask {

    private IWsPushService pushService;
    private Long standardMatchId;
    private String eventCode;
    @Override
    public void run(Timeout timeout) throws Exception {
        pushService.pushStandardSettleScores(standardMatchId,eventCode);
    }

    public StandardSettleScoresPushTimer(){

    }
    public StandardSettleScoresPushTimer(IWsPushService pushService,Long standardMatchId,String eventCode){
        this.pushService=pushService;
        this.standardMatchId=standardMatchId;
        this.eventCode=eventCode;
    }
}
