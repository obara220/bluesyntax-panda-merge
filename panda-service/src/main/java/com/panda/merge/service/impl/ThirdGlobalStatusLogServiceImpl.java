package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.ThirdGlobalStatusDTO;
import com.panda.merge.mapper.ThirdGlobalStatusLogMapper;
import com.panda.merge.model.ThirdGlobalStatusLog;
import com.panda.merge.service.ThirdGlobalStatusLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
public class ThirdGlobalStatusLogServiceImpl implements ThirdGlobalStatusLogService {

    @Autowired
    private ThirdGlobalStatusLogMapper thirdGlobalStatusLogMapper;

    @Override
    public ThirdGlobalStatusLog create(ThirdGlobalStatusDTO thirdGlobalStatusDTO) {
        ThirdGlobalStatusLog thirdGlobalStatusLog = new ThirdGlobalStatusLog();
        thirdGlobalStatusLog.setId(UUIdUtils.getId());
        BeanUtils.copyProperties(thirdGlobalStatusDTO,thirdGlobalStatusLog);
        thirdGlobalStatusLog.setSendTimeStamp(thirdGlobalStatusDTO.getSendTimestamp());
        thirdGlobalStatusLog.setSourceTimesTamp(thirdGlobalStatusDTO.getSourceTimestamp());
        thirdGlobalStatusLog.setSendTimeStampRonghe(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdGlobalStatusLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdGlobalStatusLogMapper.insertSelective(thirdGlobalStatusLog);
        return thirdGlobalStatusLog;
    }
}
