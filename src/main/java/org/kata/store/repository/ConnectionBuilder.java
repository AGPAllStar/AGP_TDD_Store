package org.kata.store.repository;

import org.kata.store.service.exceptions.PostgreSQLException;

import java.sql.*;

public class ConnectionBuilder {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public ConnectionBuilder(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public ConnectionBuilder(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        this.username = null;
        this.password = null;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            if(username == null || password == null) {
                connection = DriverManager.getConnection(jdbcUrl);
            }else{
                connection = DriverManager.getConnection(
                        jdbcUrl,
                        username,
                        password
                );
            }
        } catch (SQLException e) {
            throw new PostgreSQLException("Cannot connect to database", e);
        }
        return connection;
    }

    public void createTableIfDoesNotExist(Connection connection, String sqlQuery) throws SQLException {
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
        } finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    public void closeComponents(PreparedStatement statement, Connection connection){
        if (statement != null) {
            try{
                statement.close();
            } catch (SQLException e) {
                throw new PostgreSQLException("Problem closing the Statement");
            }
        }
        if (connection != null) {
            try{
                connection.close();
            } catch (SQLException e) {
                throw new PostgreSQLException("Problem closing the Connection");
            }
        }
    }

    public void closeComponents(ResultSet resultSet, PreparedStatement statement, Connection connection){
        if (resultSet != null) {
            try{
                resultSet.close();
            } catch (SQLException e) {
                throw new PostgreSQLException("Problem closing the ResultSet");
            }
        }
        closeComponents(statement, connection);
    }

    public long count(String sqlQuery, String queryToCreateTables) {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet result = null;
        long rt = 0L;

        try {
            createTableIfDoesNotExist(connection, queryToCreateTables);

            statement = connection.prepareStatement(sqlQuery);

            result = statement.executeQuery();
            if (result.next()) {
                rt = result.getLong("total_rows");
            }
        } catch (SQLException e) {
            throw new PostgreSQLException("Problem executing the count query");
        } finally {
            closeComponents(result, statement, connection);
        }

        return rt;
    }
}
