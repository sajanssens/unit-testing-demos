package org.example.unittesting.mockito;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, IllegalArgumentException e) {
        super(message, e);
    }
}
