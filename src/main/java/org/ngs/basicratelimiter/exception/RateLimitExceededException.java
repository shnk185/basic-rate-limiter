package org.ngs.basicratelimiter.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends BasicRateLimiterException {

    public RateLimitExceededException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
