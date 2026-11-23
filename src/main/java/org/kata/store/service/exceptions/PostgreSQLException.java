package org.kata.store.service.exceptions;

public class PostgreSQLException extends RuntimeException{
    public PostgreSQLException(String s) {
        super(s);
    }
}
