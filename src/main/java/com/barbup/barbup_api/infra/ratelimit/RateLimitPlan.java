package com.barbup.barbup_api.infra.ratelimit;

import io.github.bucket4j.Bucket;

import java.time.Duration;

public enum RateLimitPlan {
    LOGIN_IP(5, Duration.ofMinutes(1)),
    REGISTER_IP(5, Duration.ofHours(1)),
    FORGOT_PASSWORD_IP(5, Duration.ofHours(1)),
    FORGOT_PASSWORD_EMAIL(3, Duration.ofHours(1)),
    VERIFY_CODE_IP(10, Duration.ofMinutes(15)),
    CONFIRM_EMAIL_IP(10, Duration.ofMinutes(15)),
    RESEND_VERIFICATION_IP(5, Duration.ofHours(1)),
    RESEND_VERIFICATION_EMAIL(5, Duration.ofHours(24));

    private final long capacity;
    private final Duration period;

    RateLimitPlan(long capacity, Duration period) {
        this.capacity = capacity;
        this.period = period;
    }

    public Bucket newBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillGreedy(capacity, period))
                .build();
    }
}
