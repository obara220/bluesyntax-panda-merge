package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 赛季相关信息参数类
 * @author  tell
 * @since   2021年1月31日12:05:38
 */
@Data
public class ThirdSportSeasonDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "三方数据源赛季ID不能为null!")
    @Size(message = "三方数据源赛季ID长度不能超过50个字符!!",max=50)
    private String thirdSeasonSourceId;

    /** 三方数据源赛季名称，中文优先*/
    @NotNull(message = "三方数据源赛季名称不能为null!")
    private String thirdSeasonSourceName;

    /** 赛季多语言*/
    @Valid
    private List<I18nItemDTO> seasonNameList;

    /** 赛季开始时间*/
    @NotNull(message = "赛季开始时间不能为null!")
    private String startDate;

    /** 赛季结束时间*/
    @NotNull(message = "赛季结束时间不能为null!")
    private String endDate;

    /** 赛季年份*/
    @NotNull(message = "赛季年份不能为null!")
    private String year;

}
