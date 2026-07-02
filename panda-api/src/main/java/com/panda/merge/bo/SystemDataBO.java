package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  Jimmy
 * @Project Name :  data-nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description :  TODO
 * @Date: 2020-03-04 17:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class SystemDataBO implements Serializable {
    private List<SystemTypeDictBO> systemTypeDictList;

}
