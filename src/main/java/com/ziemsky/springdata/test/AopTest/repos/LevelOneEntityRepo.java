package com.ziemsky.springdata.test.AopTest.repos;

import com.ziemsky.springdata.test.AopTest.entities.LevelOneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelOneEntityRepo extends JpaRepository<LevelOneEntity, Integer> {
}
