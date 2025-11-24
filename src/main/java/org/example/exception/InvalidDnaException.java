package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Excepción para indicar que el ADN recibido es invalido
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDnaException extends RuntimeException {

    public InvalidDnaException(String message) {
        super(message);
    }
}
