package com.ziemsky.springdata.test.FirstLevelOneToManyRelationshipsTest.repos;

import com.ziemsky.springdata.test.FirstLevelOneToManyRelationshipsTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
