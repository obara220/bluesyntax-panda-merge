package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  Horus
 * @Description :
 * @Date: 2019/10/7 17:04
 * @ModificationHistory Who    What   When
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketResultMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 数据源编码
     */
    private String dataSourceCode;

    /**
     * 标准盘口id
     */
    private String standardMarketId;

    /**
     * 标准赛事id
     */
    private String standardMatchId;
    
    /**
     * 第三方盘口原始id
     */
    private String thirdMarketId;

    /**
     * 第三方赛事原始id
     */
    private String thirdMatchId;
    
    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    private Long sportId;

    /**
     * 赛事类型,0:普通赛事、1冠军赛事
     */
    private Integer matchType;

    /**
     * 投注项结算结果
     */
    private List<StandardMarketOddsResultMessage> marketOddsResultList;

    /**
     * 异常结算原因id（可为空）
     */
    private Integer reasonId;

}
