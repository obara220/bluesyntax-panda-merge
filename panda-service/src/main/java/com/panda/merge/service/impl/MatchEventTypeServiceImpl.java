package com.panda.merge.service.impl;

import com.panda.merge.mapper.MatchEventTypeMapper;
import com.panda.merge.model.MatchEventType;
import com.panda.merge.model.MatchEventTypeExample;
import com.panda.merge.service.MatchEventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class MatchEventTypeServiceImpl implements MatchEventTypeService {

    @Autowired
    private MatchEventTypeMapper matchEventTypeMapper;

    @Override
    public List<MatchEventType> getItemAll() {
        return matchEventTypeMapper.selectByExample(new MatchEventTypeExample());
    }
}
