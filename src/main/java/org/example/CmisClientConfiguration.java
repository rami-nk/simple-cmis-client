package org.example;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CmisServerProperties.class)
@AllArgsConstructor
public class CmisClientConfiguration {

    private final CmisServerProperties properties;

    @Bean
    CmisClient cmisClient() {
        return new CmisClientImpl(properties);
    }
}
