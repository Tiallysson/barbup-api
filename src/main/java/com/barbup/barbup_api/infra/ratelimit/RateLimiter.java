package com.barbup.barbup_api.infra.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;

@Component
public class RateLimiter {
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(24))
            .maximumSize(100_000)
            .build();

    public ConsumptionProbe tryConsume(RateLimitPlan plan, String key) {
        Bucket bucket = buckets.get(plan.name() + ":" + key.toLowerCase(Locale.ROOT), k -> plan.newBucket());
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    public boolean allow(RateLimitPlan plan, String key) {
        return tryConsume(plan, key).isConsumed();
    }
}
