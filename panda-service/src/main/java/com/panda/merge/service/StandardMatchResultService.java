package com.panda.merge.service;

import com.panda.merge.dto.StandardMatchResultDTO;
import com.panda.merge.model.StandardMatchResult;

/**
 * <Description> 获得标准赛果信息
 * @author
 * @since
 */
public interface StandardMatchResultService {

	StandardMatchResult getStandardMatchResult(StandardMatchResultDTO smrDto);

}
