package com.ziemsky.springdata.test.UpdatingLevelTwoReferenceToLevelThreeTest.repos;

import com.ziemsky.springdata.test.UpdatingLevelTwoReferenceToLevelThreeTest.entities.LevelThreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelThreeEntityRepo extends JpaRepository<LevelThreeEntity, Integer> {
}
