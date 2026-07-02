package com.panda.merge.dto;

import com.panda.merge.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 球队球员相关信息参数类 </br>
 * @author :        tell
 * @Date:           2020年9月2日19:42:31
 */
@Data
public class ThirdSportPlayerDTO implements Serializable {


    /**
     * 数据源球员ID
     */
    @NotNull(message = "三方数据源球员ID不能为null!")
    private String thirdSourcePlayerId;

    /**
     * 球员的中文名称, 中文简体(冗余字段,用于查询,修改时需要维护)
     */
//    @NotNull(message = "三方数据源球员中文名称不能为null!")
    private String name;

    /**
     * 英文名称(冗余字段,用于排序)
     */
    private String nameSpell;

    /**
     * 球员昵称. 例如;C罗
     */
    private String nickName;

    /**
     * 运动种类ID
     */
    @NotNull(message = "三方数据源球员运动类型不能为null!")
    private Long sportId;

    /**
     * 数据源编码. 对应 data_source.code
     */
    @NotNull(message = "三方数据源球员来源编码不能为null!")
    private String dataSourceCode;

    /**
     * 球员名称国际化信息
     */
    @Valid
    @NotNull(message = "三方数据源球员名称国际化不能为null!")
    private List<I18nItemDTO> nameI18nList;

    /**
     * 球员照片地址.
     */
    private String pictureUrl;

    /**
     * 球衣号码
     */
    private Integer jerseyNumber;

    /**
     * 球员出生日期.
     */
    private Long birthday;

    /**
     * 三方区域ID
     */
    private String thirdRegionId;

    /**
     * 三方区域名称
     */
    private String thirdRegionName;

    /**
     * 球员体重.单位: 克（g）.
     */
    private Integer weight;

    /**
     * 球员身高, 单位: 毫米(mm)
     */
    private Integer height;

    /**
     * 球员性别. 0:未知,1:男,2:女
     */
    @NotNull(message = "三方数据源球员性别不能为null!")
    @EnumValue(message = "三方数据源球员性别值非法，值应为{0,1,2}其中之一,请检查!",intValues ={0,1,2})
    private Integer gender;

    /**
     * 球员在团队中的位置.  比如:前锋,中锋,后卫,守门员
     */
    private String teamPosition;

    /**
     * 个人特效. 比如: 握拍方式, 进攻特长等.
     */
    private String personalFeature;

    /**
     * 国籍.国籍所属国家id.对应third_sport_region.id
     */
    private String countryId;

    /**
     * 国藉
     */
    private String countryName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 修改时间
     */
    private Long modifyTime;


    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     *     4：MMA(如果运动类型为拳击）
     * }
     */
    private Integer matchType;
}
