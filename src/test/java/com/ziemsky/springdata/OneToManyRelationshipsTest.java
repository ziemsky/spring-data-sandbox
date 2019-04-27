package com.ziemsky.springdata;

import com.ziemsky.springdata.jpa.entities.relationships.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.newLinkedHashSet;

@AutoConfigureTestEntityManager
@SpringBootTest(
    classes = {
        Application.class,
        Config.class
    }
)
class OneToManyRelationshipsTest {

    private Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private OneToManyParentEntityRepository oneToManyParentEntityRepository;

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

    // both orphanRemoval AND CascadeType.PERSIST are required for removal of item from collection to propagate

    @Test
    @Sql(statements = {
        "delete from one_to_many_retained_entity",
        "delete from one_to_many_removed_entity",
        "delete from one_to_many_parent_entity",

        "insert into one_to_many_parent_entity (id) values (1)",

        "insert into one_to_many_removed_entity (id, name, parent_entity_id) values (11, 'levelOneEntity1', 1)",
        "insert into one_to_many_removed_entity (id, name, parent_entity_id) values (12, 'levelOneEntity2', 1)",
        "insert into one_to_many_removed_entity (id, name, parent_entity_id) values (13, 'levelOneEntity3', 1)",
    })
    void deletesRelated_whenElementRemovedFromRootProperty_whereSetToRemoveOrphans_andLoadedFromRepo() {

        executeWithinTransaction(() -> {

            final OneToManyParentEntity parentEntity = oneToManyParentEntityRepository.findById(1).get();

            // we've loaded parentEntity with complete collection - now find and remove one element from the collection
            final OneToManyRemovedEntity entityToRemove = parentEntity.getLevelOneRemovedOrphanEntities().stream()
                .filter(levelOneEntity -> levelOneEntity.getId().equals(12)).findFirst().get();

            parentEntity.getLevelOneRemovedOrphanEntities().remove(
                entityToRemove
            );

            oneToManyParentEntityRepository.save(parentEntity);
        });

        assertThat(actualRemovedRecords()).isEqualTo(asList(
            LevelOneDto.builder().id(11).name("levelOneEntity1").parentEntityId(1).build(),
            LevelOneDto.builder().id(13).name("levelOneEntity3").parentEntityId(1).build()
        ));
    }

    @Test
    @Sql(statements = {
        "delete from one_to_many_retained_entity",
        "delete from one_to_many_removed_entity",
        "delete from one_to_many_parent_entity",

        "insert into one_to_many_parent_entity (id) values (1)",

        "insert into one_to_many_removed_entity (id, name, parent_entity_id) values (11, 'levelOneEntity1', 1)",
        "insert into one_to_many_removed_entity (id, name, parent_entity_id) values (12, 'levelOneEntity2', 1)",
        "insert into one_to_many_removed_entity (id, name, parent_entity_id) values (13, 'levelOneEntity3', 1)",
    })
    void deletesRelated_whenRootPropertyNulled_whereSetToRemoveOrphans_andCreatedInMemory() {

        executeWithinTransaction(() -> {

            final OneToManyParentEntity parentEntity = OneToManyParentEntity.builder().id(1).build();

            parentEntity.setLevelOneRemovedOrphanEntities(newLinkedHashSet(
                OneToManyRemovedEntity.builder().id(11).name("levelOneEntity1").parentEntity(parentEntity).build(),
                //                                    levelOneEntity2 'removed' by not adding
                OneToManyRemovedEntity.builder().id(13).name("levelOneEntity3").parentEntity(parentEntity).build()
            ));

            oneToManyParentEntityRepository.save(parentEntity);
        });

        assertThat(actualRemovedRecords()).isEqualTo(asList(
            LevelOneDto.builder().id(11).name("levelOneEntity1").parentEntityId(1).build(),
            LevelOneDto.builder().id(13).name("levelOneEntity3").parentEntityId(1).build()
        ));
    }

