package com.himartclone.common.property;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppPropUtils {

    private final ApplicationStrategy appStrat;

    public static String mainDomainUrl;

//    public static String resourceDomainUrl;

    @PostConstruct
    public void init() {
        mainDomainUrl = appStrat.getMainDomainUrl();
//        resourceDomainUrl = appStrat.getResourceDomainUrl();
    }
}
