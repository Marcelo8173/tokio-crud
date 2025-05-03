package com.marcelo.tokiomarine.tokiomarine.domain.exceptions;

import org.springframework.http.HttpStatus;

public class NotValid extends RuntimeException {

    private final HttpStatus httpStatus;

    public NotValid(HttpStatus httpStatus, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
