package com.ziemsky.springdata.jpa.SecondLevelOneToManyRelationshipsTest.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Setter
@Getter
public class LevelOneEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @OneToMany(mappedBy = "parentEntity", cascade = CascadeType.MERGE)
    private Set<LevelTwoEntity> levelTwoEntities;


    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Dto {
        private Integer id;
    }
}
