package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  Jimmy
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.message
 * @Description :  TODO
 * @Date: 2019-10-07 17:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchStatisticsInfoMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<MatchStatisticsInfoDetailMessage> matchStatisticsInfoDetailList;
    /**
     * 数据来源编码. 取值见: data_source.code
     */
    private String dataSourceCode;
	/**
     * id
     */
    private Long id;
    /**
     * 运动类型
     */
    private Long sportId;
    /**
     * 第三方赛事原始id
     */
    private String thirdSourceMatchId;

    /**
     * 第三方赛事id
     */
    private Long thirdMatchId;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    /**
     * 预计比赛时长.  单位:秒
     */
    private Integer matchLength;

    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     * }
     */
    private Integer matchType;

    /**
     * Game short info
     */
    private String info;

    /**
     * 比赛阶段
     */
    private Integer period;

    /**
     * Total set count
     */
    private Integer setCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 比赛剩余时间. 单位:秒
     */
    private Integer remainingTime;

    /**
     * 当前比赛进行时间.单位:秒
     */
    private Integer secondsMatchStart;

    /**
     * 更新时间. UTC时间,精确到毫秒
     */
    private Long modifyTime;

}
