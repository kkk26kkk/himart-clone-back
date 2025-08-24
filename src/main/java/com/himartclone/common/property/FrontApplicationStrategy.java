package com.himartclone.common.property;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("front")
@RequiredArgsConstructor
public class FrontApplicationStrategy implements ApplicationStrategy {

    private final ApplicationProperty appProp;

    @Override
    public String getMainDomainUrl() {
        return appProp.getMainDomainUrl();
    }

    @Override
    public String getResourceDomainUrl() {
        return appProp.getResourceDomainUrl();
    }
}
