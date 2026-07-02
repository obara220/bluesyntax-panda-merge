package com.panda.merge.dto;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MatchSettleDataSourceWeightAndSwitchV2Dto extends AbstructMatchSettleV2Dto implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 球种的类型
     */
    private Long sportId;

    private String dataSourceCode;

    private AbstructMatchSettleDto switchDto;

    private List<AbstructMatchSettleV2Dto> weightDtoList;


}
