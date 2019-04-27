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
public class OneToOneRetainedEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;
}
