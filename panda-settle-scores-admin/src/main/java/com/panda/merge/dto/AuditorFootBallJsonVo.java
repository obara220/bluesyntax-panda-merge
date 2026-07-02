package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AuditorFootBallJsonVo implements Serializable {
    //进球类审核员
    private List<String> goalAuditorList;
    //角球类审核员
    private List<String> cornerAuditorList;
    //罚牌类审核员
    private List<String> facardAuditorList;
}
