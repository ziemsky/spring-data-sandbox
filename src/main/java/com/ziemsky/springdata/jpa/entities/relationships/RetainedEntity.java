package com.ziemsky.springdata.jpa.entities.relationships;

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
public class RetainedEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "rootEntityId")
    @EqualsAndHashCode.Exclude
    private RootEntity rootEntity;
}
