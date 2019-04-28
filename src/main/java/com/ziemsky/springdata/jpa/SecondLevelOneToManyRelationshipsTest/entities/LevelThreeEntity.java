package com.ziemsky.springdata.jpa.SecondLevelOneToManyRelationshipsTest.entities;

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
public class LevelThreeEntity {

    @Id
    @Column(name = "id")
    private Integer id;




    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Dto {
        private Integer id;
    }
}
