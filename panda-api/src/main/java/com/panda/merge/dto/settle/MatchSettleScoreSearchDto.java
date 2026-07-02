package com.panda.merge.dto.settle;



import lombok.Data;


@Data
public class MatchSettleScoreSearchDto extends AbstructMatchSettleDto {

    private Long standardMatchId;

    private String eventCode;
}
