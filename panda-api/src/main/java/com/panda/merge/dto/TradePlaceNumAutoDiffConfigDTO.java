package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class TradePlaceNumAutoDiffConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准赛事ID
     */
    @NotNull(message = "赛事id不能为空")
    private Long matchId;

    /**
     * 水差配置
     */
    @Valid
    private TradePlaceNumAutoDiffConfigItemDTO diffConfigs;
}
