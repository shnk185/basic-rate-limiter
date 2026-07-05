package org.ngs.basicratelimiter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.basicratelimiter.dto.RateLimitConfig;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitConfigFetchResponse {
    private List<RateLimitConfig> rateLimitConfigs;
}
