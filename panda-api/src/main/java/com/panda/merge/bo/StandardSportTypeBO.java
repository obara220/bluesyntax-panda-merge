package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description: 体育种类查询结果单元对象
 * @date 2019/9/3 15:16
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardSportTypeBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 如果当前记录对外起作用（该体育项目是否展示在客户端）。则该visible为 1，否则为0。默认true
     */
    private Integer visible;

    /**
     * 当前运动的介绍。默认为空
     */
    private String introduction;
    /**
     * 备注。
     */
    private String remark;

    /**
     * 修改时间。
     */
    private Long modifyTime;

    /**
     * 体育名称编码. 用于多语言.存放体育种类名称
     */
    private Long nameCode;

    /**
     * 体育名称编码国际化内容
     */
    private List<I18nItemBO> il8nNameList;
}
