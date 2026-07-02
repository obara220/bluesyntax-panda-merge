package com.panda.merge.dto.settle;


import lombok.Data;

import java.io.Serializable;

@Data
public class DataSourceGrayIntervalDto extends AbstructMatchSettleDto implements Serializable {

    private String dataSourceCode;

    private Integer tournamentLevel;

    private Integer min15Goal;

    private Integer min15Corner;

    private Integer min15Bookings;

    private Integer min5Goal;

}
