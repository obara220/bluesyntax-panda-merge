package com.panda.merge.dto;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author :
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.query
 * @description :   结算参数 vo
 * @date: 2022年5月10日17:30:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@ApiModel(description = "结算查询条件")
public class SettleQueryDTO  extends AbstructMatchSettleDto  {

    //操作级别: 1-赛事级  2-玩法级
    private Integer level;
    //玩法类型:操作级别为2的时候传入
    // 1-进球 2-角球 3-罚牌
    private Integer playCategory;
    //赛事id
    private Long matchId;
    //赛种
    private Long sportId;

    /**
     * 0.未冻结  1.冻结  2.程序重跑
     */
    private Integer exInfo;

    /**
     * 操作用户名称
     */
    private String operatorName;

    /**
     * 操作用户名称
     */
    private String operatorId;
    // 冻结分钟
    private Integer mins;
    // 冻结时间日期
    private Long freezeTime;
    // 创建时间日期
    private Long createTime;
    /**
     * 结算编码
     */
    private String settleNum;

    private Integer levelNum;

    private Integer playCategoryNum;

}