package org.ngs.basicratelimiter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "api_key")
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyEntity extends BaseEntity {
    private String userName;
    private String apiKey;
}
