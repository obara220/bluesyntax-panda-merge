package com.panda.merge.job;


import com.panda.merge.mapper.MatchEventCommonExtMapper;
import com.panda.merge.mapper.MatchEventCommonMapper;
import com.panda.merge.model.MatchEventCommonExample;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
@Deprecated
@JobHandler(value = "MatchEventCommonCleanJob")
public class MatchEventCommonCleanJob extends IJobHandler {

    final Integer DELETE_LIMIT_DEFAULT=2000;
    final static Integer  DELETE_DATE=-1;
    @Autowired
    MatchEventCommonMapper matchEventCommonMapper;
    @Autowired
    MatchEventCommonExtMapper matchEventCommonExtMapper;
    @Override
    public ReturnT<String> execute(String deleteLimit) throws Exception {
        log.info("MatchEventCommonCleanJob param:{}",deleteLimit);
        Integer delete = DELETE_LIMIT_DEFAULT;
        if(StringUtils.isNotEmpty(deleteLimit)){
            try{
                delete = Integer.parseInt(deleteLimit);
            }catch (Exception e){
                log.error("{}",e);
            }
        }
        Long minId=  matchEventCommonExtMapper.selectMinPrimaryKey();
        Long deleteDate= getStatetime();
        MatchEventCommonExample matchEventCommonExample=new MatchEventCommonExample();
        matchEventCommonExample.createCriteria().andIdBetween(minId,minId+DELETE_LIMIT_DEFAULT) .andCreateTimeLessThan(deleteDate);

        matchEventCommonMapper.deleteByExample(matchEventCommonExample);
        return ReturnT.SUCCESS;
    }

    private static Long getStatetime()  {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, DELETE_DATE);
        Date time = c.getTime();
        return time.getTime();

    }
}
