package org.ngs.basicratelimiter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "basic.rate.limiter.config")
public class BasicRateLimiterConfig {
    private String adminKey;
}
