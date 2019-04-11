package com.ziemsky.springdata.jpa;

import com.ziemsky.springdata.jpa.entities.EntityWithGeneratedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialisedRepositoryD extends JpaRepository<EntityWithGeneratedId, Integer> {
}
