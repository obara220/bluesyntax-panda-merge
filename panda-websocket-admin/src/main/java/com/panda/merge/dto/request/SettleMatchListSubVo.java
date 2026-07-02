package com.panda.merge.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SettleMatchListSubVo implements Serializable {
    private List<Long> standardMatchIdList;
}
