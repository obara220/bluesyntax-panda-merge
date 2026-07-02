package com.panda.merge.dto.response;

import com.panda.merge.dto.settle.MatchSettleEventDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardSettleEventPushDto implements Serializable {
    private Long standardMatchId;
    private String eventCode;
    private List<MatchSettleEventDto> data;
}
