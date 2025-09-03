package com.example.delivery.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisHealthConfig {

    @Bean
    @ConditionalOnProperty(name = "management.health.redis.enabled", havingValue = "true", matchIfMissing = false)
    public HealthIndicator redisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return () -> {
            try {
                redisConnectionFactory.getConnection().ping();
                return Health.up()
                    .withDetail("redis", "Available")
                    .build();
            } catch (Exception e) {
                return Health.down()
                    .withDetail("redis", "Not available")
                    .withDetail("error", e.getMessage())
                    .build();
            }
        };
    }
}

