package com.saurav.ratelimitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
What to Cover in the Interview
1. Clarifying Questions (say these first)

Is this per-user, per-IP, or global rate limiting?
Should it be in-memory (single node) or distributed (Redis)?
What's the rate limit granularity — requests/second, requests/minute?
What happens on breach — hard reject (429) or queue?
Do we need burst handling?

Say: "Token Bucket is the most common for API rate limiting because it allows controlled bursts 
while enforcing an average rate — similar to what AWS API Gateway and Stripe use."


Request comes in → look up user's bucket → try to consume a token (locked) → if token exists, 
allow and decrement → else reject → background thread refills all buckets at fixed rate.


When giveAccess("alice") is called — we do computeIfAbsent to get or create Alice's bucket. 
Inside tryConsume(), we acquire the ReentrantLock, check tokens > 0, decrement if true, release the lock in finally. 
Meanwhile, a ScheduledExecutorService fires every 1 second and calls refill() on each bucket, capped at capacity. 
This separates the read path from the write path and avoids blocking


Follow-up questions to anticipate
"How would you scale this to distributed?" → 
Redis with INCRBY + TTL for fixed window; Lua scripts for atomic token bucket ops. 
Single-node lock becomes Redis distributed lock or just atomic Redis commands.

"Why ReentrantLock over synchronized?" → 
tryLock() for non-blocking attempts, fairness policy option, more explicit unlock in finally.

"What if the refill thread dies?" → 
Wrap the refill body in try-catch, log the error; the scheduler itself is resilient to thrown exceptions from the task.

 */
// ─────────────────────────────────────────
// Enums & Interface
// ─────────────────────────────────────────
enum RateLimiterType {
    TOKEN_BUCKET,
    FIXED_WINDOW,
    SLIDING_WINDOW,
    LEAKY_BUCKET
}

interface IRateLimiter {

    boolean giveAccess(String userId);

    void shutdown();
}

// ─────────────────────────────────────────
// Bucket (shared by Token Bucket strategy)
// ─────────────────────────────────────────
class Bucket {

    private int tokens;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();

    public Bucket(int capacity) {
        this.capacity = capacity;
        this.tokens = capacity; // start full
    }

    /**
     * Try to consume one token. Thread-safe. Returns true if allowed, false if
     * rate limited.
     */
    public boolean tryConsume() {
        lock.lock();
        try {
            if (tokens > 0) {
                tokens--;
                System.out.printf("    [Bucket] Token consumed. Remaining: %d/%d%n", tokens, capacity);
                return true;
            }
            System.out.printf("    [Bucket] No tokens available. Bucket empty (0/%d). REJECTED.%n", capacity);
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Refill bucket by `refillAmount` tokens, capped at capacity. Called by the
     * background refill scheduler.
     */
    public void refill(int refillAmount) {
        lock.lock();
        try {
            int before = tokens;
            tokens = Math.min(capacity, tokens + refillAmount);
            System.out.printf("    [Refill] %d → %d tokens (capacity: %d)%n", before, tokens, capacity);
        } finally {
            lock.unlock();
        }
    }

    public int getTokens() {
        lock.lock();
        try {
            return tokens;
        } finally {
            lock.unlock();
        }
    }
}

// ─────────────────────────────────────────
// Token Bucket Strategy
// ─────────────────────────────────────────
class TokenBucketRateLimiter implements IRateLimiter {

    private final int bucketCapacity;   // max tokens per user
    private final int refillAmount;     // tokens added per interval
    private final long refillIntervalMs;

    // One bucket per user — lazily created
    private final ConcurrentHashMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * @param bucketCapacity Max burst size (e.g. 5 tokens)
     * @param refillAmount Tokens added per interval (e.g. 2)
     * @param refillIntervalMs How often to refill in ms (e.g. 1000 = every 1s)
     */
    public TokenBucketRateLimiter(int bucketCapacity, int refillAmount, long refillIntervalMs) {
        this.bucketCapacity = bucketCapacity;
        this.refillAmount = refillAmount;
        this.refillIntervalMs = refillIntervalMs;

        // Background thread: refill all user buckets at fixed rate
        scheduler.scheduleAtFixedRate(
                this::refillAllBuckets,
                refillIntervalMs,
                refillIntervalMs,
                TimeUnit.MILLISECONDS
        );

        System.out.printf("[TokenBucketRateLimiter] Init: capacity=%d, refill=%d tokens every %dms%n%n",
                bucketCapacity, refillAmount, refillIntervalMs);
    }

    @Override
    public boolean giveAccess(String userId) {
        // Lazily create bucket for new users
        Bucket bucket = userBuckets.computeIfAbsent(userId, id -> new Bucket(bucketCapacity));
        return bucket.tryConsume();
    }

    private void refillAllBuckets() {
        System.out.println("\n  ── [Scheduler] Refilling all buckets ──");
        for (Map.Entry<String, Bucket> entry : userBuckets.entrySet()) {
            System.out.printf("  User '%s': ", entry.getKey());
            entry.getValue().refill(refillAmount);
        }
    }

    @Override
    public void shutdown() {
        scheduler.shutdownNow();
    }
}

// ─────────────────────────────────────────
// Fixed Window Counter Strategy
// (bonus — to show you know multiple algos)
// ─────────────────────────────────────────
class FixedWindowRateLimiter implements IRateLimiter {

    private final int maxRequests;
    private final long windowMs;

    private final ConcurrentHashMap<String, int[]> counters = new ConcurrentHashMap<>();
    // int[0] = count, int[1] = window start timestamp
    private final ReentrantLock lock = new ReentrantLock();

    public FixedWindowRateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
        System.out.printf("[FixedWindowRateLimiter] Init: max=%d req per %dms%n%n", maxRequests, windowMs);
    }

    @Override
    public boolean giveAccess(String userId) {
        long now = System.currentTimeMillis();
        lock.lock();
        try {
            int[] state = counters.computeIfAbsent(userId, id -> new int[]{0, (int) (now / windowMs)});
            long currentWindow = now / windowMs;

            if (state[1] != currentWindow) {
                // New window — reset counter
                state[0] = 0;
                state[1] = (int) currentWindow;
            }

            if (state[0] < maxRequests) {
                state[0]++;
                System.out.printf("    [FixedWindow] User '%s': %d/%d in window. ALLOWED.%n", userId, state[0], maxRequests);
                return true;
            }
            System.out.printf("    [FixedWindow] User '%s': limit %d reached. REJECTED.%n", userId, maxRequests);
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
    }
}

// ─────────────────────────────────────────
// Factory (clean extensibility point)
// ─────────────────────────────────────────
class RateLimiterFactory {

    public static IRateLimiter create(RateLimiterType type) {
        return switch (type) {
            case TOKEN_BUCKET ->
                new TokenBucketRateLimiter(5, 2, 1000); // cap=5, refill 2/sec
            case FIXED_WINDOW ->
                new FixedWindowRateLimiter(3, 2000);     // 3 req per 2s
            default ->
                throw new UnsupportedOperationException("Algorithm not yet implemented: " + type);
        };
    }
}

// ─────────────────────────────────────────
// Main — Simulation
// ─────────────────────────────────────────
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  Rate Limiter — LLD Demo");
        System.out.println("═══════════════════════════════════════════\n");

        // ── Demo 1: Token Bucket ──
        System.out.println("▶ DEMO 1: Token Bucket (capacity=5, refill=2 tokens/sec)");
        System.out.println("──────────────────────────────────────────\n");

        IRateLimiter tokenLimiter = RateLimiterFactory.create(RateLimiterType.TOKEN_BUCKET);

        // Simulate burst: 7 rapid requests from user "alice" (bucket holds 5 → last 2 rejected)
        System.out.println("Sending 7 rapid requests for user 'alice' (expect first 5 allowed, last 2 rejected):");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = tokenLimiter.giveAccess("alice");
            System.out.printf("  Request #%d → %s%n", i, allowed ? "✅ ALLOWED" : "❌ REJECTED");
        }

