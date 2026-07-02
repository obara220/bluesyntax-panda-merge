package com.panda.merge.service;

import com.panda.merge.model.StandardSportMarketM;

import java.util.List;

public interface StandardSportMarketMService {

    StandardSportMarketM getItem(Long standardMatchInfoId, Long relationMarketId);

    void insertList(String linkId, List<StandardSportMarketM> list);

    void updateBatch(String linkId, List<StandardSportMarketM> list);


}
