package com.panda.merge.dto;

import com.panda.merge.model.ThirdMarketCategoryField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author   raulvii
 * @since    2021年9月22日
 * */
public class ThirdMarketCategoryFieldDetail extends ThirdMarketCategoryField {

    @ApiModelProperty(value = "投注项名称")
    @Getter
    @Setter
    private String oddsName;
}
