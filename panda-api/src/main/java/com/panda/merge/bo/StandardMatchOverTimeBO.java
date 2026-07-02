package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author idol
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.bo
 * @Description: 赛事结束时间BO
 * @date 2021/10/09 21:44
 */
@Data
public class StandardMatchOverTimeBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事id
     */
    private Long id;

    /**
     * 数据来源编码。指的是当前赛事使用哪个数据供应商的数据。使用该数据，则使用该风控
     */
    private String dataSourceCode;



    /**
     * 第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID.
     */
    private String thirdMatchSourceId;

    /**
     * 比赛是否结束. 0: 未结束;  1: 结束. （比赛彻底结束, 双方不再有加时赛, 点球大战, 且裁判宣布结束）
     */
    private Integer matchOver;

    /**
     * 下发给赛程的时间
     */
    private Long matchOverTime;


    /**
     * 赛事状态.  比如:未开赛, 滚球, 取消, 延迟等.  取system_item_dic中的value字段
     */
    private Integer matchStatus;


    /**
     * 备注.remark
     */
    private String remark;

    /**
     * 下发给赛程的时间
     */
    private Long sendTime;



}
