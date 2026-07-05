package org.ngs.basicratelimiter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.basicratelimiter.enums.LimitType;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitConfig {
    private Long id;
    private LimitType limitType;
    private RequestMethod requestMethod;
    private String requestPath;
    private Integer refillRate;
    private Integer bucketCapacity;
}
