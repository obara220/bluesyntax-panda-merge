package com.panda.merge.rocketmq.processor;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.A99MatchOddsDiffenceDTO;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE;

@Slf4j
@Validated
@Component
public class A99MatchOddsDifferenceProcessor extends BaseProcessor {
    public void execute(Request<A99MatchOddsDiffenceDTO> request){
        A99MatchOddsDiffenceDTO data = request.getData();
        log.info("{}::接收A99赔率差值,赛事id:{},玩法集id:{},早滚:{},差值:{}", request.getLinkId(), data.getMatchId(), data.getCategorySetId(), data.getMatchType(),data.getDiffValue());
        String redisKeyPrefix = data.getMatchType() == 0 ? RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE : RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE;
        redisKeyPrefix += data.getMatchId();
        redisService.hSet(redisKeyPrefix, data.getCategorySetId(), data.getDiffValue(), 7*24*60*60);
    }

}
