package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-26
 */
@Data
public class SystemItemDictBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 字典类型id.system_type_dict.id
     */
    private Long parentTypeId;

    /**
     * 项目编码
     */
    private String code;

    /**
     * 项目值. 
     */
    private String value;

    /**
     * 是否激活.1:激活;0:没有激活. 
     */
    private Integer active;

    /**
     * 描述信息. 
     */
    private String description;

    private String addition1;

    /**
     * 备注.remark
     */
    private String remark;

    /**
     * 创建时间. create_time
     */
    private Long createTime;

    /**
     * 更新时间. modify_time
     */
    private Long modifyTime;


}
