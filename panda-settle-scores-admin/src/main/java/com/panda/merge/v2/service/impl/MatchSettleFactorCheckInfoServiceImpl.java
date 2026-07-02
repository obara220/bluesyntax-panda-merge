package com.panda.merge.v2.service.impl;

import com.panda.merge.v2.repository.MatchSettleFactorCheckInfoRepository;
import com.panda.merge.v2.service.IMatchSettleFactorCheckInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class MatchSettleFactorCheckInfoServiceImpl implements IMatchSettleFactorCheckInfoService {

    @Autowired
    private MatchSettleFactorCheckInfoRepository matchSettleFactorCheckInfoRepository;

}
