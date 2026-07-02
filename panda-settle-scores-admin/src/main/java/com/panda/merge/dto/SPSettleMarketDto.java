package com.panda.merge.dto;

import com.panda.merge.model.MatchSettleSpOdds;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SPSettleMarketDto implements Serializable {
    private String id;
    private String marketId;
    private Long standardMatchId;
    private Long sportId;
    private String categoryNameEn;
    private String categoryNameCn;
    private List<MatchSettleSpOddsDto> oddsList;
}
