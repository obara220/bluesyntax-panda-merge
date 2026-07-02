package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author :  Horus
 * @Description :
 * @Date: 2019/10/7 17:04
 * @ModificationHistory Who    What   When
 * --------  ---------  --------------------------
 */
@Data
public class VirtualToSaleMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    public static Set<Long> VIRTUAL_SPORTS_TYPE=new HashSet<>();
    static {
        VIRTUAL_SPORTS_TYPE.add(1001L);
        VIRTUAL_SPORTS_TYPE.add(1002L);
        VIRTUAL_SPORTS_TYPE.add(1007L);
        VIRTUAL_SPORTS_TYPE.add(1008L);
        VIRTUAL_SPORTS_TYPE.add(1009L);
        VIRTUAL_SPORTS_TYPE.add(1010L);
        VIRTUAL_SPORTS_TYPE.add(1011L);
        VIRTUAL_SPORTS_TYPE.add(1012L);
    }

    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    @NotNull(message="赛种不能为空")
    private Long sportId;

    /**
     * 第三方比赛原始ID
     */
    @NotNull(message="三方赛事源id不能为空")
    private String thirdMatchSourceId;

    /**
     *  第三方联赛原始ID
     */
    private String thirdTournamentSourceId;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    @NotNull(message="数据源不能为空")
    private String dataSourceCode;

    /**
     * 上游时间戳
     */
    private Long modifyTime;

    private String linkedId;
}
