package org.ngs.basicratelimiter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.basicratelimiter.enums.LimitType;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rate_limit_configs")
public class RateLimitConfigEntity extends BaseEntity {
    private LimitType limitType;
    private RequestMethod requestMethod;
    private String requestPath;
    private Integer refillRate;
    private Integer bucketCapacity;
}
