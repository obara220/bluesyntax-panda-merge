package com.panda.merge.dto.scores;

import lombok.Data;

import java.io.Serializable;

/**
 * @description B02赛事比分来源
 * @author fymen
 * @date 2023-11-24
 */
@Data
public class B02ScoresSourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 体种
     */
    private Long sportId;

    /**
     * 指定赛事ID：切换后赛事ID不为空则指定赛事直接下发比分
     */
    private Long matchManageId;

    /**
     * 数据源ID
     */
    private Long dataSourceId;

    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * 比分类型：1UOF统计比分， 0实时比分，
     */
    private Long dataSourceType;

    /**
     * 操作人
     */
    private Long operateId;

    /**
     * 操作人用户名
     */
    private String operateName;

    /**
     * 数据类型：0普通数据，1操作日志
     */
    private Integer dataType;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

}
