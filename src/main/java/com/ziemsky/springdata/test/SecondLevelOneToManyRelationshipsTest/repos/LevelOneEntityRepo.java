package com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.repos;

import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
