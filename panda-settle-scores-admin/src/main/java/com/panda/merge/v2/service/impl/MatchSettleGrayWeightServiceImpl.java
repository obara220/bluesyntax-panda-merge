package com.panda.merge.v2.service.impl;

import com.panda.merge.v2.repository.MatchSettleGrayWeightV2Repository;
import com.panda.merge.v2.service.IMatchSettleGrayWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("MatchSettleGrayWeightServiceImplV2")
public class MatchSettleGrayWeightServiceImpl implements IMatchSettleGrayWeightService {

    @Autowired
    private MatchSettleGrayWeightV2Repository matchSettleGrayWeightRepository;
}
