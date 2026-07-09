package org.ngs.basicratelimiter.controller;

import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.constants.Constants;
import org.ngs.basicratelimiter.dto.RateLimitConfig;
import org.ngs.basicratelimiter.entity.RateLimitConfigEntity;
import org.ngs.basicratelimiter.enums.LimitType;
import org.ngs.basicratelimiter.service.RateLimitConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/rateLimit/configs")
public class RateLimitConfigController {

    @Autowired
    private RateLimitConfigService rateLimitConfigService;

    @PostMapping
    public ResponseEntity<RateLimitConfig> createRateLimitConfig(@RequestBody RateLimitConfig rateLimitConfig,
                                                                         @RequestHeader(Constants.X_API_KEY) String apiKey) {
        log.info("received create rate limit config request {}", rateLimitConfig);
        RateLimitConfig response = rateLimitConfigService.createRateLimit(rateLimitConfig, apiKey);
        log.info("create rate limit config response {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{rateLimitConfigId}")
    public ResponseEntity<RateLimitConfig> updateRateLimitConfig(@RequestBody RateLimitConfig rateLimitConfig,
                                                                 @RequestHeader(Constants.X_API_KEY) String apiKey,
                                                                 @PathVariable Long rateLimitConfigId) {
        log.info("received update rate limit config request {} rateLimitConfigId {}", rateLimitConfig, rateLimitConfigId);
        RateLimitConfig response = rateLimitConfigService.updateRateLimit(rateLimitConfig, apiKey, rateLimitConfigId);
        log.info("update rate limit config response {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{rateLimitConfigId}")
    public ResponseEntity<RateLimitConfig> deleteRateLimitConfig(@RequestHeader(Constants.X_API_KEY) String apiKey,
                                                                 @PathVariable Long rateLimitConfigId) {
        log.info("received delete rate limit config request {}",  rateLimitConfigId);
        RateLimitConfig response = rateLimitConfigService.deleteRateLimit(apiKey, rateLimitConfigId);
        log.info("delete rate limit config response {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<RateLimitConfigEntity>> fetchRateLimitConfig(@RequestParam(name = "path", required = false) String path,
                                                                        @RequestParam(name = "method", required = false) String method,
                                                                        @RequestParam(name = "limitType", required = false) LimitType limitType,
                                                                        @RequestHeader(Constants.X_API_KEY) String apiKey,
                                                                        Pageable pageable) {
        log.info("received fetch rate limit config request path {} method {} limitType {} pageable {}", path, method, limitType, pageable);
        Page<RateLimitConfigEntity> response = rateLimitConfigService.fetchRateLimitConfigs(apiKey, path, method, limitType, pageable);
        log.info("fetch rate limit config response {}", response);
        return ResponseEntity.ok(response);
    }
}
