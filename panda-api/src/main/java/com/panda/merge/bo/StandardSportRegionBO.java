package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  Horus
 * @Description :  TODO
 * @Date: 2019/9/23 19:39
 * @ModificationHistory Who    What   When
 * --------  ---------  --------------------------
 */
@Data
public class StandardSportRegionBO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private Long id;

    /**
     * 如果当前记录对外起作用, 则该visible 为 1, 否则为 0. 默认true
     */
    private Integer visible;

    /**
     * 英文名称
     */
    private String introductionEn;

    /**
     * 中文名称
     */
    private String introduction;

    /**
     * 繁体名称
     */
    private String introductionZh;

    /**
     * 越南语名称
     */
    private String introductionVi;

    /**
     * 区域名称大写字母拼写
     */
    private String spell;

    /**
     * 备注
     */
    private String remark;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 区域名称编码.用于多语言.存放体育区域名称"
     */
    private Long nameCode;
    /**
     * 国际化信息
     */
    private List<I18nItemBO> il8nNameList;

}
