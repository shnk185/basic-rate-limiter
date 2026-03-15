package org.ngs.basicratelimiter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "bucket")
@AllArgsConstructor
@NoArgsConstructor
public class BucketEntity extends BaseEntity {
    private Integer refillRatePerMinute;
    private Integer capacityPerMinute;
}
