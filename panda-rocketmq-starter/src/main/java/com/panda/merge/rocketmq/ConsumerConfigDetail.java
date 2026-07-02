package com.panda.merge.rocketmq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hunta
 * @since 9/11/2023
 */


@Builder
@Getter
@Setter
public class ConsumerConfigDetail {
    private Integer threadNumber;
    private Integer messageSize;
    // 一次从broker里最多拉取多少条数据。 拉到了再分配给下面的多线程
    private Integer pullBatchSize;

    private Long pullInterval;

}
