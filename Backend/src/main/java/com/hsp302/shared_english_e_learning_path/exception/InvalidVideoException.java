package com.hsp302.shared_english_e_learning_path.exception;

public class InvalidVideoException extends RuntimeException {
    
    public InvalidVideoException(String message) {
        super(message);
    }

    public InvalidVideoException(String message, Throwable cause) {
        super(message, cause);
    }
}
