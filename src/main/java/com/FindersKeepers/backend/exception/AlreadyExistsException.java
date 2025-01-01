package com.FindersKeepers.backend.exception;

import org.hibernate.service.spi.ServiceException;

public class AlreadyExistsException extends ServiceException {
    public AlreadyExistsException(String msg) {
        super(msg);
    }

}
