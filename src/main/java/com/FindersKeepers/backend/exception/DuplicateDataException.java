package com.FindersKeepers.backend.exception;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.service.spi.ServiceException;

@Getter
@Setter
public class DuplicateDataException extends ServiceException {
    private String message;
    private String fieldName;

    public DuplicateDataException(String message, String fieldName) {
        super(message);
        this.message = message;
        this.fieldName = fieldName;
    }

}
