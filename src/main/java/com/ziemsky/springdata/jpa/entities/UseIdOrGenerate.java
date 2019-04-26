package com.ziemsky.springdata.jpa.entities;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

public class UseIdOrGenerate extends IdentityGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        if (obj == null) throw new HibernateException(new NullPointerException()) ;

        if ((((Identifiable) obj).getId()) == null) {
            Serializable id = super.generate(session, obj) ;
            return id;
        } else {
            return ((Identifiable) obj).getId();
        }
    }
}