    @Test
    @Sql(statements = {
        "delete from one_to_many_retained_entity",
        "delete from one_to_many_removed_entity",
        "delete from one_to_many_parent_entity",

        "insert into one_to_many_parent_entity (id) values (1)",

        "insert into one_to_many_retained_entity (id, name, parent_entity_id) values (11, 'levelOneEntity1', 1)",
        "insert into one_to_many_retained_entity (id, name, parent_entity_id) values (12, 'levelOneEntity2', 1)",
        "insert into one_to_many_retained_entity (id, name, parent_entity_id) values (13, 'levelOneEntity3', 1)",
    })
    void doesNotDeleteRelated_whenRootPropertyNulled_whereNotSetToRemoveOrphans_andLoadedFromRepo() {

        executeWithinTransaction(() -> {

            final OneToManyParentEntity parentEntity = oneToManyParentEntityRepository.findById(1).get();

            // we've loaded parentEntity with complete collection - now find and remove one element from the collection
            final OneToManyRetainedEntity entityToRemove = parentEntity.getLevelOneRetainedOrphanEntities().stream()
                .filter(levelOneEntity -> levelOneEntity.getId().equals(12)).findFirst().get();

            parentEntity.getLevelOneRetainedOrphanEntities().remove(
                entityToRemove
            );

            oneToManyParentEntityRepository.save(parentEntity);
        });

        assertThat(actualRetainedRecords()).isEqualTo(asList(
            LevelOneDto.builder().id(11).name("levelOneEntity1").parentEntityId(1).build(),
            LevelOneDto.builder().id(12).name("levelOneEntity2").parentEntityId(1).build(),
            LevelOneDto.builder().id(13).name("levelOneEntity3").parentEntityId(1).build()
        ));
    }

    @Test
    @Sql(statements = {
        "delete from one_to_many_retained_entity",
        "delete from one_to_many_removed_entity",
        "delete from one_to_many_parent_entity",

        "insert into one_to_many_parent_entity (id) values (1)",

        "insert into one_to_many_retained_entity (id, name, parent_entity_id) values (11, 'levelOneEntity1', 1)",
        "insert into one_to_many_retained_entity (id, name, parent_entity_id) values (12, 'levelOneEntity2', 1)",
        "insert into one_to_many_retained_entity (id, name, parent_entity_id) values (13, 'levelOneEntity3', 1)",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void doesNotDeleteRelated_whenRootPropertyNulled_whereNotSetToRemoveOrphans_andCreatedInMemory() {

        executeWithinTransaction(() -> {

            final OneToManyParentEntity parentEntity = OneToManyParentEntity.builder().id(1).build();

            // parentEntity.setLevelOneRetainedOrphanEntities(newLinkedHashSet(
            //     OneToManyRetainedEntity.builder().id(11).name("levelOneEntity1").parentEntity(parentEntity).build(),
            //     //                                             levelOneEntity2 'removed' by not adding
            //     OneToManyRetainedEntity.builder().id(13).name("levelOneEntity3").parentEntity(parentEntity).build()
            // ));

            oneToManyParentEntityRepository.save(parentEntity);
        });

        assertThat(actualRetainedRecords()).isEqualTo(asList(
            LevelOneDto.builder().id(11).name("levelOneEntity1").parentEntityId(1).build(),
            LevelOneDto.builder().id(12).name("levelOneEntity2").parentEntityId(1).build(),
            LevelOneDto.builder().id(13).name("levelOneEntity3").parentEntityId(1).build()
        ));
    }

    private void executeWithinTransaction(final Runnable runnable) {
        transactionTemplate.execute(status -> {

            runnable.run();

            return null;
        });
    }

    private List<LevelOneDto> actualRemovedRecords() {
        return getAllRecords("one_to_many_removed_entity", LevelOneDto.class);
    }

    private List<LevelOneDto> actualRetainedRecords() {
        return getAllRecords("one_to_many_retained_entity", LevelOneDto.class);
    }

    private List<ParentDto> actualRootEntityRecords() {
        return getAllRecords("one_to_many_parent_entity", ParentDto.class);
    }

    private void logRecords() {

        logRecords(actualRootEntityRecords(), ParentDto.class);
        logRecords(actualRemovedRecords(), LevelOneDto.class);
        logRecords(actualRetainedRecords(), LevelOneDto.class);
    }

    private void logTable(final String tableName) {
        log.debug("------------- {} -------------", tableName);
        jdbcTemplate.queryForList("SELECT * FROM " +
            tableName).stream().forEach(stringObjectMap -> {
            log.debug(
                stringObjectMap.entrySet().stream()

                    .map(stringObjectEntry -> stringObjectEntry.getKey() + " : " + stringObjectEntry.getValue())
                    .flatMap(Stream::of)
                    .collect(Collectors.joining(" | ")));
        });
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
}