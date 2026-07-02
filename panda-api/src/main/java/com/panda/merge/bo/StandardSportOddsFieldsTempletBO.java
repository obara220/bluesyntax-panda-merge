package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  Horus
 * @Description :  TODO
 * @Date: 2019/9/25 19:45
 * @ModificationHistory Who    What   When
 * --------  ---------  --------------------------
 */
@Data
public class StandardSportOddsFieldsTempletBO  implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
     * 投注项表ID
     */
    private Long id;

    /**
     * 玩法名称编码. 用于多语言.
     */
    private Long nameCode;
    
	/**
     * 玩法名称编码. 用于多语言.
     */
    private List<I18nItemBO> i18nNameList;

    /**
     * 运动种类id。 对应表 sport.id
     */
    private Long marketCategoryId;

    /**
     * 排序值.
     */
    private Integer orderNo;

    /**
     * 附件字段1
     */
    private String addition1;

    /**
     * 附件字段2
     */
    private String addition2;

    /**
     * 附件字段3
     */
    private String addition3;

    /**
     * 修改时间.
     */
    private Long modifyTime;
}
