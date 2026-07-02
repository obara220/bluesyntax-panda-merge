package com.panda.merge.dto.settle;

import lombok.Data;

import java.util.List;

@Data
public class SettleTemplateBatchUpdateDto extends AbstructMatchSettleDto{
    /**
     * 修改后的模版id
     * */
    private Long  templateId;
    /**
     * 修改的模版类型
     * */
    private Integer  templateType;
    /**
     * 联赛管理ID
     * */
    private List<String> tournamentManagerId;
    /**
     * 需要修改的联赛idList
     * */
    private List<Long> tournamentIdList;
    /**
     * 全部联赛等级覆盖
     * 0 默认,不做勾选
     * 1 数据商权重
     * 2 结算倒计时*
     * 3 灰色区间
     * */
    private Integer operationType;
    /**
     * 全面覆盖要联赛等级
     * */
    private Integer tournamentLevel;
}
