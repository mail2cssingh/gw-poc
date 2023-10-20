package com.example.scgw.real;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@Component
public class ProxiesRoutePredicateFactory extends AbstractRoutePredicateFactory<ProxiesRoutePredicateFactory.Config> {
    private final ObjectMapper objectMapper;

    @Autowired
    public ProxiesRoutePredicateFactory(ObjectMapper objectMapper) {
        super(Config.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("proxies");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            AtomicBoolean allow = new AtomicBoolean(false);
            String requestMethod = exchange.getRequest().getMethod().name();
            String territory = exchange.getRequest().getHeaders().getFirst("x-skyott-territory");
            try {
                List<Proxy> proxies = objectMapper.readValue(config.getProxies(), new TypeReference<>() {
                });
                proxies
                    .stream()
                    .filter(proxy -> doesRequestPathMatchConfiguredProxyMappings(exchange, proxy))
                    .findFirst()
                    .ifPresentOrElse(
                        proxy ->
                                doesRequestMethodExistsInConfig(requestMethod, proxy)
                                .ifPresentOrElse(
                                        method -> {
                                            Optional<Proxy.Method.Restriction> restriction = doesRequestTerritoryExistsInConfig(territory, method);
                                            allow.set(restriction.isPresent());
                                            exchange.getAttributes().put("ALLOWED_ROLES_ATTRIBUTE",
                                                    restriction
                                                            .map(Proxy.Method.Restriction::getAllowedRoles)
                                                            .orElse(Collections.emptyList()));
                                                }
                                      , () -> allow.set(false))
                        ,
                        (() -> allow.set(false))
                    );
            } catch (JsonProcessingException exp) {
                throw new RuntimeException(String.format("Cannot parse proxy mapping Json due to:%s",
                    exp.getMessage()), exp);
            }
            return allow.get();
        };
    }

    @Validated
    public static class Config {
        private String proxies;

        public String getProxies() {
            return proxies;
        }

        public void setProxies(String proxies) {
            this.proxies = proxies;
        }
    }

    private Optional<Proxy.Method.Restriction> doesRequestTerritoryExistsInConfig(String territory, Proxy.Method method) {
        return method
                .getRestrictions()
                .stream()
                .filter(restriction -> restriction.getTerritories().contains(territory))
                .findFirst();
    }


    private Optional<Proxy.Method> doesRequestMethodExistsInConfig(String requestMethod, Proxy proxy) {
        return proxy
            .getMethods()
            .stream()
            .filter(method -> method.getMethod().equals(requestMethod))
            .findAny();
    }

    private boolean doesRequestPathMatchConfiguredProxyMappings(ServerWebExchange exchange, Proxy proxy) {
        PathRoutePredicateFactory.Config pathConfig =  new PathRoutePredicateFactory.Config();
        pathConfig.setPatterns(List.of(proxy.getPath()));
        PathRoutePredicateFactory pathRoutePredicateFactory = new PathRoutePredicateFactory();
        Predicate<ServerWebExchange> pathPredicate = pathRoutePredicateFactory.apply(pathConfig);
        return pathPredicate.test(exchange);
    }
}
