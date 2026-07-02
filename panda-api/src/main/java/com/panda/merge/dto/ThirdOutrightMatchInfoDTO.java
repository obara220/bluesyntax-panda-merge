package com.panda.merge.dto;

import com.panda.merge.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author : nonhung
 * @project Name : data-nonrealtime
 * @package Name : com.panda.sport.data.nonrealtime.api.dto
 * @description : TODO
 * @date: 2020-09-10 14:58
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Data
public class ThirdOutrightMatchInfoDTO implements Serializable {
    private static final long serialVersionUID = -2615008542394560538L;
    /**
     * 赛种
     */
    @NotNull(message = "三方运动类型不能为null!")
    private Long sportId;

    /**
     * 区域id
     */
    @NotNull(message = "三方区域ID不能为null!")
    private String regionId;

    /**
     * 三方联赛id
     */
    @NotNull(message = "三方联赛ID不能为null!")
    private String tournamentId;
    /**
     * 数据源  sr, bc ,bg
     */
    @NotNull(message = "三方数据源不能为null!")
    private String dataSourceCode;

    /**
     * 三方冠军赛事开始时间
     */
    @NotNull(message = "三方赛事开始时间不能为null!")
    private Long thirdOutrightBeginTime;

    /**
     * 三方冠军赛事结束时间
     */
    private Long thirdOutrightEndTime;

    /**
     * 三方冠军赛事源id
     */
    @NotNull(message = "三方冠军赛事ID不能为null!")
    private String thirdOutrightSourceId;

    /**
     * 赛季id
     */
   // @NotNull(message = "三方赛季ID不能为null!")
    private String seasonId;

    /**
     * 三方冠军赛事赛季名称
     */
    private String thirdOutrightYear;

    /**
     * 是否订阅 0 未订阅  1 已订阅
     */
    @NotNull(message = "三方数据源赛事是否订阅不能为null!")
    @EnumValue(message = "三方数据源赛事是否订阅值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer booked;

    /**
     * 是否可见 0 不可见  1 可见
     */
//    @NotNull(message = "三方数据源赛事是否可见不能为null!")
//    @EnumValue(message = "三方数据源赛事是否可见值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer isVisible;

    /**
     * 赛事名称
     */
    @Valid
    @NotNull(message = "三方数据源赛事名称不能为null!")
    private List<I18nItemDTO> matchNameList;
}
