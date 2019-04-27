package com.ziemsky.springdata.jpa.entities.relationships;


import com.ziemsky.springdata.jpa.entities.Identifiable;
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
public class RootEntity implements Identifiable {

    @Id
    @Column(name = "id")
    private Integer id;

    // single-value properties

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "level_one_removed_orphan_id")
    private RemovedEntity levelOneRemovedOrphanEntity;

    @OneToOne
    @JoinColumn(name = "level_one_retained_orphan_id")
    private RetainedEntity levelOneRetainedOrphanEntity;

    // collection properties

    @OneToMany(mappedBy = "rootEntity", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<RemovedEntity> levelOneRemovedOrphanEntities;

    @OneToMany(mappedBy = "rootEntity")
    private Set<RetainedEntity> levelOneRetainedOrphanEntities;
}
