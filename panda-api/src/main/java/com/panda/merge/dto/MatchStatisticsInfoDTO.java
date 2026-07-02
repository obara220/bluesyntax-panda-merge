package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author :  Jimmy
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description :  TODO
 * @Date: 2019-10-04 15:56
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchStatisticsInfoDTO implements Serializable{
	private static final long serialVersionUID = 1L;
    /**
     * 各类比分详细信息
     */
    @NotNull(message = "统计详细信息不能为null")
    private List<MatchStatisticsInfoDetailDTO> matchStatisticsInfoDetailList;
    /**
     * 虚拟赛事统计信息
     */
    //@NotNull(message = "虚拟统计详细信息不能为null")
    private List<MatchStatisticsInfoCommonDTO> matchStatisticsInfoCommonDTO;
    /**
     * 数据来源编码. 取值见: data_source.code
     */
    @NotEmpty(message = "数据源不能为空")
    private String dataSourceCode;

    /**
     * 第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID.
     */
    @NotEmpty(message = "第三方赛事id不能为空")
    private String thirdMatchSourceId;

    /**
     * 运动类型
     */
    @NotEmpty(message = "赛种不能为空")
    private String sportId;

    /**
     * 预计比赛时长.  单位:秒
     */
    private Integer matchLength;

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
     * 更新时间. UTC时间,精确到毫秒
     */
    private Long modifyTime;

    /**
     * 比赛剩余时间. 单位:秒
     */
    private Integer remainingTime;

    /**
     * 当前比赛进行时间.单位:秒
     */
    private Integer secondsMatchStart;

}
