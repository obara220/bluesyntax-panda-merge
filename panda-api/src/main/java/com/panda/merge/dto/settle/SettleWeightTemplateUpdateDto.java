package com.panda.merge.dto.settle;

import lombok.Data;

@Data
public class SettleWeightTemplateUpdateDto extends AbstructMatchSettleDto{
    /**
     * 模版id
     * */
    private Long templateId;
    /**
     * 模版名称
     * */
    private String templateName;
    /**
     * 模版联赛等级 非联赛等级专用 = -1
     * */
    private Integer tournamentLevel;
    /**
     * 数据商结算权重json
     * */
    private String weightJson;

}
