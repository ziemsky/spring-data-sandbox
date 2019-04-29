package com.ziemsky.springdata;

import com.ziemsky.springdata.test.FirstLevelOneToManyRelationshipsTest.entities.LevelOneEntity;
import com.ziemsky.springdata.test.FirstLevelOneToManyRelationshipsTest.entities.LevelTwoEntity;
import com.ziemsky.springdata.test.FirstLevelOneToManyRelationshipsTest.repos.LevelOneEntityRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.newLinkedHashSet;

@SpringBootTest(
    classes = {
        AddingReplacingLevelTwoCollectionRecordsTest.Config.class
    },
    properties = {
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming" +
            ".PhysicalNamingStrategyStandardImpl"
    }
)
class AddingReplacingLevelTwoCollectionRecordsTest {

    private Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private LevelOneEntityRepo levelOneEntityRepo;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);

        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @AfterEach
    void tearDown() {
        logRecords();
    }

    // CascadeType.ALL with orphanRemoval required for target entries to get REPLACED with new or existing ones

    @Test
    @Sql(statements = {
        "delete from LevelTwoEntity",
        "delete from LevelOneEntity",

        "insert into LevelOneEntity   (id)                 values (1)",

        "insert into LevelTwoEntity   (id, parentEntityId) values (1, 1)",
        "insert into LevelTwoEntity   (id, parentEntityId) values (2, 1)",
    })
    void replacesSecondLevelRecordsWithNewOne_whereSetCascadeAll_andCreatedInMemory() {

        final LevelOneEntity levelOneEntity = LevelOneEntity.builder()
            .id(1)
            .build();


        final LevelTwoEntity levelTwoEntityExisting = LevelTwoEntity.builder()
            .id(2)
            .parentEntity(levelOneEntity)
            .build();

        final LevelTwoEntity levelTwoEntityNew = LevelTwoEntity.builder()
            .id(3)
            .parentEntity(levelOneEntity)
            .build();

        levelOneEntity.setLevelTwoEntities(newLinkedHashSet(
            levelTwoEntityExisting,
            levelTwoEntityNew
        ));


        levelOneEntityRepo.save(levelOneEntity);


        assertThat(actualLevelOneRecords()).isEqualTo(asList(
            LevelOneEntity.Dto.builder().id(1).build()
        ));

        assertThat(actualLevelTwoRecords()).isEqualTo(asList(
            LevelTwoEntity.Dto.builder().id(2).parentEntityId(1).build(),
            LevelTwoEntity.Dto.builder().id(3).parentEntityId(1).build()
        ));
    }

    private void executeWithinTransaction(final Runnable runnable) {
        transactionTemplate.execute(status -> {

            runnable.run();

            return null;
        });
    }

    private List<LevelOneEntity.Dto> actualLevelOneRecords() {
        return getAllRecords("LevelOneEntity", LevelOneEntity.Dto.class);
    }

    private List<LevelTwoEntity.Dto> actualLevelTwoRecords() {
        return getAllRecords("LevelTwoEntity", LevelTwoEntity.Dto.class);
    }



    private void logRecords() {
        logRecords(actualLevelOneRecords(), LevelOneEntity.Dto.class);
        logRecords(actualLevelTwoRecords(), LevelTwoEntity.Dto.class);
    }

    private <T> void logRecords(final List<T> entities, final Class<T> dtoClass) {
        log.debug("FOUND RECORDS {} x {}{}", entities.size(), dtoClass.getSimpleName(), entities.isEmpty() ? "" : ":");
        entities.forEach(entity -> log.debug("{}", entity));
    }

    private <T> List<T> getAllRecords(final String tableName, final Class<T> dtoClass) {
        return jdbcTemplate.query(
            "SELECT * FROM " + tableName, new BeanPropertyRowMapper<>(dtoClass)
        ).stream().collect(toList());
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories("com.ziemsky.springdata.test.FirstLevelOneToManyRelationshipsTest.repos")
    @EntityScan("com.ziemsky.springdata.jpa.AddingReplacingLevelTwoCollectionRecordsTest.entities")
    public static class Config {

    }
}