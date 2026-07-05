package org.ngs.basicratelimiter.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.ngs.basicratelimiter.entity.RateLimitConfigEntity;
import org.ngs.basicratelimiter.enums.LimitType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RateLimitConfigSpecification {

    public static Specification<RateLimitConfigEntity> withFilters(String path, String method, LimitType limitType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (path != null) {
                predicates.add(cb.equal(root.get("requestPath"), path));
            }
            if (method != null) {
                predicates.add(cb.equal(root.get("requestMethod"), method));
            }
            if (limitType != null) {
                predicates.add(cb.equal(root.get("limitType"), limitType));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
