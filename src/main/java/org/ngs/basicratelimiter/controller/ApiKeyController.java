package org.ngs.basicratelimiter.controller;

import lombok.extern.slf4j.Slf4j;
import org.ngs.basicratelimiter.dto.ApiKeyDto;
import org.ngs.basicratelimiter.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/apiKey")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyDto> createApiKey(@RequestBody ApiKeyDto apiKeyDto, @RequestHeader(name = "x-api-key") String key) {
        log.info("received create api key request {}", apiKeyDto);
        ApiKeyDto response = apiKeyService.createApiKey(apiKeyDto, key);
        return ResponseEntity.ok(apiKeyDto);
    }

}
