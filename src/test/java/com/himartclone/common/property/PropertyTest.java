package com.himartclone.common.property;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"local", "front"})
@Slf4j
public class PropertyTest {

    @Autowired
    ApplicationProperty appProp;

    @Autowired
    ApplicationStrategy appStrat;

    @Value("${file.dir}")
    private String fileDir;

    @Test
    void appPropTest() {
        log.info("fileDir={}", fileDir);
        log.info("appProp={}", appProp);
    }

    @Test
    void appStratTest() {
        log.info("mainDomainUrl={}", appStrat.getMainDomainUrl());
        log.info("resourceDomainUrl={}", appStrat.getResourceDomainUrl());
    }

    @Test
    void appPropUtilsTest() {
        log.info("AppPropUtils.mainDomainUrl={}", AppPropUtils.mainDomainUrl);
    }
}
