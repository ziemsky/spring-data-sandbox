package com.ziemsky.springdata;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private String username;

    private User() {
    }

    public User(final String username) {
        this.username = username;
    }

    @Override public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            '}';
    }
}
