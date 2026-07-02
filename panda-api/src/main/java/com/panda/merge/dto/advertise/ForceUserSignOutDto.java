package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class ForceUserSignOutDto extends AbstructAdvertiseDto  {
    private String pdUserName;
    private Long managerId;
    private String managerName;
}
