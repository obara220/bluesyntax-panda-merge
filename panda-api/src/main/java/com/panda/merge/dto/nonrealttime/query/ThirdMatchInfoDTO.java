package com.panda.merge.dto.nonrealttime.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.dto
 * @Description:
 * @date 2019/10/24 15:22
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdMatchInfoDTO implements Serializable {
    /**
     * 开始时间 ，utc时间戳
     */
    private Long beginTime;
    /**
     * 结束时间 ，utc时间戳
     */
    private Long endTime;

    private String dataSourceCode;

    private String thirdMatchSourceId;

    private Long thirdSportId;
}
