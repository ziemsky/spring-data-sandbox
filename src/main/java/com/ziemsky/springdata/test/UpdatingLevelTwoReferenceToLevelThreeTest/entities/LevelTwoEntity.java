package com.ziemsky.springdata.test.UpdatingLevelTwoReferenceToLevelThreeTest.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Setter
@Getter
public class LevelTwoEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parentEntityId")
    @EqualsAndHashCode.Exclude
    private LevelOneEntity parentEntity;

    @ManyToOne
    @JoinColumn(name = "levelThreeEntityId")
    @EqualsAndHashCode.Exclude
    private LevelThreeEntity levelThreeEntity;


    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Dto {
        private Integer id;
        private Integer parentEntityId;
        private Integer levelThreeEntityId;
    }
}
