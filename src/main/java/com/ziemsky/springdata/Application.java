package com.ziemsky.springdata;

import com.ziemsky.springdata.jpa.DomainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
    }


    @Autowired
    @Qualifier("proxyRepositoryA")
    private DomainRepository specialisedRepositoryA;

    @Override public void run(final ApplicationArguments args) throws Exception {

        specialisedRepositoryA.save(new User("user A"));
        specialisedRepositoryA.save(new User("user B"));

        specialisedRepositoryA.flush();

        final Optional<User> user_a = specialisedRepositoryA.findById("user A");

        final Optional<User> user_b = specialisedRepositoryA.findThroughNativeQuery("user B");

        logUserFound(user_a, "user_a via findById");
        logUserFound(user_b, "user_b via findThroughNativeQuery");
    }

    private void logUserFound(final Optional<User> user, final String username) {
        if (user.isPresent()) {
            log.info("FOUND {}: {}", username, user);
        } else {
            log.error("NOT FOUND: {}", username);
        }
    }
}

