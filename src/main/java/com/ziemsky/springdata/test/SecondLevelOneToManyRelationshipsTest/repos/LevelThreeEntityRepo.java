package com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.repos;

import com.ziemsky.springdata.test.SecondLevelOneToManyRelationshipsTest.entities.LevelThreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelThreeEntityRepo extends JpaRepository<LevelThreeEntity, Integer> {
}
