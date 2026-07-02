package com.panda.merge.dto.settle;

import lombok.Data;

import java.util.List;

@Data
public class DataSourceWeightSearchDto extends AbstructMatchSettleDto{
    /**
     * 联赛id
     * */
    private  Long tournamentId;
    /**
     * 联赛名称
     * */
    private String templateName;

    private Integer  templateType;

    private List<Long> tournamentIdList;

    private Long sportId;
}
