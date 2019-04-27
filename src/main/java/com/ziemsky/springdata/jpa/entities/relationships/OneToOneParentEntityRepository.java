package com.ziemsky.springdata.jpa.entities.relationships;

import org.springframework.data.jpa.repository.JpaRepository;


public interface OneToOneParentEntityRepository extends JpaRepository<OneToOneParentEntity, Integer> {
}
