package org.ngs.basicratelimiter.service;

import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.dto.RateLimitConfig;
import org.ngs.basicratelimiter.enums.LimitType;
import org.ngs.basicratelimiter.enums.RateLimitHeader;
import org.ngs.basicratelimiter.exception.RateLimitExceededException;
import org.ngs.basicratelimiter.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.pattern.PathPattern;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RateLimitService {

    private final InMemoryRateLimitConfigService inMemoryRateLimitConfigService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> rateLimitRedisScript;

    public RateLimitService(InMemoryRateLimitConfigService inMemoryRateLimitConfigService,
                            RedisTemplate<String, String> redisTemplate,
                            @Qualifier("rateLimitRedisScript") RedisScript<Long> rateLimitRedisScript) {
        this.inMemoryRateLimitConfigService = inMemoryRateLimitConfigService;
        this.redisTemplate = redisTemplate;
        this.rateLimitRedisScript = rateLimitRedisScript;
    }

    public void rateLimit(Map<RateLimitHeader, String> rateLimitParams, Long userId) {
        String method = rateLimitParams.get(RateLimitHeader.X_REQUEST_METHOD);
        String ip = rateLimitParams.get(RateLimitHeader.X_IP_ADDRESS);
        String path = sanitize(rateLimitParams.get(RateLimitHeader.X_REQUEST_URI));
        String matchedPath = getMatchingPath(path);
        if ("".equals(matchedPath)) {
            throw new RuntimeException("invalid uri");
        }

        if (userId != null) {
            rateLimitAuthenticatedUser(userId, method, matchedPath);
        } else {
            rateLimitIPAddress(ip, method, matchedPath);
            rateLimitNonAuthUser(method, matchedPath);
        }
    }

    private void rateLimitNonAuthUser(String method, String matchedPath) {
        String key = RedisKeyUtil.generateNonAuthRateLimitKey(method, matchedPath);
        RateLimitConfig rateLimitConfig = inMemoryRateLimitConfigService.fetchConfig(LimitType.NON_AUTH, RequestMethod.valueOf(method), matchedPath);
        Long res = redisTemplate.execute(rateLimitRedisScript, List.of(key),
                String.valueOf(rateLimitConfig.getBucketCapacity()),
                String.valueOf(rateLimitConfig.getRefillRate()),
                String.valueOf(Instant.now().getEpochSecond()));
        if (res == null || res == 0L) {
            throw new RateLimitExceededException("rate limit exceeded");
        }
    }

    private void rateLimitIPAddress(String ip, String method, String matchedPath) {
        String key = RedisKeyUtil.generateIPRateLimitKey(ip, method, matchedPath);
        RateLimitConfig rateLimitConfig = inMemoryRateLimitConfigService.fetchConfig(LimitType.IP, RequestMethod.valueOf(method), matchedPath);
        Long res = redisTemplate.execute(rateLimitRedisScript, List.of(key),
                String.valueOf(rateLimitConfig.getBucketCapacity()),
                String.valueOf(rateLimitConfig.getRefillRate()),
                String.valueOf(Instant.now().getEpochSecond()));
        if (res == null || res == 0L) {
            throw new RateLimitExceededException("rate limit exceeded");
        }
    }


    private void rateLimitAuthenticatedUser(Long userId, String method, String matchedPath) {
        String key = RedisKeyUtil.generateUserRateLimitKey(userId, method, matchedPath);
        RateLimitConfig rateLimitConfig = inMemoryRateLimitConfigService.fetchConfig(LimitType.AUTH, RequestMethod.valueOf(method), matchedPath);
        Long res = redisTemplate.execute(rateLimitRedisScript, List.of(key),
                String.valueOf(rateLimitConfig.getBucketCapacity()),
                String.valueOf(rateLimitConfig.getRefillRate()),
                String.valueOf(Instant.now().getEpochSecond()));
        if (res == null || res == 0L) {
            throw new RateLimitExceededException("rate limit exceeded");
        }
    }

    private String sanitize(String url) {
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            url = url.substring(0, questionMarkIndex);
        }
        if (url.length() > 1 && url.charAt(url.length()-1) == '/') {
            url = url.substring(0, url.length()-1);
        }
        log.info("sanitized url {}", url);
        return url;
    }

    private String getMatchingPath(String source) {
        ConcurrentHashMap<String, PathPattern> pathPatternMap = inMemoryRateLimitConfigService.fetchPathPatternMap();
        PathContainer pathContainer = PathContainer.parsePath(source);
        for (String path: pathPatternMap.keySet()) {
            if (pathPatternMap.get(path).matches(pathContainer)) {
                log.info("matched path {}", path);
                return path;
            }
        }
        return "";
    }
}
