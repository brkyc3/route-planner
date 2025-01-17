package com.thy.transport.service;

import com.thy.transport.config.Constants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
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

    public void evictRouteCache(String originCode) {
        Optional.ofNullable(cacheManager.getCache(Constants.RedisCacheNames.ROUTES_BY_ORIGIN))
                .ifPresent(cache -> cache.evict(originCode));
    }

} 