package com.udesk.sharding.practice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fanfever
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.data-sources")
public class DataSourcesProperties {
    HikariConfig global;
    Map<String, HikariConfig> primary;
    Map<String, HikariConfig> vertical;

    @Bean("globalDataSource")
    public DataSource globalDataSource() {
        return getDataSource(global);
    }

    @Bean("primaryDataSources")
    public Map<String, DataSource> primaryDataSources() {
        return primary.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getDataSource(e.getValue())));
    }

    @Bean("verticalDataSources")
    public Map<String, DataSource> verticalDataSources() {
        if (MapUtils.isEmpty(vertical)) {
            return Collections.emptyMap();
        }
        return vertical.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getDataSource(e.getValue())));
    }

    private DataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }

}
