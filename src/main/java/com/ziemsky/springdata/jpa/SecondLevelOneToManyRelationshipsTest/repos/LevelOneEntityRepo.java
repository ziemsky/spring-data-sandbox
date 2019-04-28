package com.ziemsky.springdata.jpa.SecondLevelOneToManyRelationshipsTest.repos;

import com.ziemsky.springdata.jpa.SecondLevelOneToManyRelationshipsTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
