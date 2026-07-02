package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description: 赛事查询结果单元对象
 * @date 2019/9/3 17:05
 * @ModificationHistory Who    When    What
 */
@Data
public class GoalTypeBO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String zhValue;

    private String enValue;
}
