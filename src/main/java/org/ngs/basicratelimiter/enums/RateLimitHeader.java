package org.ngs.basicratelimiter.enums;

import lombok.Getter;
import org.ngs.basicratelimiter.constants.Constants;

@Getter
public enum RateLimitHeader {
    X_IP_ADDRESS(Constants.X_IP_ADDRESS),
    X_REQUEST_URI(Constants.X_REQUEST_URI),
    X_REQUEST_METHOD(Constants.X_REQUEST_METHOD);

    private final String headerName;

    RateLimitHeader(String headerName) {
        this.headerName = headerName;
    }
}
