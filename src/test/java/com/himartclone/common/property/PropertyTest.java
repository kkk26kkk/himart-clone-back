package com.himartclone.common.property;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"staging", "frontMobile"})
@Slf4j
public class PropertyTest {

    @Autowired
    ApplicationProperty appProp;

    @Autowired
    ApplicationStrategy applicationStrategy;

    @Value("${file.dir:/data/home}")
    private String fileDir;

    @Test
    void appPropTest() {
        log.info("fileDir={}", fileDir);
        log.info("appProp={}", appProp);
    }

    @Test
    void applicationStrategyTest() {
        log.info("symbol={}, mainDomainUrl={}, resourceDomainUrl={}, cookieSessionName={}",
                applicationStrategy.getSymbol(), applicationStrategy.getMainDomainUrl(),
                applicationStrategy.getResourceDomainUrl(), applicationStrategy.getCookieSessionName());
    }

    @Test
    void appPropUtilsTest() {
        log.info("AppPropUtils.mainDomainUrl={}", AppPropUtils.mainDomainUrl);
    }
}
