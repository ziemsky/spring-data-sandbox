package com.ziemsky.springdata;


import com.ziemsky.springdata.test.AopTest.entities.LevelOneEntity;
import com.ziemsky.springdata.test.AopTest.repos.LevelOneEntityRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

public class TransactionalService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public LevelOneEntity transactionalMethod(LevelOneEntityRepo levelOneEntityRepo) {
        log.error("TRANSACTIONAL METHOD CALLED");

        return levelOneEntityRepo.findById(1).get();
    }
}
