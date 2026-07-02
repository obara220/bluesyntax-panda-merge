package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ThirdMatchAnalysisModifyTimeDTO {
    @NotNull(message = "target不能为null")
    private String target;
    @NotNull(message = "dataSourceCode不能为null")
    private String dataSourceCode;
    @NotNull(message = "thirdTournamentId不能为null")
    private Long thirdTournamentId;
    @NotNull(message = "thirdMatchId不能为null")
    private Long thirdMatchId;

}
