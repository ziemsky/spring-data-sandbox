package com.ziemsky.springdata.jpa;

import com.ziemsky.springdata.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpecialisedRepositoryA extends JpaRepository<User, String>, DomainRepository {

    @Query(nativeQuery = true, value = "SELECT * FROM User WHERE username = :username")
    Optional<User> findThroughNativeQuery(@Param("username") String username);
}
