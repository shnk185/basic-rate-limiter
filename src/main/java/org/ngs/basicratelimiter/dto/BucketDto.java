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
public class BucketDto {
    private List<ApiDto> apiPaths;
    private Integer refillRatePerMinute;
    private Integer capacityPerMinute;
}
