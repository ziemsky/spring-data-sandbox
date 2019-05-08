package com.ziemsky.springdata;

import com.ziemsky.springdata.test.AopTest.entities.LevelOneEntity;
import com.ziemsky.springdata.test.AopTest.entities.LevelTwoEntity;
import com.ziemsky.springdata.test.AopTest.repos.LevelOneEntityRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestEntityManager
@SpringBootTest(
    classes = {
        AopTest.Config.class,
        AopConfig.class
    },
    properties = {
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming" +
            ".PhysicalNamingStrategyStandardImpl"
    }
)
class AopTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PlatformTransactionManager platformTransactionManager;



    @Autowired
    private TransactionalService transactionalService;

    @Autowired
    private LevelOneEntityRepo levelOneEntityRepo;

    private TransactionTemplate transactionTemplate;


    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Test
    @Sql(statements = {
        "delete from LevelTwoEntity",
        "delete from LevelOneEntity",

        "insert into LevelOneEntity   (id)                   values (1)",

        "insert into LevelTwoEntity   (id, levelOneEntityId) values (1, 1)",
        "insert into LevelTwoEntity   (id, levelOneEntityId) values (2, 1)",
    })
    void enablesHibernateFiltersForTransactionalAnnotatedMethodsThroughAspect() {

        executeWithinTransaction(() -> {
            final LevelOneEntity loadedLevelOneEntity = transactionalService.transactionalMethod(levelOneEntityRepo);

            assertThat(loadedLevelOneEntity.getLevelTwoEntities()).containsExactly(
                LevelTwoEntity.builder().id(2).build()
            );
        });
    }

    private void executeWithinTransaction(final Runnable runnable) {
        transactionTemplate.execute(status -> {

            runnable.run();

            return null;
        });
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories("com.ziemsky.springdata.test.AopTest.repos")
    @EntityScan("com.ziemsky.springdata.test.AopTest.entities")
    public static class Config {

    }
}