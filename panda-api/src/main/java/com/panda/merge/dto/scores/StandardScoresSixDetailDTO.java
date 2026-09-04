package com.panda.merge.dto.scores;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 标准比分中心页面-篮球6分钟比分
 */
@Slf4j
@Data
public class StandardScoresSixDetailDTO implements Serializable{
    private Long periodId;
    private Integer q1Home;
    private Integer q2Home;
    private Integer q3Home;
    private Integer q4Home;

    private Integer q1Away;
    private Integer q2Away;
    private Integer q3Away;
    private Integer q4Away;

}
