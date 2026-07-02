package com.panda.merge.dto.advertise;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FreeThrowDetailDto implements Serializable {
    /**
     * PD赛事id
     * */
    private String thirdMatchId;
    /**
     * 罚球总数
     * */
    private Integer freeThrowNumber;
    /**
     * 1 进球 2未进
     * */
    private Integer freeThrowResult ;
    /**
     * 当前状态： 0 没有罚球 1未开始  2进行中
     * */
    private Integer status = 0;
    /**
     *  -1 未投  0 未进  1进了
     * */
    private List<Integer> freeThrowDetailList;
    /**
     * 当前罚球第几个
     * */
    private Integer eventOrder=0;

    private String homeAway;
    /**
     * 当前得分
     * */
    private Integer score;

    /**
     * 罚球编号、进球状态、增加删除状态
     */
    private List<SetFreeThrowBasketballDto> ballOrder;

    public FreeThrowDetailDto() {

    }
    public FreeThrowDetailDto(String thirdMatchId) {
        super();
        this.thirdMatchId = thirdMatchId;
    }
}
