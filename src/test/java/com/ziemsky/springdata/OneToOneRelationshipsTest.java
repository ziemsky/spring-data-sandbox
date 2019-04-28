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

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {
        Application.class,
        Config.class
    }
)
class OneToOneRelationshipsTest {

    private Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private OneToOneParentEntityRepository oneToOneParentEntityRepository;

    @Autowired
    private DataSource dataSource;

    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AfterEach
    void tearDown() {
        logRecords();
    }

    // orphanRemoval determines whether related record will be removed when root property gets nulled,
    // works the same for entities loaded from db and those created in memory (matching existing ones)

    @Test
    @Sql(statements = {
        "delete from one_to_one_parent_entity",
        "delete from one_to_one_removed_entity",
        "delete from one_to_one_retained_entity",

        "insert into one_to_one_removed_entity (id, name) values (10, 'levelOneEntity')",
        "insert into one_to_one_parent_entity (id, level_one_removed_orphan_id) values (1, 10)",
    })
    void deletesRelated_whenRootPropertyNulled_whereSetToRemoveOrphans_andLoadedFromRepo() {

        final OneToOneParentEntity rootEntity = oneToOneParentEntityRepository.findById(1).get();

        rootEntity.setLevelOneRemovedOrphanEntity(null);


        oneToOneParentEntityRepository.saveAndFlush(rootEntity);


        assertThat(actualParentEntityRecords()).isEqualTo(asList(
            ParentDto.builder().id(1).build()
        ));

        assertThat(actualRemovedRecords()).isEmpty();
    }

    @Test
    @Sql(statements = {
        "delete from one_to_one_parent_entity",
        "delete from one_to_one_removed_entity",
        "delete from one_to_one_retained_entity",

        "insert into one_to_one_removed_entity (id, name) values (10, 'levelOneEntity')",
        "insert into one_to_one_parent_entity (id, level_one_removed_orphan_id) values (1, 10)",
    })
    void deletesRelated_whenRootPropertyNulled_whereSetToRemoveOrphans_andCreatedInMemory() {

        final OneToOneParentEntity rootEntity = OneToOneParentEntity.builder().id(1).build();

        rootEntity.setLevelOneRemovedOrphanEntity(null);


        oneToOneParentEntityRepository.saveAndFlush(rootEntity);


        assertThat(actualParentEntityRecords()).isEqualTo(asList(
            ParentDto.builder().id(1).build()
        ));

        assertThat(actualRemovedRecords()).isEmpty();
    }

    @Test
    @Sql(statements = {
        "delete from one_to_one_parent_entity",
        "delete from one_to_one_removed_entity",
        "delete from one_to_one_retained_entity",

        "insert into one_to_one_retained_entity (id, name) values (10, 'levelOneEntity')",
        "insert into one_to_one_parent_entity (id, level_one_retained_orphan_id) values (1, 10)",
    })
    void doesNotDeleteRelated_whenRootPropertyNulled_whereNotSetToRemoveOrphans_andLoadedFromRepo() {

        final OneToOneParentEntity rootEntity = oneToOneParentEntityRepository.findById(1).get();

        rootEntity.setLevelOneRetainedOrphanEntity(null);


        oneToOneParentEntityRepository.saveAndFlush(rootEntity);


        assertThat(actualParentEntityRecords()).isEqualTo(asList(
            ParentDto.builder().id(1).build()
        ));

        assertThat(actualRetainedRecords()).isEqualTo(asList(
            LevelOneDto.builder()
                .id(10)
                .name("levelOneEntity")
                .build()
        ));
    }

    @Test
    @Sql(statements = {
        "delete from one_to_one_parent_entity",
        "delete from one_to_one_removed_entity",
        "delete from one_to_one_retained_entity",

        "insert into one_to_one_retained_entity (id, name) values (10, 'levelOneEntity')",
        "insert into one_to_one_parent_entity (id, level_one_retained_orphan_id) values (1, 10)",
    })
    void doesNotDeleteRelated_whenRootPropertyNulled_whereNotSetToRemoveOrphans_andCreatedInMemory() {

        final OneToOneParentEntity rootEntity = OneToOneParentEntity.builder().id(1).build();

        rootEntity.setLevelOneRetainedOrphanEntity(null);

        oneToOneParentEntityRepository.saveAndFlush(rootEntity);


        assertThat(actualParentEntityRecords()).isEqualTo(asList(
            ParentDto.builder().id(1).build()
        ));

        assertThat(actualRetainedRecords()).isEqualTo(asList(
            LevelOneDto.builder()
                .id(10)
                .name("levelOneEntity")
                .build()
        ));
    }

    private List<LevelOneDto> actualRemovedRecords() {
        return getAllRecords("one_to_one_removed_entity", LevelOneDto.class);
    }

    private List<LevelOneDto> actualRetainedRecords() {
        return getAllRecords("one_to_one_retained_entity", LevelOneDto.class);
    }

    private List<ParentDto> actualParentEntityRecords() {
        return getAllRecords("one_to_one_parent_entity", ParentDto.class);
    }

    private void logRecords() {
        final List<ParentDto> parentDtos = actualParentEntityRecords();
        final List<LevelOneDto> levelOneDtos = actualRemovedRecords();

        logRecords(parentDtos, ParentDto.class);
        logRecords(levelOneDtos, OneToOneRemovedEntity.class);
        logRecords(levelOneDtos, OneToOneRetainedEntity.class);
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

    private <T> void logRecords(final List<T> dtos, final Class<?> dtoClass) {
        log.debug("FOUND RECORDS {} x {}{}", dtos.size(), dtoClass.getSimpleName(), dtos.isEmpty() ? "" : ":");
        dtos.forEach(dto -> log.debug("{}", dto));
    }

    private <T> List<T> getAllRecords(final String tableName, final Class<T> dtoClass) {
        return jdbcTemplate.query(
            "SELECT * FROM " + tableName, new BeanPropertyRowMapper<>(dtoClass)
        ).stream().collect(toList());
    }
}