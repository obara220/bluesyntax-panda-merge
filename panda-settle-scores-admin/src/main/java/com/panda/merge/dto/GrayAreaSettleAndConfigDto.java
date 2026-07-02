package com.panda.merge.dto;

import com.panda.merge.utils.Compare;
import lombok.Data;

/**
 * 灰色区间json模版
 * */
@Data
public class GrayAreaSettleAndConfigDto {
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
     * 进球5分钟灰色区间
     * */
  @Compare("goal5Min")
  private Integer goal5Min;

  /**
   * 进球5分钟灰色区间
   * */
  @Compare("goal5Min")
  private Integer goal6Min;
  /**
   *数据商编码
   * */
  private String dataSourceCode;

  /**
   * 权重上限
   */
  private Integer WeightNum;

  /**
   * 初始化参数
   * @return
   */
  public static GrayAreaSettleAndConfigDto initGrayAreaSettle() {
    GrayAreaSettleAndConfigDto grayAreaSettle =new GrayAreaSettleAndConfigDto();
    grayAreaSettle.setGoal5Min(0);
    grayAreaSettle.setGoal15Min(0);
    grayAreaSettle.setCorner15Min(0);
    grayAreaSettle.setBooking15Min(0);
    grayAreaSettle.setWeightNum(0);
    return grayAreaSettle;
  }

}
