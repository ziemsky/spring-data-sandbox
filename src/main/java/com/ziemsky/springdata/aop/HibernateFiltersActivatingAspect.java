package com.ziemsky.springdata.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Aspect
public class HibernateFiltersActivatingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext EntityManager entityManager;

    @Before("execution(@javax.transaction.Transactional * *(*))")
    public void enableFilters() {

        log.debug("ENABLING FILTERS");

        entityManager.unwrap(Session.class).enableFilter("activeAssociations");

        log.debug("FILTERS ENABLED");
    }
}
