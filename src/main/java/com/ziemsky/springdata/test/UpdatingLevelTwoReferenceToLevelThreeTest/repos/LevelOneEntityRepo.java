package com.ziemsky.springdata.test.UpdatingLevelTwoReferenceToLevelThreeTest.repos;

import com.ziemsky.springdata.test.UpdatingLevelTwoReferenceToLevelThreeTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
