package org.kata.store.repository;

import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.service.ConnectionBuilder;
import org.kata.store.service.exceptions.PostgreSQLException;

import java.sql.*;
import java.util.Optional;

public class OrderPostgreRepository implements OrderRepository {

    private org.kata.store.service.ConnectionBuilder connectionBuilder;

    public OrderPostgreRepository(ConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
    }

    @Override
    public Order save(Order order) {
        Connection connection = connectionBuilder.getConnection();
        PreparedStatement statement = null;

        try {
            connectionBuilder.createTableIfDoesNotExist(connection, getSQLQueryToCreateTables());

            String sql = "INSERT INTO orders (id, price, status, user_id) VALUES (?, ?, ?, ?)";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, order.getId());
            statement.setFloat(2, order.getPrice());
            statement.setInt(3, order.getStatus().ordinal());
            statement.setLong(4, order.getUserId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the save query");
        } finally {
            connectionBuilder.closeComponents(statement, connection);
        }

        return order;
    }

    @Override
    public Optional<Order> findById(long id) {
        Connection connection = connectionBuilder.getConnection();
        PreparedStatement statement = null;
        ResultSet result = null;
        Optional<Order> rt = Optional.empty();

        try {
            connectionBuilder.createTableIfDoesNotExist(connection, getSQLQueryToCreateTables());

            String sql = "SELECT * FROM orders WHERE id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);

            result = statement.executeQuery();
            if (result.next()) {
                Order order = new Order(OrderStatus.fromInt(result.getInt("status")));
                order.setId(result.getLong("id"));
                order.setPrice(result.getFloat("price"));
                order.setUserId(result.getLong("user_id"));
                rt = Optional.of(order);
            }
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the findById query");
        } finally {
            connectionBuilder.closeComponents(result, statement, connection);
        }

        return rt;
    }

    @Override
    public long count() {
        Connection connection = connectionBuilder.getConnection();
        PreparedStatement statement = null;
        ResultSet result = null;
        long rt = 0L;

        try {
            connectionBuilder.createTableIfDoesNotExist(connection, getSQLQueryToCreateTables());

            String sql = "SELECT COUNT(*) AS total_rows FROM orders";

            statement = connection.prepareStatement(sql);

            result = statement.executeQuery();
            if (result.next()) {
                rt = result.getLong("total_rows");
            }
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the count query");
        } finally {
            connectionBuilder.closeComponents(result, statement, connection);
        }

        return rt;
    }

    @Override
    public Order update(Order order) {
        Connection connection = connectionBuilder.getConnection();
        PreparedStatement statement = null;

        try {
            connectionBuilder.createTableIfDoesNotExist(connection, getSQLQueryToCreateTables());

            String sql = "UPDATE orders SET price = ?, status = ?, user_id = ? WHERE id = ?";

            statement = connection.prepareStatement(sql);
            statement.setFloat(1, order.getPrice());
            statement.setInt(2, order.getStatus().ordinal());
            statement.setLong(3, order.getUserId());
            statement.setLong(4, order.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the update query");
        } finally {
            connectionBuilder.closeComponents(statement, connection);
        }

        return order;
    }

    private String getSQLQueryToCreateTables() {
        return """
            CREATE TABLE IF NOT EXISTS orders (
                id BIGSERIAL PRIMARY KEY,
                price NUMERIC(10,2) NOT NULL,
                status INTEGER NOT NULL,
                user_id BIGINT NOT NULL
            )
        """;
    }
}
