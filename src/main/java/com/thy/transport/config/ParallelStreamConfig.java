package com.thy.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class ParallelStreamConfig {

    @Bean
    public ForkJoinPool customForkJoinPool() {
        return new ForkJoinPool(100);
    }
}