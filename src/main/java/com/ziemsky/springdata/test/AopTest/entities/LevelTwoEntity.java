package com.ziemsky.springdata.test.AopTest.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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



    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    // @ToString
    public static class Dto {
        private Integer id;
        private Integer levelOneEntityId;
    }
}
