package com.ziemsky.springdata;

import com.ziemsky.springdata.jpa.DomainRepository;
import com.ziemsky.springdata.jpa.SpecialisedRepositoryA;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.reflect.Proxy;

@Configuration
@EnableJpaRepositories("com.ziemsky.springdata.jpa")
public class Config {

    private Logger log = LoggerFactory.getLogger(Config.class);

    // @Bean
    // public DomainRepository repositoryA(SpecialisedRepositoryA specialisedRepositoryA) {
    //
    //     return specialisedRepositoryA;
    // }

    @Bean
    public DomainRepository proxyRepositoryA(SpecialisedRepositoryA specialisedRepositoryA) {

        return (DomainRepository) Proxy.newProxyInstance(
            Config.class.getClassLoader(),

            new Class[]{SpecialisedRepositoryA.class},

            (proxy, method, args) -> {
                log.info("INVOKING through proxy: {}", method.getName());

                return specialisedRepositoryA
                    .getClass()
                    .getDeclaredMethod(method.getName(), method.getParameterTypes())
                    .invoke(specialisedRepositoryA, args);
            });
    }
}
