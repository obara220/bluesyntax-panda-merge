package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 球队参数类（用于投递球员） </br>
 * @author :        tell
 * @Date:           2020年9月2日19:42:31
 */
@Data
public class ThirdSportTeamDTO implements Serializable {

    /**
     * 数据源球队ID
     */
    @NotNull(message = "三方数据源球队ID不能为null!")
    private String thirdTeamSourceId;

    /**
     * 体育种类id. 体育种类id
     */
    @NotNull(message = "三方数据源球队运动类型不能为null!")
    private Long sportId;

    /**
     * 数据来源编码.取值: SR BC分别代表:SportRadar、FeedConstruc.详情见data_source
     */
    @NotNull(message = "三方数据源球队来源编码不能为null!")
    private String dataSourceCode;


    /**
     * 该球队下所有的球员列表
     */
    @Valid
    @NotNull(message = "三方数据源球队人员列表不能为null!")
    @Size(message = "三方数据源球队人员列表超过[200]条，拒绝处理!",max = 200)
    private List<ThirdSportPlayerDTO> thirdSportPlayerList;

    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     *     4：MMA(如果运动类型为拳击）
     * }
     */
    private Integer matchType;

}
