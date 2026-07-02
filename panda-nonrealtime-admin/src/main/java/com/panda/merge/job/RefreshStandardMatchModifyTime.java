package com.panda.merge.job;

import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.StandardMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 刷新标准赛事修改时间
 * @author : tell
 * @date: 2021年3月7日16:56:38
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "RefreshStandardMatchModifyTime")
public class RefreshStandardMatchModifyTime extends IJobHandler {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;


    @Override
    public ReturnT<String> execute(String standardMatchId){
        if(StringUtils.isNotBlank(standardMatchId)){
            StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
            standardMatchInfo.setId(Long.valueOf(standardMatchId));
            standardMatchInfoService.updateByPrimaryKeySelective(standardMatchInfo);
        }
        return ReturnT.SUCCESS;
    }
}
