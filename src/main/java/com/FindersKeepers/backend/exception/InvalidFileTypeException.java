package com.FindersKeepers.backend.exception;

import org.hibernate.service.spi.ServiceException;

public class InvalidFileTypeException extends ServiceException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}