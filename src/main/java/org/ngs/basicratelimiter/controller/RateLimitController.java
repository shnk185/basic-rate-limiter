package org.ngs.basicratelimiter.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.enums.RateLimitHeader;
import org.ngs.basicratelimiter.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void rateLimit(HttpServletRequest httpServletRequest) {
        Map<RateLimitHeader, String> rateLimitParams = new HashMap<>();
        for (RateLimitHeader rateLimitHeader: RateLimitHeader.values()) {
            if (httpServletRequest.getHeader(rateLimitHeader.getHeaderName()) != null) {
                rateLimitParams.put(rateLimitHeader, httpServletRequest.getHeader(rateLimitHeader.getHeaderName()));
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.getPrincipal() instanceof Long) {
            try {
                userId = (long) auth.getPrincipal();
            } catch (NumberFormatException e) {
                log.info("non auth user");
            }
        }

        log.info("received rate limit request params {} userId {}", rateLimitParams, userId);
        rateLimitService.rateLimit(rateLimitParams, userId);
        log.info("allowing request");
    }
}
