package com.panda.merge.dto.advertise;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PDFootBallMatchEventDto implements Serializable {
    private String thirdMatchId;
    private List<PDFootBallEventDto> eventDtoList;
}
