package com.ziemsky.springdata.jpa.entities.relationships;

import org.springframework.data.jpa.repository.JpaRepository;


public interface OneToManyParentEntityRepository extends JpaRepository<OneToManyParentEntity, Integer> {
}
