package com.udesk.sharding.practice.config;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author fanfever
 * @date 2019/10/21
 */
@Configuration
public class PrimaryDataSourceConfiguration {

    static final String PACKAGE = "com.udesk.sharding.practice.dal.mapper";
    private static final String MAPPER_LOCATION = "classpath*:mybatis/sqlmap/*.xml";

    /**
     * 配置数据源规则，即将多个数据源交给sharding-jdbc管理，并且可以设置默认的数据源，
     * 当表没有配置分库规则时会使用默认的数据源
     */
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource(@Qualifier("primaryDataSources") Map<String, DataSource> primaryDataSources) throws SQLException {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();

        shardingRuleConfiguration.setDefaultDataSourceName("primary1");
        shardingRuleConfiguration.setDefaultDatabaseShardingStrategyConfig(new HintShardingStrategyConfiguration(new UdeskHintShardingAlgorithm()));
        shardingRuleConfiguration.setDefaultTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("agent_hints");

//        tableRuleConfiguration.setTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());
//        tableRuleConfiguration.setDatabaseShardingStrategyConfig(new HintShardingStrategyConfiguration(new UdeskHintShardingAlgorithm()));
        shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);
//        shardingRuleConfiguration.setDefaultTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());
//        shardingRuleConfiguration.getTableRuleConfigs().addAll(shardTableConfiguration.getPrimary().stream()
//                .map(TableRuleConfiguration::new)
//                .collect(Collectors.toList()));
        Map<String, DataSource> dataSourceMap = Maps.newHashMapWithExpectedSize(8);
        dataSourceMap.putAll(primaryDataSources);
        Properties properties = new Properties();
        properties.setProperty("sql.show", "true");
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, properties);
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("primaryDataSource") DataSource primaryDataSource) throws SQLException {
        return new DataSourceTransactionManager(primaryDataSource);
    }

    @Primary
    @Bean(name = "primarySqlSessionFactory")
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource primaryDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(primaryDataSource);
        Resource[] primaryResources = new PathMatchingResourcePatternResolver()
                .getResources(PrimaryDataSourceConfiguration.MAPPER_LOCATION);
//        if (MapUtils.isNotEmpty(verticalDataSources)) {
//            Set<String> verticalResource = Sets.newHashSetWithExpectedSize(6);
//            if (verticalDataSources.containsKey("imlogs")) {
//                verticalResource.add("ImlogsQueryMapper.xml");
//                verticalResource.add("ImlogsCommandMapper.xml");
//            }
//            if (verticalDataSources.containsKey("imrobotlogs")) {
//                verticalResource.add("ImRobotLogQueryMapper.xml");
//                verticalResource.add("ImRobotLogCommandMapper.xml");
//            }
//            primaryResources = Arrays.stream(primaryResources).filter(i -> !verticalResource.contains(i.getFilename())).toArray(Resource[]::new);
//        }
        sessionFactory.setMapperLocations(primaryResources);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
//        sessionFactory.setPlugins(dataScopePlugin, resultLimitPlugin);
        return sessionFactory.getObject();
    }

    @Bean
    @Primary
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("primarySqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(PACKAGE);
        return mapperScannerConfigurer;
    }

    @Bean(name = "imlogsTransactionManager")
    public DataSourceTransactionManager imlogsTransactionManager(@Qualifier("verticalDataSources") Map<String, DataSource> verticalDataSources) {
        if(Objects.isNull(verticalDataSources.get("imlogs"))){
            return null;
        }
        return new DataSourceTransactionManager(verticalDataSources.get("imlogs"));
    }

//    @Bean(name = "imlogsSqlSessionFactory")
//    public SqlSessionFactory imlogsSqlSessionFactory(@Qualifier("verticalDataSources") Map<String, DataSource> verticalDataSources, @Qualifier("dataScopePlugin") DataScopePlugin dataScopePlugin, @Qualifier("resultLimitPlugin") ResultLimitPlugin resultLimitPlugin)
//            throws Exception {
//        if(Objects.isNull(verticalDataSources.get("imlogs"))){
//            return null;
//        }
//        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(verticalDataSources.get("imlogs"));
//        sessionFactory.setMapperLocations(Arrays.stream(new PathMatchingResourcePatternResolver()
//                .getResources(MAPPER_LOCATION)).filter(i -> StringUtils.equalsAny(i.getFilename(), "ImlogsQueryMapper.xml", "ImlogsCommandMapper.xml")).toArray(Resource[]::new));
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        configuration.setMapUnderscoreToCamelCase(true);
//        sessionFactory.setConfiguration(configuration);
//        sessionFactory.setPlugins(dataScopePlugin, resultLimitPlugin);
//        return sessionFactory.getObject();
//    }

    @Bean(name = "imrobotlogsTransactionManager")
    public DataSourceTransactionManager imrobotlogsTransactionManager(@Qualifier("verticalDataSources") Map<String, DataSource> verticalDataSources) throws SQLException {
        if(Objects.isNull(verticalDataSources.get("imrobotlogs"))){
            return null;
        }
        return new DataSourceTransactionManager(verticalDataSources.get("imrobotlogs"));
    }

//    @Bean(name = "imrobotlogsSqlSessionFactory")
//    public SqlSessionFactory imrobotlogsSqlSessionFactory(@Qualifier("verticalDataSources") Map<String, DataSource> verticalDataSources, @Qualifier("dataScopePlugin") DataScopePlugin dataScopePlugin, @Qualifier("resultLimitPlugin") ResultLimitPlugin resultLimitPlugin)
//            throws Exception {
//        if(Objects.isNull(verticalDataSources.get("imrobotlogs"))){
//            return null;
//        }
//        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(verticalDataSources.get("imrobotlogs"));
//        sessionFactory.setMapperLocations(Arrays.stream(new PathMatchingResourcePatternResolver()
//                .getResources(MAPPER_LOCATION)).filter(i -> StringUtils.equalsAny(i.getFilename(), "ImRobotLogsQueryMapper.xml", "ImRobotLogsCommandMapper.xml")).toArray(Resource[]::new));
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        configuration.setMapUnderscoreToCamelCase(true);
//        sessionFactory.setConfiguration(configuration);
//        sessionFactory.setPlugins(dataScopePlugin, resultLimitPlugin);
//        return sessionFactory.getObject();
//    }

}
