package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleFiveMinSwitchDTO extends AbstructMatchSettleDto implements Serializable {


    private Long matchId;

    private Integer fiveMinSwitch;





}