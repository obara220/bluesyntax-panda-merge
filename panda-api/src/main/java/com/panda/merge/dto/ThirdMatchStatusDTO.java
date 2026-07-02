package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * SR BC提供的比赛数据信息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-04
 */
@Data
public class ThirdMatchStatusDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    @NotNull(message = "第三方赛事体育Id非法，请确认数据是否正确")
    @Min(value = 1, message = "第三方赛事体育Id非法，请确认数据是否正确")
    private Long sportId;

    /**
     * 第三方赛事原始id。比如： SportRadar 发送数据时，这场比赛的ID。
     */
    @NotNull(message = "第三方赛事对象的thirdMatchSourceId为空，请确认数据是否正确")
    private String thirdMatchSourceId;

    /**
     * 数据来源编码。取值： SR BC分别代表：SportRadar、BetConstruct。详情见data_source
     */
    @NotNull(message = "第三方赛事对象的dataSourceCode为空，请确认数据是否正确")
    private String dataSourceCode;

    /**
     * 赛事状态.
     * 字典数据，对应 parent_type_id = 5
     */
    private Integer matchStatus;


    private Long modifyTime;
}
