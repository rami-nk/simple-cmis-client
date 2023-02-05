package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "cmis.server")
public class CmisServerProperties {

    private String atompub;
    private String user;
    private String password;
}
