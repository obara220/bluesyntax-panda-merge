package com.panda.merge.service.impl;

import com.panda.merge.mapper.EuropeConvertMalayMapper;
import com.panda.merge.model.EuropeConvertMalay;
import com.panda.merge.model.EuropeConvertMalayExample;
import com.panda.merge.service.EuropeConvertMalayService;
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
 * @see com.panda.merge.service.impl <br>
 */
@Service
public class EuropeConvertMalayServiceImpl implements EuropeConvertMalayService {

    @Autowired
    private EuropeConvertMalayMapper europeConvertMalayMapper;

    @Override
    public List<EuropeConvertMalay> listAll() {
        return europeConvertMalayMapper.selectByExample(new EuropeConvertMalayExample());
    }
}
