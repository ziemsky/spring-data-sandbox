package com.ziemsky.springdata;

import com.ziemsky.springdata.aop.HibernateFiltersActivatingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableAspectJAutoProxy
public class AopConfig {

    @Bean
    public HibernateFiltersActivatingAspect hibernateFiltersActivatingAspect() {
        return new HibernateFiltersActivatingAspect();
    }

    @Bean
    public TransactionalService transactionalService() {
        return new TransactionalService();
    }
}
