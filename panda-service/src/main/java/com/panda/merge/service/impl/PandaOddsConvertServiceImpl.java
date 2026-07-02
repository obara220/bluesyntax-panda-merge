package com.panda.merge.service.impl;

import com.panda.merge.mapper.PandaOddsConvertMapper;
import com.panda.merge.model.PandaOddsConvert;
import com.panda.merge.model.PandaOddsConvertExample;
import com.panda.merge.service.PandaOddsConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/17 <br>
 */
@Service
public class PandaOddsConvertServiceImpl implements PandaOddsConvertService {

    @Autowired
    private PandaOddsConvertMapper pandaOddsConvertMapper;

    @Override
    public List<PandaOddsConvert> listAll() {
        return pandaOddsConvertMapper.selectByExample(new PandaOddsConvertExample());
    }
}
