package com.panda.merge.bo;

import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.ThirdMarketCategoryField;
import com.panda.merge.model.ThirdSportMarketCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description:
 * @date 2019/10/24 14:58
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdSportMarketCategoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    @ApiModelProperty(value = "玩法名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "投注项数量")
    private Integer fieldsNum;

    @ApiModelProperty(value = "第三方玩法原始ID.")
    private String thirdSourceId;

    @ApiModelProperty(value = "标准玩法id")
    private Long referenceId;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "该玩法是否生效.1生效;0不生效.默认不生效")
    private Integer active;

    private List<ThirdMarketCategoryField> categoryFieldList;

    private List<ThirdSportMarketCategory> sportMarketCategoryList;

    private List<I18nMarketCategory> i18nMarketCategoryList;

    private Long createTime;

    private Long modifyTime;
}
