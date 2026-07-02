package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description:
 * @date 2019/10/24 15:26
 * @ModificationHistory Who    When    What
 */
@Data
public class DataSourceBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 数据表id, 自增
     */
    private Long id;

    /**
     * 该数据源的编码.比如 SportRadar的编码是 SR
     */
    private String code;

    /**
     * 数据源全称.比如 SportRadar
     */
    private String fullName;

    /**
     * 数据源简称.比如 SR , 球探
     */
    private String shortName;

    /**
     * 数据的优先级. 值越大, 重要程度越高.
     */
    private Integer priority;

    /**
     * 是否是商业来源的数据. 1: 商业来源;0:非商业
     */
    private Integer commerce;

    /**
     * 是否为标准数据源. 1: 是; 0: 否
     */
    private Integer standard;

    private String remark;

    private Long createTime;

    private Long modifyTime;

}
