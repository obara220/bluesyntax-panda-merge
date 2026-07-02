package com.panda.merge.v2.service.impl;

import com.panda.merge.v2.repository.MatchSettleDataSourceConfigRepository;
import com.panda.merge.v2.service.IMatchSettleDataSourceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("MatchSettleDataSourceConfigServiceImplV2")
public class MatchSettleDataSourceConfigServiceImpl implements IMatchSettleDataSourceConfigService {

    @Autowired
    private MatchSettleDataSourceConfigRepository matchSettleDataSourceConfigRepository;
}
