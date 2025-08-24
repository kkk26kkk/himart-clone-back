package com.himartclone.common.property;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("frontMobile")
@RequiredArgsConstructor
public class FrontMobileApplicationStrategy implements ApplicationStrategy {

    private final ApplicationProperty appProp;

    @Override
    public String getMainDomainUrl() {
        return "m" + appProp.getMainDomainUrl();
    }

    @Override
    public String getResourceDomainUrl() {
        return "m" + appProp.getResourceDomainUrl();
    }
}
