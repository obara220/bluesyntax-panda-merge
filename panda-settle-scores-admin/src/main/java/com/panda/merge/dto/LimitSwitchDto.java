package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * dto
 * */
@Data
public class LimitSwitchDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int level;
    private Boolean onOff;
    private Boolean realTimeOnOff;
    private int limitSecond;
}
