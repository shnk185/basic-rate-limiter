package org.ngs.basicratelimiter.service;

import lombok.RequiredArgsConstructor;
import org.ngs.basicratelimiter.config.BasicRateLimiterConfig;
import org.ngs.basicratelimiter.dto.RateLimitConfig;
import org.ngs.basicratelimiter.entity.RateLimitConfigEntity;
import org.ngs.basicratelimiter.enums.LimitType;
import org.ngs.basicratelimiter.repository.RateLimitConfigRepository;
import org.ngs.basicratelimiter.repository.specification.RateLimitConfigSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class RateLimitConfigService {

    private final BasicRateLimiterConfig basicRateLimiterConfig;
    private final RateLimitConfigRepository rateLimitConfigRepository;
    private final ObjectMapper objectMapper;
    private final InMemoryRateLimitConfigService inMemoryRateLimitConfigService;

    public RateLimitConfig createRateLimit(RateLimitConfig rateLimitConfig, String apiKey) {
        if (!basicRateLimiterConfig.getAdminKey().equals(apiKey)) {
            throw new RuntimeException("invalid api key");
        }

        RateLimitConfigEntity rateLimitConfigEntity = objectMapper.convertValue(rateLimitConfig, RateLimitConfigEntity.class);
        rateLimitConfigRepository.save(rateLimitConfigEntity);
        inMemoryRateLimitConfigService.update();
        return objectMapper.convertValue(rateLimitConfigEntity, RateLimitConfig.class);
    }

    public RateLimitConfig updateRateLimit(RateLimitConfig rateLimitConfig, String apiKey, Long rateLimitConfigId) {
        if (!basicRateLimiterConfig.getAdminKey().equals(apiKey)) {
            throw new RuntimeException("invalid api key");
        }
        RateLimitConfigEntity rateLimitConfigEntity = rateLimitConfigRepository.findById(rateLimitConfigId).orElseThrow();

        rateLimitConfigEntity.setLimitType(rateLimitConfig.getLimitType());
        rateLimitConfigEntity.setBucketCapacity(rateLimitConfig.getBucketCapacity());
        rateLimitConfigEntity.setRequestMethod(rateLimitConfig.getRequestMethod());
        rateLimitConfigEntity.setRequestPath(rateLimitConfig.getRequestPath());
        rateLimitConfigEntity.setRefillRate(rateLimitConfig.getRefillRate());

        rateLimitConfigRepository.save(rateLimitConfigEntity);
        inMemoryRateLimitConfigService.update();
        return objectMapper.convertValue(rateLimitConfigEntity, RateLimitConfig.class);
    }

    public RateLimitConfig deleteRateLimit(String apiKey, Long rateLimitConfigId) {
        if (!basicRateLimiterConfig.getAdminKey().equals(apiKey)) {
            throw new RuntimeException("invalid api key");
        }
        RateLimitConfigEntity rateLimitConfigEntity = rateLimitConfigRepository.findById(rateLimitConfigId).orElseThrow();
        rateLimitConfigEntity.setDeleted(true);
        rateLimitConfigRepository.save(rateLimitConfigEntity);
        inMemoryRateLimitConfigService.update();
        return objectMapper.convertValue(rateLimitConfigEntity, RateLimitConfig.class);
    }

    public Page<RateLimitConfigEntity> fetchRateLimitConfigs(String apiKey, String path, String method, LimitType limitType, Pageable pageable) {
        if (!basicRateLimiterConfig.getAdminKey().equals(apiKey)) {
            throw new RuntimeException("invalid api key");
        }
        Specification<RateLimitConfigEntity> specification = RateLimitConfigSpecification.withFilters(path, method, limitType);
        return rateLimitConfigRepository.findAll(specification, pageable);
    }
}
