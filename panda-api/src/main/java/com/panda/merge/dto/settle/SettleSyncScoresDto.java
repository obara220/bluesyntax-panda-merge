package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettleSyncScoresDto implements Serializable {

    /**
     * 是否开启结算同步比分中心 1是，0否
     */
    private Boolean isEnableSyncScores;

}
