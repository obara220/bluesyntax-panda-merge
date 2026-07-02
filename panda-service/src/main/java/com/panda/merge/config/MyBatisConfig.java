package com.panda.merge.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置
 * Created by macro on 2019/4/8.
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.panda.merge.mapper", "com.panda.merge.dao", "com.panda.merge.data.mapper", "com.panda.merge.v2.mapper"})
public class MyBatisConfig {
}
