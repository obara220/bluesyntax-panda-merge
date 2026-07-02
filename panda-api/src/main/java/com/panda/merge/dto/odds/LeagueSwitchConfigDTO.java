package com.panda.merge.dto.odds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueSwitchConfigDTO implements Serializable {


    private String leagueLevel;

    /**
     * 早转滚状态开关
     */
    private boolean status;

    /**
     * 是否补充盘口状态开关
     */
    private boolean addMarketStatus;

}
