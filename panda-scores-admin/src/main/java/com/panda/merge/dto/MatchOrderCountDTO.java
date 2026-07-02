package com.panda.merge.dto;


import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.TeamTypeConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;

@Slf4j
@Data
public class MatchOrderCountDTO implements Serializable{

    private String linkId;
    private Long mid;
    private Long timestamp;
    private Integer orderCount;

}
