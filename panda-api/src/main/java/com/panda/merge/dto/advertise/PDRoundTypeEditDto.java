package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import lombok.Data;

@Data
public class PDRoundTypeEditDto extends AbstructMatchSettleDto {

    private Long thirdMatchId;

    private Long standardMatchId;

    private Integer roundType;
}
