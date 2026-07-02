package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PDBasketBallSendEventDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;  // PD三方赛事id
    private Long timeFromStartSecond; //篮球也是用这个做倒计时的
    private String  eventType ;//  (1) 助攻 2）失误 3）抢断 4）盖帽 5)犯规 6) 进攻篮板 7)防守篮板 8)控球权
    private String  homeAway ;//  home  主队  away 客队
}
