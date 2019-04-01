package com.ziemsky.springdata.jpa;

import com.ziemsky.springdata.User;

import java.util.Optional;

public interface DomainRepository {


    User save(User user);


    void flush();


    Optional<User> findById(String username);

    Optional<User> findThroughNativeQuery(String username);

}
