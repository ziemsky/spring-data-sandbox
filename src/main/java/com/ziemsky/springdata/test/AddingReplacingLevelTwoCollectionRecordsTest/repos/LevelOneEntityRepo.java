package com.ziemsky.springdata.test.AddingReplacingLevelTwoCollectionRecordsTest.repos;

import com.ziemsky.springdata.test.AddingReplacingLevelTwoCollectionRecordsTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
