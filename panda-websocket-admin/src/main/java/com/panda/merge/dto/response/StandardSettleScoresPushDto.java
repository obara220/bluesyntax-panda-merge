package com.panda.merge.dto.response;

import com.panda.merge.dto.settle.MatchSettleScoreDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardSettleScoresPushDto implements Serializable {
    private Long standardMatchId;
    private String eventCode;
    private List<MatchSettleScoreDto> data;
}
