package com.panda.merge.dto;

import com.panda.merge.validator.EnumValue;
import lombok.Data;

import java.io.Serializable;

/**
 * 赛事球队关系信息参数类 </br>
 * @author :        tell
 * @Date:           2020年9月2日19:42:31
 */
@Data
public class ThirdMatchTeamRelationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 比赛中的作用。主：home,客:away,其他:other
     */
    @EnumValue(message = "三方数据源赛事球队关系球队作用值非法，值应为{home,away,other}其中之一,请检查",strValues ={"home","away","other"})
    private String matchPosition;

    /**
     * 球队的国际化名称. 可以使用json 字符串存储。
     */
    private String teamNameRecord;

    /**
     * 备注
     */
    private String remark;
}
