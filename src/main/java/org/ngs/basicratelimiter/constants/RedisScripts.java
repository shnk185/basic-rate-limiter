package org.ngs.basicratelimiter.constants;

public class RedisScripts {
    public static final String RATE_LIMIT_SCRIPT = """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refill_rate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            
            local tokens = redis.call('HGET', key, 'tokens')
            local last_refill = redis.call('HGET', key, 'last_refill')
            
            if not tokens then
                tokens = capacity
                last_refill = now
            else
                tokens = tonumber(tokens)
                last_refill = tonumber(last_refill)
            end
            
            local delta = now - last_refill
            if delta > 0 then
                tokens = math.min(capacity, tokens + delta * refill_rate)
                last_refill = now
            end
            
            if tokens >= 1 then
                tokens = tokens - 1
                redis.call('HSET', key, 'tokens', tokens, 'last_refill', last_refill)
                redis.call('EXPIRE', key, math.ceil(2 * capacity / refill_rate) + 10)
                return 1
            else
                redis.call('HSET', key, 'tokens', tokens, 'last_refill', last_refill)
                redis.call('EXPIRE', key, math.ceil(2 * capacity / refill_rate) + 10)
                return 0
            end
            """;
}
