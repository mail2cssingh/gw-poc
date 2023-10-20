package com.example.scgw.poc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomPredicatesConfig {
    @Bean
    public CustomRoutePredicateFactory custom() {
        return new CustomRoutePredicateFactory(CustomRoutePredicateFactory.Config.class);
    }
}
