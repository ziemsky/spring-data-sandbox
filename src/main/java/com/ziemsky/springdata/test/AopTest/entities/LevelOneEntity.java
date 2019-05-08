package com.ziemsky.springdata.test.AopTest.entities;

import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.Set;

@Entity
// @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
// @ToString
@Setter
@Getter
@FilterDef(name = "activeAssociations")
public class LevelOneEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    // @OneToMany(fetch = FetchType.EAGER) // makes test fail
    @OneToMany
    @JoinColumn(name = "levelOneEntityId")
    @Filter(name = "activeAssociations", condition = "id = 2")
    private Set<LevelTwoEntity> levelTwoEntities;

    public Integer getId() {
        return id;
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
