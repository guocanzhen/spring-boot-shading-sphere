package com.oujiong.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author guocz
 * @date 2022/7/21 14:47
 *
 * 连接数据库信息
 */
@Configuration
public class DalModule {

    /**
     * SqlSessionFactory 实体
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setFailFast(true);
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*Mapper.xml"));
        return sessionFactory.getObject();
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("tab_user");
        shardingRuleConfig.getBroadcastTables().add("t_config");
        // 根据年龄分库 一共分为2个库
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("age", "db${age % 2}"));
        // 根据ID分表  一共分为2张表
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new PreciseModuloShardingTableAlgorithm()));
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new Properties());
    }

    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration() {
        return new KeyGeneratorConfiguration("SNOWFLAKE", "id");
    }

    TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("tab_user", "db${0..1}.tab_user${0..1}");
        result.setKeyGeneratorConfig(getKeyGeneratorConfiguration());
        return result;
    }

    Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>(16);
        result.put("db0", DataSourceUtil.createDataSource("db0"));
        result.put("db1", DataSourceUtil.createDataSource("db1"));
        return result;
    }

}
