package com.panda.merge.dto.scores;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 查询比分对象 风控调用
 */
@Slf4j
@Data
public class QueryMatchScoresParamDTO implements Serializable{
    private List<Long> matchIds;

}
