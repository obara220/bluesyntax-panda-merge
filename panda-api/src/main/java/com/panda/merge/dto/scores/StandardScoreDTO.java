package com.panda.merge.dto.scores;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * 标准比分中心页面
 */
@Slf4j
@Data
public class StandardScoreDTO implements Serializable{
    private Long periodId;
    private Integer home;
    private Integer away;
    //于标准比分是否存在差异
    private Boolean isDifference;
    //主队是否存在差异
    private Boolean isDiffHome;
    //客队是否存在差异
    private Boolean isDiffAway;
    //序号
    private int index;
    //开关 0:关闭 1:开启
    private Integer switchs;
//    private Map<String,Object> scores;

}
