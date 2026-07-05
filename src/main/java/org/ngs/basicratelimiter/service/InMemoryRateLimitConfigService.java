package org.ngs.basicratelimiter.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.dto.RateLimitConfig;
import org.ngs.basicratelimiter.entity.RateLimitConfigEntity;
import org.ngs.basicratelimiter.enums.LimitType;
import org.ngs.basicratelimiter.repository.RateLimitConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryRateLimitConfigService {

    private ConcurrentHashMap<LimitType, ConcurrentHashMap<RequestMethod,
            ConcurrentHashMap<String, RateLimitConfig>>> inMemoryRateLimitConfig;

    @Autowired
    private RateLimitConfigRepository rateLimitConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void setUp() {
        this.inMemoryRateLimitConfig = readConfig();
    }

    public void update() {
        this.inMemoryRateLimitConfig = readConfig();
    }

    public ConcurrentHashMap<LimitType, ConcurrentHashMap<RequestMethod, ConcurrentHashMap<String, RateLimitConfig>>> readConfig() {
        ConcurrentHashMap<LimitType, ConcurrentHashMap<RequestMethod,
                ConcurrentHashMap<String, RateLimitConfig>>> res = new ConcurrentHashMap<>();
        for (LimitType limitType: LimitType.values()) {
            ConcurrentHashMap<RequestMethod, ConcurrentHashMap<String, RateLimitConfig>> requestMethodMap = new ConcurrentHashMap<>();
            for (RequestMethod requestMethod: RequestMethod.values()) {
                requestMethodMap.put(requestMethod, new ConcurrentHashMap<>());
            }
            res.put(limitType, requestMethodMap);
        }
        List<RateLimitConfigEntity> rateLimitConfigEntities = rateLimitConfigRepository.findByDeletedFalse();
        for (RateLimitConfigEntity rateLimitConfigEntity: rateLimitConfigEntities) {
            res.get(rateLimitConfigEntity.getLimitType())
                    .get(rateLimitConfigEntity.getRequestMethod())
                    .put(rateLimitConfigEntity.getRequestPath(), objectMapper.convertValue(rateLimitConfigEntity, RateLimitConfig.class));
        }
        return res;
    }

    public RateLimitConfig fetchConfig(LimitType limitType, RequestMethod requestMethod, String path) {
        ConcurrentHashMap<String, RateLimitConfig> configByPath = inMemoryRateLimitConfig.get(limitType).get(requestMethod);
        if (!configByPath.containsKey(path)) {
            log.error("no rate limit found for limitType {} requestMethod {} path {}", limitType, requestMethod, path);
            throw new RuntimeException("no rate limit found for path");
        }
        else return configByPath.get(path);
    }

}
