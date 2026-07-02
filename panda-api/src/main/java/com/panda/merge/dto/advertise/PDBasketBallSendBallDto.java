package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PDBasketBallSendBallDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;  // PD三方赛事id
    private Long timeFromStartSecond; //篮球也是用这个做倒计时的
    private Integer ballEventType ;  //  1 未命中  2投篮命中  3取消投篮
    private Integer  score ; // 投篮比分  1  分罚球  2分投篮  3分投篮
    private String  homeAway;  //  home   away 主客队得分
    private boolean freeThrow; // 罚球状态
    private Integer freeThrowNumber; // 罚球总数
    private long ballId;
    /**
     * false: 输入 进球数/总数
     */
    private boolean input;
    /**
     * 第几个罚球 1 2 3
     */
    private long eventOrder;
}
