package org.kata.store.repository;

import org.kata.store.domain.Order;
import org.kata.store.domain.User;

import java.sql.*;
import java.util.Optional;

public class UserPostgreRepository implements UserRepository {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public UserPostgreRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findById(Long id) {
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

        String sql = "SELECT * FROM users WHERE id = ?";

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
                User user = new User();
                user.setId(result.getLong("id"));
                user.setName(result.getString("name"));
                user.setSurname(result.getString("surname"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining the result");
        }

        return Optional.empty();
    }

    @Override
    public User save(User user) {
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

        String sql = "INSERT INTO users (id, name, surname) VALUES (?, ?, ?)";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getSurname());
        } catch (SQLException e) {
            throw new RuntimeException("Problem preparing the statement");
        }

        int result = -1;
        try {
            result = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Problem executing the query");
        }

        return user;
    }

    @Override
    public long count() {
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

        String sql = "SELECT COUNT(*) AS total_rows FROM users";

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
    public User update(User user) {
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

        String sql = "UPDATE users SET name = ?, surname = ? WHERE id = ?";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getSurname());
        } catch (SQLException e) {
            throw new RuntimeException("Problem preparing the statement");
        }

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Problem executing the query");
        }

        return user;
    }

    private void createTableIfDoesNotExist(Connection connection) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                surname VARCHAR(100) NOT NULL
            )
        """;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
}
