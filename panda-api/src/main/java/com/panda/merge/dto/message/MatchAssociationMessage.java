package com.panda.merge.dto.message;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author :  wade
 * @Project Name :  data-router
 * @Package Name :  com.panda.sport.data.router.bo
 * @Description :  TODO
 * @Date: 2020-01-23 16:14
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
public class MatchAssociationMessage implements Serializable {

    /**
     * 标注赛事id
     */
    private Long standardMatchId;
    /**
     * 第三方比赛id. 第三方比赛在 表 third_match_info 中的id
     */
    @NotNull(message = "三方赛事ID不能为null!")
    private Long thirdMatchId;

    /**
     * 第三方赛事原始id.比如: SportRadar 发送数据时,这场比赛的ID.
     */
    private String thirdMatchSourceId;
    /**
     * 体育种类id. 运动种类id 对应sport.id
     */
    private Long sportId;
    /**
     * 数据来源编码. 取值见: data_source.code
     */
    private String dataSourceCode;
    /**
     * 联赛分级. 1: 一级联赛; 2:二级联赛; 3: 三级联赛; 以此类推; 0: 未分级
     */
    private Integer tournamentLevel;

    /**
     * 是否关联 Y 关联，N 取消
     */
    private String association;

    public MatchAssociationMessage(Long standardMatchId, Long thirdMatchId, Long sportId, String dataSourceCode) {
        this.standardMatchId = standardMatchId;
        this.thirdMatchId = thirdMatchId;
        this.sportId = sportId;
        this.dataSourceCode = dataSourceCode;
    }

    public MatchAssociationMessage() {
    }
}
