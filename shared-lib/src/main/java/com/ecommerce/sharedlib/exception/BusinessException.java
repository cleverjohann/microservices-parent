package com.ecommerce.sharedlib.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base para todos los errores de negocio en los microservicios.
 * Todas las excepciones específicas de dominio deben extender de esta clase.
 */
public abstract class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;

    protected BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    protected BusinessException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

