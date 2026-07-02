package com.panda.merge.dto.scores;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchTimeInfoDTO implements Serializable {

    Long period;

    Long secondFromStart;

    Long remainingTime;

    Integer timeIsGo;


}
