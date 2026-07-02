package com.panda.merge.dto;

import com.panda.merge.utils.Compare;
import lombok.Data;

/**
 * 灰色区间json模版
 * */
@Data
public class GrayAreaSettleDto {
    /**
     * 进球15分钟灰色区间
     * */
  @Compare("goal15Min")
  private Integer goal15Min;
    /**
     * 角球15分钟灰色区间
     * */
  @Compare("corner15Min")
  private Integer corner15Min;
    /**
     * 罚牌15分钟灰色区间
     * */
  @Compare("booking15Min")
  private Integer booking15Min;
  /**
   * 篮球6分钟进球
   */
  @Compare("goal6Min")
  private Integer goal6Min;

    /**
     * 进球5分钟灰色区间
     * */
  @Compare("goal5Min")
  private Integer goal5Min;
  /**
   *数据商编码
   * */
  private String dataSourceCode;

  /**
   * 初始化参数
   * @return
   */
  public static GrayAreaSettleDto initGrayAreaSettle() {
    GrayAreaSettleDto grayAreaSettle =new GrayAreaSettleDto();
    grayAreaSettle.setGoal5Min(0);
    grayAreaSettle.setGoal15Min(0);
    grayAreaSettle.setCorner15Min(0);
    grayAreaSettle.setBooking15Min(0);
    grayAreaSettle.setGoal6Min(0);
    return grayAreaSettle;
  }

}
