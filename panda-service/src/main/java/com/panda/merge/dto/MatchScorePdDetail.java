package com.panda.merge.dto;

import com.panda.merge.model.StandardMatchInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchScorePdDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private StandardMatchInfo standardMatchInfo;

    private Integer marketStatus;

    private String operatorName;

    private String ipAddress;

}
