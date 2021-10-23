package com.udesk.sharding.practice.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * @author fanfever
 * @date 2019/10/21
 */
@Configuration
@MapperScan(basePackages = GlobalDataSourceConfiguration.PACKAGE, sqlSessionFactoryRef = "globalSqlSessionFactory")
public class GlobalDataSourceConfiguration {

    // 精确到 global 目录，以便跟其他数据源隔离
    static final String PACKAGE = "cn.udesk.im.repository.global";
    private static final String MAPPER_LOCATION = "classpath*:sqlmap/global/**/*.xml";

    @Bean(name = "globalTransactionManager")
    public DataSourceTransactionManager globalTransactionManager(@Qualifier("globalDataSource") DataSource globalDataSource) {
        return new DataSourceTransactionManager(globalDataSource);
    }

    @Bean(name = "globalSqlSessionFactory")
    public SqlSessionFactory globalSqlSessionFactory(@Qualifier("globalDataSource") DataSource globalDataSource,  @Qualifier("pageInterceptor") PageInterceptor pageInterceptor)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(globalDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MAPPER_LOCATION));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
//        sessionFactory.setPlugins(pageInterceptor, dataScopePlugin);
        return sessionFactory.getObject();
    }

}
