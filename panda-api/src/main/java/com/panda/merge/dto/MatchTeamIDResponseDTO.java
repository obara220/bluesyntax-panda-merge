package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MatchTeamIDResponseDTO implements Serializable {
    List<Long> thirdTeamIdList;
    List<Long> standardTeamIdList;
    Long total;
    Integer size;
    Integer page;
}
