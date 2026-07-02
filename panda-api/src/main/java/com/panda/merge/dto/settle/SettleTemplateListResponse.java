package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SettleTemplateListResponse extends AbstructMatchSettleDto {
    private Integer page;
    private Integer size;
    private Integer total;
    private List<MatchSettleTemplateTournamentDto> list;
}
