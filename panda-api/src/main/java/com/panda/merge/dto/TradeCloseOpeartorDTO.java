package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TradeCloseOpeartorDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "赛事Id不能为空!")
    private Long matchId;

    private Integer marketType;

    @NotNull(message = "数据源编码不能为空!")
    private String dataSourceCode;
}
