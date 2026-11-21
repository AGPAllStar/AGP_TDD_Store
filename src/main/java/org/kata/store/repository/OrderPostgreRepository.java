package org.kata.store.repository;

import org.kata.store.domain.Order;

import java.util.Optional;

public class OrderPostgreRepository implements OrderRepository {

    private String jdbcUrl;
    private String username;
    private String password;

    public OrderPostgreRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Optional<Order> findById(long id) {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }
}
