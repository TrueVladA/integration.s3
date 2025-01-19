package ru.bpmcons.client.ecm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ecm")
public class EcmProperties {
    private String bearerAuth;
    private String pathToDisk;
}
