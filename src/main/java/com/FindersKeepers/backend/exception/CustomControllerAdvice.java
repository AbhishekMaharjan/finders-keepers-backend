package com.FindersKeepers.backend.exception;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.expression.ParseException;
import org.springframework.http.*;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.*;


@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    private static final String MESSAGE = "Error";
    private final CustomMessageSource customMessageSource;

    public CustomControllerAdvice(CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = customMessageSource.get(ERROR_TYPE_MISMATCH, ex.getValue(), ex.getPropertyName(), ex.getRequiredType());
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(error));
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = customMessageSource.get(ERROR_REQUEST_PART_MISSING, ex.getRequestPartName());
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(error));
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = customMessageSource.get(ERROR_REQUEST_PARAMETER_MISSING, ex.getParameterName());
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(error));
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final String error = customMessageSource.get(ERROR_METHOD_ARGUMENT_MISMATCH, ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getName());
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(error));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateDataException.class})
    public ResponseEntity<Object> duplicateData(final DuplicateDataException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final String error = customMessageSource.get(ERROR_DUPLICATE_DATA, ex.getFieldName());
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(error));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolation(final DataIntegrityViolationException e, final WebRequest request) {
        List<String> errors = new ArrayList<>();
        Throwable throwable = e.getCause();
        String violationType;
        String message = e.getLocalizedMessage();
        if (throwable instanceof PropertyValueException ex) {
            violationType = determineViolationType(ex.getMessage());
            message = determineMessage(violationType, ex);
        }

        GlobalApiResponse apiError = new GlobalApiResponse(false, message, null, errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private String determineViolationType(String violation) {
        if (violation.contains("unique")) {
            return "Unique Constraint Violation";
        } else if (violation.contains("foreign key")) {
            return "Foreign Key Constraint Violation ";
        } else if (violation.contains("check constraint")) {
            return "Check Constraint Violation";
        } else if (violation.contains("null")) {
            return "Not Null Constraint Violation";
        } else {
            return "Unknown Constraint Violation";
        }
    }

    private String determineMessage(String violation, PropertyValueException ex) {
        if (violation.contains("Unique Constraint Violation")) {
            return customMessageSource.get(ERROR_NAME_MUST_BE_UNIQUE, ex.getPropertyName());
        } else if (violation.contains("Foreign Key Constraint Violation")) {
            return customMessageSource.get(ERROR_FOREIGN_KEY, ex.getPropertyName());
        } else if (violation.contains("Check Constraint Violation")) {
            return customMessageSource.get(ERROR_CHECK_CONSTRAINT, ex.getPropertyName());
        } else if (violation.contains("Not Null Constraint Violation")) {
            return customMessageSource.get(ERROR_METHOD_ARGUMENT_NOTNULL, ex.getPropertyName());
        } else {
            return "Unknown Constraint Violation";
        }
    }


    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = "No handler updateLostAndFound for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(error));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        for (HttpMethod t : Objects.requireNonNull(ex.getSupportedHttpMethods())) {
            builder.append(t).append(" ");
        }

        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(builder.toString()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(builder.toString()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(bodyOfResponse));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = TransactionSystemException.class)
    public ResponseEntity<Object> handleTransactionSystemException(final TransactionSystemException e, final WebRequest request) {
        if (e.getCause().getCause() instanceof ConstraintViolationException constraintViolationException)
            return handleConstraintViolation(constraintViolationException, request);
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(e.getLocalizedMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<Object> handleIOException(final IOException e, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(e.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleAllNotFound(final Exception ex, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({SessionException.class})
    public ResponseEntity<Object> handleAllSessionException(final Exception ex, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<Object> handleAllAlreadyExist(final Exception ex, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ValidationFailedException.class})
    public ResponseEntity<Object> handleAllValidationException(final Exception ex, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({InvalidRequestBodyException.class})
    public ResponseEntity<Object> handleInvalidRequestBody(final Exception ex, final WebRequest request) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidFileTypeException(InvalidFileTypeException ex) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> parseException(ParseException ex) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, MESSAGE, null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> runtimeException(ServiceException ex) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> runtimeException(RuntimeException ex) {
        final GlobalApiResponse apiError = new GlobalApiResponse(false, ex.getMessage(), null, Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}