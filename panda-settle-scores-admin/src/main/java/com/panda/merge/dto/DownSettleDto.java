package com.panda.merge.dto;

import com.panda.merge.utils.Compare;
import lombok.Data;

/**
 * 灰色区间json模版
 * */
@Data
public class DownSettleDto {
    /**
     * 进球15分钟倒计时
     * */
  @Compare("goal15Min")
  private Integer goal15Min;
    /**
     * 角球15分钟倒计时
     * */
  @Compare("corner15Min")
  private Integer corner15Min;
    /**
     * 罚牌15分钟倒计时
     * */
  @Compare("booking15Min")
  private Integer booking15Min;

  @Compare("goal")
  private Integer goal;
//  /**
//   * 篮球6分钟进球
//   */
//  @Compare("goal6Min")
//  private Integer goal6Min;
//
//    /**
//     * 进球5分钟灰色区间
//     * */
//  @Compare("goal5Min")
////  private Integer goal5Min;
//  /**
//   *数据商编码
//   * */
//  private String dataSourceCode;

  /**
   * 初始化参数
   * @return
   */
  public static DownSettleDto initDownSettle() {
    DownSettleDto downSettleDto =new DownSettleDto();
    downSettleDto.setGoal15Min(0);
    downSettleDto.setCorner15Min(0);
    downSettleDto.setBooking15Min(0);
    downSettleDto.setGoal(0);
    return downSettleDto;
  }

}
