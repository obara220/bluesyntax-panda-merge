package com.panda.merge.v2.service.impl;

import com.panda.merge.v2.repository.MatchSettleThirdEventRepository;
import com.panda.merge.v2.service.IMatchSettleThirdEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class MatchSettleThirdEventServiceImpl implements IMatchSettleThirdEventService {

    @Autowired
    private MatchSettleThirdEventRepository matchSettleThirdEventRepository;
}
