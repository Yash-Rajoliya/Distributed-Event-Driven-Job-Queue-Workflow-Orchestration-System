package com.djqueue.common.annotations;

import java.lang.annotation.*;

/**
 * Ensures safe idempotent execution across distributed workers.
 *
 * Supports:
 * - deterministic key generation
 * - tenant-aware namespace isolation
 * - collision prevention
 * - configurable expiration
 * - payload fingerprint fallback
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * SpEL expression for explicit idempotency key.
     *
     * Example:
     * "#job.jobId"
     * "#request.transactionId"
     */
    String key() default "";

    /**
     * Logical namespace to isolate key domains.
     *
     * Prevents collisions between unrelated operations.
     *
     * Example:
     * "payment"
     * "email"
     * "workflow"
     */
    String namespace() default "default";

    /**
     * Enables tenant-aware key partitioning.
     *
     * Prevents cross-tenant collisions.
     */
    boolean tenantScoped() default true;

    /**
     * Fallback to payload fingerprint hashing
     * when explicit key is missing.
     */
    boolean hashPayloadFallback() default true;

    /**
     * Expiration window in seconds.
     *
     * Prevents stale key retention while ensuring replay safety.
     */
    long ttlSeconds() default 86400;

    /**
     * Collision handling strategy.
     */
    CollisionStrategy collisionStrategy()
            default CollisionStrategy.REJECT_DUPLICATE;

    enum CollisionStrategy {

        /**
         * Reject duplicate execution immediately.
         */
        REJECT_DUPLICATE,

        /**
         * Allow replay if previous execution expired.
         */
        ALLOW_AFTER_EXPIRY,

        /**
         * Regenerate using payload fingerprint salt.
         */
        REGENERATE_ON_COLLISION
    }
}