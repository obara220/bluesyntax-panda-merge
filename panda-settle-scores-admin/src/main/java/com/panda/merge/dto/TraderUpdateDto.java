package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
public class TraderUpdateDto implements Serializable {

    private String matchId;
    private String marketType;
    private List<HashMap<String,String>> trader;
}
