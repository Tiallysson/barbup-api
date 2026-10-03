package com.barbup.barbup_api.infra.ratelimit;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private static final Map<String, RateLimitPlan> PLANS = Map.of(
            "/auth/login", RateLimitPlan.LOGIN_IP,
            "/auth/register", RateLimitPlan.REGISTER_IP,
            "/auth/forgot-password", RateLimitPlan.FORGOT_PASSWORD_IP,
            "/auth/verify-code", RateLimitPlan.VERIFY_CODE_IP,
            "/auth/confirm-email", RateLimitPlan.CONFIRM_EMAIL_IP,
            "/auth/resend-verification", RateLimitPlan.RESEND_VERIFICATION_IP
    );

    private final RateLimiter rateLimiter;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equals(request.getMethod()) || !PLANS.containsKey(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        RateLimitPlan plan = PLANS.get(request.getServletPath());
        ConsumptionProbe probe = rateLimiter.tryConsume(plan, request.getRemoteAddr());

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(request, response);
            return;
        }

        long retryAfter = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Too many requests\"}");
    }
}
