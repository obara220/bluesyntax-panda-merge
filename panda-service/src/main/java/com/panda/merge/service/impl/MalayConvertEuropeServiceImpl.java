package com.panda.merge.service.impl;

import com.panda.merge.mapper.MalayConvertEuropeMapper;
import com.panda.merge.model.MalayConvertEurope;
import com.panda.merge.model.MalayConvertEuropeExample;
import com.panda.merge.service.MalayConvertEuropeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/27 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
public class MalayConvertEuropeServiceImpl implements MalayConvertEuropeService {

    @Autowired
    MalayConvertEuropeMapper malayConvertEuropeMapper;

    @Override
    public List<MalayConvertEurope> listAll() {
        return malayConvertEuropeMapper.selectByExample(new MalayConvertEuropeExample());
    }
}
