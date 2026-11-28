package org.kata.store.repository;

import org.kata.store.domain.User;
import org.kata.store.service.exceptions.PostgreSQLException;

import java.sql.*;
import java.util.Optional;

public class UserSQLRepository implements UserRepository {

    private ConnectionBuilder connectionBuilder;

    public UserSQLRepository(ConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
    }

    @Override
    public Optional<User> findById(Long id) {
        Connection connection = connectionBuilder.getConnection();
        PreparedStatement statement = null;
        ResultSet result = null;
        Optional<User> rt = Optional.empty();

        try {
            connectionBuilder.createTableIfDoesNotExist(connection, getSQLQueryToCreateTables());

            String sql = "SELECT * FROM users WHERE id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);

            result = statement.executeQuery();
            if (result.next()) {
                User user = new User();
                user.setId(result.getLong("id"));
                user.setName(result.getString("name"));
                user.setSurname(result.getString("surname"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the findById query");
        } finally {
            connectionBuilder.closeComponents(result, statement, connection);
        }

        return rt;
    }

    @Override
    public User save(User user) {
        Connection connection = connectionBuilder.getConnection();
        PreparedStatement statement = null;

        try {
            connectionBuilder.createTableIfDoesNotExist(connection, getSQLQueryToCreateTables());

            String sql = "INSERT INTO users (id, name, surname) VALUES (?, ?, ?)";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getSurname());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the save query");
        } finally {
            connectionBuilder.closeComponents(statement, connection);
        }

        return user;
    }

    @Override
    public long count() {
        return connectionBuilder.count(
                "SELECT COUNT(*) AS total_rows FROM users",
                getSQLQueryToCreateTables()
        );
    }

    private String getSQLQueryToCreateTables() {
        return """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    surname VARCHAR(100) NOT NULL
                )
            """;
    }
}
