package org.ngs.basicratelimiter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyDto {
    private String apiKey;
    private String userName;
    private List<BucketDto> buckets;
}
