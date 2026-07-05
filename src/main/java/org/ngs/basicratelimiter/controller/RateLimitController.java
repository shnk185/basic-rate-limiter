package org.ngs.basicratelimiter.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.dto.response.RateLimitResponse;
import org.ngs.basicratelimiter.enums.RateLimitHeader;
import org.ngs.basicratelimiter.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/rateLimit")
public class RateLimitController {

    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping
    public ResponseEntity<RateLimitResponse> rateLimit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Map<RateLimitHeader, String> rateLimitParams = new HashMap<>();
        for (RateLimitHeader rateLimitHeader: RateLimitHeader.values()) {
            if (httpServletRequest.getHeader(rateLimitHeader.getHeaderName()) != null) {
                rateLimitParams.put(rateLimitHeader, httpServletRequest.getHeader(rateLimitHeader.getHeaderName()));
            }
        }
        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            userId = (Long) auth.getPrincipal();
        }

        log.info("received rate limit request params {} userId {}", rateLimitParams, userId);
        RateLimitResponse response = rateLimitService.rateLimit(rateLimitParams, userId);
        log.info("rate limit response {}", response);
        return ResponseEntity.ok(response);
    }
}
