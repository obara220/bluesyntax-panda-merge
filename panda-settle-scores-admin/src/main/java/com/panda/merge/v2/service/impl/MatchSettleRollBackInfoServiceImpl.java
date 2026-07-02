package com.panda.merge.v2.service.impl;

import com.panda.merge.v2.repository.MatchSettleRollBackInfoRepository;
import com.panda.merge.v2.service.IMatchSettleRollBackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("MatchSettleRollBackInfoServiceImplV2")
public class MatchSettleRollBackInfoServiceImpl implements IMatchSettleRollBackInfoService {
    @Autowired
    private MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;

}
