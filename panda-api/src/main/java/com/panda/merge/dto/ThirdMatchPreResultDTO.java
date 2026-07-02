package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 三方 消费数据源盘口提前结算信息
 *
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 16:07
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdMatchPreResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    private Long sportId;

    /**
     * 第三方赛事原始id
     */
    private String thirdMatchId;

    /**
     * 取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source
     */
    private String dataSourceCode;

    /**
     * 赛事类型,0:普通赛事、1冠军赛事
     */
    private Integer matchType;

    private String requestType;

    /**
     * 提前结算盘口信息
     */
    @NotNull(message = "提前结算盘口信息不能为空")
    private List<ThirdMarketPreResultDTO> marketResultList;

}