        // Wait for refill
        System.out.println("\nWaiting 1.2s for token refill...");
        Thread.sleep(1200);

        // After refill: 2 new tokens added → 2 more requests should pass
        System.out.println("\nAfter refill, sending 3 more requests for 'alice' (expect 2 allowed, 1 rejected):");
        for (int i = 1; i <= 3; i++) {
            boolean allowed = tokenLimiter.giveAccess("alice");
            System.out.printf("  Request #%d → %s%n", i, allowed ? "✅ ALLOWED" : "❌ REJECTED");
        }

        // Second user — independent bucket
        System.out.println("\nNew user 'bob' sending 3 requests (fresh bucket, all should pass):");
        for (int i = 1; i <= 3; i++) {
            boolean allowed = tokenLimiter.giveAccess("bob");
            System.out.printf("  Request #%d → %s%n", i, allowed ? "✅ ALLOWED" : "❌ REJECTED");
        }

        tokenLimiter.shutdown();

        // ── Demo 2: Fixed Window ──
        System.out.println("\n\n▶ DEMO 2: Fixed Window (max=3 requests per 2s window)");
        System.out.println("──────────────────────────────────────────\n");

        IRateLimiter fixedLimiter = RateLimiterFactory.create(RateLimiterType.FIXED_WINDOW);

        System.out.println("Sending 5 requests for user 'charlie' (expect 3 allowed, 2 rejected):");
        for (int i = 1; i <= 5; i++) {
            boolean allowed = fixedLimiter.giveAccess("charlie");
            System.out.printf("  Request #%d → %s%n", i, allowed ? "✅ ALLOWED" : "❌ REJECTED");
        }

        System.out.println("\nWaiting 2.1s for new window...");
        Thread.sleep(2100);

        System.out.println("\nNew window — sending 2 requests (both should pass):");
        for (int i = 1; i <= 2; i++) {
            boolean allowed = fixedLimiter.giveAccess("charlie");
            System.out.printf("  Request #%d → %s%n", i, allowed ? "✅ ALLOWED" : "❌ REJECTED");
        }

        fixedLimiter.shutdown();

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  Demo complete.");
        System.out.println("═══════════════════════════════════════════");
    }
}
