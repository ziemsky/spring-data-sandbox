package com.ziemsky.springdata;

import com.ziemsky.springdata.jpa.SpecialisedRepositoryA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class Application implements ApplicationRunner {

    Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
    }


    @Autowired
    private SpecialisedRepositoryA specialisedRepositoryA;

    @Override public void run(final ApplicationArguments args) throws Exception {

        specialisedRepositoryA.save(new User("user a"));

        specialisedRepositoryA.flush();


        Optional<User> user_a = specialisedRepositoryA.findById("user a");

        user_a.ifPresent(
            user -> log.info("FOUND users: {}", user)
        );

        user_a.orElseThrow(() -> new RuntimeException("NOTHING FOUND"));
    }
}
