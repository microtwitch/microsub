package de.com.fdm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "eventsub")
public record EventsubProps(
        String clientId,
        String clientSecret,
        String secret,
        String url
) {}
