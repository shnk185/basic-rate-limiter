package org.ngs.basicratelimiter.service;

import org.ngs.basicratelimiter.dto.response.RateLimitResponse;
import org.ngs.basicratelimiter.enums.LimitType;
import org.ngs.basicratelimiter.enums.RateLimitHeader;
import org.ngs.basicratelimiter.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    @Autowired
    private InMemoryRateLimitConfigService inMemoryRateLimitConfigService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    @Qualifier("rateLimitRedisScript")
    private RedisScript<Long> rateLimitRedisScript;

    public RateLimitResponse rateLimit(Map<RateLimitHeader, String> rateLimitParams, Long userId) {
        String method = rateLimitParams.get(RateLimitHeader.X_REQUEST_METHOD);
        String ip = rateLimitParams.get(RateLimitHeader.X_IP_ADDRESS);
        String path = sanitize(rateLimitParams.get(RateLimitHeader.X_REQUEST_URI));
        if (userId != null) {
            String matchedPath = getMatchingPath(path, LimitType.AUTH, RequestMethod.valueOf(method));
            String key = RedisKeyUtil.generateUserRateLimitKey(userId, method, matchedPath);

            //redisTemplate.execute(rateLimitRedisScript, key, )
        } else {
            String matchedPath = getMatchingPath(path, LimitType.IP, RequestMethod.valueOf(method));
            String key = RedisKeyUtil.generateIPRateLimitKey(ip, method, path);

            matchedPath = getMatchingPath(path, LimitType.NON_AUTH, RequestMethod.valueOf(method));
            key = RedisKeyUtil.generateNonAuthRateLimitKey(method, path);

        }
        return null;
    }

    private String sanitize(String url) {
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            url = url.substring(0, questionMarkIndex);
        }
        if (url.charAt(url.length()-1) == '/') {
            url = url.substring(0, url.length()-1);
        }
        return url;
    }

    private String getMatchingPath(String source, LimitType limitType, RequestMethod requestMethod) {
        Set<String> paths = inMemoryRateLimitConfigService.fetchPaths(limitType, requestMethod);
        ConcurrentHashMap<String, PathPattern> pathPatternMap = inMemoryRateLimitConfigService.fetchPathPatternMap();
        PathContainer pathContainer = PathContainer.parsePath(source);
        for (String path: paths) {
            if (pathPatternMap.get(path).matches(pathContainer)) {
                return path;
            }
        }
        return "";
    }
}
