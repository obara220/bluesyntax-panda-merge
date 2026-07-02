package com.panda.merge.dto;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.StandardSportTournament;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class StandardSportTournamentDetail extends StandardSportTournament {
    /**
     * 联赛名称编码。联赛名称编码. 用于多语言
     */
    @Getter
    @Setter
    private List<LanguageInternation> il8nNameList;
}