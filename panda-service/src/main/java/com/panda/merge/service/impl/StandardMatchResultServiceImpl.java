package com.panda.merge.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.StandardMatchResultDTO;
import com.panda.merge.mapper.StandardMatchResultMapper;
import com.panda.merge.model.StandardMatchResult;
import com.panda.merge.model.StandardMatchResultExample;
import com.panda.merge.service.StandardMatchResultService;

/**
 * <Description> 标准赛果信息
 * @author
 * @since
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardMatchResultServiceImpl implements StandardMatchResultService {

    @Autowired
    private StandardMatchResultMapper smrMapper;

	@Override
	public StandardMatchResult getStandardMatchResult(StandardMatchResultDTO smrDto) {
		StandardMatchResultExample example = new StandardMatchResultExample();
		example.createCriteria()
			.andStandardMatchIdEqualTo(smrDto.getStandardMatchId())
			//2，已确认；9，已修正
			.andStatusIn(Arrays.asList(2,9))
			.andMatchPeriodIdEqualTo(smrDto.getMatchPeriodId());
		if(smrDto.getFirstNumber() != null && smrDto.getFirstNumber() > 0) {
			example.createCriteria().andFirstNumberEqualTo(smrDto.getFirstNumber());
		}
		if(smrDto.getSecondNumber() != null && smrDto.getSecondNumber() > 0) {
			example.createCriteria().andSecondNumberEqualTo(smrDto.getSecondNumber());
		}

		List<StandardMatchResult> smrList = smrMapper.selectByExample(example);
		if(CollectionUtils.isEmpty(smrList)) {
			return null;
		}
		return smrList.get(0);
	}

}
