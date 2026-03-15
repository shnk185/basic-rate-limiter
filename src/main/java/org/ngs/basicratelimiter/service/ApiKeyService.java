package org.ngs.basicratelimiter.service;

import org.ngs.basicratelimiter.config.BasicRateLimiterConfig;
import org.ngs.basicratelimiter.dto.ApiKeyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    @Autowired
    private BasicRateLimiterConfig basicRateLimiterConfig;

    public ApiKeyDto createApiKey(ApiKeyDto apiKeyDto, String key) {
        if (!basicRateLimiterConfig.getAdminKey().equals(key)) {
            throw new RuntimeException("invalid key");
        }




    }
}
