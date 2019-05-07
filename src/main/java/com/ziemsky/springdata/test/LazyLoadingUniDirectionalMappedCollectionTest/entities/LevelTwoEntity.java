package com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.entities;

import com.ziemsky.springdata.test.AddingReplacingLevelTwoCollectionRecordsTest.entities.LevelOneEntity;
import lombok.*;

import javax.persistence.*;

@Entity
// @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
// @ToString
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
