package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SetFreeThrowDto   extends AbstructMatchSettleDto {
    /**
     * PD赛事id
     * */
    private Long thirdMatchId;
    /**
     * 之前罚球数
     */
    private Integer oldFreeThrowNumber;
    /**
     * 罚球次数
     * */
    private Integer freeThrowNumber;
    /**
     * 主客队
     * */
    private String homeAway;

    /**
     * 当前删除的罚球ID
     */
    private long id;

    /**
     * 罚球编号、进球状态、增加删除状态
     */
    private boolean delete;

    /**
     * 第一次点击罚球1、2、3时 freeThrowNumber=当前罚球数, homeAway="home|away", type=1
     * 加操作 type=2, freeThrowNumber=最新罚球数（原+1=最新）
     * 减操作 type置空, delete=true, id=当前删除的罚球ID, freeThrowNumber=最新罚球数（原-1=最新）
     */
    private int type;

    private Long timeFromStartSecond; //篮球也是用这个做倒计时的

    /**
     * free_throw 罚球
     * free_throw_add 增加罚球
     * free_throw_sub 减少罚球
     */
    private String eventCode;

    /**
     * true 取消; false 未取消
     */
    private boolean cancel;
}
