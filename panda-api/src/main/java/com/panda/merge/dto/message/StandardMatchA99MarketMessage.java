package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardMatchA99MarketMessage implements Serializable {

    private List<String> requestType;

    private Long standardMatchId;

}
