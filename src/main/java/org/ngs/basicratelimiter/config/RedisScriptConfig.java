package org.ngs.basicratelimiter.config;

import org.ngs.basicratelimiter.constants.RedisScripts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    @Qualifier("rateLimitRedisScript")
    public RedisScript<Long> rateLimitRedisScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(RedisScripts.RATE_LIMIT_SCRIPT);
        script.setResultType(Long.class);
        return script;
    }
}
