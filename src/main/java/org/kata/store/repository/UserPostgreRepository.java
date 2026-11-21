package org.kata.store.repository;

import org.kata.store.domain.User;

import java.util.Optional;

public class UserPostgreRepository implements UserRepository {

    private String jdbcUrl;
    private String username;
    private String password;

    public UserPostgreRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }
}
