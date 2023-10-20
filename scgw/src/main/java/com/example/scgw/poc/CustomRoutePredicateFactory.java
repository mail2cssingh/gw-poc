package com.example.scgw.poc;

import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

public class CustomRoutePredicateFactory extends
        AbstractRoutePredicateFactory<CustomRoutePredicateFactory.Config> {

    public CustomRoutePredicateFactory(Class<Config> configClass) {
        super(configClass);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (ServerWebExchange t) -> {
            String path = t.getRequest().getPath().value();
            String provider = t.getRequest().getHeaders().get("X-skyott-provider") != null ? t.getRequest().getHeaders().get("X-skyott-provider").get(0) : null;
            if (config.getPath().equals(path) && (Strings.isBlank(config.getProvider()) || config.getProvider().equals(provider))) {
                return true;
            } else {
                return false;
            }
        };
    }

    @Validated
    public static class Config {
        String path;
        String provider;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}