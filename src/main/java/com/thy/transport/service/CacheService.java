package com.thy.transport.service;

import com.thy.transport.config.Constants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private final CacheManager cacheManager;

    @PostConstruct
    public void init() {
        evictAllCaches();
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    public void evictTransportaionCache(String originCode) {
        Optional.ofNullable(cacheManager.getCache(Constants.RedisCacheNames.TRANSPORTATION_BY_ORIGIN))
                .ifPresent(cache -> cache.evict(originCode));
    }


    public void logCacheStats() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            RedisCache cache = (RedisCache)cacheManager.getCache(cacheName);
            if (cache != null) {
                log.info("Cache [{}] gets: {} , hits : {}, miss : {}",
                        cacheName, cache.getStatistics().getGets(),
                        cache.getStatistics().getHits(),
                        cache.getStatistics().getMisses()
                );
            }
        });


    }

} 