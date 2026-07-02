package com.panda.merge.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * @author jitwxs
 * @date 2024年01月13日 17:07
 */
@Configuration
public class AppConfig {
    @Primary
    @Bean(name = "panda")
    @ConfigurationProperties("spring.shardingsphere.datasource.ds0")
    public DataSource dataSourceOne(){
        return new DruidDataSource();
    }

    @Bean(name = "pandaOdds")
    @ConfigurationProperties("spring.shardingsphere.datasource.ds1")
    public DataSource dataSourceTwo(){
        return new DruidDataSource();
    }

    @Bean(name = "pandaJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(
            @Qualifier("panda") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "pandaOddsJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate(
            @Qualifier("pandaOdds") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
