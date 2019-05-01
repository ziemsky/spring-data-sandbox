package com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.repos;

import com.ziemsky.springdata.test.LazyLoadingUniDirectionalMappedCollectionTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
