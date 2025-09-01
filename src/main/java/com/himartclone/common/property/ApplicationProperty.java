package com.himartclone.common.property;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("app.prop")
@ToString
public class ApplicationProperty {

    private final String mainDomainUrl;
    private final String[] resourceDomainUrl;
    private final String stage;

    public ApplicationProperty(String mainDomainUrl, String[] resourceDomainUrl, String stage) {
        this.mainDomainUrl = mainDomainUrl;
        this.resourceDomainUrl = resourceDomainUrl;
        this.stage = stage;
    }
}
