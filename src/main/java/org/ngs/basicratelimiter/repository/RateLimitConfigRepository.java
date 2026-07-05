package org.ngs.basicratelimiter.repository;

import org.ngs.basicratelimiter.entity.RateLimitConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateLimitConfigRepository extends JpaRepository<RateLimitConfigEntity, Long>, JpaSpecificationExecutor<RateLimitConfigEntity> {
    List<RateLimitConfigEntity> findByDeletedFalse();
}
