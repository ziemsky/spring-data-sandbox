package com.ziemsky.springdata.jpa.entities.relationships;

import lombok.*;

import javax.persistence.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LevelOneDto {

    private Integer id;
    private String name;

    private Integer parentEntityId;
}
