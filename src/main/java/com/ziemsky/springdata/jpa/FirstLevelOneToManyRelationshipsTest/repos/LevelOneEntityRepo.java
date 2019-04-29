package com.ziemsky.springdata.jpa.FirstLevelOneToManyRelationshipsTest.repos;

import com.ziemsky.springdata.jpa.FirstLevelOneToManyRelationshipsTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
