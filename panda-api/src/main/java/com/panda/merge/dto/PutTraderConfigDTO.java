package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 操盘配置
 *
 * @author bevan
 * @since 2021年10月19日12:05:38
 */
@Data
public class PutTraderConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;
    /**
     * 类型定义
     * 1.玩法集 开关封锁配置
     * 2.
     * 3.
     * 4.
     * 5.
     * 6.
     */
    private Integer type;

    /**
     * 玩法集 开关封锁配置
     */
    private List<CategorySetConfigDTO> categorySets;

}
