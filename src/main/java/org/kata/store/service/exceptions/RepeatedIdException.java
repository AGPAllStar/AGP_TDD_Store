package org.kata.store.service.exceptions;

public class RepeatedIdException extends RuntimeException{
    public RepeatedIdException(String s) {
        super(s);
    }
}
