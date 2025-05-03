package com.marcelo.tokiomarine.tokiomarine.domain.exceptions;

import org.springframework.http.HttpStatus;

public class AlredyExist extends Exception {
    private final HttpStatus httpStatus;

    public AlredyExist(HttpStatus httpStatus, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
