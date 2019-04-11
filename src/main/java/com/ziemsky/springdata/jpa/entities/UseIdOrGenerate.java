package com.ziemsky.springdata.jpa.entities;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

public class UseIdOrGenerate extends IdentityGenerator {

    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
        if (obj == null) throw new HibernateException(new NullPointerException()) ;

        if ((((EntityWithGeneratedId) obj).getId()) == null) {
            Serializable id = super.generate(session, obj) ;
            return id;
        } else {
            return ((EntityWithGeneratedId) obj).getId();
        }
    }
}
