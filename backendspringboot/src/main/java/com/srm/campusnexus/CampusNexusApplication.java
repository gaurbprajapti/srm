package com.srm.campusnexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class CampusNexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusNexusApplication.class, args);
    }
} 