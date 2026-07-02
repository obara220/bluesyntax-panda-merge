package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdGlobalStatusDTO;
import com.panda.merge.model.ThirdGlobalStatusLog;
import com.panda.merge.rocketmq.producer.ThirdGlobalStatusProducer;
import com.panda.merge.service.ThirdGlobalStatusLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Slf4j
@Component
@Validated
public class ThirdGlobalStatusProcessor {

    @Autowired
    ThirdGlobalStatusLogService thirdGlobalStatusLogService;

    @Autowired
    ThirdGlobalStatusProducer thirdGlobalStatusProducer;

    public void putGlobalStatus(@Valid Request<ThirdGlobalStatusDTO> request){
        ThirdGlobalStatusDTO thirdGlobalStatusDTO = request.getData();
        //log.info("::{}::数据源状态实时数据, thirdGlobalStatusDTO: {}", request.getLinkId(), JSON.toJSONString(thirdGlobalStatusDTO));
        ThirdGlobalStatusLog thirdGlobalStatusLog = thirdGlobalStatusLogService.create(thirdGlobalStatusDTO);
        //推送给下游MQ
        thirdGlobalStatusProducer.pushThirdGlobalStatus(request.getLinkId(),thirdGlobalStatusDTO);
    }
}
