package com.ziemsky.springdata.jpa.entities.relationships;


import com.ziemsky.springdata.jpa.entities.Identifiable;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Setter
@Getter
public class OneToManyParentEntity implements Identifiable {

    @Id
    @Column(name = "id")
    private Integer id;

    @OneToMany(mappedBy = "parentEntity", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<OneToManyRemovedEntity> levelOneRemovedOrphanEntities;

    @OneToMany(mappedBy = "parentEntity")
    private Set<OneToManyRetainedEntity> levelOneRetainedOrphanEntities;
}
