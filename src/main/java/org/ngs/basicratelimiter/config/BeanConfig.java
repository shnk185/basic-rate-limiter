package org.ngs.basicratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class BeanConfig {
    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
