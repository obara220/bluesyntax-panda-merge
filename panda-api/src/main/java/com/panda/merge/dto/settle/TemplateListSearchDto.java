package com.panda.merge.dto.settle;

import lombok.Data;

@Data
public class TemplateListSearchDto extends AbstructMatchSettleDto{
    /**
     * 页数
     * */
    private Integer page;
    /**
     * 分页大小
     * */
    private Integer size;
    /**
     * 联赛名称
     * */
    private String tournamentName;
    /**
     * 联赛等级 -1 默认为 特定 不传 则为全部
     * */
    private Integer tournamentLevel;
    /**
     * 数据商权重模版名称
     * */
    private String dataSourceWeight;
    /**
     * 灰色区间设置名称
     * */
    private String grayAreaSet;
    /**
     * 结算倒计时名称
     * */
    private String countDown;
    /**
     * 分页开始
     * */
    private Integer start;
    /**
     * 分页结束
     * */
    private Integer end ;

    /**
     *数据商权重模版联赛等级查询
     * */
    private Integer dataSourceWeightLevel;
    /**
     * 灰色区间模版联赛等级查询
     * */
    private Integer grayAreaSetLevel;

    private  Integer countDownLevel;
}
