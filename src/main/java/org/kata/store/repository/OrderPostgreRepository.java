package org.kata.store.repository;

import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;

import java.sql.*;
import java.util.Optional;

public class OrderPostgreRepository implements OrderRepository {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public OrderPostgreRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public Order save(Order order) {
        Connection connection = getConnection();

        String sql = "INSERT INTO orders (id, price, status, user_id) VALUES (?, ?, ?, ?)";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, order.getId());
            statement.setFloat(2, order.getPrice());
            statement.setInt(3, order.getStatus().ordinal());
            statement.setLong(4, order.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException("Problem preparing the statement");
        }

        int result = -1;
        try {
            result = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Problem executing the query");
        }

        return order;
    }

    @Override
    public Optional<Order> findById(long id) {
        Connection connection = getConnection();

        String sql = "SELECT * FROM orders WHERE id = ?";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
        } catch (SQLException e) {
            throw new RuntimeException("Problem preparing the statement");
        }

        ResultSet result = null;
        try {
            result = statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Problem executing the query");
        }

        try {
            if (result.next()) {
                Order order = new Order(OrderStatus.fromInt(result.getInt("status")));
                order.setId(result.getLong("id"));
                order.setPrice(result.getFloat("price"));
                order.setUserId(result.getLong("user_id"));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining the result");
        }

        return Optional.empty();
    }

    @Override
    public long count() {
        Connection connection = getConnection();

        String sql = "SELECT COUNT(*) AS total_rows FROM orders";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Problem preparing the statement");
        }

        ResultSet result = null;
        try {
            result = statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Problem executing the query");
        }

        try {
            if (result.next()) {
                return result.getLong("total_rows");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining the result");
        }
        return -1;
    }

    @Override
    public Order update(Order order) {
        Connection connection = getConnection();

        String sql = "UPDATE orders SET price = ?, status = ?, user_id = ? WHERE id = ?";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setFloat(1, order.getPrice());
            statement.setInt(2, order.getStatus().ordinal());
            statement.setLong(3, order.getUserId());
            statement.setLong(4, order.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Problem preparing the statement");
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Problem executing the query");
        }

        return order;
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    jdbcUrl,
                    username,
                    password
            );
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database");
        }

        try {
            createTableIfDoesNotExist(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Problem creating the table");
        }

        return connection;
    }

    private void createTableIfDoesNotExist(Connection connection) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS orders (
                id BIGSERIAL PRIMARY KEY,
                price NUMERIC(10,2) NOT NULL,
                status INTEGER NOT NULL,
                user_id BIGINT NOT NULL
            )
        """;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
}
