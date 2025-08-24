package com.himartclone.common.property;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("app.prop")
@ToString
public class ApplicationProperty {

    private String mainDomainUrl;

    private String resourceDomainUrl;

    private String profile;

    public ApplicationProperty(String mainDomainUrl, String resourceDomainUrl, String profile) {
        this.mainDomainUrl = mainDomainUrl;
        this.resourceDomainUrl = resourceDomainUrl;
        this.profile = profile;
    }
}
