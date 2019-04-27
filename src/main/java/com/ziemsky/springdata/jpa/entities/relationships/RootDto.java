package com.ziemsky.springdata.jpa.entities.relationships;


import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RootDto {

    private Integer id;
    private Integer levelOneRemovedOrphanId;
    private Integer levelOneRetainedOrphanId;
}
