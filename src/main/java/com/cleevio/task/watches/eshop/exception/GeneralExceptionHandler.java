/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String FIELDS_SEPARATOR = ":";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus httpStatus,
                                                                  WebRequest request) {
        List<String> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(violation -> violation.getField() + FIELDS_SEPARATOR + violation.getDefaultMessage())
                .collect(Collectors.toList());

        ExceptionResponseBody responseBody = ExceptionResponseBody.builder(
                HttpStatus.BAD_REQUEST,
                exception.getClass().getSimpleName(),
                httpStatus.getReasonPhrase()
        ).atPath(request.getDescription(false)).hasErrors(validationErrors).build();
        return new ResponseEntity<>(responseBody, responseBody.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus httpStatus,
                                                                  WebRequest request) {
        ExceptionResponseBody responseBody = ExceptionResponseBody.builder(
                HttpStatus.BAD_REQUEST,
                exception.getClass().getSimpleName(),
                httpStatus.getReasonPhrase()
        ).atPath(request.getDescription(false))
                .hasErrors(Collections.singletonList(exception.getLocalizedMessage())).build();
        return new ResponseEntity<>(responseBody, responseBody.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception,
                                                                     HttpHeaders headers,
                                                                     HttpStatus httpStatus,
                                                                     WebRequest request) {
        ExceptionResponseBody responseBody = ExceptionResponseBody.builder(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                exception.getClass().getSimpleName(),
                httpStatus.getReasonPhrase()
        ).atPath(request.getDescription(false))
                .hasErrors(Collections.singletonList(exception.getLocalizedMessage())).build();
        return new ResponseEntity<>(responseBody, responseBody.getHttpStatus());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException exception, WebRequest request) {
        return new ResponseEntity<>(ExceptionResponseBody.builder(
                exception.getStatus(),
                exception.getClass().getSimpleName(),
                exception.getLocalizedMessage()
        ).atPath(request.getDescription(false)).build(), exception.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception,
                                                            WebRequest request) {
        List<String> validationErrors = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + FIELDS_SEPARATOR + violation.getMessage())
                .collect(Collectors.toList());

        ExceptionResponseBody responseBody = ExceptionResponseBody.builder(
                HttpStatus.BAD_REQUEST,
                exception.getClass().getSimpleName(),
                exception.getLocalizedMessage()
        ).atPath(request.getDescription(false)).hasErrors(validationErrors).build();

        return new ResponseEntity<>(responseBody, responseBody.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus httpStatus = (responseStatus != null) ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponseBody responseBody = ExceptionResponseBody.builder(
                httpStatus,
                exception.getClass().getSimpleName(),
                httpStatus.getReasonPhrase()
        ).atPath(request.getDescription(false))
                .hasErrors(Collections.singletonList(exception.getLocalizedMessage())).build();
        return new ResponseEntity<>(responseBody, responseBody.getHttpStatus());
    }

}
