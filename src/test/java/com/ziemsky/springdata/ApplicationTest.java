package com.ziemsky.springdata;

import com.ziemsky.springdata.jpa.entities.EntityWithGeneratedId;
import com.ziemsky.springdata.jpa.DomainRepository;
import com.ziemsky.springdata.jpa.SpecialisedRepositoryD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestEntityManager
@SpringBootTest(
    classes = {
        Application.class,
        Config.class
    }
)
class ApplicationTest {

    private Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    @Qualifier("proxyRepositoryA")
    private DomainRepository domainRepository;

    @Autowired
    private SpecialisedRepositoryD specialisedRepositoryD;

    @Autowired
    private DataSource dataSource;


    JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void testSpecialisedRepo_withDomainRepoInterface_throughProxy() {

        // Search for logging statements with 'INVOKING through proxy
        // for indication that methods were called through proxy:

        domainRepository.save(new User("user A"));
        domainRepository.save(new User("user B"));

        domainRepository.flush();

        final Optional<User> user_a = domainRepository.findById("user A");

        final Optional<User> user_b = domainRepository.findThroughNativeQuery("user B");

        logUserFound(user_a, "user_a via findById");
        logUserFound(user_b, "user_b via findThroughNativeQuery");
    }

    @Test
    void autoIncrementIdOverride() {

        final EntityWithGeneratedId firstRecord = new EntityWithGeneratedId();
        firstRecord.setTextProp("aaa");

        specialisedRepositoryD.saveAndFlush(firstRecord);




        final EntityWithGeneratedId secondRecord = new EntityWithGeneratedId(10, "bbb");

        specialisedRepositoryD.saveAndFlush(secondRecord);

        log.info("------------------------------------------------------");
        log.info("COUNT: {}", JdbcTestUtils.countRowsInTable(jdbcTemplate, "entity_with_generated_id"));

        final List<TestDto> foundObjects = jdbcTemplate.query(
            "SELECT * FROM entity_with_generated_id", new BeanPropertyRowMapper<>(TestDto.class)
        ).stream()
            .collect(toList());

        log.info("COUNT: {}", foundObjects.size());
        foundObjects.forEach(testDto -> log.info("FOUND: {}", testDto));

        assertThat(foundObjects).isEqualTo(asList(
            new TestDto(1, "aaa"),
            new TestDto(10, "bbb")
        ));


    }


    private void logUserFound(final Optional<User> user, final String username) {
        if (user.isPresent()) {
            log.info("FOUND {}: {}", username, user);
        } else {
            log.error("NOT FOUND: {}", username);
        }
    }
}