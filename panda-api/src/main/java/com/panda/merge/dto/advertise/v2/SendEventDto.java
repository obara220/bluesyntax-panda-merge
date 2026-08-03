package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

/**
 * 发送事件DTO
 * 用于发送不影响比分的事件（如持杆、未进球、自由球未进球等）
 * 
 * @author Auto
 * @date 2026-02-01
 */
@Data
public class SendEventDto extends AbstructAdvertiseDto {
    /**
     * 运动种类ID（斯诺克：7）
     */
    private Long sportId;
    
    /**
     * 第三方赛事ID
     */
    private Long thirdMatchId;
    
    /**
     * 事件编码（如：Striker, nopot, free_ball_nopot等）
     */
    private String eventCode;
    
    /**
     * 主客队：home/away
     */
    private String homeAway;
    
    /**
     * 距离开始秒数
     */
    private Long secondFromStart;
}

