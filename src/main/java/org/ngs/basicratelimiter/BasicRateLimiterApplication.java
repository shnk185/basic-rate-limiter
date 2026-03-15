package org.ngs.basicratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BasicRateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicRateLimiterApplication.class, args);
    }

}
