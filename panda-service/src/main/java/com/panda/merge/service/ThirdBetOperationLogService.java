package com.panda.merge.service;

import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.dto.ThirdBetCancelRollbackDTO;
import com.panda.merge.dto.ThirdBetSettlementRollbackDTO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-01-16
 */

public interface ThirdBetOperationLogService {

    void thirdBetCancelCreate(ThirdBetCancelDTO thirdBetCancelDTO);

    void betCancelRollbackCreate(ThirdBetCancelRollbackDTO thirdBetCancelRollbackDTO);

    void betBetSettlementRollbackCreate(ThirdBetSettlementRollbackDTO thirdBetSettlementRollbackDTO);
}
