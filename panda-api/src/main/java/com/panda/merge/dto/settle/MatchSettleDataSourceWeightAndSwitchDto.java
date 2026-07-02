package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MatchSettleDataSourceWeightAndSwitchDto extends AbstructMatchSettleDto implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 球种的类型
     */
    private Long sportId;

    private String dataSourceCode;

    private AbstructMatchSettleDto switchDto;

    private List<AbstructMatchSettleDto> weightDtoList;



}
