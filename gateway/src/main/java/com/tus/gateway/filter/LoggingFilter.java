package com.tus.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    logger.info("Request {}: {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI().getPath());
    logger.debug("Headers {}", exchange.getRequest().getHeaders());

    return chain.filter(exchange)
        .then(Mono.fromRunnable(() -> {
          logger.info("Response {}: {}", exchange.getResponse().getStatusCode().toString(), exchange.getRequest().getURI().getPath());
          logger.debug("Headers {}", exchange.getResponse().getHeaders());
        }));
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
