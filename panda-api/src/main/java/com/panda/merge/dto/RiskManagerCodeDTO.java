package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @ClassName RiskManagerCodeDTO
 * @Author Top
 * @date 2020.06.20 14:11
 */
@Data
public class RiskManagerCodeDTO implements Serializable {

    private static final long serialVersionUID = 8022886078487815193L;
    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 类型  PRE 早盘  LIVE 滚球
     */
    private String type;

    /**
     * 修改后的操盘平台 （这里默认为MTS）
     */
    private String riskManagerCode;

    /**
     * 操作人id
     */
    private Long userId;

    /**
     * 操作人名称
     */
    private String userName;
    /**
     * MTS切换到PA需要重新开售的玩法id集合（PA切MTS不用传）
     */
    private List<Long> categoryIds;

    /**
     * 4405：玩法级操盘模式切换的玩法集合（风控/操盘传入）
     * 说明：当该字段不为空时，走“玩法级写入 playRiskManager”逻辑，而不是赛事级改 sell.pre/liveRiskManagerCode。
     */
    private List<Long> categoryIds4405;

    /**
     * 4405：玩法级数据源映射（可选）。
     * key=categoryId, value=dataSourceCode（与 market_category_sell.data_source_code 口径一致）
     * - 若风控/操盘侧能一并传入，可减少服务端查库并保证一致性
     * - 若不传，服务端会回落从玩法开售表查询
     */
    private Map<Long, String> categoryDataSourceMap4405;

    /**
     * 风控xts自动切换时恢复专用字段   1 开启 0 关闭
     *
     */
    private int xtsMatchAutoSwitch = 0;
}
