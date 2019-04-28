package com.ziemsky.springdata.jpa.SecondLevelOneToManyRelationshipsTest.repos;

import com.ziemsky.springdata.jpa.SecondLevelOneToManyRelationshipsTest.entities.LevelThreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelThreeEntityRepo extends JpaRepository<LevelThreeEntity, Integer> {
}
