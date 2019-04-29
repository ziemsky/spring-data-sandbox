package com.ziemsky.springdata;

import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.entities.LevelOneEntity;
import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.entities.LevelTwoEntity;
import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.entities.LevelThreeEntity;
import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.repos.LevelOneEntityRepo;
import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.repos.LevelThreeEntityRepo;
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
        UpdatingLevelTwoReferenceToLevelThreeTest.Config.class
    },
    properties = {
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming" +
            ".PhysicalNamingStrategyStandardImpl"
    }
)
class UpdatingLevelTwoReferenceToLevelThreeTest {

    private Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private LevelOneEntityRepo levelOneEntityRepo;

    @Autowired
    private LevelThreeEntityRepo levelThreeEntityRepo;

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

    // CascadeType.MERGE required on LevelOneEntity.levelTwoEntities
    // for update to LevelTwoEntity.levelThreeEntity to get saved!

    @Test
    @Sql(statements = {
        "delete from LevelTwoEntity",
        "delete from LevelThreeEntity",
        "delete from LevelOneEntity",

        "insert into LevelThreeEntity (id)                 values (1)",
        "insert into LevelThreeEntity (id)                 values (2)",

        "insert into LevelOneEntity   (id)                 values (1)",
        "insert into LevelOneEntity   (id)                 values (2)",

        "insert into LevelTwoEntity   (id, parentEntityId, levelThreeEntityId) values (1, 1, 1)",
        "insert into LevelTwoEntity   (id, parentEntityId, levelThreeEntityId) values (2, 2, 2)",
    })
    void updatesSecondLevelWithReferenceToThirdLevel_whereSetCascadeMerge_andCreatedInMemory() {

        final LevelOneEntity levelOneEntity = LevelOneEntity.builder() // original (id) LevelOneEntity
            .id(1)
            .build();


        final LevelTwoEntity levelTwoEntity = LevelTwoEntity.builder() // original (id) LevelTwoEntity
            .id(1)
            .parentEntity(levelOneEntity)
            .levelThreeEntity(                    // ...but re-pointed to a different LevelThreeEntity
                LevelThreeEntity.builder()
                    .id(2)
                    .build()
            ).build();

        levelOneEntity.setLevelTwoEntities(newLinkedHashSet(
            levelTwoEntity
        ));


        levelOneEntityRepo.save(levelOneEntity);


        assertThat(actualLevelOneRecords()).isEqualTo(asList(
            LevelOneEntity.Dto.builder().id(1).build(),
            LevelOneEntity.Dto.builder().id(2).build()
        ));

        assertThat(actualLevelTwoRecords()).isEqualTo(asList(
            LevelTwoEntity.Dto.builder().id(1).parentEntityId(1).levelThreeEntityId(2).build(),
            LevelTwoEntity.Dto.builder().id(2).parentEntityId(2).levelThreeEntityId(2).build()
        ));

        assertThat(actualLevelThreeRecords()).isEqualTo(asList(
            LevelThreeEntity.Dto.builder().id(1).build(),
            LevelThreeEntity.Dto.builder().id(2).build()
        ));
    }

    @Test
    @Sql(statements = {
        "delete from LevelTwoEntity",
        "delete from LevelThreeEntity",
        "delete from LevelOneEntity",

        "insert into LevelThreeEntity (id)                 values (1)",
        "insert into LevelThreeEntity (id)                 values (2)",

        "insert into LevelOneEntity   (id)                 values (1)",
        "insert into LevelOneEntity   (id)                 values (2)",

        "insert into LevelTwoEntity   (id, parentEntityId, levelThreeEntityId) values (1, 1, 1)",
        "insert into LevelTwoEntity   (id, parentEntityId, levelThreeEntityId) values (2, 2, 2)",
    })
    void updatesSecondLevelWithReferenceToThirdLevel_whereSetCascadeMerge_andLoadedFromRepo() {

        {
            final LevelOneEntity levelOneEntity = LevelOneEntity.builder() // original (id) LevelOneEntity
                .id(1)
                .build();

            final LevelTwoEntity levelTwoEntity = LevelTwoEntity.builder() // original (id) LevelTwoEntity
                .id(1)
                .parentEntity(levelOneEntity)
                .levelThreeEntity(                    // ...but re-pointed to a different LevelThreeEntity
                    LevelThreeEntity.builder()
                        .id(2)
                        .build()
                ).build();

            levelOneEntity.setLevelTwoEntities(newLinkedHashSet(
                levelTwoEntity
            ));
        }

        executeWithinTransaction(() -> {
            final LevelOneEntity levelOneEntity = levelOneEntityRepo.findById(1).get();
            final LevelThreeEntity targetLevelThreeEntity = levelThreeEntityRepo.findById(2).get();


            levelOneEntity.getLevelTwoEntities().iterator().next().setLevelThreeEntity(targetLevelThreeEntity);

            levelOneEntityRepo.save(levelOneEntity);
        });


        assertThat(actualLevelOneRecords()).isEqualTo(asList(
            LevelOneEntity.Dto.builder().id(1).build(),
            LevelOneEntity.Dto.builder().id(2).build()
        ));

        assertThat(actualLevelTwoRecords()).isEqualTo(asList(
            LevelTwoEntity.Dto.builder().id(1).parentEntityId(1).levelThreeEntityId(2).build(),
            LevelTwoEntity.Dto.builder().id(2).parentEntityId(2).levelThreeEntityId(2).build()
        ));

        assertThat(actualLevelThreeRecords()).isEqualTo(asList(
            LevelThreeEntity.Dto.builder().id(1).build(),
            LevelThreeEntity.Dto.builder().id(2).build()
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

    private List<LevelThreeEntity.Dto> actualLevelThreeRecords() {
        return getAllRecords("LevelThreeEntity", LevelThreeEntity.Dto.class);
    }

    private void logRecords() {

        logRecords(actualLevelOneRecords(), LevelOneEntity.Dto.class);
        logRecords(actualLevelTwoRecords(), LevelTwoEntity.Dto.class);
        logRecords(actualLevelThreeRecords(), LevelThreeEntity.Dto.class);
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
    @EnableJpaRepositories("com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.repos")
    @EntityScan("com.ziemsky.springdata.jpa.UpdatingLevelTwoReferenceToLevelThreeTest.entities")
    public static class Config {

    }
}