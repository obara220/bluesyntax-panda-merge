package com.panda.merge.dto.advertise;

import lombok.Data;

import java.util.List;

@Data
public class Goal15MinDto extends AbstructAdvertiseDto  {
    private Long thirdMatchId;
    private Long period;
    private String confirmEventCode;
    private Long timeFromStartSecond;
    private List<Goal15MinDataDto> dataList;
    //ip地址
    private String  ipAddress;
}
