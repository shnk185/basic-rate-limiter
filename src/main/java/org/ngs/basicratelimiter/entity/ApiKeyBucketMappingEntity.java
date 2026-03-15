package org.ngs.basicratelimiter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "api_key_bucket_mapping")
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyBucketMappingEntity extends BaseEntity {
    private Long apiKeyId;
    private Long bucketId;
    private String apiPath;
    private String apiMethod;
}
