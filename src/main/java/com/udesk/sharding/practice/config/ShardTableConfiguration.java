package com.udesk.sharding.practice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "shard-table")
class ShardTableConfiguration {
    List<String> primary;
}
