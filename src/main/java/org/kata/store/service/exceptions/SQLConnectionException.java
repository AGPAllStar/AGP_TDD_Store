package org.kata.store.service.exceptions;

public class SQLConnectionException extends RuntimeException{
    public SQLConnectionException(String s) {
        super(s);
    }

    public SQLConnectionException(String s, Exception e) {
        super(s, e);
    }
}
