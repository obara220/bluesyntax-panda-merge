package com.panda.merge.dto.advertise.v2;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class PDSnookerCurMatchInfoDto implements Serializable {
    PDSnookerEventDto pdSnookerEventDto;

    Map<String, Object> matchStatus;
}
