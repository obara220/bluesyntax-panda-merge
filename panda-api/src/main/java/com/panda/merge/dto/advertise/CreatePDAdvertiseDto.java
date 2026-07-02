package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class CreatePDAdvertiseDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private String dataSourceCode;
}
