package com.panda.merge.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 标准球员表
 * </p>
 *
 * @author idol
 * @since 2022-3-19 15:19:54
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandardPlayerLanguageBO implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 球员名称编码. 对应 language_internation.name_code
     */
    private String nameCode;

    /**
     * 英文(冗余字段,用于排序)
     */
    private Map<String,String> names;

}
