package org.ngs.basicratelimiter.util;

import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.constants.KeyConstants;

@Slf4j
public class RedisKeyUtil {

    public static String generateLogoutKey(Long userId) {
        String key = String.join(KeyConstants.UNDERSCORE, KeyConstants.AUTH_SERVICE, KeyConstants.LOG_OUT,
                KeyConstants.USER_ID, String.valueOf(userId));
        log.info("generated log out key {}", key);
        return key;
    }

    public static String generateUserRateLimitKey(Long userId, String method, String path) {
        String key = String.join(KeyConstants.UNDERSCORE, KeyConstants.BASIC_RATE_LIMITER_SERVICE, KeyConstants.RATE_LIMIT,
                KeyConstants.REQUEST_METHOD, method, KeyConstants.REQUEST_PATH, path, KeyConstants.USER_ID, String.valueOf(userId));
        log.info("generated user rate limit key {}", key);
        return key;
    }

    public static String generateIPRateLimitKey(String userIp, String method, String path) {
        String key = String.join(KeyConstants.UNDERSCORE, KeyConstants.BASIC_RATE_LIMITER_SERVICE, KeyConstants.RATE_LIMIT,
                KeyConstants.REQUEST_METHOD, method, KeyConstants.REQUEST_PATH, path, KeyConstants.IP, String.valueOf(userIp));
        log.info("generated ip rate limit key {}", key);
        return key;
    }

    public static String generateNonAuthRateLimitKey(String method, String path) {
        String key = String.join(KeyConstants.UNDERSCORE, KeyConstants.BASIC_RATE_LIMITER_SERVICE, KeyConstants.RATE_LIMIT,
                KeyConstants.REQUEST_METHOD, method, KeyConstants.REQUEST_PATH, path);
        log.info("generated non auth rate limit key {}", key);
        return key;
    }

}
