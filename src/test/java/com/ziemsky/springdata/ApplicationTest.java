package com.ziemsky.springdata;

import com.ziemsky.springdata.jpa.SpecialisedRepositoryA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
@ContextConfiguration(classes = {Config.class})
@AutoConfigureTestEntityManager
@Transactional
class ApplicationTest {

    @Autowired
    private SpecialisedRepositoryA specialisedRepositoryA;

    @Test
    void runApp() {
        specialisedRepositoryA.save(new User("user a"));

        specialisedRepositoryA.flush();


        Optional<User> user_a = specialisedRepositoryA.findById("user a");

        user_a.ifPresent(
            user -> System.out.println("FOUND users: " + user)
        );

        user_a.orElseThrow(RuntimeException::new);
    }

    // @Configuration
    // static class TestConfig {
    //
    //
    //     @Bean
    //     public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
    //         return new LocalEntityManagerFactoryBean();
    //     }
    // }
}