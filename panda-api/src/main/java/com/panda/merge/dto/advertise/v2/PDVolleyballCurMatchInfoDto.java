package com.panda.merge.dto.advertise.v2;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class PDVolleyballCurMatchInfoDto implements Serializable {
    PDVolleyballEventDto pdVolleyballEventDto;

    Map<String, Object> matchStatus;
}
