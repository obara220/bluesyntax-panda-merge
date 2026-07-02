package com.panda.merge.dto;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.StandardSportPlayer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 标准球员信息详情累
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class StandardSportPlayerDetail extends StandardSportPlayer {

    @ApiModelProperty(value = "标准球员名称国际化信息")
    @Getter
    @Setter
    @Deprecated
    private List<LanguageInternation> il8nNameList;

}