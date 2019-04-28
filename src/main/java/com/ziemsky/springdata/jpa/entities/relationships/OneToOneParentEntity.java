package com.ziemsky.springdata.jpa.entities.relationships;


import com.ziemsky.springdata.jpa.entities.Identifiable;
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
public class OneToOneParentEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "level_one_removed_orphan_id")
    private OneToOneRemovedEntity levelOneRemovedOrphanEntity;

    @OneToOne
    @JoinColumn(name = "level_one_retained_orphan_id")
    private OneToOneRetainedEntity levelOneRetainedOrphanEntity;


}
