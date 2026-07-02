package com.panda.merge.service;

import com.panda.merge.dto.ThirdGlobalStatusDTO;
import com.panda.merge.model.ThirdGlobalStatusLog;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface ThirdGlobalStatusLogService {

    ThirdGlobalStatusLog create(ThirdGlobalStatusDTO thirdGlobalStatusDTO);

}
