package com.himartclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan("com.himartclone.common")
public class HimartCloneBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(HimartCloneBackApplication.class, args);
    }

}
