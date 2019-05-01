package com.ziemsky.springdata;

import com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.entities.LevelOneEntity;
import com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.entities.LevelTwoEntity;
import com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.repos.LevelOneEntityRepo;
import org.hibernate.collection.internal.AbstractPersistentCollection;
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
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {
        LazyLoadingUniDirectionalMappedCollectionTest.Config.class
    },
    properties = {
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming" +
            ".PhysicalNamingStrategyStandardImpl"
    }
)
class LazyLoadingUniDirectionalMappedCollectionTest {

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


    @Test
    @Sql(statements = {
        "delete from LevelTwoEntity",
        "delete from LevelOneEntity",

        "insert into LevelOneEntity   (id)                   values (1)",

        "insert into LevelTwoEntity   (id, levelOneEntityId) values (1, 1)",
        "insert into LevelTwoEntity   (id, levelOneEntityId) values (2, 1)",
    })
    void doesNotLoadOneToManyCollectionPropertyEagerly_followingDefaultSettings() {


        final AtomicReference<LevelOneEntity> loadedLevelOneEntity = new AtomicReference<>();

        executeWithinTransaction(() -> loadedLevelOneEntity.set(
            levelOneEntityRepo.findById(1).get()
        ));

        // Using reflection to extract value to ensure that loadedLevelOneEntity.getLevelTwoEntities() of lazy loaded property never
        // gets called directly so as to not to trigger initialisation of the collection
        final Object actualLevelTwoEntitiesValue;
        try {
            final Field field = LevelOneEntity.class.getDeclaredField("levelTwoEntities");
            field.setAccessible(true);

            actualLevelTwoEntitiesValue = field.get(loadedLevelOneEntity.get());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        assertThat(actualLevelTwoEntitiesValue).isInstanceOf(AbstractPersistentCollection.class);

        final AbstractPersistentCollection actualValue = (AbstractPersistentCollection) actualLevelTwoEntitiesValue;

        assertThat(actualValue.wasInitialized()).isFalse();
    }

    // final Mapper mapper = DozerBeanMapperBuilder.create()
    //     .withCustomFieldMapper((source, destination, sourceFieldValue, classMap, fieldMapping) -> {
    //         // Check if field is a Hibernate collection proxy
    //         if (!(sourceFieldValue instanceof AbstractPersistentCollection)) {
    //             // Allow dozer to map as normal
    //             return false;
    //         }
    //
    //         // Check if field is already initialized. Return false if we want dozer to map it
    //         return !((AbstractPersistentCollection) sourceFieldValue).wasInitialized();
    //     })
    //     .build();


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
    @EnableJpaRepositories("com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.repos")
    @EntityScan("com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.entities")
    public static class Config {

    }
}