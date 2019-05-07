package com.ziemsky.springdata;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.HashSet;
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

        executeWithinTransaction(() -> {

            final LevelOneEntity loadedLevelOneEntity = levelOneEntityRepo.findById(1).get();

            // Using reflection to extract value to ensure that loadedLevelOneEntity.getLevelTwoEntities() of lazy
            // loaded
            // property never gets called directly so as to not to trigger initialisation of the collection.
            // Not using  ReflectionTestUtils.getField as it calls toString which invokes getter.
            final Object actualLevelTwoEntitiesValue = getFieldValue(loadedLevelOneEntity, "levelTwoEntities");


            assertThat(actualLevelTwoEntitiesValue).isInstanceOf(AbstractPersistentCollection.class);

            final AbstractPersistentCollection actualValue = (AbstractPersistentCollection) actualLevelTwoEntitiesValue;

            assertThat(actualValue.wasInitialized()).isFalse();
        });
    }


    @Test
    @Sql(statements = {
        "delete from LevelTwoEntity",
        "delete from LevelOneEntity",

        "insert into LevelOneEntity   (id)                   values (1)",

        "insert into LevelTwoEntity   (id, levelOneEntityId) values (1, 1)",
        "insert into LevelTwoEntity   (id, levelOneEntityId) values (2, 1)",
    })
    void dozerMapperDoesNotLoadOneToManyCollection_whenCollectionPropertyConfiguredAsAccessibleField() {

        final Mapper mapper = DozerBeanMapperBuilder
            .create()
            .withMappingBuilders(
                new BeanMappingBuilder() {
                    @Override protected void configure() {
                        mapping(
                            LevelOneEntity.class,
                            LevelOneEntity.class
                        )
                            .fields(
                                field("levelTwoEntities").accessible(),
                                field("levelTwoEntities").accessible()
                            );

                    }
                }
            )
            .build();

        executeWithinTransaction(() -> {

            final LevelOneEntity loadedLevelOneEntity = levelOneEntityRepo.findById(1).get();


            // Despite not calling the getter, Dozer Mapper somehow manages to initialise the collection!
            // Moving this call to after loadedActualValue assertion makes the assertion pass.
            final LevelOneEntity mappedLevelOneEntity = null;
            // final LevelOneEntity mappedLevelOneEntity = mapper.map(loadedLevelOneEntity, LevelOneEntity.class);





            Object loadedPropertyValue = getFieldValue(loadedLevelOneEntity, "levelTwoEntities");

            assertThat(loadedPropertyValue).isInstanceOf(AbstractPersistentCollection.class);

            final AbstractPersistentCollection loadedActualValue = (AbstractPersistentCollection) loadedPropertyValue;

            assertThat(loadedActualValue.wasInitialized()).isFalse();






            Object actualMappedPropertyValue = getFieldValue(mappedLevelOneEntity, "levelTwoEntities");

            assertThat(actualMappedPropertyValue).isInstanceOf(HashSet.class);

            final HashSet mappedActualValue = (HashSet) actualMappedPropertyValue;

            assertThat(mappedActualValue.size()).isEqualTo(0);
        });
    }

    private Object getFieldValue(final LevelOneEntity loadedLevelOneEntity, final String fieldName) {
        final Object actualLevelTwoEntitiesValue;
        try {
            final Field field = LevelOneEntity.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            actualLevelTwoEntitiesValue = field.get(loadedLevelOneEntity);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return actualLevelTwoEntitiesValue;
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