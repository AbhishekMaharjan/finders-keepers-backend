package com.FindersKeepers.backend.exception;

import java.rmi.ServerException;

public class InvalidRequestBodyException extends ServerException {
    public InvalidRequestBodyException(String e) {
        super(e);
    }
}