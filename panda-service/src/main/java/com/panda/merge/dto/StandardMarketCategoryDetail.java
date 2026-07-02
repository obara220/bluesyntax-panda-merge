package com.panda.merge.dto;

import com.panda.merge.model.I18nMarketCategory;
import com.panda.merge.model.StandardMarketCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class StandardMarketCategoryDetail extends StandardMarketCategory {

    /**
     * 玩法名称多语言
     */
    @Getter
    @Setter
    private List<I18nMarketCategory> il8nNameList;

    @Getter
    @Setter
    private Integer fieldType;
}
