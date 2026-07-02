package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;
@Data
public class MatchSettleOrderClosedDTO extends AbstructMatchSettleDto implements Serializable {


    private Long matchId;

    private Integer settleOrderClosed;





}