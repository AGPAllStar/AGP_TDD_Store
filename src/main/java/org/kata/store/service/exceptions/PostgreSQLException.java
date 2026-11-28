package org.kata.store.service.exceptions;

public class PostgreSQLException extends RuntimeException{
    public PostgreSQLException(String s) {
        super(s);
    }

    public PostgreSQLException(String s, Exception e) {
        super(s, e);
    }
}
