package com.tictactoe.domain.exception;

public class InvalidGameConfigException extends RuntimeException {

    public InvalidGameConfigException(String message) {
        super(message);
    }
}
