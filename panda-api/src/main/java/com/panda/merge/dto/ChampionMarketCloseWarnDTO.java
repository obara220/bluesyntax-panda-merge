package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 冠军盘口结束时间变化的预警
 * @Author kepa
 * @since  2022年06月04日 18:43:18
 */
@Data
public class ChampionMarketCloseWarnDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dataSourceCode;

    private String data;

    private String linkId;
}
