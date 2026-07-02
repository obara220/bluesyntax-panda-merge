package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoSettleDataSourceDto implements Serializable {

    /**
     * 是否开启自动结算 1是，0否
     */
    private Boolean isEnableAutoSettle;

    /**
     * 赛事id
     */
    private String standardMatchId;
    /**
     *    corner
     *
     *    goal
     *
     *    facard
     *
     * */
    private String type;
}
