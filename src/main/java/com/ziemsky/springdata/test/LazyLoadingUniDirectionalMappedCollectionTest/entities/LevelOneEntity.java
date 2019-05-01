package com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
// @ToString
@Setter
@Getter
public class LevelOneEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @OneToMany
    @JoinColumn(name = "levelOneEntityId")
    private Set<LevelTwoEntity> levelTwoEntities;


    public Set<LevelTwoEntity> getLevelTwoEntities() {
        System.out.println("getLevelTwoEntities CALLED");
        new Exception("LOADING!!!").printStackTrace();
        return levelTwoEntities;
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    // @ToString
    public static class Dto {
        private Integer id;
    }
}
