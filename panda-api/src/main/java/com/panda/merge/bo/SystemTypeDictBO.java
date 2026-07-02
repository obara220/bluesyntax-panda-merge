package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-11
 */
@Data
public class SystemTypeDictBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 运动种类id
     */
    private Long sportId;

    /**
     * 字典关键字
     */
    private String code;

    /**
     * 当前数据所表示的含义. 
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

    /**
     * 备注. remark
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

    private List<SystemItemDictBO> systemItemDictList;

}
