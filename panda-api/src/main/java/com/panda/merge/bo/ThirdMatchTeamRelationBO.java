package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description:
 * @date 2019/10/25 10:20
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdMatchTeamRelationBO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 比赛中的作用。主客队或者其他
     */
    private String matchPosition;

    /**
     * 备注
     */
    private String remark;
}
