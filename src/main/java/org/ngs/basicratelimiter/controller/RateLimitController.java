package org.ngs.basicratelimiter.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.dto.response.RateLimitResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;


@Slf4j
@RestController
@RequestMapping("/rateLimit")
public class RateLimitController {

    @GetMapping
    public ResponseEntity<RateLimitResponse> rateLimit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Enumeration<String> e = httpServletRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            String header = e.nextElement();
            log.info("{} {}", header, httpServletRequest.getHeader(header));
        }
        return null;
    }
}
