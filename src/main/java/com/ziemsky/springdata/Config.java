package com.ziemsky.springdata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.ziemsky.springdata.jpa")
public class Config {

    @Bean
    public String bean() {
        System.out.println("RUNNING");
        return "";
    }



}
