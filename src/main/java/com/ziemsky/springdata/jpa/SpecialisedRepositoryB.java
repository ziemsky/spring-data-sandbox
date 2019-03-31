package com.ziemsky.springdata.jpa;

import com.ziemsky.springdata.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialisedRepositoryB extends JpaRepository<User, String> {
}
