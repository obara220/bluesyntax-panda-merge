package com.panda.merge.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: Kepa
 * @Date: 2020/10/3 13:35
 */
@Data

public class OutrightMarketOddsBO implements Serializable {

    private static final long serialVersionUID = 1L;


    private String oddsName;


    private String id;


    private String thirdOddsFieldSourceId;


    private Double oddsValue;


    private Integer betSettlementResultStatus;

    private String betSettlementCertainty;

    private String SettlementResult;

    private Integer active;

    //投注项对应的多语言
   private Map<String,String> oddsNameMap;

}
