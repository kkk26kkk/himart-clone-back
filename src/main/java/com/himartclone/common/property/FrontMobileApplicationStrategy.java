package com.himartclone.common.property;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;

@Component
@Profile("frontMobile")
public class FrontMobileApplicationStrategy implements ApplicationStrategy {

    private final String symbol;
    private final String mainDomainUrl;
    private final String[] resourceDomainUrl;
    private final String cookieSessionName;

    public FrontMobileApplicationStrategy(ApplicationProperty appProp) {
        this.symbol = "M";
        this.mainDomainUrl = "m" + appProp.getMainDomainUrl();
        this.resourceDomainUrl = Arrays.stream(appProp.getResourceDomainUrl())
                .map(url -> "m" + url).toArray(String[]::new);
        this.cookieSessionName = "__cs" + symbol + appProp.getStage().charAt(0);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getMainDomainUrl() {
        return mainDomainUrl;
    }

    @Override
    public String getResourceDomainUrl() {
        return resourceDomainUrl[new Random().nextInt(2)];
    }

    @Override
    public String getCookieSessionName() {
        return cookieSessionName;
    }
}
