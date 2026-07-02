package com.panda.merge.advertise.dto;

import com.panda.merge.dto.CommonItem;
import lombok.Data;

import java.io.Serializable;

@Data
public class IceHockeyScoreVo implements Serializable {

    private CommonItem Q1;
    private CommonItem Q2;
    private CommonItem Q3;
    private CommonItem ET;
    private CommonItem PEN;
    private CommonItem whole;
    private CommonItem bigFa;
    private CommonItem smallFa;
}
