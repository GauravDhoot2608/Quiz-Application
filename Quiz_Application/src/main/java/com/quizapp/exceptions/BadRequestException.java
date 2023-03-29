package com.quizapp.exceptions;

import java.util.List;

public class BadRequestException extends RuntimeException {

    private final List<String> errors;

    public BadRequestException(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        return errors.toString();
    }
}
